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

import com.noctarius.borabora.Output;
import com.noctarius.borabora.spi.Constants;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;

public enum TagWriters
        implements TagWriter<Object> {

    DateTime((value, offset, encoderContext) -> {
        Output output = encoderContext.output();
        Instant instant = ((Date) value).toInstant();
        ZonedDateTime zonedDateTime = instant.atZone(Constants.UTC);
        return Encoder.putDateTime(zonedDateTime, offset, output);
    }),

    BigNum((value, offset, encoderContext) -> {
        Output output = encoderContext.output();
        return Encoder.putNumber((BigInteger) value, offset, output);
    }),

    Fraction((value, offset, encoderContext) -> {
        Output output = encoderContext.output();
        return Encoder.putFraction((BigDecimal) value, offset, output);
    }),

    Timestamp((value, offset, encoderContext) -> {
        Output output = encoderContext.output();
        long epochSeconds;
        if (value instanceof Instant) {
            epochSeconds = ((Instant) value).getEpochSecond();
        } else {
            epochSeconds = ((Timestamp) value).toInstant().getEpochSecond();
        }
        return Encoder.putTimestamp(epochSeconds, offset, output);
    }),

    URI((value, offset, encoderContext) -> {
        Output output = encoderContext.output();
        return Encoder.putUri((URI) value, offset, output);
    });

    private final TagWriter<Object> tagWriter;

    TagWriters(TagWriter<Object> tagWriter) {
        this.tagWriter = tagWriter;
    }

    @Override
    public long process(Object value, long offset, EncoderContext encoderContext) {
        return tagWriter.process(value, offset, encoderContext);
    }

}
