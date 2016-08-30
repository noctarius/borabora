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

import com.noctarius.borabora.builder.semantictag.CBORBuilder;
import com.noctarius.borabora.builder.semantictag.DateTimeBuilder;
import com.noctarius.borabora.builder.semantictag.FractionBuilder;
import com.noctarius.borabora.builder.semantictag.NBigNumberBuilder;
import com.noctarius.borabora.builder.semantictag.TimestampBuilder;
import com.noctarius.borabora.builder.semantictag.UBigNumberBuilder;
import com.noctarius.borabora.builder.semantictag.URIBuilder;
import com.noctarius.borabora.spi.ValueValidators;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

import static com.noctarius.borabora.spi.Constants.TAG_ENCCBOR;
import static com.noctarius.borabora.spi.Constants.TAG_TIMESTAMP;
import static com.noctarius.borabora.spi.Constants.UTC;

class TagBuilders {

    private TagBuilders() {
    }

    static final class EmptyTagBuilder
            implements TagBuilder {

        private static final TagBuilder INSTANCE = new EmptyTagBuilder();

        @Override
        public <B> TagBuilderConsumer<B> endSemanticTag() {
            return null;
        }
    }

    static final class DateTimeBuilderImpl
            implements DateTimeBuilder {

        private final EncoderContext encoderContext;

        DateTimeBuilderImpl(EncoderContext encoderContext) {
            this.encoderContext = encoderContext;
        }

        @Override
        public TagBuilder putDateTime(Date date) {
            return putDateTime(date.toInstant());
        }

        @Override
        public TagBuilder putDateTime(Instant instant) {
            encoderContext.encode(offset -> Encoder.putDateTime(instant.atZone(UTC), offset, encoderContext.output()));
            return EmptyTagBuilder.INSTANCE;
        }
    }

    static final class TimestampBuilderImpl
            implements TimestampBuilder {

        private final EncoderContext encoderContext;

        TimestampBuilderImpl(EncoderContext encoderContext) {
            this.encoderContext = encoderContext;
        }

        @Override
        public TagBuilder putTimestamp(Timestamp timestamp) {
            return putTimestamp(timestamp.toInstant());
        }

        @Override
        public TagBuilder putTimestamp(Instant timestamp) {
            return putTimestamp(timestamp.getEpochSecond());
        }

        @Override
        public TagBuilder putTimestamp(long timestamp) {
            encoderContext.encode(offset -> Encoder.putSemanticTag(TAG_TIMESTAMP, offset, encoderContext.output()));
            encoderContext.encode(offset -> Encoder.putNumber(timestamp, offset, encoderContext.output()));
            return EmptyTagBuilder.INSTANCE;
        }
    }

    static final class UBigNumberBuilderImpl
            implements UBigNumberBuilder {

        private final EncoderContext encoderContext;

        UBigNumberBuilderImpl(EncoderContext encoderContext) {
            this.encoderContext = encoderContext;

        }

        @Override
        public TagBuilder putNumber(BigInteger value) {
            ValueValidators.isPositive(null, value);
            encoderContext.encode(offset -> Encoder.putBigInteger(value, offset, encoderContext.output()));
            return EmptyTagBuilder.INSTANCE;
        }

        @Override
        public TagBuilder putBigInteger(BigInteger value) {
            ValueValidators.isPositive(null, value);
            encoderContext.encode(offset -> Encoder.putBigInteger(value, offset, encoderContext.output()));
            return EmptyTagBuilder.INSTANCE;
        }
    }

    static final class NBigNumberBuilderImpl
            implements NBigNumberBuilder {

        private final EncoderContext encoderContext;

        NBigNumberBuilderImpl(EncoderContext encoderContext) {
            this.encoderContext = encoderContext;

        }

        @Override
        public TagBuilder putNumber(BigInteger value) {
            ValueValidators.isNegative(null, value);
            encoderContext.encode(offset -> Encoder.putNumber(value, offset, encoderContext.output()));
            return EmptyTagBuilder.INSTANCE;
        }

        @Override
        public TagBuilder putBigInteger(BigInteger value) {
            ValueValidators.isNegative(null, value);
            encoderContext.encode(offset -> Encoder.putBigInteger(value, offset, encoderContext.output()));
            return EmptyTagBuilder.INSTANCE;
        }
    }

    static final class FractionBuilderImpl
            implements FractionBuilder {

        private final EncoderContext encoderContext;

        FractionBuilderImpl(EncoderContext encoderContext) {
            this.encoderContext = encoderContext;
        }

        @Override
        public TagBuilder putFraction(BigDecimal value) {
            encoderContext.encode(offset -> Encoder.putFraction(value, offset, encoderContext.output()));
            return EmptyTagBuilder.INSTANCE;
        }
    }

    static final class URIBuilderImpl
            implements URIBuilder {

        private final EncoderContext encoderContext;

        URIBuilderImpl(EncoderContext encoderContext) {
            this.encoderContext = encoderContext;
        }

        @Override
        public TagBuilder putURI(URI value) {
            encoderContext.encode(offset -> Encoder.putUri(value, offset, encoderContext.output()));
            return EmptyTagBuilder.INSTANCE;
        }

        @Override
        public TagBuilder putURL(URL value)
                throws URISyntaxException {

            return putURI(value.toURI());
        }
    }

    static final class CBORBuilderImpl
            extends AbstractStreamValueBuilder<CBORBuilder>
            implements CBORBuilder {

        CBORBuilderImpl(EncoderContext encoderContext) {
            super(encoderContext);
            encoderContext.encode(offset -> Encoder.putSemanticTag(TAG_ENCCBOR, offset, encoderContext.output()));
        }

        @Override
        public TagBuilder endCBOR() {
            return EmptyTagBuilder.INSTANCE;
        }
    }

}
