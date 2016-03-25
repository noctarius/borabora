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

final class ElementCounts {

    static final ObjectLongToLongFunction<Decoder> SINGLE_ELEMENT = (s, i) -> 1;

    static long sequenceElementCount(Decoder stream, long index) {
        return elementCount(stream, index, "Sequence");
    }

    static long dictionaryElementCount(Decoder stream, long index) {
        return elementCount(stream, index, "Dictionary");
    }

    private static long elementCount(Decoder stream, long index, String elementType) {
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
                throw new IllegalStateException(elementType + " of 64bit sizesare not yet supported");
            case 31:
                // TODO Indefinite sizes
                throw new IllegalStateException(elementType + " of indefinite sizes are not yet supported");
                //return untilBreakCode(stream, index);
            default:
                return addInfo;
        }

    }

}
