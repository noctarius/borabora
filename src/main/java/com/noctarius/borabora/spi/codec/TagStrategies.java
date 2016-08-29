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

import com.noctarius.borabora.builder.semantictag.BigNumberBuilder;
import com.noctarius.borabora.builder.semantictag.CBORBuilder;
import com.noctarius.borabora.builder.semantictag.DateTimeBuilder;
import com.noctarius.borabora.builder.semantictag.FractionBuilder;
import com.noctarius.borabora.builder.semantictag.TimestampBuilder;
import com.noctarius.borabora.builder.semantictag.URIBuilder;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.noctarius.borabora.spi.Constants.TAG_DATE_TIME;
import static com.noctarius.borabora.spi.Constants.TAG_ENCCBOR;
import static com.noctarius.borabora.spi.Constants.TAG_FRACTION;
import static com.noctarius.borabora.spi.Constants.TAG_NEGATIVE_BIGNUM;
import static com.noctarius.borabora.spi.Constants.TAG_TIMESTAMP;
import static com.noctarius.borabora.spi.Constants.TAG_UNSIGNED_BIGNUM;
import static com.noctarius.borabora.spi.Constants.TAG_URI;
import static com.noctarius.borabora.spi.codec.TagBuilders.BigNumberBuilderImpl;
import static com.noctarius.borabora.spi.codec.TagBuilders.CBORBuilderImpl;
import static com.noctarius.borabora.spi.codec.TagBuilders.DateTimeBuilderImpl;
import static com.noctarius.borabora.spi.codec.TagBuilders.FractionBuilderImpl;
import static com.noctarius.borabora.spi.codec.TagBuilders.TimestampBuilderImpl;
import static com.noctarius.borabora.spi.codec.TagBuilders.URIBuilderImpl;

public enum TagStrategies
        implements TagStrategy, TagEncoder {

    DateTime(TAG_DATE_TIME, DateTimeBuilder.class, TagWriters.DateTime, //
            TypeMatchers.DateTime, DateTimeBuilderImpl::new),

    Timestamp(TAG_TIMESTAMP, TimestampBuilder.class, TagWriters.Timestamp, //
            TypeMatchers.Timestamp, TimestampBuilderImpl::new),

    UBigNum(TAG_UNSIGNED_BIGNUM, BigNumberBuilder.class, TagWriters.BigNum,//
            TypeMatchers.UBigNum, BigNumberBuilderImpl::new),

    NBigNum(TAG_NEGATIVE_BIGNUM, BigNumberBuilder.class, TagWriters.BigNum, //
            TypeMatchers.NBigNum, BigNumberBuilderImpl::new),

    Fraction(TAG_FRACTION, FractionBuilder.class, TagWriters.Fraction, //
            TypeMatchers.Fraction, FractionBuilderImpl::new),

    URI(TAG_URI, URIBuilder.class, TagWriters.URI, //
            TypeMatchers.URI, URIBuilderImpl::new),

    EncCBOR(TAG_ENCCBOR, CBORBuilder.class, null, //
            null, CBORBuilderImpl::new);

    private final int tagId;
    private final TagWriter tagWriter;
    private final Class<?> tagBuilderType;
    private final Predicate<Object> handlesPredicate;
    private final Function<EncoderContext, Object> tagBuilderFunction;

    TagStrategies(int tagId, Class<?> tagBuilderType, TagWriter tagWriter, Predicate<Object> handlesPredicate,
                  Function<EncoderContext, Object> tagBuilderFunction) {

        this.tagId = tagId;
        this.tagWriter = tagWriter;
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
    public Class<?> tagBuilderType() {
        return tagBuilderType;
    }

    @Override
    public TagEncoder tagEncoder() {
        return tagWriter == null ? null : this;
    }

    @Override
    public long process(Object value, long offset, EncoderContext encoderContext) {
        return tagWriter.process(value, offset, encoderContext);
    }

    @Override
    public boolean handles(Object value) {
        return handlesPredicate != null && handlesPredicate.test(value);
    }
}
