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

import com.noctarius.borabora.Value;
import com.noctarius.borabora.spi.io.Constants;
import com.noctarius.borabora.spi.io.StringEncoders;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.function.Predicate;

enum TypeMatchers
        implements Predicate<Object> {

    ASCII((v) -> v instanceof String && StringEncoders.ASCII_ENCODER.canEncode((String) v)),

    DateTime((v) -> !Timestamp.class.isAssignableFrom(v.getClass()) //
            && (Date.class.isAssignableFrom(v.getClass()) || java.sql.Date.class.isAssignableFrom(v.getClass()))),

    Timestamp((v) -> Timestamp.class.isAssignableFrom(v.getClass()) || Instant.class.isAssignableFrom(v.getClass())),

    UBigNum((v) -> //
            BigInteger.class.isAssignableFrom(v.getClass()) && ((BigInteger) v).signum() >= 0),

    NBigNum((v) -> //
            BigInteger.class.isAssignableFrom(v.getClass()) && ((BigInteger) v).signum() < 0),

    Fraction((v) -> //
            BigDecimal.class.isAssignableFrom(v.getClass())),

    URI((v) -> URI.class.isAssignableFrom(v.getClass())),

    EncCBOR((v) -> Value.class.isAssignableFrom(v.getClass()));

    private final Predicate<Object> predicate;

    TypeMatchers(Predicate<Object> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean test(Object value) {
        return predicate.test(value);
    }

}
