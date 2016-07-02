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
package com.noctarius.borabora;

import com.noctarius.borabora.spi.Dictionary;
import com.noctarius.borabora.spi.QueryContext;
import com.noctarius.borabora.spi.Sequence;

import java.math.BigInteger;
import java.util.function.Predicate;

import static com.noctarius.borabora.Bytes.readUInt16;
import static com.noctarius.borabora.Bytes.readUInt32;
import static com.noctarius.borabora.Bytes.readUInt64BigInt;
import static com.noctarius.borabora.Bytes.readUInt64Long;
import static com.noctarius.borabora.Bytes.readUInt8;
import static com.noctarius.borabora.Constants.ADDITIONAL_INFORMATION_MASK;
import static com.noctarius.borabora.Constants.ASCII;
import static com.noctarius.borabora.Constants.EMPTY_BYTE_ARRAY;
import static com.noctarius.borabora.Constants.FP_VALUE_DOUBLE_PRECISION;
import static com.noctarius.borabora.Constants.FP_VALUE_FALSE;
import static com.noctarius.borabora.Constants.FP_VALUE_HALF_PRECISION;
import static com.noctarius.borabora.Constants.FP_VALUE_NULL;
import static com.noctarius.borabora.Constants.FP_VALUE_SINGLE_PRECISION;
import static com.noctarius.borabora.Constants.FP_VALUE_TRUE;
import static com.noctarius.borabora.Constants.OPCODE_BREAK_MASK;
import static com.noctarius.borabora.Constants.UTF8;

enum Decoder {
    ;

    static Number readInt(Input input, long offset) {
        short head = readUInt8(input, offset);
        long mask = -((head & 0xff) >>> 5);
        int byteSize = ByteSizes.intByteSize(input, offset);
        Number number;
        switch (byteSize) {
            case 2:
                number = mask ^ readUInt8(input, offset + 1);
                break;
            case 3:
                number = mask ^ readUInt16(input, offset + 1);
                break;
            case 5:
                number = mask ^ readUInt32(input, offset + 1);
                break;
            case 9:
                long v = readUInt64Long(input, offset + 1);
                if (v < 0) {
                    number = BigInteger.valueOf(mask).xor(readUInt64BigInt(input, offset + 1));
                } else {
                    number = mask ^ v;
                }
                break;
            default:
                number = mask ^ (head & ADDITIONAL_INFORMATION_MASK);
        }
        return number;
    }

    static Number readUint(Input input, long offset) {
        short head = readUInt8(input, offset);
        int byteSize = ByteSizes.intByteSize(input, offset);
        Number number;
        switch (byteSize) {
            case 2:
                number = readUInt8(input, offset + 1);
                break;
            case 3:
                number = readUInt16(input, offset + 1);
                break;
            case 5:
                number = readUInt32(input, offset + 1);
                break;
            case 9:
                number = readUInt64BigInt(input, offset + 1);
                break;
            default:
                number = head & ADDITIONAL_INFORMATION_MASK;
        }
        return number;
    }

    static Number readFloat(Input input, long offset) {
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

    static Number readNumber(Input input, ValueType valueType, long offset) {
        if (valueType.matches(ValueTypes.Float)) {
            return readFloat(input, offset);
        }
        return readInt(input, offset);
    }

    static String readString(Input input, long offset) {
        int addInfo = additionalInfo(input, offset);
        if (addInfo == 31) {
            // Concatenated string!
            long position = offset + 1;
            StringBuilder sb = new StringBuilder();
            while (true) {
                short h = readUInt8(input, position);
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

    static Sequence readSequence(long offset, QueryContext queryContext) {
        Input input = queryContext.input();
        long headByteSize = ByteSizes.headByteSize(input, offset);
        long size = ElementCounts.sequenceElementCount(input, offset);
        long[][] elementIndexes = readElementIndexes(input, offset + headByteSize, size);
        return new SequenceImpl(size, elementIndexes, queryContext);
    }

    static Dictionary readDictionary(long offset, QueryContext queryContext) {
        Input input = queryContext.input();
        long headByteSize = ByteSizes.headByteSize(input, offset);
        long size = ElementCounts.dictionaryElementCount(input, offset);
        long[][] elementIndexes = readElementIndexes(input, offset + headByteSize, size * 2);
        return new DictionaryImpl(size, elementIndexes, queryContext);
    }

    static long length(Input input, MajorType majorType, long offset) {
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

    static long skip(Input input, long offset) {
        short head = readUInt8(input, offset);
        MajorType majorType = MajorType.findMajorType(head);
        return skip(input, majorType, offset);
    }

    static long skip(Input input, MajorType majorType, long offset) {
        long size = length(input, majorType, offset);
        return offset + size;
    }

    static boolean isNull(short head) {
        MajorType majorType = MajorType.findMajorType(head);
        if (MajorType.FloatingPointOrSimple != majorType) {
            return false;
        }
        int addInfo = head & ADDITIONAL_INFORMATION_MASK;
        return addInfo == FP_VALUE_NULL;
    }

    static boolean getBooleanValue(Input input, long offset) {
        short head = readUInt8(input, offset);
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

    static float readHalfFloatValue(Input input, long offset) {
        int value = readUInt16(input, offset);
        return HalfPrecision.toFloat(value);
    }

    static float readSinglePrecisionFloat(Input input, long offset) {
        return Float.intBitsToFloat((int) readUInt32(input, offset));
    }

    static double readDoublePrecisionFloat(Input input, long offset) {
        return Double.longBitsToDouble(readUInt64Long(input, offset));
    }

    static long findByDictionaryKey(Predicate<Value> predicate, long offset, QueryContext queryContext) {
        // Search for key element
        long position = findByPredicate(predicate, offset, queryContext);
        if (position == -1) {
            return -1;
        }
        return skip(queryContext.input(), position);
    }

    static StreamValue readValue(long offset, QueryContext queryContext) {
        short head = readUInt8(queryContext.input(), offset);
        MajorType mt = MajorType.findMajorType(head);
        ValueType vt = ValueTypes.valueType(queryContext.input(), offset);
        return new StreamValue(mt, vt, offset, queryContext);
    }

    static int additionalInfo(Input input, long offset) {
        short head = readUInt8(input, offset);
        return additionalInfo(head);
    }

    static int additionalInfo(short head) {
        return head & ADDITIONAL_INFORMATION_MASK;
    }

    static byte[] readRaw(Input input, MajorType majorType, long offset) {
        long length = length(input, majorType, offset);
        if (length == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        // Cannot be larger than Integer.MAX_VALUE as this is checked in Decoder
        byte[] data = new byte[(int) length];
        input.read(data, offset, length);
        return data;
    }

    private static long findByPredicate(Predicate<Value> predicate, long offset, QueryContext queryContext) {
        RelocatableStreamValue streamValue = new RelocatableStreamValue();
        Input input = queryContext.input();
        do {
            short head = readUInt8(input, offset);
            MajorType majorType = MajorType.findMajorType(head);
            ValueType valueType = ValueTypes.valueType(input, offset);

            streamValue.relocate(queryContext, majorType, valueType, offset);
            if (predicate.test(streamValue)) {
                return offset;
            }
            long length = length(input, majorType, offset);
            offset = skip(input, offset + length);
        } while (input.offsetValid(offset) && readUInt8(input, offset) != OPCODE_BREAK_MASK);
        return -1;

    }

    private static String readString0(Input input, long offset) {
        short head = readUInt8(input, offset);
        MajorType majorType = MajorType.findMajorType(head);
        int headByteSize = ByteSizes.headByteSize(input, offset);
        // Cannot be larger than Integer.MAX_VALUE as this is checked in Decoder
        int dataSize = (int) ByteSizes.stringDataSize(input, offset);
        // Empty string
        if (dataSize == 0) {
            return "";
        }
        byte[] data = new byte[dataSize];
        input.read(data, offset + headByteSize, dataSize);
        if (MajorType.ByteString == majorType) {
            return new String(data, ASCII);
        }
        return new String(data, UTF8);
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
                short head = readUInt8(input, position);
                MajorType majorType = MajorType.findMajorType(head);
                position += length(input, majorType, position);
            }
        }
        return elementIndexes;
    }

}
