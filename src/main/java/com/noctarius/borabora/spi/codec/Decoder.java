/*
 * Copyright (c) 2016, Christoph Engelbert (aka noctarius) and
 * contributors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.noctarius.borabora.spi.codec;

import com.noctarius.borabora.Dictionary;
import com.noctarius.borabora.HalfPrecisionFloat;
import com.noctarius.borabora.Input;
import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.Sequence;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.ValueType;
import com.noctarius.borabora.ValueTypes;
import com.noctarius.borabora.impl.DictionaryImpl;
import com.noctarius.borabora.impl.SequenceImpl;
import com.noctarius.borabora.spi.Constants;
import com.noctarius.borabora.spi.RelocatableStreamValue;
import com.noctarius.borabora.spi.StreamValue;
import com.noctarius.borabora.spi.query.QueryContext;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Predicate;

public final class Decoder
        implements Constants {

    private Decoder() {
    }

    public static short readUInt8(Input input, long offset) {
        return Bytes.readUInt8(input, offset);
    }

    public static Number readInt(Input input, long offset) {
        short head = Bytes.readUInt8(input, offset);
        long mask = -((head & 0xff) >>> 5);
        int byteSize = ByteSizes.intByteSize(input, offset);
        Number number;
        switch (byteSize) {
            case 2:
                number = mask ^ Bytes.readUInt8(input, offset + 1);
                break;
            case 3:
                number = mask ^ Bytes.readUInt16(input, offset + 1);
                break;
            case 5:
                number = mask ^ Bytes.readUInt32(input, offset + 1);
                break;
            case 9:
                long v = Bytes.readUInt64Long(input, offset + 1);
                if (v < 0) {
                    number = BigInteger.valueOf(mask).xor(Bytes.readUInt64BigInt(input, offset + 1));
                } else {
                    number = mask ^ v;
                }
                break;
            default:
                number = mask ^ (head & ADDITIONAL_INFORMATION_MASK);
        }
        return number;
    }

    public static Number readUint(Input input, long offset) {
        short head = Bytes.readUInt8(input, offset);
        int byteSize = ByteSizes.intByteSize(input, offset);
        Number number;
        switch (byteSize) {
            case 2:
                number = Bytes.readUInt8(input, offset + 1);
                break;
            case 3:
                number = Bytes.readUInt16(input, offset + 1);
                break;
            case 5:
                number = Bytes.readUInt32(input, offset + 1);
                break;
            case 9:
                number = Bytes.readUInt64BigInt(input, offset + 1);
                break;
            default:
                number = head & ADDITIONAL_INFORMATION_MASK;
        }
        return number;
    }

    public static Number readFloat(Input input, long offset) {
        int addInfo = additionalInfo(input, offset);
        switch (addInfo) {
            case FP_VALUE_HALF_PRECISION:
                return readHalfFloatValue(input, offset + 1);
            case FP_VALUE_SINGLE_PRECISION:
                return readSinglePrecisionFloat(input, offset + 1);
            case FP_VALUE_DOUBLE_PRECISION:
                return readDoublePrecisionFloat(input, offset + 1);
            default:
                throw new IllegalStateException("Additional Info '" + addInfo + "' is not a floating point value");
        }
    }

    public static Number readNumber(Input input, ValueType valueType, long offset) {
        if (valueType.matches(ValueTypes.Float)) {
            return readFloat(input, offset);
        }
        return readInt(input, offset);
    }

    public static String readString(Input input, long offset) {
        int addInfo = additionalInfo(input, offset);
        if (addInfo == 31) {
            // Concatenated string!
            long position = offset + 1;
            StringBuilder sb = new StringBuilder();
            while (true) {
                short h = Bytes.readUInt8(input, position);
                if ((h & OPCODE_BREAK_MASK) == OPCODE_BREAK_MASK) {
                    break;
                }
                sb.append(readString(input, position));
                position += ByteSizes.stringByteSize(input, position);
            }
            return sb.toString();
        }

        return readString0(input, offset);
    }

    public static Sequence readSequence(long offset, QueryContext queryContext) {
        Input input = queryContext.input();
        long headByteSize = ByteSizes.headByteSize(input, offset);
        long size = ElementCounts.sequenceElementCount(input, offset);
        long[][] elementIndexes = readElementIndexes(input, offset + headByteSize, size);
        return new SequenceImpl(size, elementIndexes, queryContext);
    }

    public static Dictionary readDictionary(long offset, QueryContext queryContext) {
        Input input = queryContext.input();
        long headByteSize = ByteSizes.headByteSize(input, offset);
        long size = ElementCounts.dictionaryElementCount(input, offset);
        long[][] elementIndexes = readElementIndexes(input, offset + headByteSize, size * 2);
        return new DictionaryImpl(size, elementIndexes, queryContext);
    }

    public static long length(Input input, MajorType majorType, long offset) {
        switch (majorType) {
            case UnsignedInteger:
            case NegativeInteger:
                return ByteSizes.intByteSize(input, offset);
            case ByteString:
            case TextString:
                return ByteSizes.stringByteSize(input, offset);
            case Sequence:
                return ByteSizes.sequenceByteSize(input, offset);
            case Dictionary:
                return ByteSizes.dictionaryByteSize(input, offset);
            case SemanticTag:
                return ByteSizes.semanticTagByteSize(input, offset);
            case FloatingPointOrSimple:
                return ByteSizes.floatOrSimpleByteSize(input, offset);
        }
        throw new IllegalStateException("Illegal MajorType requested");
    }

    public static long skip(Input input, long offset) {
        short head = Bytes.readUInt8(input, offset);
        MajorType majorType = MajorType.findMajorType(head);
        return skip(input, majorType, offset);
    }

    public static long skip(Input input, MajorType majorType, long offset) {
        long size = length(input, majorType, offset);
        return offset + size;
    }

    public static boolean isNull(short head) {
        MajorType majorType = MajorType.findMajorType(head);
        if (MajorType.FloatingPointOrSimple != majorType) {
            return false;
        }
        int addInfo = head & ADDITIONAL_INFORMATION_MASK;
        return addInfo == FP_VALUE_NULL;
    }

    public static boolean getBooleanValue(Input input, long offset) {
        short head = Bytes.readUInt8(input, offset);
        MajorType majorType = MajorType.findMajorType(head);
        if (MajorType.FloatingPointOrSimple == majorType) {
            int addInfo = head & ADDITIONAL_INFORMATION_MASK;
            switch (addInfo) {
                case FP_VALUE_FALSE:
                    return false;
                case FP_VALUE_TRUE:
                    return true;
            }
        }
        throw new IllegalStateException("Illegal boolean value");
    }

    public static HalfPrecisionFloat readHalfFloatValue(Input input, long offset) {
        int value = Bytes.readUInt16(input, offset);
        return HalfPrecisionFloat.valueOf(HalfPrecision.toFloat(value));
    }

    public static float readSinglePrecisionFloat(Input input, long offset) {
        return Float.intBitsToFloat((int) Bytes.readUInt32(input, offset));
    }

    public static double readDoublePrecisionFloat(Input input, long offset) {
        return Double.longBitsToDouble(Bytes.readUInt64Long(input, offset));
    }

    public static BigDecimal readDecimalFraction(Input input, long offset) {
        // Skip semantic tag header
        offset += ByteSizes.headByteSize(input, offset);
        // Verify sequence header
        short sequenceHead = readUInt8(input, offset);
        if (sequenceHead != DECIMAL_FRACTION_TWO_ELEMENT_SEQUENCE_HEAD) {
            throw new IllegalArgumentException("Cannot read fraction, wrong data element");
        }
        // If ok skip sequence head
        offset++;
        // Read scale (always int)
        int scale = readInt(input, offset).intValue();
        // Read unscaled part, normally int, can be BigInteger
        Number unscaledValue = readInt(input, offset);

        BigInteger unscaled;
        if (unscaledValue instanceof BigInteger) {
            unscaled = (BigInteger) unscaledValue;
        } else {
            unscaled = BigInteger.valueOf(unscaledValue.longValue());
        }

        // Rebuild the BigDecimal
        return new BigDecimal(unscaled, scale);
    }

    public static long findByDictionaryKey(Predicate<Value> predicate, long offset, QueryContext queryContext) {
        // Search for key element
        long position = findByPredicate(predicate, offset, queryContext);
        if (position == OFFSET_CODE_NULL) {
            return OFFSET_CODE_NULL;
        }
        return skip(queryContext.input(), position);
    }

    public static Value readValue(long offset, QueryContext queryContext) {
        short head = Bytes.readUInt8(queryContext.input(), offset);
        MajorType majorType = MajorType.findMajorType(head);
        ValueType valueType = ValueTypes.valueType(queryContext.input(), offset);
        if (ValueTypes.Null == valueType) {
            return Value.NULL_VALUE;
        }
        return new StreamValue(majorType, valueType, offset, queryContext);
    }

    public static int additionalInfo(Input input, long offset) {
        short head = Bytes.readUInt8(input, offset);
        return additionalInfo(head);
    }

    public static int additionalInfo(short head) {
        return head & ADDITIONAL_INFORMATION_MASK;
    }

    public static byte[] readRaw(Input input, MajorType majorType, long offset) {
        long length = length(input, majorType, offset);
        if (length == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        // Cannot be larger than Integer.MAX_VALUE as this is checked in Decoder
        byte[] data = new byte[(int) length];
        input.read(data, offset, length);
        return data;
    }

    public static byte[] extractStringBytes(Input input, long offset) {
        int headByteSize = ByteSizes.headByteSize(input, offset);
        // Cannot be larger than Integer.MAX_VALUE as this is checked in Decoder
        int dataSize = (int) ByteSizes.stringDataSize(input, offset);
        if (dataSize == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        byte[] bytes = new byte[dataSize];
        input.read(bytes, offset + headByteSize, dataSize);
        return bytes;
    }

    private static long findByPredicate(Predicate<Value> predicate, long offset, QueryContext queryContext) {
        RelocatableStreamValue streamValue = new RelocatableStreamValue();
        Input input = queryContext.input();
        do {
            short head = Bytes.readUInt8(input, offset);
            MajorType majorType = MajorType.findMajorType(head);
            ValueType valueType = ValueTypes.valueType(input, offset);

            streamValue.relocate(queryContext, majorType, valueType, offset);
            if (predicate.test(streamValue)) {
                return offset;
            }
            long length = length(input, majorType, offset);
            offset = skip(input, offset + length);
        } while (input.offsetValid(offset) && Bytes.readUInt8(input, offset) != OPCODE_BREAK_MASK);
        return OFFSET_CODE_NULL;

    }

    private static String readString0(Input input, long offset) {
        byte[] bytes = extractStringBytes(input, offset);
        // Empty string
        if (bytes.length == 0) {
            return "";
        }
        short head = Bytes.readUInt8(input, offset);
        MajorType majorType = MajorType.findMajorType(head);
        if (MajorType.ByteString == majorType) {
            return new String(bytes, ASCII);
        }
        return new String(bytes, UTF8);
    }

    private static long[][] readElementIndexes(Input input, long offset, long elementSize) {
        int baseSize = (int) (elementSize / Integer.MAX_VALUE) + 1;
        long[][] elementIndexes = new long[baseSize][];

        long position = offset;
        long remainingElements = elementSize;
        for (int base = 0; base < baseSize; base++) {
            int remain = (int) Math.min(Integer.MAX_VALUE, remainingElements);
            elementIndexes[base] = new long[remain];
            for (int elIndex = 0; elIndex < remain; elIndex++) {
                // Store element position
                elementIndexes[base][elIndex] = position;

                // Skip elements content to next element
                short head = Bytes.readUInt8(input, position);
                MajorType majorType = MajorType.findMajorType(head);
                position += length(input, majorType, position);
            }
        }
        return elementIndexes;
    }

}
