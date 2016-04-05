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

import static com.noctarius.borabora.Constants.ADDITIONAL_INFORMATION_MASK;
import static com.noctarius.borabora.Constants.OPCODE_BREAK_MASK;

final class ElementCounts {

    static final ObjectLongToLongFunction<Input> SINGLE_ELEMENT_COUNT = (s, i) -> 1;

    static final ObjectLongToLongFunction<Input> SEQUENCE_ELEMENT_COUNT = ElementCounts::sequenceElementCount;

    static final ObjectLongToLongFunction<Input> DICTIONARY_ELEMENT_COUNT = ElementCounts::dictionaryElementCount;

    static long sequenceElementCount(Input input, long offset) {
        return elementCount(input, offset, "Sequence", false);
    }

    static long dictionaryElementCount(Input input, long offset) {
        return elementCount(input, offset, "Dictionary", true);
    }

    private static long elementCount(Input input, long offset, String elementType, boolean keyValue) {
        short head = Decoder.transientUint8(input, offset);
        int addInfo = head & ADDITIONAL_INFORMATION_MASK;
        switch (addInfo) {
            case 24:
                return Bytes.readUInt8(input, offset + 1);
            case 25:
                return Bytes.readUInt16(input, offset + 1);
            case 26:
                return Bytes.readUInt32(input, offset + 1);
            case 27:
                throw new IllegalStateException(elementType + " of 64bit sizes are not yet supported");
            case 31:
                return untilBreakCode(input, offset, keyValue);
            default:
                return addInfo;
        }

    }

    private static long untilBreakCode(Input input, long offset, boolean keyValue) {
        long headByteSize = ByteSizes.headByteSize(input, offset);
        long position = offset + headByteSize;

        long elementCount = 0;
        short head;
        while (true) {
            head = Decoder.transientUint8(input, position);
            if ((head & OPCODE_BREAK_MASK) == OPCODE_BREAK_MASK) {
                break;
            }
            MajorType majorType = MajorType.findMajorType(head);
            position += Decoder.length(input, majorType, position);
            elementCount++;
        }
        if (keyValue) {
            return elementCount / 2;
        }
        return elementCount;
    }

}
