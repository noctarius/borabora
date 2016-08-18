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
import com.noctarius.borabora.spi.codec.Decoder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DecoderTestCase
        extends AbstractTestCase {

    @Test
    public void call_constructor() {
        callConstructor(Decoder.class);
    }

    @Test(expected = IllegalStateException.class)
    public void fail_illegal_boolean_value() {
        Input input = Input.fromByteArray(hexToBytes("0xf6"));
        Decoder.getBooleanValue(input, 0);
    }

    @Test(expected = IllegalStateException.class)
    public void fail_illegal_floatingpoint_type() {
        Input input = Input.fromByteArray(hexToBytes("0xf6"));
        Decoder.readFloat(input, 0);
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

}
