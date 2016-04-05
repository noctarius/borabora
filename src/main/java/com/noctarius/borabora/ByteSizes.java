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

final class ByteSizes {

    static int uintByteSize(Decoder stream, long offset) {
        return intByteSize(stream, stream.transientUint8(offset));
    }

    static int intByteSize(Decoder stream, long offset) {
        return intByteSize(stream, stream.transientUint8(offset));
    }

    static int intByteSize(Decoder stream, short head) {
        return headByteSize(stream, head);
    }

    static long byteStringByteSize(Decoder stream, long offset) {
        return stringByteSize(stream, offset);
    }

    static long textStringByteSize(Decoder stream, long offset) {
        return stringByteSize(stream, offset);
    }

    static long sequenceByteSize(Decoder stream, long offset) {
        long elementCount = ElementCounts.sequenceElementCount(stream, offset);
        return containerByteSize(stream, offset, elementCount);
    }

    static long dictionaryByteSize(Decoder stream, long offset) {
        long elementCount = ElementCounts.sequenceElementCount(stream, offset);
        return containerByteSize(stream, offset, elementCount / 2);
    }

    static long semanticTagByteSize(Decoder stream, long offset) {
        short head = stream.transientUint8(offset);
        long byteSize = ByteSizes.intByteSize(stream, head);
        short itemHead = stream.transientUint8(offset + byteSize);
        MajorType majorType = MajorType.findMajorType(itemHead);
        return byteSize + stream.length(majorType, offset + byteSize);
    }

    static long floatingPointOrSimpleByteSize(Decoder stream, long offset) {
        int addInfo = stream.additionInfo(offset);
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
                return untilBreakCode(stream, offset) + 1;
            default:
                return 1;
        }
    }

    static long stringByteSize(Decoder stream, long offset) {
        int addInfo = stream.additionInfo(offset);
        switch (addInfo) {
            case ADD_INFO_ONE_BYTE:
                return stringDataSize(stream, offset) + 2;
            case ADD_INFO_TWO_BYTES:
                return stringDataSize(stream, offset) + 3;
            case ADD_INFO_FOUR_BYTES:
                return stringDataSize(stream, offset) + 5;
            case ADD_INFO_EIGHT_BYTES:
                throw new IllegalStateException("String sizes of 64bit are not yet supported");
            case ADD_INFO_RESERVED_1: // Unassigned
            case ADD_INFO_RESERVED_2: // Unassigned
            case ADD_INFO_RESERVED_3: // Unassigned
                throw new IllegalStateException("28|29|30 are unassigned");
            case ADD_INFO_INDEFINITE:
                return untilBreakCode(stream, offset) + 1;
            default:
                return addInfo + 1;
        }
    }

    static long stringDataSize(Decoder stream, long offset) {
        int addInfo = stream.additionInfo(offset);
        switch (addInfo) {
            case ADD_INFO_ONE_BYTE:
                return stream.readUint8(offset + 1);
            case ADD_INFO_TWO_BYTES:
                return stream.readUint16(offset + 1);
            case ADD_INFO_FOUR_BYTES:
                return stream.readUint32(offset + 1);
            case ADD_INFO_EIGHT_BYTES:
                throw new IllegalStateException("String sizes of 64bit are not yet supported");
            case ADD_INFO_RESERVED_1: // Unassigned
            case ADD_INFO_RESERVED_2: // Unassigned
            case ADD_INFO_RESERVED_3: // Unassigned
                throw new IllegalStateException("28|29|30 are unassigned");
            case ADD_INFO_INDEFINITE:
                return untilBreakCode(stream, offset);
            default:
                return addInfo;
        }
    }

    static int headByteSize(Decoder stream, long offset) {
        short head = stream.transientUint8(offset);
        return headByteSize(stream, head);
    }

    static int headByteSize(Decoder stream, short head) {
        int addInfo = stream.additionInfo(head);
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

    private static long untilBreakCode(Decoder stream, long offset) {
        long start = offset;
        short uint;
        do {
            uint = stream.transientUint8(offset++);
        } while ((uint & OPCODE_BREAK_MASK) != OPCODE_BREAK_MASK);
        return offset - start;
    }

    private static long containerByteSize(Decoder stream, long offset, long elementCount) {
        long headByteSize = headByteSize(stream, offset);
        int addInfo = stream.additionInfo(offset);

        long position = offset + headByteSize;
        for (long i = 0; i < elementCount; i++) {
            short head = stream.transientUint8(position);
            MajorType majorType = MajorType.findMajorType(head);
            position += majorType.byteSize(stream, position);
        }
        // Indefinite length? -> +1
        return position - offset + (addInfo == 31 ? 1 : 0);
    }

}
