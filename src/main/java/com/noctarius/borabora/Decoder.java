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

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Collection;

import static com.noctarius.borabora.Constants.ADDITIONAL_INFORMATION_MASK;
import static com.noctarius.borabora.Constants.FP_VALUE_FALSE;
import static com.noctarius.borabora.Constants.FP_VALUE_NULL;
import static com.noctarius.borabora.Constants.FP_VALUE_TRUE;
import static com.noctarius.borabora.Constants.OPCODE_BREAK_MASK;

final class Decoder {

    private static final Charset ASCII = Charset.forName("ASCII");
    private static final Charset UTF8 = Charset.forName("UTF8");

    private final Input input;

    Decoder(Input input) {
        this.input = input;
    }

    short transientUint8(long index) {
        return readUint8(index);
    }

    short readUint8(long index) {
        byte v = readInt8(index);
        return (short) (v & 0xFF);
    }

    byte readInt8(long index) {
        return input.read(index);
    }

    int readInt16(long index) {
        int highByte = readUint8(index);
        int lowByte = readUint8(index + 1);
        return (highByte << 8) | lowByte;
    }

    int readUint16(long index) {
        return readInt16(index) & 0xffff;
    }

    long readInt32(long index) {
        long b1 = readUint8(index);
        long b2 = readUint8(index + 1);
        long b3 = readUint8(index + 2);
        long b4 = readUint8(index + 3);
        return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
    }

    long readUint32(long index) {
        return readInt32(index) & 0xffffffff;
    }

    long readInt64(long index) {
        byte b1 = readInt8(index);
        byte b2 = readInt8(index + 1);
        byte b3 = readInt8(index + 2);
        byte b4 = readInt8(index + 3);
        byte b5 = readInt8(index + 4);
        byte b6 = readInt8(index + 5);
        byte b7 = readInt8(index + 6);
        byte b8 = readInt8(index + 7);
        return (b1 << 56) | (b2 << 48) | (b3 << 40) | (b4 << 32) | (b5 << 24) | (b6 << 16) | (b7 << 8) | b8;
    }

    BigInteger readUint64(long index) {
        byte b1 = readInt8(index);
        byte b2 = readInt8(index + 1);
        byte b3 = readInt8(index + 2);
        byte b4 = readInt8(index + 3);
        byte b5 = readInt8(index + 4);
        byte b6 = readInt8(index + 5);
        byte b7 = readInt8(index + 6);
        byte b8 = readInt8(index + 7);
        return new BigInteger(1, new byte[]{b1, b2, b3, b4, b5, b6, b7, b8});
    }

    Number readInt(long index) {
        short head = transientUint8(index);
        if (isNull(head)) {
            return null;
        }
        long mask = -((head & 0xff) >>> 5);
        int byteSize = ByteSizes.intByteSize(head);
        Number number;
        switch (byteSize) {
            case 2:
                number = mask ^ readUint8(index + 1);
                break;
            case 3:
                number = mask ^ readUint16(index + 1);
                break;
            case 5:
                number = mask ^ readUint32(index + 1);
                break;
            case 9:
                number = BigInteger.valueOf(mask).xor(readUint64(index + 1));
                break;
            default:
                number = mask ^ (head & ADDITIONAL_INFORMATION_MASK);
        }
        return number;
    }

    Number readUint(long index) {
        short head = transientUint8(index);
        if (isNull(head)) {
            return null;
        }
        int byteSize = ByteSizes.intByteSize(head);
        Number number;
        switch (byteSize) {
            case 2:
                number = readUint8(index + 1);
                break;
            case 3:
                number = readUint16(index + 1);
                break;
            case 5:
                number = readUint32(index + 1);
                break;
            case 9:
                number = readUint64(index + 1);
                break;
            default:
                number = head & ADDITIONAL_INFORMATION_MASK;
        }
        return number;
    }

    String readString(long index) {
        short head = transientUint8(index);
        if (isNull(head)) {
            return null;
        }
        int addInfo = head & ADDITIONAL_INFORMATION_MASK;
        if (addInfo == 31) {
            // Concatenated string!
            long position = index + 1;
            StringBuilder sb = new StringBuilder();
            while (true) {
                short h = transientUint8(position);
                if ((h & OPCODE_BREAK_MASK) == OPCODE_BREAK_MASK) {
                    break;
                }
                sb.append(readString(position));
                position += ByteSizes.textStringByteSize(this, position);
            }
            return sb.toString();
        }

        return readString0(index);
    }

    Sequence readSequence(long index, Collection<SemanticTagProcessor> processors) {
        short head = transientUint8(index);
        if (isNull(head)) {
            return null;
        }
        long headByteSize = ByteSizes.headByteSize(this, index);
        long size = ElementCounts.sequenceElementCount(this, index);
        long[][] elementIndexes = readElementIndexes(index + headByteSize, size);
        return new SequenceImpl(this, index + headByteSize, size, elementIndexes, processors);
    }

    long length(MajorType majorType, long index) {
        short head = transientUint8(index);
        switch (majorType) {
            case UnsignedInteger:
            case NegativeInteger:
                return ByteSizes.intByteSize(head);
            case ByteString:
            case TextString:
                return ByteSizes.stringByteSize(this, index);
            case Sequence:
                return ByteSizes.sequenceByteSize(this, index);
            case Dictionary:
                return ByteSizes.dictionaryByteSize(this, index);
            case SemanticTag:
                return ByteSizes.semanticTagByteSize(this, index);
            case FloatingPointOrSimple:
                return ByteSizes.floatingPointOrSimpleByteSize(this, index);
        }
        throw new IllegalStateException("Illegal MajorType requested");
    }

    long skip(MajorType majorType, long index) {
        long size = length(majorType, index);
        return index + size;
    }

    boolean isNull(short head) {
        MajorType majorType = MajorType.findMajorType(head);
        if (MajorType.FloatingPointOrSimple != majorType) {
            return false;
        }
        int addInfo = head & ADDITIONAL_INFORMATION_MASK;
        return addInfo == FP_VALUE_NULL;
    }

    boolean getBooleanValue(long index) {
        short head = transientUint8(index);
        // Null is legal for all types
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

    private String readString0(long index) {
        short head = transientUint8(index);
        MajorType majorType = MajorType.findMajorType(head);
        long byteSize = ByteSizes.textStringByteSize(this, index);
        if (byteSize > Integer.MAX_VALUE) {
            throw new IllegalStateException("Strings of size > Integer.MAX_VALUE are not implemented");
        }
        int dataSize = (int) ByteSizes.stringDataSize(this, index);
        byte[] data = new byte[dataSize];
        for (int i = 0; i < dataSize; i++) {
            data[i] = readInt8(index + 1 + i);
        }
        if (MajorType.ByteString == majorType) {
            return new String(data, ASCII);
        }
        return new String(data, UTF8);
    }

    private long[][] readElementIndexes(long index, long elementSize) {
        int baseSize = (int) (elementSize / Integer.MAX_VALUE) + 1;
        long[][] elementIndexes = new long[baseSize][];

        long position = index;
        long remainingElements = elementSize;
        for (int base = 0; base < baseSize; base++) {
            int remain = (int) Math.min(Integer.MAX_VALUE, remainingElements);
            elementIndexes[base] = new long[remain];
            for (int elIndex = 0; elIndex < remain; elIndex++) {
                // Store element position
                elementIndexes[base][elIndex] = position;

                // Skip elements content to next element
                short head = transientUint8(position);
                MajorType majorType = MajorType.findMajorType(head);
                position += length(majorType, position);
            }
        }
        return elementIndexes;
    }

}
