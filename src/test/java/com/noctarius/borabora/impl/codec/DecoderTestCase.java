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
package com.noctarius.borabora.impl.codec;

import com.noctarius.borabora.AbstractTestCase;
import com.noctarius.borabora.Input;
import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.NoSuchByteException;
import com.noctarius.borabora.Output;
import com.noctarius.borabora.spi.Constants;
import com.noctarius.borabora.spi.codec.Decoder;
import com.noctarius.borabora.spi.codec.Encoder;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DecoderTestCase
        extends AbstractTestCase {

    @Test
    public void call_constructor() {
        callConstructor(Decoder.class);
    }

    @Test(expected = IllegalStateException.class)
    public void fail_readsemantictagid_ubignum() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toOutputStream(baos);
        Encoder.encodeLengthAndValue(MajorType.SemanticTag, Constants.BI_VAL_MAX_VALUE.subtract(BigInteger.ONE), 0, output);

        Input input = Input.fromByteArray(baos.toByteArray());
        Decoder.readSemanticTagId(input, 0);
    }

    @Test(expected = IllegalStateException.class)
    public void fail_readsemantictagid_larger_integer_maxvalue() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toOutputStream(baos);
        Encoder.encodeLengthAndValue(MajorType.SemanticTag, Integer.MAX_VALUE + 1L, 0, output);

        Input input = Input.fromByteArray(baos.toByteArray());
        Decoder.readSemanticTagId(input, 0);
    }

    @Test(expected = IllegalStateException.class)
    public void fail_illegal_boolean_value() {
        Input input = Input.fromByteArray(hexToBytes("0xf6"));
        Decoder.getBooleanValue(input, 0);
    }

    @Test(expected = IllegalStateException.class)
    public void fail_illegal_boolean_value_wrong_major() {
        Input input = Input.fromByteArray(hexToBytes("0x06"));
        Decoder.getBooleanValue(input, 0);
    }

    @Test(expected = IllegalStateException.class)
    public void fail_illegal_floatingpoint_type() {
        Input input = Input.fromByteArray(hexToBytes("0xf6"));
        Decoder.readFloat(input, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_readfraction_non_fraction_encountered() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toOutputStream(baos);

        long offset = Encoder.encodeLengthAndValue(MajorType.SemanticTag, Constants.TAG_FRACTION, 0, output);
        Encoder.encodeLengthAndValue(MajorType.Sequence, 1, offset, output);

        Input input = Input.fromByteArray(baos.toByteArray());
        Decoder.readFraction(0, newQueryContext(input));
    }

    @Test
    public void test_readfraction_biginteger_unscaled() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toOutputStream(baos);

        long offset = Encoder.encodeLengthAndValue(MajorType.SemanticTag, Constants.TAG_FRACTION, 0, output);
        offset = Encoder.encodeLengthAndValue(MajorType.Sequence, 2, offset, output);
        offset = Encoder.encodeLengthAndValue(MajorType.UnsignedInteger, 1, offset, output);
        Encoder.putBigInteger(BigInteger.valueOf(120), offset, output);

        Input input = Input.fromByteArray(baos.toByteArray());
        BigDecimal fraction = Decoder.readFraction(0, newQueryContext(input));
        assertEquals(BigDecimal.valueOf(12.0d), fraction);
    }

    @Test(expected = IllegalStateException.class)
    public void fail_calculate_length_illegal_major_type() {
        Decoder.length(null, MajorType.Unknown, 0);
    }

    @Test
    public void test_read_uint_head_byte() {
        Input input = Input.fromByteArray(hexToBytes("0x01"));
        assertEqualsNumber(1, Decoder.readUint(input, 0));
    }

    @Test
    public void test_read_uint_1_byte() {
        Input input = Input.fromByteArray(hexToBytes("0x187f"));
        assertEqualsNumber(Byte.MAX_VALUE, Decoder.readUint(input, 0));
    }

    @Test
    public void test_read_uint_2_byte() {
        Input input = Input.fromByteArray(hexToBytes("0x197fff"));
        assertEqualsNumber(Short.MAX_VALUE, Decoder.readUint(input, 0));
    }

    @Test
    public void test_read_uint_4_byte() {
        Input input = Input.fromByteArray(hexToBytes("0x1a7fffffff"));
        assertEqualsNumber(Integer.MAX_VALUE, Decoder.readUint(input, 0));
    }

    @Test
    public void test_read_uint_8_byte() {
        Input input = Input.fromByteArray(hexToBytes("0x1b7fffffffffffffff"));
        assertEqualsNumber(Long.MAX_VALUE, Decoder.readUint(input, 0));
    }

    @Test(expected = NoSuchByteException.class)
    public void fail_nosuchbyteexception() {
        Input input = Input.fromByteArray(new byte[0]);
        Decoder.additionalInfo(input, 0);
    }

    @Test
    public void test_nosuchbyteexception_offset() {
        Input input = Input.fromByteArray(new byte[0]);
        try {
            Decoder.additionalInfo(input, 0);
            fail();
        } catch (NoSuchByteException e) {
            assertEquals(0, e.getOffset());
        }
    }

    @Test
    public void test_T_Z() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2003, 11, 13, 18, 30, 02);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.MILLISECOND, 0);
        Instant expected = calendar.toInstant();

        String date = "2003-12-13T18:30:02Z";
        Instant actual = Decoder.parseDate(date);
        assertEquals(expected, actual);
    }

    @Test
    public void test_T_Fraction_Z() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2003, 11, 13, 18, 30, 02);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.MILLISECOND, 250);
        Instant expected = calendar.toInstant();

        String date = "2003-12-13T18:30:02.25Z";
        Instant actual = Decoder.parseDate(date);
        assertEquals(expected, actual);
    }

    @Test
    public void test_T_Offset() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2003, 11, 13, 18, 30, 02);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.MILLISECOND, 0);
        Instant expected = calendar.toInstant();

        String date = "2003-12-13T18:30:02+01:00";
        Instant actual = Decoder.parseDate(date);
        assertEquals(expected, actual);
    }

    @Test
    public void test_T_Fraction_Offset() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2003, 11, 13, 18, 30, 02);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.MILLISECOND, 250);
        Instant expected = calendar.toInstant();

        String date = "2003-12-13T18:30:02.25+01:00";
        Instant actual = Decoder.parseDate(date);
        assertEquals(expected, actual);
    }

}
