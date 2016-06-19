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

import com.noctarius.borabora.spi.QueryContext;

import java.util.function.Function;

import static com.noctarius.borabora.Bytes.readUInt8;
import static com.noctarius.borabora.Constants.ADDITIONAL_INFORMATION_MASK;
import static com.noctarius.borabora.Constants.FP_VALUE_FALSE;
import static com.noctarius.borabora.Constants.FP_VALUE_NULL;
import static com.noctarius.borabora.Constants.FP_VALUE_TRUE;
import static com.noctarius.borabora.Constants.FP_VALUE_UNDEF;
import static com.noctarius.borabora.Constants.TAG_BIGFLOAT;
import static com.noctarius.borabora.Constants.TAG_DATE_TIME;
import static com.noctarius.borabora.Constants.TAG_ENCCBOR;
import static com.noctarius.borabora.Constants.TAG_FRACTION;
import static com.noctarius.borabora.Constants.TAG_MIME;
import static com.noctarius.borabora.Constants.TAG_REGEX;
import static com.noctarius.borabora.Constants.TAG_SIGNED_BIGNUM;
import static com.noctarius.borabora.Constants.TAG_TIMESTAMP;
import static com.noctarius.borabora.Constants.TAG_UNSIGNED_BIGNUM;
import static com.noctarius.borabora.Constants.TAG_URI;

public enum ValueTypes
        implements ValueType, TagProcessor {

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
    DateTime(TagProcessors::readDateTime, Value::tag),
    Timestamp(TagProcessors::readTimestamp, Value::tag),
    UBigNum(TagProcessors::readUBigNum, Value::tag, UInt),
    NBigNum(TagProcessors::readNBigNum, Value::tag, NInt),
    EncCBOR(TagProcessors::readEncCBOR, Value::tag),
    URI(TagProcessors::readURI, Value::tag),
    Unknown(Value::raw);

    private final Function<Value, Object> byValueType;
    private final TagProcessor processor;
    private final ValueType identity;

    ValueTypes(Function<Value, Object> byValueType) {
        this(null, byValueType, null);
    }

    ValueTypes(Function<Value, Object> byValueType, ValueType identity) {
        this(null, byValueType, identity);
    }

    ValueTypes(TagProcessor processor, Function<Value, Object> byValueType) {
        this(processor, byValueType, null);
    }

    ValueTypes(TagProcessor processor, Function<Value, Object> byValueType, ValueType identity) {
        this.byValueType = byValueType;
        this.processor = processor;
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
        if (processor == null) {
            return null;
        }
        return processor.process(offset, length, queryContext);
    }

    static ValueTypes valueType(Input input, long offset) {
        short head = readUInt8(input, offset);

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
