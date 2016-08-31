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

import com.noctarius.borabora.Input;
import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.spi.Constants;

import java.math.BigInteger;

public final class ElementCounts {

    private ElementCounts() {
    }

    public static long elementCountByMajorType(MajorType majorType, Input input, long offset) {
        switch (majorType) {
            case Sequence:
                return sequenceElementCount(input, offset);

            case Dictionary:
                return dictionaryElementCount(input, offset);

            default:
                return 1;
        }
    }

    public static long sequenceElementCount(Input input, long offset) {
        return elementCount(input, offset, false);
    }

    public static long dictionaryElementCount(Input input, long offset) {
        return elementCount(input, offset, true);
    }

    private static long elementCount(Input input, long offset, boolean keyValue) {
        int addInfo = Decoder.additionalInfo(input, offset);
        switch (addInfo) {
            case Constants.ADD_INFO_ONE_BYTE:
                return Bytes.readUInt8(input, offset + 1);
            case Constants.ADD_INFO_TWO_BYTES:
                return Bytes.readUInt16(input, offset + 1);
            case Constants.ADD_INFO_FOUR_BYTES:
                return Bytes.readUInt32(input, offset + 1);
            case Constants.ADD_INFO_EIGHT_BYTES:
                Number value = Bytes.readUInt64(input, offset + 1);
                if (value instanceof BigInteger) {
                    throw new IllegalStateException("Object sizes larger Long.MAX_VALUE are not supported");
                }
                return value.longValue();
            case Constants.ADD_INFO_INDEFINITE:
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
            head = Bytes.readUInt8(input, position);
            if ((head & Constants.OPCODE_BREAK_MASK) == Constants.OPCODE_BREAK_MASK) {
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
