/*
 * Copyright (c) 2016-2018, Christoph Engelbert (aka noctarius) and
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
package com.noctarius.borabora.spi.io;

import com.noctarius.borabora.Input;
import com.noctarius.borabora.MajorType;

public final class ByteSizes {

    private ByteSizes() {
    }

    public static long byteSizeByMajorType(MajorType majorType, Input input, long offset) {
        switch (majorType) {
            case UnsignedInteger:
            case NegativeInteger:
                return intByteSize(input, offset);

            case ByteString:
            case TextString:
                return stringByteSize(input, offset);

            case Sequence:
                return sequenceByteSize(input, offset);

            case Dictionary:
                return dictionaryByteSize(input, offset);

            case SemanticTag:
                return semanticTagByteSize(input, offset);

            case FloatingPointOrSimple:
                return floatOrSimpleByteSize(input, offset);

            default:
                return 0;
        }
    }

    public static int intByteSize(Input input, long offset) {
        return headByteSize(input, offset);
    }

    public static long sequenceByteSize(Input input, long offset) {
        int addInfo = Decoder.additionalInfo(input, offset);
        if (addInfo == Constants.ADD_INFO_INDEFINITE) {
            return indefiniteContainerByteSize(input, offset);
        }

        long elementCount = ElementCounts.sequenceElementCount(input, offset);
        return containerByteSize(input, offset, elementCount);
    }

    public static long dictionaryByteSize(Input input, long offset) {
        int addInfo = Decoder.additionalInfo(input, offset);
        if (addInfo == Constants.ADD_INFO_INDEFINITE) {
            return indefiniteContainerByteSize(input, offset);
        }

        long elementCount = ElementCounts.dictionaryElementCount(input, offset);
        return containerByteSize(input, offset, elementCount * 2);
    }

    public static long semanticTagByteSize(Input input, long offset) {
        long byteSize = ByteSizes.intByteSize(input, offset);
        short itemHead = Bytes.readUInt8(input, offset + byteSize);
        MajorType majorType = MajorType.findMajorType(itemHead);
        return byteSize + Decoder.length(input, majorType, offset + byteSize);
    }

    public static long floatOrSimpleByteSize(Input input, long offset) {
        int addInfo = Decoder.additionalInfo(input, offset);
        switch (addInfo) {
            case Constants.ADD_INFO_ONE_BYTE:
                return 2;
            case Constants.ADD_INFO_TWO_BYTES:
                return 3;
            case Constants.ADD_INFO_FOUR_BYTES:
                return 5;
            case Constants.ADD_INFO_EIGHT_BYTES:
                return 9;
            case Constants.ADD_INFO_RESERVED_1: // Unassigned
            case Constants.ADD_INFO_RESERVED_2: // Unassigned
            case Constants.ADD_INFO_RESERVED_3: // Unassigned
                throw throwUnassigned();
            case Constants.ADD_INFO_INDEFINITE:
                return untilBreakCode(input, offset);
            default:
                return 1;
        }
    }

    public static long stringByteSize(Input input, long offset) {
        int addInfo = Decoder.additionalInfo(input, offset);
        switch (addInfo) {
            case Constants.ADD_INFO_ONE_BYTE:
                return stringDataSize(input, offset) + 2;
            case Constants.ADD_INFO_TWO_BYTES:
                return stringDataSize(input, offset) + 3;
            case Constants.ADD_INFO_FOUR_BYTES:
                return stringDataSize(input, offset) + 5;
            case Constants.ADD_INFO_EIGHT_BYTES:
                throw throwString64bitUnsupported();
            case Constants.ADD_INFO_RESERVED_1: // Unassigned
            case Constants.ADD_INFO_RESERVED_2: // Unassigned
            case Constants.ADD_INFO_RESERVED_3: // Unassigned
                throw throwUnassigned();
            case Constants.ADD_INFO_INDEFINITE:
                return untilBreakCode(input, offset);
            default:
                return addInfo + 1;
        }
    }

    public static long stringDataSize(Input input, long offset) {
        int addInfo = Decoder.additionalInfo(input, offset);
        switch (addInfo) {
            case Constants.ADD_INFO_ONE_BYTE:
                return Bytes.readUInt8(input, offset + 1);
            case Constants.ADD_INFO_TWO_BYTES:
                return Bytes.readUInt16(input, offset + 1);
            case Constants.ADD_INFO_FOUR_BYTES:
                return Bytes.readUInt32(input, offset + 1);
            case Constants.ADD_INFO_EIGHT_BYTES:
                throw throwString64bitUnsupported();
            case Constants.ADD_INFO_RESERVED_1: // Unassigned
            case Constants.ADD_INFO_RESERVED_2: // Unassigned
            case Constants.ADD_INFO_RESERVED_3: // Unassigned
                throw throwUnassigned();
            default:
                return addInfo;
        }
    }

    public static int headByteSize(Input input, long offset) {
        int addInfo = Decoder.additionalInfo(input, offset);
        switch (addInfo) {
            case Constants.ADD_INFO_ONE_BYTE:
                return 2;
            case Constants.ADD_INFO_TWO_BYTES:
                return 3;
            case Constants.ADD_INFO_FOUR_BYTES:
                return 5;
            case Constants.ADD_INFO_EIGHT_BYTES:
                return 9;
            case Constants.ADD_INFO_RESERVED_1: // Unassigned
            case Constants.ADD_INFO_RESERVED_2: // Unassigned
            case Constants.ADD_INFO_RESERVED_3: // Unassigned
                throw throwUnassigned();
            default:
                return 1;
        }
    }

    private static RuntimeException throwUnassigned() {
        return new IllegalStateException("28|29|30 are unassigned");
    }

    private static RuntimeException throwString64bitUnsupported() {
        return new IllegalStateException("String sizes of 64bit are not yet supported");
    }

    private static long untilBreakCode(Input input, long offset) {
        long start = offset;
        short uint;
        do {
            uint = Bytes.readUInt8(input, offset++);
        } while ((uint & Constants.OPCODE_BREAK_MASK) != Constants.OPCODE_BREAK_MASK);
        return offset - start;
    }

    private static long containerByteSize(Input input, long offset, long elementCount) {
        long headByteSize = headByteSize(input, offset);
        int addInfo = Decoder.additionalInfo(input, offset);

        long position = offset + headByteSize;
        for (long i = 0; i < elementCount; i++) {
            short head = Bytes.readUInt8(input, position);
            MajorType majorType = MajorType.findMajorType(head);
            position += byteSizeByMajorType(majorType, input, position);
        }
        // Indefinite length? -> +1
        return position - offset + (addInfo == 31 ? 1 : 0);
    }

    private static long indefiniteContainerByteSize(Input input, long offset) {
        long headByteSize = ByteSizes.headByteSize(input, offset);
        long position = offset + headByteSize;

        short head;
        while (true) {
            head = Bytes.readUInt8(input, position);
            if ((head & Constants.OPCODE_BREAK_MASK) == Constants.OPCODE_BREAK_MASK) {
                break;
            }
            MajorType majorType = MajorType.findMajorType(head);
            position += Decoder.length(input, majorType, position);
        }

        // Indefinite length? -> +1
        return position - offset + 1;
    }

}
