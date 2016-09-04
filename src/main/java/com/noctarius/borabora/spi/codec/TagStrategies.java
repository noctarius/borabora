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
import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.ValueType;
import com.noctarius.borabora.ValueTypes;
import com.noctarius.borabora.builder.encoder.semantictag.CBORBuilder;
import com.noctarius.borabora.builder.encoder.semantictag.DateTimeBuilder;
import com.noctarius.borabora.builder.encoder.semantictag.FractionBuilder;
import com.noctarius.borabora.builder.encoder.semantictag.NBigNumberBuilder;
import com.noctarius.borabora.builder.encoder.semantictag.TimestampBuilder;
import com.noctarius.borabora.builder.encoder.semantictag.UBigNumberBuilder;
import com.noctarius.borabora.builder.encoder.semantictag.URIBuilder;
import com.noctarius.borabora.spi.io.Constants;
import com.noctarius.borabora.spi.query.TypeSpec;
import com.noctarius.borabora.spi.query.TypeSpecs;
import com.noctarius.borabora.spi.builder.EncoderContext;
import com.noctarius.borabora.spi.io.Decoder;
import com.noctarius.borabora.spi.query.QueryContext;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.noctarius.borabora.spi.io.Constants.TAG_DATE_TIME;
import static com.noctarius.borabora.spi.io.Constants.TAG_ENCCBOR;
import static com.noctarius.borabora.spi.io.Constants.TAG_FRACTION;
import static com.noctarius.borabora.spi.io.Constants.TAG_NEGATIVE_BIGNUM;
import static com.noctarius.borabora.spi.io.Constants.TAG_TIMESTAMP;
import static com.noctarius.borabora.spi.io.Constants.TAG_UNSIGNED_BIGNUM;
import static com.noctarius.borabora.spi.io.Constants.TAG_URI;
import static com.noctarius.borabora.spi.codec.TagBuilders.CBORBuilderImpl;
import static com.noctarius.borabora.spi.codec.TagBuilders.DateTimeBuilderImpl;
import static com.noctarius.borabora.spi.codec.TagBuilders.FractionBuilderImpl;
import static com.noctarius.borabora.spi.codec.TagBuilders.NBigNumberBuilderImpl;
import static com.noctarius.borabora.spi.codec.TagBuilders.TimestampBuilderImpl;
import static com.noctarius.borabora.spi.codec.TagBuilders.UBigNumberBuilderImpl;
import static com.noctarius.borabora.spi.codec.TagBuilders.URIBuilderImpl;

public enum TagStrategies
        implements TagStrategy {

    DateTime(TAG_DATE_TIME, ValueTypes.DateTime, DateTimeBuilder.class, TagWriters.DateTime, //
            TagReaders.DateTime, TypeSpecs.DateTime, TypeMatchers.DateTime, DateTimeBuilderImpl::new),

    Timestamp(TAG_TIMESTAMP, ValueTypes.Timestamp, TimestampBuilder.class, TagWriters.Timestamp, //
            TagReaders.Timestamp, TypeSpecs.Timstamp, TypeMatchers.Timestamp, TimestampBuilderImpl::new),

    UBigNum(TAG_UNSIGNED_BIGNUM, ValueTypes.UBigNum, UBigNumberBuilder.class, TagWriters.BigNum,//
            TagReaders.UBigNum, TypeSpecs.UInt, TypeMatchers.UBigNum, UBigNumberBuilderImpl::new),

    NBigNum(TAG_NEGATIVE_BIGNUM, ValueTypes.NBigNum, NBigNumberBuilder.class, TagWriters.BigNum, //
            TagReaders.NBigNum, TypeSpecs.NInt, TypeMatchers.NBigNum, NBigNumberBuilderImpl::new),

    Fraction(TAG_FRACTION, ValueTypes.Fraction, FractionBuilder.class, TagWriters.Fraction, //
            TagReaders.Fraction, TypeSpecs.Float, TypeMatchers.Fraction, FractionBuilderImpl::new),

    URI(TAG_URI, ValueTypes.URI, URIBuilder.class, TagWriters.URI, //
            TagReaders.URI, TypeSpecs.URI, TypeMatchers.URI, URIBuilderImpl::new),

    EncCBOR(TAG_ENCCBOR, ValueTypes.EncCBOR, CBORBuilder.class, null, //
            TagReaders.EncCBOR, TypeSpecs.EncCBOR, null, CBORBuilderImpl::new);

    private static final TagStrategy[] TAG_STRATEGIES = TagStrategies.values();

    private final int tagId;
    private final TypeSpec typeSpec;
    private final ValueType valueType;
    private final TagWriter tagWriter;
    private final TagReader tagReader;
    private final Class<?> tagBuilderType;
    private final Predicate<Object> handlesPredicate;
    private final Function<EncoderContext, Object> tagBuilderFunction;

    TagStrategies(int tagId, ValueType valueType, Class<?> tagBuilderType, TagWriter tagWriter, TagReader tagReader,
                  TypeSpec typeSpec, Predicate<Object> handlesPredicate, Function<EncoderContext, Object> tagBuilderFunction) {

        this.tagId = tagId;
        this.typeSpec = typeSpec;
        this.valueType = valueType;
        this.tagWriter = tagWriter;
        this.tagReader = tagReader;
        this.tagBuilderType = tagBuilderType;
        this.handlesPredicate = handlesPredicate;
        this.tagBuilderFunction = tagBuilderFunction;
    }

    @Override
    public Object newTagBuilder(EncoderContext encoderContext) {
        return tagBuilderFunction.apply(encoderContext);
    }

    @Override
    public int tagId() {
        return tagId;
    }

    @Override
    public ValueType valueType() {
        return valueType;
    }

    @Override
    public Class<?> tagBuilderType() {
        return tagBuilderType;
    }

    @Override
    public long process(Object value, long offset, EncoderContext encoderContext) {
        return tagWriter.process(value, offset, encoderContext);
    }

    @Override
    public boolean handles(Object value) {
        return handlesPredicate != null && handlesPredicate.test(value);
    }

    @Override
    public Object process(ValueType valueType, long offset, long length, QueryContext queryContext) {
        return tagReader.process(valueType, offset, length, queryContext);
    }

    @Override
    public boolean handles(Input input, long offset) {
        short head = Decoder.readUInt8(input, offset);
        MajorType majorType = MajorType.findMajorType(head);
        if (majorType != MajorType.SemanticTag) {
            return false;
        }
        int tagId = Decoder.readSemanticTagId(input, offset);
        return this.tagId == tagId;
    }

    @Override
    public TypeSpec handles(long tagId) {
        return this.tagId == tagId ? typeSpec : null;
    }

    @Override
    public ValueType valueType(Input input, long offset) {
        int tagId = Decoder.readSemanticTagId(input, offset);
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

    public static ValueType valueyType(Object value) {
        for (TagStrategy tagStrategy : TAG_STRATEGIES) {
            if (tagStrategy.handles(value)) {
                return tagStrategy.valueType();
            }
        }
        return ValueTypes.Unknown;
    }

}
