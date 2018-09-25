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

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class TypeMatchersTestCase {

    @Test
    public void test_datetime_timestamp() {
        assertFalse(TypeMatchers.DateTime.test(new Timestamp(123)));
    }

    @Test
    public void test_datetime_date() {
        assertTrue(TypeMatchers.DateTime.test(new Date()));
    }

    @Test
    public void test_datetime_sqldate() {
        assertTrue(TypeMatchers.DateTime.test(new java.sql.Date(123)));
    }

    @Test
    public void test_timestamp_timestamp() {
        assertTrue(TypeMatchers.Timestamp.test(new Timestamp(123)));
    }

    @Test
    public void test_timestamp_instant() {
        assertTrue(TypeMatchers.Timestamp.test(Instant.now()));
    }

    @Test
    public void test_ubignum_positive() {
        assertTrue(TypeMatchers.UBigNum.test(BigInteger.ONE));
    }

    @Test
    public void test_ubignum_negative() {
        assertFalse(TypeMatchers.UBigNum.test(BigInteger.valueOf(-1)));
    }

    @Test
    public void test_nbignum_positive() {
        assertFalse(TypeMatchers.NBigNum.test(BigInteger.ONE));
    }

    @Test
    public void test_nbignum_negative() {
        assertTrue(TypeMatchers.NBigNum.test(BigInteger.valueOf(-1)));
    }

    @Test
    public void test_fraction() {
        assertTrue(TypeMatchers.Fraction.test(BigDecimal.valueOf(-12.d)));
    }

    @Test
    public void test_uri()
            throws Exception {

        assertTrue(TypeMatchers.URI.test(new URI("www.noctarius.com")));
    }

    @Test
    public void test_enccbor() {
        assertFalse(TypeMatchers.EncCBOR.test(new Object()));
    }

}
