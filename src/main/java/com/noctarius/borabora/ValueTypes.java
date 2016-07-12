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

import com.noctarius.borabora.spi.Decoder;
import com.noctarius.borabora.spi.EncoderContext;
import com.noctarius.borabora.spi.QueryContext;
import com.noctarius.borabora.spi.TagReader;
import com.noctarius.borabora.spi.TagWriter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.noctarius.borabora.spi.CommonTagCodec.TAG_READER.DATE_TIME_READER;
import static com.noctarius.borabora.spi.CommonTagCodec.TAG_READER.ENCODED_CBOR_READER;
import static com.noctarius.borabora.spi.CommonTagCodec.TAG_READER.NBIG_NUM_READER;
import static com.noctarius.borabora.spi.CommonTagCodec.TAG_READER.TIMESTAMP_READER;
import static com.noctarius.borabora.spi.CommonTagCodec.TAG_READER.UBIG_NUM_READER;
import static com.noctarius.borabora.spi.CommonTagCodec.TAG_READER.URI_READER;
import static com.noctarius.borabora.spi.CommonTagCodec.TAG_WRITER.BIG_NUM_WRITER;
import static com.noctarius.borabora.spi.CommonTagCodec.TAG_WRITER.DATE_TIME_WRITER;
import static com.noctarius.borabora.spi.CommonTagCodec.TAG_WRITER.ENCODED_CBOR_WRITER;
import static com.noctarius.borabora.spi.CommonTagCodec.TAG_WRITER.TIMESTAMP_WRITER;
import static com.noctarius.borabora.spi.CommonTagCodec.TAG_WRITER.URI_WRITER;
import static com.noctarius.borabora.spi.CommonTagCodec.TYPE_MATCHER.BIG_NUM_MATCHER;
import static com.noctarius.borabora.spi.CommonTagCodec.TYPE_MATCHER.DATE_TIME_MATCHER;
import static com.noctarius.borabora.spi.CommonTagCodec.TYPE_MATCHER.ENCODED_CBOR_MATCHER;
import static com.noctarius.borabora.spi.CommonTagCodec.TYPE_MATCHER.TIMESTAMP_MATCHER;
import static com.noctarius.borabora.spi.CommonTagCodec.TYPE_MATCHER.URI_MATCHER;
import static com.noctarius.borabora.spi.Constants.ADDITIONAL_INFORMATION_MASK;
import static com.noctarius.borabora.spi.Constants.ASCII_ENCODER;
import static com.noctarius.borabora.spi.Constants.FP_VALUE_FALSE;
import static com.noctarius.borabora.spi.Constants.FP_VALUE_NULL;
import static com.noctarius.borabora.spi.Constants.FP_VALUE_TRUE;
import static com.noctarius.borabora.spi.Constants.FP_VALUE_UNDEF;
import static com.noctarius.borabora.spi.Constants.TAG_BIGFLOAT;
import static com.noctarius.borabora.spi.Constants.TAG_DATE_TIME;
import static com.noctarius.borabora.spi.Constants.TAG_ENCCBOR;
import static com.noctarius.borabora.spi.Constants.TAG_FRACTION;
import static com.noctarius.borabora.spi.Constants.TAG_MIME;
import static com.noctarius.borabora.spi.Constants.TAG_REGEX;
import static com.noctarius.borabora.spi.Constants.TAG_SIGNED_BIGNUM;
import static com.noctarius.borabora.spi.Constants.TAG_TIMESTAMP;
import static com.noctarius.borabora.spi.Constants.TAG_UNSIGNED_BIGNUM;
import static com.noctarius.borabora.spi.Constants.TAG_URI;

public enum ValueTypes
        implements ValueType, TagReader, TagWriter {

    Number(Value::number),
    Int(Value::number, Number),
    UInt(Value::number, Int),
    NInt(Value::number, Int),
    String(Value::string),
    ByteString(Value::string, String),
    TextString(Value::string, String),
    Sequence(Value::sequence),
    Dictionary(Value::dictionary),
    Float(Value::number, Number),
    UFloat(Value::number, Float),
    NFloat(Value::number, Float),
    Bool(Value::bool),
    Null((v) -> null),
    Undefined((v) -> null),
    DateTime(DATE_TIME_READER, DATE_TIME_WRITER, DATE_TIME_MATCHER, Value::tag),
    Timestamp(TIMESTAMP_READER, TIMESTAMP_WRITER, TIMESTAMP_MATCHER, Value::tag),
    UBigNum(UBIG_NUM_READER, BIG_NUM_WRITER, BIG_NUM_MATCHER, Value::tag, UInt),
    NBigNum(NBIG_NUM_READER, BIG_NUM_WRITER, BIG_NUM_MATCHER, Value::tag, NInt),
    EncCBOR(ENCODED_CBOR_READER, ENCODED_CBOR_WRITER, ENCODED_CBOR_MATCHER, Value::tag),
    URI(URI_READER, URI_WRITER, URI_MATCHER, Value::tag),
    Unknown(Value::raw);

    private final Predicate<Object> encodeableTypeMatcher;
    private final Function<Value, Object> byValueType;
    private final TagReader<Object> tagReader;
    private final TagWriter<Object> tagWriter;
    private final ValueType identity;

    ValueTypes(Function<Value, Object> byValueType) {
        this(null, null, (v) -> false, byValueType, null);
    }

    ValueTypes(Function<Value, Object> byValueType, ValueType identity) {
        this(null, null, (v) -> false, byValueType, identity);
    }

    ValueTypes(TagReader<Object> tagReader, TagWriter<Object> tagWriter, Predicate<Object> encodeableTypeMatcher,
               Function<Value, Object> byValueType) {

        this(tagReader, tagWriter, encodeableTypeMatcher, byValueType, null);
    }

    ValueTypes(TagReader<Object> tagReader, TagWriter<Object> tagWriter, Predicate<Object> encodeableTypeMatcher,
               Function<Value, Object> byValueType, ValueType identity) {

        this.encodeableTypeMatcher = encodeableTypeMatcher;
        this.byValueType = byValueType;
        this.tagReader = tagReader;
        this.tagWriter = tagWriter;
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
        return (T) byValueType.apply(value);
    }

    @Override
    public Object process(long offset, long length, QueryContext queryContext) {
        if (tagReader == null) {
            return null;
        }
        return tagReader.process(offset, length, queryContext);
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
            case SemanticTag:
                return semanticTagType(input, offset);
        }
        throw new IllegalArgumentException("Illegal value type requested");
    }

    public static ValueTypes valueType(Object value) {
        if (value == null) {
            return Null;
        }

        Class<?> type = value.getClass();
        if (java.lang.Number.class.isAssignableFrom(type)) {
            if (value instanceof java.lang.Float || value instanceof Double || value instanceof BigDecimal) {
                return ((Number) value).doubleValue() < 0.0 ? NFloat : UFloat;
            }
            return ((Number) value).longValue() < 0 ? NInt : UInt;
        } else if (java.lang.String.class.isAssignableFrom(type)) {
            return ASCII_ENCODER.canEncode((String) value) ? ByteString : TextString;
        } else if (List.class.isAssignableFrom(type) || value.getClass().isArray()) {
            return Sequence;
        } else if (Map.class.isAssignableFrom(type)) {
            return Dictionary;
        } else {
            for (ValueTypes valueType : ValueTypes.values()) {
                if (valueType.typeEncodeable(value)) {
                    return valueType;
                }
            }
        }
        return null;
    }

    private static ValueTypes floatNullOrBool(short head) {
        int addInfo = head & ADDITIONAL_INFORMATION_MASK;
        switch (addInfo) {
            case FP_VALUE_NULL:
                return Null;
            case FP_VALUE_TRUE:
            case FP_VALUE_FALSE:
                return Bool;
            case FP_VALUE_UNDEF:
                return Undefined;
            default:
                return NFloat;
        }
    }

    private static ValueTypes semanticTagType(Input input, long offset) {
        Number tagType = Decoder.readUint(input, offset);
        switch (tagType.intValue()) {
            case TAG_DATE_TIME:
                return DateTime;
            case TAG_TIMESTAMP:
                return Timestamp;
            case TAG_UNSIGNED_BIGNUM:
                return UBigNum;
            case TAG_SIGNED_BIGNUM:
                return NBigNum;
            case TAG_BIGFLOAT:
                // return BigFloat;
                throw new IllegalStateException("BigFloat is not supported");
            case TAG_ENCCBOR:
                return EncCBOR;
            case TAG_FRACTION:
                //return Fraction;
                throw new IllegalStateException("Fraction is not supported");
            case TAG_URI:
                return URI;
            case TAG_REGEX:
                //return RegEx;
                throw new IllegalStateException("RegEx is not supported");
            case TAG_MIME:
                //return Mime;
                throw new IllegalStateException("Mime is not supported");
        }
        return Unknown;
    }

}
