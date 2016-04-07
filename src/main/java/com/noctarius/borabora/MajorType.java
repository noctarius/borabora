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

public enum MajorType {
    UnsignedInteger(0, 0b000, false, ByteSizes::intByteSize, ElementCounts.SINGLE_ELEMENT_COUNT),
    NegativeInteger(1, 0b001, false, ByteSizes::intByteSize, ElementCounts.SINGLE_ELEMENT_COUNT),
    ByteString(2, 0b010, true, ByteSizes::stringByteSize, ElementCounts.SINGLE_ELEMENT_COUNT),
    TextString(3, 0b011, true, ByteSizes::stringByteSize, ElementCounts.SINGLE_ELEMENT_COUNT),
    Sequence(4, 0b100, true, ByteSizes::sequenceByteSize, ElementCounts.SEQUENCE_ELEMENT_COUNT),
    Dictionary(5, 0b101, true, ByteSizes::dictionaryByteSize, ElementCounts.DICTIONARY_ELEMENT_COUNT),
    SemanticTag(6, 0b110, false, ByteSizes::semanticTagByteSize, ElementCounts.SINGLE_ELEMENT_COUNT),
    FloatingPointOrSimple(7, 0b111, false, ByteSizes::floatingPointOrSimpleByteSize, ElementCounts.SINGLE_ELEMENT_COUNT),
    Unknown(-1, -1, false, (s, i) -> 0, (s, i) -> 0);

    private static final short HIGH_BITS_MASK = 0b1110_0000;

    private final int typeId;
    private final int mask;
    private final boolean indefinite;
    private final ObjectLongToLongFunction<Input> byteSize;
    private final ObjectLongToLongFunction<Input> elementCount;

    MajorType(int typeId, int mask, boolean indefinite, ObjectLongToLongFunction<Input> byteSize,
              ObjectLongToLongFunction<Input> elementCount) {

        this.typeId = typeId;
        this.mask = mask;
        this.indefinite = indefinite;
        this.byteSize = byteSize;
        this.elementCount = elementCount;
    }

    public int typeId() {
        return typeId;
    }

    public boolean indefinite() {
        return indefinite;
    }

    public boolean match(short head) {
        int highBits = (head & HIGH_BITS_MASK) >>> 5;
        return (highBits | mask) == mask;
    }

    long byteSize(Input input, long offset) {
        return byteSize.apply(input, offset);
    }

    long elementCount(Input input, long offset) {
        return elementCount.apply(input, offset);
    }

    public static MajorType findMajorType(short head) {
        for (MajorType mt : values()) {
            if (mt.match(head)) {
                return mt;
            }
        }
        throw new IllegalArgumentException("Unknown MajorType: " + ((head & HIGH_BITS_MASK) >>> 5));
    }

}
