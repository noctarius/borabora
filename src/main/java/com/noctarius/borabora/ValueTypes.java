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

import com.noctarius.borabora.spi.Constants;
import com.noctarius.borabora.spi.ValueValidators;
import com.noctarius.borabora.spi.codec.Decoder;
import com.noctarius.borabora.spi.codec.StringEncoders;
import com.noctarius.borabora.spi.codec.TagStrategies;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * ValueTypes are a set of prebuilt {@link ValueType} implementations. They define the
 * common basic {@link MajorType}s, as well as some integrated semantic tag types and
 * super types to group different types.
 *
 * @see ValueType
 * @see MajorType
 */
public enum ValueTypes
        implements ValueType {

    /**
     * <tt>Number</tt> defines a group of number related types, like UInt, NInt, Float, ...
     */
    Number(Value::number),

    /**
     * <tt>Int</tt> defines a group of integer related types, like UInt, NInt, UBigNum, ...
     */
    Int(Value::number, Number),

    /**
     * <tt>UInt</tt> defines a value type for {@link MajorType#UnsignedInteger}. It is
     * also defined as part of the groups {@link #Int} and {@link #Number}.
     */
    UInt(Value::number, Int, ValueValidators::isPositive),

    /**
     * <tt>NInt</tt> defines a value type for {@link MajorType#NegativeInteger}. It is
     * also defined as part of the groups {@link #Int} and {@link #Number}.
     */
    NInt(Value::number, Int, ValueValidators::isNegative),

    /**
     * <tt>String</tt> defines a group for the string value types {@link #ByteString}
     * and {@link #TextString}.
     */
    String(Value::string),

    /**
     * <tt>ByteString</tt> defines a value type for ASCII only strings. It is also
     * defined as part of the group {@link #String}.
     */
    ByteString(Value::string, String, ValueValidators::isByteString),

    /**
     * <tt>ByteString</tt> defines a value type for UTF-8 encoded strings. It is
     * also defined as part of the group {@link #String}.
     */
    TextString(Value::string, String, ValueValidators::isTextString),

    /**
     * <tt>Sequence</tt> defines a value type representing a collection of values.
     * Each element in a sequence can have a different value type, and sequences can
     * have a fixed element count or represent an indefinite number of elements. In
     * the latter case borabora does a quick pre-scan to find the number and offsets
     * of the elements.
     */
    Sequence(Value::sequence),

    /**
     * <tt>Dictionary</tt> defines a value type representing a collection of key-value
     * pairs. Each key and each value in a dictionary can have a different value types,
     * and dictionaries can have a fixed pair count or represent an indefinite number of
     * pairs. In the latter case borabora does a quick pre-scan to find the number and
     * offsets of the pair-elements.
     */
    Dictionary(Value::dictionary),

    /**
     * <tt>Float</tt> defines a value type representing a floating point value of either
     * of the types: {@link HalfPrecisionFloat}, {@link Float} {@link Double}.
     */
    Float(Value::number, Number),

    /**
     * <tt>Bool</tt> defines a value type representing a boolean (true / false) value.
     */
    Bool(Value::bool),

    /**
     * <tt>Null</tt> defines a value type representing a null value.
     */
    Null((v) -> null),

    /**
     * <tt>Undefined</tt> defines a simple value of an undefined type. Currently CBOR
     * defines <tt>null</tt>, <tt>true</tt>, <tt>false</tt> as simple values, however
     * the specification is extensible to add more simple values at a later type.
     */
    Undefined((v) -> null),

    /**
     * <tt>DateTime</tt> defines a value type of date and time. The value is stored as
     * a semantic tag and a string. The DateTime encoding is defined to be based on
     * <a href="https://tools.ietf.org/html/rfc3339">RFC 3339</a> and refined by
     * <a href="https://tools.ietf.org/html/rfc4287#section-3.3">RFC 4287</a>.
     */
    DateTime(Value::tag),

    /**
     * <tt>Timestamp</tt> defines a value type representing a timestamp value of seconds
     * since 1970-01-01 00:00:00 UTC. The value is of 64 bit and will not exceed at
     * 2038-01-19 03:14:08 UTC.
     */
    Timestamp(Value::tag),

    /**
     * <tt>UBigNum</tt> represents a value type of an unsigned integer bigger than
     * {@link Long#MAX_VALUE} which cannot be represented without a
     * {@link java.math.BigInteger} anymore.
     */
    UBigNum(Value::tag, UInt, ValueValidators::isPositive),

    /**
     * <tt>NBigNum</tt> represents a value type of a negative integer smaller than
     * {@link Long#MIN_VALUE} which cannot be represented without a
     * {@link java.math.BigInteger} anymore.
     */
    NBigNum(Value::tag, NInt, ValueValidators::isNegative),

    /**
     * <tt>Fraction</tt> represents a value type of a floating point number outside
     * the representable range of {@link #Float}. The value will be represented as
     * a {@link java.math.BigDecimal}.
     */
    Fraction(Value::tag, Float),

    /**
     * <tt>EncCBOR</tt> represents a value type of still encoded CBOR. The value
     * is represented to the user as a {@link Value}.
     */
    EncCBOR(Value::tag),

    /**
     * <tt>URI</tt> represents a value type of an URI encoded value. the URI type
     * is represented in Java as a {@link java.net.URI} value.
     */
    URI(Value::tag),

    /**
     * <tt>Unknown</tt> represents a value of an unknown type. The value can still be
     * extracted using {@link Parser#extract(Input, long)}. Unknown type can happen
     * for semantic tags which are not known to the parser, however this is valid to
     * the CBOR specifications as long as the parser is able to ignore the type itself.
     */
    Unknown(Value::raw);

    private static final ValueTypes[] VALUE_TYPES_VALUES = values();

    private final Function<Value, Object> byValueType;
    private final BiConsumer<Value, Object> validator;
    private final ValueType identity;

    ValueTypes(Function<Value, Object> byValueType, ValueType identity) {
        this(byValueType, identity, null);
    }

    ValueTypes(Function<Value, Object> byValueType) {
        this(byValueType, null, null);
    }

    ValueTypes(Function<Value, Object> byValueType, ValueType identity, BiConsumer<Value, Object> validator) {
        this.byValueType = byValueType;
        this.validator = validator;
        this.identity = identity;
    }

    @Override
    public ValueType identity() {
        return identity != null ? identity : this;
    }

    @Override
    public <T> T value(Value value, boolean validate) {
        if (!value.valueType().matches(this)) {
            String msg = java.lang.String.format("ValueType does not match: %s, %s", this, value.valueType());
            throw new IllegalArgumentException(msg);
        }

        Object extracted = byValueType.apply(value);
        if (validate && validator != null) {
            validator.accept(value, extracted);
        }
        return (T) extracted;
    }

    public static ValueTypes valueType(Input input, long offset) {
        short head = Decoder.readUInt8(input, offset);

        // Read major type first
        MajorType majorType = MajorType.findMajorType(head);

        // Simple major types are assigned directly
        switch (majorType) {
            case UnsignedInteger:
                return UInt;
            case NegativeInteger:
                return NInt;
            case ByteString:
                return ByteString;
            case TextString:
                return TextString;
            case Sequence:
                return Sequence;
            case Dictionary:
                return Dictionary;
            case FloatingPointOrSimple:
                return floatNullOrBool(head);
            default: // Always a semantic tag
                return Unknown;
        }
    }

    public static ValueType valueType(Object value) {
        if (value == null) {
            return Null;
        }

        Class<?> type = value.getClass();
        if (java.lang.Number.class.isAssignableFrom(type)) {
            if (value instanceof HalfPrecisionFloat || value instanceof java.lang.Float //
                    || value instanceof Double) {

                return Float;
            }
            if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long) {
                return ((Number) value).longValue() < 0 ? NInt : UInt;
            }
            if (value instanceof BigDecimal) {
                return Fraction;
            }
        } else if (java.lang.String.class.isAssignableFrom(type)) {
            return StringEncoders.ASCII_ENCODER.canEncode((String) value) ? ByteString : TextString;
        } else if (List.class.isAssignableFrom(type) || value.getClass().isArray()) {
            return Sequence;
        } else if (Map.class.isAssignableFrom(type)) {
            return Dictionary;
        } else if (value instanceof Boolean) {
            return Bool;
        }
        return TagStrategies.valueyType(value);
    }

    private static ValueTypes floatNullOrBool(short head) {
        int addInfo = head & Constants.ADDITIONAL_INFORMATION_MASK;
        switch (addInfo) {
            case Constants.FP_VALUE_NULL:
                return Null;
            case Constants.FP_VALUE_TRUE:
            case Constants.FP_VALUE_FALSE:
                return Bool;
            case Constants.FP_VALUE_UNDEF:
                return Undefined;
            default:
                return Float;
        }
    }

}
