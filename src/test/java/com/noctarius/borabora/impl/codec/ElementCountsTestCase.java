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
package com.noctarius.borabora.impl.codec;

import com.noctarius.borabora.AbstractTestCase;
import com.noctarius.borabora.Input;
import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.Output;
import com.noctarius.borabora.spi.io.ElementCounts;
import com.noctarius.borabora.spi.io.Encoder;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

import static com.noctarius.borabora.spi.io.Constants.BI_VAL_MAX_VALUE;
import static com.noctarius.borabora.spi.io.Constants.EMPTY_BYTE_ARRAY;
import static org.junit.Assert.assertEquals;

public class ElementCountsTestCase
        extends AbstractTestCase {

    @Test
    public void call_constructor() {
        callConstructor(ElementCounts.class);
    }

    @Test
    public void test_sequence_elementcount_head_byte() {
        Input input = Input.fromByteArray(hexToBytes("0x81"));
        assertEquals(1, ElementCounts.sequenceElementCount(input, 0));
    }

    @Test
    public void test_sequence_elementcount_1_byte() {
        Input input = Input.fromByteArray(hexToBytes("0x98ff"));
        assertEquals(255, ElementCounts.sequenceElementCount(input, 0));
    }

    @Test
    public void test_sequence_elementcount_2_byte() {
        Input input = Input.fromByteArray(hexToBytes("0x99ffff"));
        assertEquals(65535, ElementCounts.sequenceElementCount(input, 0));
    }

    @Test
    public void test_sequence_elementcount_4_byte() {
        Input input = Input.fromByteArray(hexToBytes("0x9affffffff"));
        assertEquals(Integer.MAX_VALUE * 2L + 1, ElementCounts.sequenceElementCount(input, 0));
    }

    @Test
    public void test_sequence_elementcount_8_byte() {
        Input input = Input.fromByteArray(hexToBytes("0x9b7fffffffffffffff"));
        assertEquals(Long.MAX_VALUE, ElementCounts.sequenceElementCount(input, 0));
    }

    @Test
    public void test_bymajortype_uint() {
        assertEquals(1, ElementCounts.elementCountByMajorType(MajorType.UnsignedInteger, emptyInput(), 0));
    }

    @Test
    public void test_bymajortype_nint() {
        assertEquals(1, ElementCounts.elementCountByMajorType(MajorType.NegativeInteger, emptyInput(), 0));
    }

    @Test
    public void test_bymajortype_bytestring() {
        assertEquals(1, ElementCounts.elementCountByMajorType(MajorType.ByteString, emptyInput(), 0));
    }

    @Test
    public void test_bymajortype_textstring() {
        assertEquals(1, ElementCounts.elementCountByMajorType(MajorType.TextString, emptyInput(), 0));
    }

    @Test
    public void test_bymajortype_tag() {
        assertEquals(1, ElementCounts.elementCountByMajorType(MajorType.SemanticTag, emptyInput(), 0));
    }

    @Test
    public void test_bymajortype_float() {
        assertEquals(1, ElementCounts.elementCountByMajorType(MajorType.FloatingPointOrSimple, emptyInput(), 0));
    }

    @Test
    public void test_bymajortype_sequence_head() {
        byte[] bytes = new byte[1];
        Output output = Output.toByteArray(bytes);
        Encoder.encodeLengthAndValue(MajorType.Sequence, 1, 0, output);
        assertEquals(1, ElementCounts.elementCountByMajorType(MajorType.Sequence, input(bytes), 0));
    }

    @Test
    public void test_bymajortype_sequence_1_byte() {
        byte[] bytes = new byte[2];
        Output output = Output.toByteArray(bytes);
        Encoder.encodeLengthAndValue(MajorType.Sequence, 255, 0, output);
        assertEquals(255, ElementCounts.elementCountByMajorType(MajorType.Sequence, input(bytes), 0));
    }

    @Test
    public void test_bymajortype_sequence_2_byte() {
        byte[] bytes = new byte[3];
        Output output = Output.toByteArray(bytes);
        Encoder.encodeLengthAndValue(MajorType.Sequence, 65535, 0, output);
        assertEquals(65535, ElementCounts.elementCountByMajorType(MajorType.Sequence, input(bytes), 0));
    }

    @Test
    public void test_bymajortype_sequence_4_byte() {
        long expected = Integer.MAX_VALUE * 2L + 1;
        byte[] bytes = new byte[5];
        Output output = Output.toByteArray(bytes);
        Encoder.encodeLengthAndValue(MajorType.Sequence, expected, 0, output);
        assertEquals(expected, ElementCounts.elementCountByMajorType(MajorType.Sequence, input(bytes), 0));
    }

    @Test
    public void test_bymajortype_sequence_8_byte() {
        byte[] bytes = new byte[9];
        Output output = Output.toByteArray(bytes);
        Encoder.encodeLengthAndValue(MajorType.Sequence, Long.MAX_VALUE, 0, output);
        assertEquals(Long.MAX_VALUE, ElementCounts.elementCountByMajorType(MajorType.Sequence, input(bytes), 0));
    }

    @Test(expected = IllegalStateException.class)
    public void test_bymajortype_sequence_bignum_byte() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toOutputStream(baos);
        Encoder.encodeLengthAndValue(MajorType.Sequence, BI_VAL_MAX_VALUE.subtract(BigInteger.ONE), 0, output);
        assertEquals(1, ElementCounts.elementCountByMajorType(MajorType.Sequence, input(baos.toByteArray()), 0));
    }

    @Test
    public void test_bymajortype_dictionary_head() {
        byte[] bytes = new byte[1];
        Output output = Output.toByteArray(bytes);
        Encoder.encodeLengthAndValue(MajorType.Dictionary, 1, 0, output);
        assertEquals(1, ElementCounts.elementCountByMajorType(MajorType.Dictionary, input(bytes), 0));
    }

    @Test
    public void test_bymajortype_dictionary_1_byte() {
        byte[] bytes = new byte[2];
        Output output = Output.toByteArray(bytes);
        Encoder.encodeLengthAndValue(MajorType.Dictionary, 255, 0, output);
        assertEquals(255, ElementCounts.elementCountByMajorType(MajorType.Dictionary, input(bytes), 0));
    }

    @Test
    public void test_bymajortype_dictionary_2_byte() {
        byte[] bytes = new byte[3];
        Output output = Output.toByteArray(bytes);
        Encoder.encodeLengthAndValue(MajorType.Dictionary, 65535, 0, output);
        assertEquals(65535, ElementCounts.elementCountByMajorType(MajorType.Dictionary, input(bytes), 0));
    }

    @Test
    public void test_bymajortype_dictionary_4_byte() {
        long expected = Integer.MAX_VALUE * 2L + 1;
        byte[] bytes = new byte[5];
        Output output = Output.toByteArray(bytes);
        Encoder.encodeLengthAndValue(MajorType.Dictionary, expected, 0, output);
        assertEquals(expected, ElementCounts.elementCountByMajorType(MajorType.Dictionary, input(bytes), 0));
    }

    @Test
    public void test_bymajortype_dictionary_8_byte() {
        byte[] bytes = new byte[9];
        Output output = Output.toByteArray(bytes);
        Encoder.encodeLengthAndValue(MajorType.Dictionary, Long.MAX_VALUE, 0, output);
        assertEquals(Long.MAX_VALUE, ElementCounts.elementCountByMajorType(MajorType.Dictionary, input(bytes), 0));
    }

    @Test(expected = IllegalStateException.class)
    public void test_bymajortype_dictionary_bignum_byte() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toOutputStream(baos);
        Encoder.encodeLengthAndValue(MajorType.Dictionary, BI_VAL_MAX_VALUE.subtract(BigInteger.ONE), 0, output);
        assertEquals(1, ElementCounts.elementCountByMajorType(MajorType.Dictionary, input(baos.toByteArray()), 0));
    }

    private Input input(byte[] bytes) {
        return Input.fromByteArray(bytes);
    }

    private Input emptyInput() {
        return Input.fromByteArray(EMPTY_BYTE_ARRAY);
    }

}
