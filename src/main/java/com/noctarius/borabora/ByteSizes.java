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

final class ByteSizes implements Constants {

    static byte uintByteSize(Decoder stream, long index) {
        return intByteSize(stream.transientUint8(index));
    }

    static byte intByteSize(Decoder stream, long index) {
        return intByteSize(stream.transientUint8(index));
    }

    static byte intByteSize(short head) {
        int addInfo = head & ADDITIONAL_INFORMATION_MASK;
        switch (addInfo) {
            case 24:
                return 2;
            case 25:
                return 3;
            case 26:
                return 5;
            case 27:
                return 9;
            default:
                return 1;
        }
    }

    static long byteStringByteSize(Decoder stream, long index) {
        return stringByteSize(stream, index);
    }


    static long textStringByteSize(Decoder stream, long index) {
        return stringByteSize(stream, index);
    }

    static long sequenceByteSize(Decoder stream, long index) {
        // TODO
        return -1;
    }

    static long dictionaryByteSize(Decoder stream, long index) {
        // TODO
        return -1;
    }

    static long semanticTagByteSize(Decoder stream, long index) {
        short head = stream.transientUint8(index);
        int byteSize = ByteSizes.intByteSize(head);
        short itemHead = stream.transientUint8(index + byteSize);
        MajorType majorType = MajorType.findMajorType(itemHead);
        return byteSize + stream.length(majorType, index + byteSize);
    }

    static long floatingPointOrSimpleByteSize(Decoder stream, long index) {
        short head = stream.transientUint8(index);
        int addInfo = head & ADDITIONAL_INFORMATION_MASK;
        switch (addInfo) {
            case 24:
                return 2;
            case 25:
                return 3;
            case 26:
                return 5;
            case 27:
                return 9;
            case 28: // Unassigned
            case 29: // Unassigned
            case 30: // Unassigned
                throw new IllegalStateException("28|29|30 are unassigned");
            case 31:
                return untilBreakCode(stream, index) + 1;
            default:
                return 1;
        }
    }

    static long stringByteSize(Decoder stream, long index) {
        short head = stream.transientUint8(index);
        int addInfo = head & ADDITIONAL_INFORMATION_MASK;
        long dataSize = addInfo == 0 ? 0 : stringDataSize(stream, index + 1);
        switch (addInfo) {
            case 24:
                return dataSize + 1;
            case 25:
                return dataSize + 2;
            case 26:
                return dataSize + 4;
            case 27:
                throw new IllegalStateException("String sizes of 64bit are not yet supported");
            case 31:
                return untilBreakCode(stream, index) + 1;
            default:
                return addInfo + 1;
        }
    }

    static long stringDataSize(Decoder stream, long index) {
        short head = stream.transientUint8(index);
        int addInfo = head & ADDITIONAL_INFORMATION_MASK;
        switch (addInfo) {
            case 24:
                return stream.readUint8(index);
            case 25:
                return stream.readUint16(index);
            case 26:
                return stream.readUint32(index);
            case 27:
                throw new IllegalStateException("String sizes of 64bit are not yet supported");
            case 31:
                return untilBreakCode(stream, index);
            default:
                return addInfo;
        }
    }

    private static long untilBreakCode(Decoder stream, long index) {
        long start = index;
        short uint;
        do {
            uint = stream.transientUint8(index++);
        } while ((uint & OPCODE_BREAK_MASK) != OPCODE_BREAK_MASK);
        return index - start;
    }

}
