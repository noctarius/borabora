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

import static com.noctarius.borabora.Constants.ADD_INFO_EIGHT_BYTES;
import static com.noctarius.borabora.Constants.ADD_INFO_FOUR_BYTES;
import static com.noctarius.borabora.Constants.ADD_INFO_INDEFINITE;
import static com.noctarius.borabora.Constants.ADD_INFO_ONE_BYTE;
import static com.noctarius.borabora.Constants.ADD_INFO_RESERVED_1;
import static com.noctarius.borabora.Constants.ADD_INFO_RESERVED_2;
import static com.noctarius.borabora.Constants.ADD_INFO_RESERVED_3;
import static com.noctarius.borabora.Constants.ADD_INFO_TWO_BYTES;
import static com.noctarius.borabora.Constants.OPCODE_BREAK_MASK;

enum ByteSizes {
    ;

    static int intByteSize(Input input, long offset) {
        return headByteSize(input, offset);
    }

    static long byteStringByteSize(Input input, long offset) {
        return stringByteSize(input, offset);
    }

    static long textStringByteSize(Input input, long offset) {
        return stringByteSize(input, offset);
    }

    static long sequenceByteSize(Input input, long offset) {
        long elementCount = ElementCounts.sequenceElementCount(input, offset);
        return containerByteSize(input, offset, elementCount);
    }

    static long dictionaryByteSize(Input input, long offset) {
        long elementCount = ElementCounts.sequenceElementCount(input, offset);
        return containerByteSize(input, offset, elementCount / 2);
    }

    static long semanticTagByteSize(Input input, long offset) {
        long byteSize = ByteSizes.intByteSize(input, offset);
        short itemHead = Decoder.transientUint8(input, offset + byteSize);
        MajorType majorType = MajorType.findMajorType(itemHead);
        return byteSize + Decoder.length(input, majorType, offset + byteSize);
    }

    static long floatingPointOrSimpleByteSize(Input input, long offset) {
        int addInfo = Decoder.additionInfo(input, offset);
        switch (addInfo) {
            case ADD_INFO_ONE_BYTE:
                return 2;
            case ADD_INFO_TWO_BYTES:
                return 3;
            case ADD_INFO_FOUR_BYTES:
                return 5;
            case ADD_INFO_EIGHT_BYTES:
                return 9;
            case ADD_INFO_RESERVED_1: // Unassigned
            case ADD_INFO_RESERVED_2: // Unassigned
            case ADD_INFO_RESERVED_3: // Unassigned
                throw new IllegalStateException("28|29|30 are unassigned");
            case ADD_INFO_INDEFINITE:
                return untilBreakCode(input, offset) + 1;
            default:
                return 1;
        }
    }

    static long stringByteSize(Input input, long offset) {
        int addInfo = Decoder.additionInfo(input, offset);
        switch (addInfo) {
            case ADD_INFO_ONE_BYTE:
                return stringDataSize(input, offset) + 2;
            case ADD_INFO_TWO_BYTES:
                return stringDataSize(input, offset) + 3;
            case ADD_INFO_FOUR_BYTES:
                return stringDataSize(input, offset) + 5;
            case ADD_INFO_EIGHT_BYTES:
                throw new IllegalStateException("String sizes of 64bit are not yet supported");
            case ADD_INFO_RESERVED_1: // Unassigned
            case ADD_INFO_RESERVED_2: // Unassigned
            case ADD_INFO_RESERVED_3: // Unassigned
                throw new IllegalStateException("28|29|30 are unassigned");
            case ADD_INFO_INDEFINITE:
                return untilBreakCode(input, offset) + 1;
            default:
                return addInfo + 1;
        }
    }

    static long stringDataSize(Input input, long offset) {
        int addInfo = Decoder.additionInfo(input, offset);
        switch (addInfo) {
            case ADD_INFO_ONE_BYTE:
                return Bytes.readUInt8(input, offset + 1);
            case ADD_INFO_TWO_BYTES:
                return Bytes.readUInt16(input, offset + 1);
            case ADD_INFO_FOUR_BYTES:
                return Bytes.readUInt32(input, offset + 1);
            case ADD_INFO_EIGHT_BYTES:
                throw new IllegalStateException("String sizes of 64bit are not yet supported");
            case ADD_INFO_RESERVED_1: // Unassigned
            case ADD_INFO_RESERVED_2: // Unassigned
            case ADD_INFO_RESERVED_3: // Unassigned
                throw new IllegalStateException("28|29|30 are unassigned");
            case ADD_INFO_INDEFINITE:
                return untilBreakCode(input, offset);
            default:
                return addInfo;
        }
    }

    static int headByteSize(Input input, long offset) {
        short head = Decoder.transientUint8(input, offset);
        int addInfo = Decoder.additionInfo(head);
        switch (addInfo) {
            case ADD_INFO_ONE_BYTE:
                return 2;
            case ADD_INFO_TWO_BYTES:
                return 3;
            case ADD_INFO_FOUR_BYTES:
                return 5;
            case ADD_INFO_EIGHT_BYTES:
                return 9;
            case ADD_INFO_RESERVED_1: // Unassigned
            case ADD_INFO_RESERVED_2: // Unassigned
            case ADD_INFO_RESERVED_3: // Unassigned
                throw new IllegalStateException("28|29|30 are unassigned");
            default:
                return 1;
        }
    }

    private static long untilBreakCode(Input input, long offset) {
        long start = offset;
        short uint;
        do {
            uint = Decoder.transientUint8(input, offset++);
        } while ((uint & OPCODE_BREAK_MASK) != OPCODE_BREAK_MASK);
        return offset - start;
    }

    private static long containerByteSize(Input input, long offset, long elementCount) {
        long headByteSize = headByteSize(input, offset);
        int addInfo = Decoder.additionInfo(input, offset);

        long position = offset + headByteSize;
        for (long i = 0; i < elementCount; i++) {
            short head = Decoder.transientUint8(input, position);
            MajorType majorType = MajorType.findMajorType(head);
            position += majorType.byteSize(input, position);
        }
        // Indefinite length? -> +1
        return position - offset + (addInfo == 31 ? 1 : 0);
    }

}
