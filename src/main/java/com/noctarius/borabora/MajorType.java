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
    UnsignedInteger(MT_UNSINGED_INT, 0b000, false),
    NegativeInteger(MT_NEGATIVE_INT, 0b001, false),
    ByteString(MT_BYTESTRING, 0b010, true),
    TextString(MT_TEXTSTRING, 0b011, true),
    Sequence(MT_SEQUENCE, 0b100, true),
    Dictionary(MT_DICTIONARY, 0b101, true),
    SemanticTag(MT_SEMANTIC_TAG, 0b110, false),
    FloatingPointOrSimple(MT_FLOAT_SIMPLE, 0b111, false),
    Unknown(-1, -1, false);

    private static final short HIGH_BITS_MASK = 0b1110_0000;

    private final short typeId;
    private final int mask;
    private final boolean indefinite;

    MajorType(int typeId, int mask, boolean indefinite) {
        this.typeId = (short) typeId;
        this.mask = mask;
        this.indefinite = indefinite;
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
