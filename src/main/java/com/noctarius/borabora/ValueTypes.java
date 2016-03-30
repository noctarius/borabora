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

import java.util.Collection;
import java.util.function.Function;

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

enum ValueTypes
        implements ValueType, TagProcessor {

    Number(TypeSpecs.Number, Value::number),
    Int(TypeSpecs.Int, Value::number),
    Uint(TypeSpecs.UInt, Value::number),
    NInt(TypeSpecs.Int, Value::number),
    ByteString(TypeSpecs.String, Value::string),
    TextString(TypeSpecs.String, Value::string),
    Sequence(TypeSpecs.Sequence, Value::sequence),
    Dictionary(TypeSpecs.Dictionary, Value::dictionary),
    NFloat(TypeSpecs.Float, Value::number),
    Bool(TypeSpecs.Bool, Value::bool),
    Null(null, (v) -> null),
    Undefined(TypeSpecs.Null, (v) -> null),
    DateTime(TypeSpecs.DateTime, TagProcessors::readDateTime, Value::tag),
    Timestamp(TypeSpecs.Timstamp, TagProcessors::readTimestamp, Value::tag),
    UBigNum(TypeSpecs.UInt, TagProcessors::readUBigNum, Value::tag, Uint),
    NBigNum(TypeSpecs.NInt, TagProcessors::readNBigNum, Value::tag, NInt),
    EncCBOR(TypeSpecs.EncCBOR, TagProcessors::readEncCBOR, Value::tag),
    URI(TypeSpecs.URI, TagProcessors::readURI, Value::tag),
    Unknown(TypeSpecs.Unknown, Value::raw);

    private final Function<Value, Object> byValueType;
    private final TagProcessor processor;
    private final TypeSpec typeSpec;
    private final ValueType identity;

    ValueTypes(TypeSpec superType, Function<Value, Object> byValueType) {
        this(superType, null, byValueType, null);
    }

    ValueTypes(TypeSpec superType, TagProcessor processor, Function<Value, Object> byValueType) {
        this(superType, processor, byValueType, null);
    }

    ValueTypes(TypeSpec typeSpec, TagProcessor processor, Function<Value, Object> byValueType, ValueType identity) {
        this.byValueType = byValueType;
        this.processor = processor;
        this.typeSpec = typeSpec;
        this.identity = identity;
    }

    @Override
    public TypeSpec typeSpec() {
        return typeSpec;
    }

    @Override
    public String spec() {
        return typeSpec != null ? typeSpec.spec() : null;
    }

    @Override
    public int tagId() {
        return typeSpec != null ? typeSpec.tagId() : -1;
    }

    @Override
    public boolean matches(ValueType other) {
        if (matchesExact(other)) {
            return true;
        }
        if (typeSpec == null) {
            return false;
        }
        return typeSpec.matches(other.typeSpec());
    }

    @Override
    public boolean matchesExact(ValueType other) {
        if (this == other) {
            return true;
        }
        if (typeSpec == null) {
            return false;
        }
        return typeSpec.matchesExact(other.typeSpec());
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
    public Object process(Decoder stream, long offset, long length, Collection<SemanticTagProcessor> processors) {
        if (processor == null) {
            return null;
        }
        return processor.process(stream, offset, length, processors);
    }

    static ValueTypes valueType(Decoder stream, long offset) {
        short head = stream.transientUint8(offset);

        // Read major type first
        MajorType majorType = MajorType.findMajorType(head);

        // Simple major types are assigned directly
        switch (majorType) {
            case UnsignedInteger:
                return Uint;
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
                return semanticTagType(stream, offset);
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

    private static ValueTypes semanticTagType(Decoder stream, long offset) {
        Number tagType = stream.readUint(offset);
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
