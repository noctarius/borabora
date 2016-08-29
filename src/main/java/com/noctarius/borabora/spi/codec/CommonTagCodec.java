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
package com.noctarius.borabora.spi.codec;

import com.noctarius.borabora.Input;
import com.noctarius.borabora.ValueType;
import com.noctarius.borabora.ValueTypes;
import com.noctarius.borabora.spi.Constants;
import com.noctarius.borabora.spi.TypeSpec;
import com.noctarius.borabora.spi.TypeSpecs;
import com.noctarius.borabora.spi.query.QueryContext;

import java.math.BigInteger;

public final class CommonTagCodec
        implements TagDecoder<Object>, TagEncoder<Object> {

    public static final CommonTagCodec INSTANCE = new CommonTagCodec();

    private static final TypeSpecs[] TYPE_SPECS_VALUES = TypeSpecs.values();

    private CommonTagCodec() {
    }

    @Override
    public boolean handles(Input input, long offset) {
        return valueType(input, offset) != ValueTypes.Unknown;
    }

    @Override
    public Object process(ValueType valueType, long offset, long length, QueryContext queryContext) {
        return ((ValueTypes) valueType).process(valueType, offset, length, queryContext);
    }

    @Override
    public TypeSpec handles(long tagId) {
        for (TypeSpec typeSpec : TYPE_SPECS_VALUES) {
            if (typeSpec.tagId() == tagId) {
                return typeSpec;
            }
        }
        return null;
    }

    @Override
    public ValueType valueType(Input input, long offset) {
        Number tagType = Decoder.readUint(input, offset);
        if (tagType instanceof BigInteger) {
            return ValueTypes.Unknown;
        }
        if (tagType.longValue() > Integer.MAX_VALUE) {
            return ValueTypes.Unknown;
        }
        int tagId = tagType.intValue();
        switch (tagId) {
            case Constants.TAG_DATE_TIME:
                return ValueTypes.DateTime;
            case Constants.TAG_TIMESTAMP:
                return ValueTypes.Timestamp;
            case Constants.TAG_UNSIGNED_BIGNUM:
                return ValueTypes.UBigNum;
            case Constants.TAG_NEGATIVE_BIGNUM:
                return ValueTypes.NBigNum;
            case Constants.TAG_BIGFLOAT:
                // return BigFloat;
                throw new IllegalStateException("BigFloat is not supported");
            case Constants.TAG_ENCCBOR:
                return ValueTypes.EncCBOR;
            case Constants.TAG_FRACTION:
                return ValueTypes.Fraction;
            case Constants.TAG_URI:
                return ValueTypes.URI;
            case Constants.TAG_REGEX:
                //return RegEx;
                throw new IllegalStateException("RegEx is not supported");
            case Constants.TAG_MIME:
                //return Mime;
                throw new IllegalStateException("Mime is not supported");
        }
        return ValueTypes.Unknown;
    }

    @Override
    public boolean handles(Object value) {
        return ValueTypes.valueType(value) != ValueTypes.Unknown;
    }

    @Override
    public long process(Object value, long offset, EncoderContext encoderContext) {
        ValueTypes valueType = ValueTypes.valueType(value);
        if (valueType == null) {
            throw new IllegalStateException("Unknown ValueType found: " + value.getClass());
        }
        return valueType.process(value, offset, encoderContext);
    }

}
