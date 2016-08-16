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
import com.noctarius.borabora.spi.codec.CommonTagCodec;
import com.noctarius.borabora.spi.codec.Decoder;
import com.noctarius.borabora.spi.codec.EncoderContext;
import com.noctarius.borabora.spi.codec.StringEncoders;
import com.noctarius.borabora.spi.codec.TagReader;
import com.noctarius.borabora.spi.codec.TagWriter;
import com.noctarius.borabora.spi.query.QueryContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public enum ValueTypes
        implements ValueType, TagReader, TagWriter {

    Number(Value::number), //
    Int(Value::number, Number), //
    UInt(Value::number, Int, ValueValidators::isPositive), //
    NInt(Value::number, Int, ValueValidators::isNegative), //
    String(Value::string), //
    ByteString(Value::string, String, ValueValidators::isByteString), //
    TextString(Value::string, String, ValueValidators::isTextString), //
    Sequence(Value::sequence), //
    Dictionary(Value::dictionary), //
    Float(Value::number, Number), //
    Bool(Value::bool), //
    Null((v) -> null), //
    Undefined((v) -> null), //
    DateTime(CommonTagCodec.TAG_READER.DATE_TIME_READER, CommonTagCodec.TAG_WRITER.DATE_TIME_WRITER,
            CommonTagCodec.TYPE_MATCHER.DATE_TIME_MATCHER, Value::tag), //
    Timestamp(CommonTagCodec.TAG_READER.TIMESTAMP_READER, CommonTagCodec.TAG_WRITER.TIMESTAMP_WRITER,
            CommonTagCodec.TYPE_MATCHER.TIMESTAMP_MATCHER, Value::tag), //
    UBigNum(CommonTagCodec.TAG_READER.UBIG_NUM_READER, CommonTagCodec.TAG_WRITER.BIG_NUM_WRITER,
            CommonTagCodec.TYPE_MATCHER.UBIG_NUM_MATCHER, Value::tag, UInt, ValueValidators::isPositive), //
    NBigNum(CommonTagCodec.TAG_READER.NBIG_NUM_READER, CommonTagCodec.TAG_WRITER.BIG_NUM_WRITER,
            CommonTagCodec.TYPE_MATCHER.NBIG_NUM_MATCHER, Value::tag, NInt, ValueValidators::isNegative), //
    EncCBOR(CommonTagCodec.TAG_READER.ENCODED_CBOR_READER, CommonTagCodec.TAG_WRITER.ENCODED_CBOR_WRITER,
            CommonTagCodec.TYPE_MATCHER.ENCODED_CBOR_MATCHER, Value::tag), //
    URI(CommonTagCodec.TAG_READER.URI_READER, CommonTagCodec.TAG_WRITER.URI_WRITER, CommonTagCodec.TYPE_MATCHER.URI_MATCHER,
            Value::tag), //
    Unknown(CommonTagCodec.TAG_READER.UNKNOWN_TAG_READER, null, null, Value::raw);

    private static final ValueTypes[] VALUE_TYPES_VALUES = values();

    private final Predicate<Object> encodeableTypeMatcher;
    private final Function<Value, Object> byValueType;
    private final TagReader<Object> tagReader;
    private final TagWriter<Object> tagWriter;
    private final BiConsumer<Value, Object> validator;
    private final ValueType identity;

    ValueTypes(Function<Value, Object> byValueType) {
        this(null, null, (v) -> false, byValueType, null, null);
    }

    ValueTypes(Function<Value, Object> byValueType, ValueType identity) {
        this(null, null, (v) -> false, byValueType, identity, null);
    }

    ValueTypes(Function<Value, Object> byValueType, ValueType identity, BiConsumer<Value, Object> validator) {
        this(null, null, (v) -> false, byValueType, identity, validator);
    }

    ValueTypes(TagReader<Object> tagReader, TagWriter<Object> tagWriter, Predicate<Object> encodeableTypeMatcher,
               Function<Value, Object> byValueType) {

        this(tagReader, tagWriter, encodeableTypeMatcher, byValueType, null, null);
    }

    ValueTypes(TagReader<Object> tagReader, TagWriter<Object> tagWriter, Predicate<Object> encodeableTypeMatcher,
               Function<Value, Object> byValueType, ValueType identity, BiConsumer<Value, Object> validator) {

        this.encodeableTypeMatcher = encodeableTypeMatcher;
        this.byValueType = byValueType;
        this.tagReader = tagReader;
        this.tagWriter = tagWriter;
        this.validator = validator;
        this.identity = identity;
    }

    @Override
    public boolean matches(ValueType other) {
        if (matchesExact(other)) {
            return true;
        }
        if (identity == other) {
            return true;
        }
        if (identity == null) {
            return false;
        }
        return identity.matches(other);
    }

    @Override
    public boolean matchesExact(ValueType other) {
        if (this == other) {
            return true;
        }
        return false;
    }

    @Override
    public ValueType identity() {
        return identity != null ? identity : this;
    }

    @Override
    public <T> T value(Value value) {
        return value(value, false);
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

    @Override
    public Object process(ValueType valueType, long offset, long length, QueryContext queryContext) {
        return tagReader.process(valueType, offset, length, queryContext);
    }

    @Override
    public long process(Object value, long offset, EncoderContext encoderContext) {
        return tagWriter.process(value, offset, encoderContext);
    }

    private boolean typeEncodeable(Object value) {
        return encodeableTypeMatcher.test(value);
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
                return semanticTagType(input, offset);
        }
    }

    public static ValueTypes valueType(Object value) {
        if (value == null) {
            return Null;
        }

        Class<?> type = value.getClass();
        if (java.lang.Number.class.isAssignableFrom(type)) {
            if (value instanceof HalfPrecisionFloat || value instanceof java.lang.Float //
                    || value instanceof Double || value instanceof BigDecimal) {

                return Float;
            }
            if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long) {
                return ((Number) value).longValue() < 0 ? NInt : UInt;
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
        for (ValueTypes valueType : VALUE_TYPES_VALUES) {
            if (valueType.typeEncodeable(value)) {
                return valueType;
            }
        }
        return null;
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

    private static ValueTypes semanticTagType(Input input, long offset) {
        Number tagType = Decoder.readUint(input, offset);
        switch (tagType.intValue()) {
            case Constants.TAG_DATE_TIME:
                return DateTime;
            case Constants.TAG_TIMESTAMP:
                return Timestamp;
            case Constants.TAG_UNSIGNED_BIGNUM:
                return UBigNum;
            case Constants.TAG_SIGNED_BIGNUM:
                return NBigNum;
            case Constants.TAG_BIGFLOAT:
                // return BigFloat;
                throw new IllegalStateException("BigFloat is not supported");
            case Constants.TAG_ENCCBOR:
                return EncCBOR;
            case Constants.TAG_FRACTION:
                //return Fraction;
                throw new IllegalStateException("Fraction is not supported");
            case Constants.TAG_URI:
                return URI;
            case Constants.TAG_REGEX:
                //return RegEx;
                throw new IllegalStateException("RegEx is not supported");
            case Constants.TAG_MIME:
                //return Mime;
                throw new IllegalStateException("Mime is not supported");
        }
        return Unknown;
    }

}
