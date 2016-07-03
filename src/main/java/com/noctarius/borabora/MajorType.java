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

import static com.noctarius.borabora.spi.Constants.MT_BYTESTRING;
import static com.noctarius.borabora.spi.Constants.MT_DICTIONARY;
import static com.noctarius.borabora.spi.Constants.MT_FLOAT_SIMPLE;
import static com.noctarius.borabora.spi.Constants.MT_NEGATIVE_INT;
import static com.noctarius.borabora.spi.Constants.MT_SEMANTIC_TAG;
import static com.noctarius.borabora.spi.Constants.MT_SEQUENCE;
import static com.noctarius.borabora.spi.Constants.MT_TEXTSTRING;
import static com.noctarius.borabora.spi.Constants.MT_UNSINGED_INT;

public enum MajorType {
    UnsignedInteger(MT_UNSINGED_INT, 0b000, false, ByteSizes::intByteSize, ElementCounts.SINGLE_ELEMENT_COUNT),
    NegativeInteger(MT_NEGATIVE_INT, 0b001, false, ByteSizes::intByteSize, ElementCounts.SINGLE_ELEMENT_COUNT),
    ByteString(MT_BYTESTRING, 0b010, true, ByteSizes::stringByteSize, ElementCounts.SINGLE_ELEMENT_COUNT),
    TextString(MT_TEXTSTRING, 0b011, true, ByteSizes::stringByteSize, ElementCounts.SINGLE_ELEMENT_COUNT),
    Sequence(MT_SEQUENCE, 0b100, true, ByteSizes::sequenceByteSize, ElementCounts.SEQUENCE_ELEMENT_COUNT),
    Dictionary(MT_DICTIONARY, 0b101, true, ByteSizes::dictionaryByteSize, ElementCounts.DICTIONARY_ELEMENT_COUNT),
    SemanticTag(MT_SEMANTIC_TAG, 0b110, false, ByteSizes::semanticTagByteSize, ElementCounts.SINGLE_ELEMENT_COUNT),
    FloatingPointOrSimple(MT_FLOAT_SIMPLE, 0b111, false, ByteSizes::floatOrSimpleByteSize, ElementCounts.SINGLE_ELEMENT_COUNT),
    Unknown(-1, -1, false, (s, i) -> 0, (s, i) -> 0);

    private static final short HIGH_BITS_MASK = 0b1110_0000;

    private final short typeId;
    private final int mask;
    private final boolean indefinite;
    private final ObjectLongToLongFunction<Input> byteSize;
    private final ObjectLongToLongFunction<Input> elementCount;

    MajorType(int typeId, int mask, boolean indefinite, ObjectLongToLongFunction<Input> byteSize,
              ObjectLongToLongFunction<Input> elementCount) {

        this.typeId = (short) typeId;
        this.mask = mask;
        this.indefinite = indefinite;
        this.byteSize = byteSize;
        this.elementCount = elementCount;
    }

    public short typeId() {
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
        switch ((head & 0xff) >>> 5) {
            case MT_UNSINGED_INT:
                return UnsignedInteger;
            case MT_NEGATIVE_INT:
                return NegativeInteger;
            case MT_BYTESTRING:
                return ByteString;
            case MT_TEXTSTRING:
                return TextString;
            case MT_SEQUENCE:
                return Sequence;
            case MT_DICTIONARY:
                return Dictionary;
            case MT_SEMANTIC_TAG:
                return SemanticTag;
            case MT_FLOAT_SIMPLE:
                return FloatingPointOrSimple;
            default:
                throw new IllegalArgumentException("Unknown MajorType: " + ((head & HIGH_BITS_MASK) >>> 5));
        }
    }

}
