/*
 * Copyright (c) 2016-2018, Christoph Engelbert (aka noctarius) and
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

import com.noctarius.borabora.builder.encoder.semantictag.AsciiStringBuilder;
import com.noctarius.borabora.builder.encoder.semantictag.CBORBuilder;
import com.noctarius.borabora.builder.encoder.semantictag.DateTimeBuilder;
import com.noctarius.borabora.builder.encoder.semantictag.FractionBuilder;
import com.noctarius.borabora.builder.encoder.semantictag.NBigNumberBuilder;
import com.noctarius.borabora.builder.encoder.semantictag.TimestampBuilder;
import com.noctarius.borabora.builder.encoder.semantictag.UBigNumberBuilder;
import com.noctarius.borabora.builder.encoder.semantictag.URIBuilder;
import com.noctarius.borabora.spi.ValueValidators;
import com.noctarius.borabora.spi.builder.AbstractStreamValueBuilder;
import com.noctarius.borabora.spi.builder.EncoderContext;
import com.noctarius.borabora.spi.builder.TagBuilder;
import com.noctarius.borabora.spi.builder.TagBuilderConsumer;
import com.noctarius.borabora.spi.io.Encoder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

import static com.noctarius.borabora.spi.io.Constants.TAG_ENCCBOR;
import static com.noctarius.borabora.spi.io.Constants.TAG_TIMESTAMP;
import static com.noctarius.borabora.spi.io.Constants.UTC;

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

    static final class AsciiStringBuilderImpl
            implements AsciiStringBuilder {

        private final EncoderContext encoderContext;

        AsciiStringBuilderImpl(EncoderContext encoderContext) {
            Objects.requireNonNull(encoderContext, "encoderContext must not be null");
            this.encoderContext = encoderContext;
        }

        @Override
        public TagBuilder putAsciiString(String value) {
            encoderContext.encodeNullOrType(value, ((offset, output) -> Encoder.putAsciiString(value, offset, output)));
            return EmptyTagBuilder.INSTANCE;
        }
    }

    static final class DateTimeBuilderImpl
            implements DateTimeBuilder {

        private final EncoderContext encoderContext;

        DateTimeBuilderImpl(EncoderContext encoderContext) {
            Objects.requireNonNull(encoderContext, "encoderContext must not be null");
            this.encoderContext = encoderContext;
        }

        @Override
        public TagBuilder putDateTime(Date date) {
            return putDateTime(date == null ? null : date.toInstant());
        }

        @Override
        public TagBuilder putDateTime(Instant instant) {
            encoderContext.encodeNullOrType(instant, //
                    (offset, output) -> Encoder.putDateTime(instant.atZone(UTC), offset, output));
            return EmptyTagBuilder.INSTANCE;
        }
    }

    static final class TimestampBuilderImpl
            implements TimestampBuilder {

        private final EncoderContext encoderContext;

        TimestampBuilderImpl(EncoderContext encoderContext) {
            Objects.requireNonNull(encoderContext, "encoderContext must not be null");
            this.encoderContext = encoderContext;
        }

        @Override
        public TagBuilder putTimestamp(Timestamp timestamp) {
            if (timestamp == null) {
                encoderContext.encodeNull();
                return EmptyTagBuilder.INSTANCE;
            }
            return putTimestamp(timestamp.toInstant());
        }

        @Override
        public TagBuilder putTimestamp(Instant timestamp) {
            if (timestamp == null) {
                encoderContext.encodeNull();
                return EmptyTagBuilder.INSTANCE;
            }
            return putTimestamp(timestamp.getEpochSecond());
        }

        @Override
        public TagBuilder putTimestamp(long timestamp) {
            encoderContext.encode((offset, output) -> Encoder.putSemanticTag(TAG_TIMESTAMP, offset, output));
            encoderContext.encode((offset, output) -> Encoder.putNumber(timestamp, offset, output));
            return EmptyTagBuilder.INSTANCE;
        }
    }

    static final class UBigNumberBuilderImpl
            implements UBigNumberBuilder {

        private final EncoderContext encoderContext;

        UBigNumberBuilderImpl(EncoderContext encoderContext) {
            Objects.requireNonNull(encoderContext, "encoderContext must not be null");
            this.encoderContext = encoderContext;
        }

        @Override
        public TagBuilder putBigInteger(BigInteger value) {
            if (value == null) {
                encoderContext.encodeNull();
            } else {
                ValueValidators.isPositive(null, value);
                encoderContext.encode((offset, output) -> Encoder.putBigInteger(value, offset, output));
            }
            return EmptyTagBuilder.INSTANCE;
        }
    }

    static final class NBigNumberBuilderImpl
            implements NBigNumberBuilder {

        private final EncoderContext encoderContext;

        NBigNumberBuilderImpl(EncoderContext encoderContext) {
            Objects.requireNonNull(encoderContext, "encoderContext must not be null");
            this.encoderContext = encoderContext;
        }

        @Override
        public TagBuilder putBigInteger(BigInteger value) {
            if (value == null) {
                encoderContext.encodeNull();
            } else {
                ValueValidators.isNegative(null, value);
                encoderContext.encode((offset, output) -> Encoder.putBigInteger(value, offset, output));
            }
            return EmptyTagBuilder.INSTANCE;
        }
    }

    static final class FractionBuilderImpl
            implements FractionBuilder {

        private final EncoderContext encoderContext;

        FractionBuilderImpl(EncoderContext encoderContext) {
            Objects.requireNonNull(encoderContext, "encoderContext must not be null");
            this.encoderContext = encoderContext;
        }

        @Override
        public TagBuilder putFraction(BigDecimal value) {
            encoderContext.encodeNullOrType(value, (offset, output) -> Encoder.putFraction(value, offset, output));
            return EmptyTagBuilder.INSTANCE;
        }
    }

    static final class URIBuilderImpl
            implements URIBuilder {

        private final EncoderContext encoderContext;

        URIBuilderImpl(EncoderContext encoderContext) {
            Objects.requireNonNull(encoderContext, "encoderContext must not be null");
            this.encoderContext = encoderContext;
        }

        @Override
        public TagBuilder putURI(URI value) {
            encoderContext.encodeNullOrType(value, (offset, output) -> Encoder.putUri(value, offset, output));
            return EmptyTagBuilder.INSTANCE;
        }

        @Override
        public TagBuilder putURL(URL value)
                throws URISyntaxException {

            return putURI(value == null ? null : value.toURI());
        }
    }

    static final class CBORBuilderImpl
            extends AbstractStreamValueBuilder<CBORBuilder>
            implements CBORBuilder {

        CBORBuilderImpl(EncoderContext encoderContext) {
            super(encoderContext);
            encoderContext.encode((offset, output) -> Encoder.putSemanticTag(TAG_ENCCBOR, offset, output));
        }

        @Override
        public TagBuilder endCBOR() {
            return EmptyTagBuilder.INSTANCE;
        }
    }

}
