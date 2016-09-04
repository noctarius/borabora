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

import static com.noctarius.borabora.spi.io.Constants.MT_BYTESTRING;
import static com.noctarius.borabora.spi.io.Constants.MT_DICTIONARY;
import static com.noctarius.borabora.spi.io.Constants.MT_FLOAT_SIMPLE;
import static com.noctarius.borabora.spi.io.Constants.MT_NEGATIVE_INT;
import static com.noctarius.borabora.spi.io.Constants.MT_SEMANTIC_TAG;
import static com.noctarius.borabora.spi.io.Constants.MT_SEQUENCE;
import static com.noctarius.borabora.spi.io.Constants.MT_TEXTSTRING;
import static com.noctarius.borabora.spi.io.Constants.MT_UNSINGED_INT;

/**
 * The <tt>MajorType</tt> enum defines the basic types as specified by the CBOR
 * specification (RFC 7049). Major types are directly encoded in the header,
 * whereas {@link ValueType}s can be a special case inside a given major type
 * or a semantic tag tagged element.
 *
 * @see ValueType
 * @see ValueTypes
 */
public enum MajorType {

    /**
     * <tt>UnsignedInteger</tt> describes an unsigned integer with a bit-size of
     * 1 to 64 bit.
     */
    UnsignedInteger(MT_UNSINGED_INT, 0b000, false),

    /**
     * <tt>NegativeInteger</tt> describes an always negative integer. This is not
     * the commonly expected signed integer as it cannot take positive values.
     * The bit-size is between 1 and 64 bit.
     */
    NegativeInteger(MT_NEGATIVE_INT, 0b001, false),

    /**
     * <tt>ByteString</tt> describes an ASCII char only containing string. The
     * length of a string is defined as an {@link #UnsignedInteger} (up to 64
     * bit), however Java cannot represent string of that size.
     */
    ByteString(MT_BYTESTRING, 0b010, true),

    /**
     * <tt>TextString</tt> describes an UTF-8 encoded char containing string.
     * The length of a string is defined as an {@link #UnsignedInteger} (up to 64
     * bit), however Java cannot represent string of that size.
     */
    TextString(MT_TEXTSTRING, 0b011, true),

    /**
     * <tt>Sequence</tt> describes a value type representing a collection of values.
     * Each element in a sequence can have a different value type, and sequences can
     * have a fixed element count or represent an indefinite number of elements. In
     * the latter case borabora does a quick pre-scan to find the number and offsets
     * of the elements.
     */
    Sequence(MT_SEQUENCE, 0b100, true),

    /**
     * <tt>Dictionary</tt> describes a value type representing a collection of key-value
     * pairs. Each key and each value in a dictionary can have a different value types,
     * and dictionaries can have a fixed pair count or represent an indefinite number of
     * pairs. In the latter case borabora does a quick pre-scan to find the number and
     * offsets of the pair-elements.
     */
    Dictionary(MT_DICTIONARY, 0b101, true),

    /**
     * <tt>SemanticTag</tt> describes a further specification of a following data item
     * to flag the containing value as another {@link ValueType}, for example a string
     * can be flagged as a datetime item.
     * <p>SemanticTags are defined using tag ids that are specified by the IANA and
     * available in the <a href="http://www.iana.org/assignments/cbor-tags/cbor-tags.xhtml">
     * IANA CBOR Tag Registry</a>.</p>
     */
    SemanticTag(MT_SEMANTIC_TAG, 0b110, false),

    /**
     * <tt>FloatingPointOrSimple</tt> describes two distinct types of data. The first
     * value is either a {@link HalfPrecisionFloat}, a {@link Float} or a {@link Double}
     * in the Java world. Values can be positive or negative.
     * <p>The second type of data encoded in this major type are commonly used simple
     * values like <tt>null</tt>, <tt>true</tt> and <tt>false</tt>.</p>
     * <p>The corresponding {@link ValueType} will automatically take into account what
     * is encoded in the actual data item.</p>
     */
    FloatingPointOrSimple(MT_FLOAT_SIMPLE, 0b111, false),

    /**
     * <tt>Unknown</tt> describes an unknown major type that be added to a later version
     * of the CBOR specification or is returned whenever the type cannot be read for
     * other, yet unknown, reasons.
     */
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

    /**
     * Returns the type id, the CBOR specified id, for this MajorType.
     *
     * @return the type id
     */
    public short typeId() {
        return typeId;
    }

    /**
     * Returns if this MajorType can be of indefinite size or not.
     *
     * @return true if indefinite size is possible, otherwise false
     */
    public boolean indefinite() {
        return indefinite;
    }

    /**
     * Matches a given header byte (represented as a short) against this MajorType.
     *
     * @param head the header byte to match
     * @return true if the header represents this MajorType, otherwise false
     */
    public boolean match(short head) {
        int highBits = (head & HIGH_BITS_MASK) >>> 5;
        return highBits == mask;
    }

    /**
     * Returns the <tt>MajorType</tt> according to the given header byte (represented
     * as a short).
     *
     * @param head the header byte to match
     * @return the MajorType matching the given header byte
     */
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
            default: // Always MT_FLOAT_SIMPLE
                return FloatingPointOrSimple;
        }
    }

}
