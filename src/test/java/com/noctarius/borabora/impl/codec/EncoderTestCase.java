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
import com.noctarius.borabora.NoSuchByteException;
import com.noctarius.borabora.Output;
import com.noctarius.borabora.spi.io.Encoder;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertArrayEquals;

public class EncoderTestCase
        extends AbstractTestCase {

    @Test
    public void call_constructor() {
        callConstructor(Encoder.class);
    }

    @Test
    public void test_put_boolean_value_true() {
        byte[] result = new byte[1];
        Output output = Output.toByteArray(result);
        Encoder.putBoolean(true, 0, output);
        assertArrayEquals(hexToBytes("0xf5"), result);
    }

    @Test
    public void test_put_boolean_value_false() {
        byte[] result = new byte[1];
        Output output = Output.toByteArray(result);
        Encoder.putBoolean(false, 0, output);
        assertArrayEquals(hexToBytes("0xf4"), result);
    }

    @Test
    public void test_put_uint_head_byte() {
        byte[] result = new byte[1];
        Output output = Output.toByteArray(result);
        Encoder.putNumber(1, 0, output);
        assertArrayEquals(hexToBytes("0x01"), result);
    }

    @Test
    public void test_put_uint_1_byte() {
        byte[] result = new byte[2];
        Output output = Output.toByteArray(result);
        Encoder.putNumber(Byte.MAX_VALUE, 0, output);
        assertArrayEquals(hexToBytes("0x187f"), result);
    }

    @Test
    public void test_read_uint_2_byte() {
        byte[] result = new byte[3];
        Output output = Output.toByteArray(result);
        Encoder.putNumber(Short.MAX_VALUE, 0, output);
        assertArrayEquals(hexToBytes("0x197fff"), result);
    }

    @Test
    public void test_read_uint_4_byte() {
        byte[] result = new byte[5];
        Output output = Output.toByteArray(result);
        Encoder.putNumber(Integer.MAX_VALUE, 0, output);
        assertArrayEquals(hexToBytes("0x1a7fffffff"), result);
    }

    @Test
    public void test_read_uint_8_byte() {
        byte[] result = new byte[9];
        Output output = Output.toByteArray(result);
        Encoder.putNumber(Long.MAX_VALUE, 0, output);
        assertArrayEquals(hexToBytes("0x1b7fffffffffffffff"), result);
    }

    @Test
    public void test_put_biguint_head_byte() {
        byte[] result = new byte[1];
        Output output = Output.toByteArray(result);
        Encoder.putNumber(BigInteger.ONE, 0, output);
        assertArrayEquals(hexToBytes("0x01"), result);
    }

    @Test
    public void test_put_biguint_1_byte() {
        byte[] result = new byte[2];
        Output output = Output.toByteArray(result);
        Encoder.putNumber(BigInteger.valueOf(Byte.MAX_VALUE), 0, output);
        assertArrayEquals(hexToBytes("0x187f"), result);
    }

    @Test
    public void test_read_biguint_2_byte() {
        byte[] result = new byte[3];
        Output output = Output.toByteArray(result);
        Encoder.putNumber(BigInteger.valueOf(Short.MAX_VALUE), 0, output);
        assertArrayEquals(hexToBytes("0x197fff"), result);
    }

    @Test
    public void test_read_biguint_4_byte() {
        byte[] result = new byte[5];
        Output output = Output.toByteArray(result);
        Encoder.putNumber(BigInteger.valueOf(Integer.MAX_VALUE), 0, output);
        assertArrayEquals(hexToBytes("0x1a7fffffff"), result);
    }

    @Test
    public void test_read_biguint_8_byte() {
        byte[] result = new byte[9];
        Output output = Output.toByteArray(result);
        Encoder.putNumber(BigInteger.valueOf(Long.MAX_VALUE), 0, output);
        assertArrayEquals(hexToBytes("0x1b7fffffffffffffff"), result);
    }

    @Test(expected = NoSuchByteException.class)
    public void test_nosuchbyteexception() {
        Output output = Output.toByteArray(new byte[0]);
        Encoder.putNull(0, output);
    }

}
