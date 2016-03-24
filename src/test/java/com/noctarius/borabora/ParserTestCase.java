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
package com.noctarius.borabora;

import org.junit.Test;

import java.math.BigInteger;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

public class ParserTestCase {

    private static final TestValueCollection<Number> UINT_TEST_VALUES = new TestValueCollection<>(
            MajorType.UnsignedInteger,
            new TestValue<>(0, (byte) 0x0),
            new TestValue<>(1, (byte) 0x1),
            new TestValue<>(10, (byte) 0x0a),
            new TestValue<>(23, (byte) 0x17),
            new TestValue<>(24, (byte) 0x18, (byte) 0x18),
            new TestValue<>(25, (byte) 0x18, (byte) 0x19),
            new TestValue<>(100, (byte) 0x18, (byte) 0x64),
            new TestValue<>(1000, "1903e8"),
            new TestValue<>(1000000, "1a000f4240"),
            new TestValue<>(1000000000000L, "1b000000e8d4a51000"),
            new TestValue<>(new BigInteger("18446744073709551615"), "1bffffffffffffffff"),
            new TestValue<>(new BigInteger("18446744073709551616"), "c249010000000000000000")
    );

    @Test
    public void test_parse_majortype0_unsignedinteger() throws Exception {
        for (TestValue<Number> testValue : UINT_TEST_VALUES.getTestValues()) {
            Input input = Input.fromByteArray(testValue.getInputData());
            Parser parser = Parser.newBuilder(input).build();
            Value value = parser.read(new SequenceGraph(0));

            assertEquals(MajorType.UnsignedInteger, value.majorType());
            BigInteger expected = testValue.getExpectedValue() instanceof BigInteger
                    ? (BigInteger) testValue.getExpectedValue() : BigInteger.valueOf(testValue.getExpectedValue().longValue());

            Number result = value.uint();
            BigInteger actual = result instanceof BigInteger
                    ? (BigInteger) result : BigInteger.valueOf(result.longValue());

            System.out.println(testValue.getExpectedValue().toString());

            assertEquals(expected, actual);
        }
    }

    @Test
    public void test_parse_majortype1_signedinteger() throws Exception {
        byte[] array = new byte[]{0b001_11001, 0x0000_0001, (byte) 0b1111_0100};
        Input input = Input.fromByteArray(array);
        Parser parser = Parser.newBuilder(input).build();
        Value value = parser.read(new SequenceGraph(0));

        assertEquals(MajorType.NegativeInteger, value.majorType());
        assertEquals(-500, value.sint());
    }

    @Test
    public void test_parse_majortype2_bytestring() throws Exception {
        byte[] array = new byte[]{0b010_00101, (byte) 0xaa, (byte) 0xbb, (byte) 0xcc, (byte) 0xdd, (byte) 0xee};
        String expected = new String(new byte[]{(byte) 0xaa, (byte) 0xbb, (byte) 0xee, (byte) 0xdd, (byte) 0xee});

        Input input = Input.fromByteArray(array);
        Parser parser = Parser.newBuilder(input).build();
        Value value = parser.read(new SequenceGraph(0));

        assertEquals(MajorType.ByteString, value.majorType());
        assertEquals(expected, value.string());
    }

    @Test
    public void test_parse_majortype3_textstring() throws Exception {
        String expected = "üäö";
        byte[] data = expected.getBytes(Charset.forName("UTF8"));
        byte[] array = new byte[7];
        array[0] = 0b011_00110;
        System.arraycopy(data, 0, array, 1, data.length);

        Input input = Input.fromByteArray(array);
        Parser parser = Parser.newBuilder(input).build();
        Value value = parser.read(new SequenceGraph(0));

        assertEquals(MajorType.TextString, value.majorType());
        assertEquals(expected, value.string());
    }

    @Test
    public void test_parse_majortype4_sequence() throws Exception {
        /*
        Input input = Input.fromByteArray(array);
        Parser parser = Parser.newParser(input);
        Value value = parser.read(new SequenceGraph(0));

        assertEquals(MajorType.TextString, value.majorType());
        assertEquals(expected, value.string());
        */
    }

    @Test
    public void test_parse_unsignedinteger_with_index() throws Exception {
        byte[] array = new byte[]{0b000_11001, 0x0000_0001, (byte) 0b1111_0100, 0b000_11001, 0x0000_0001, (byte) 0b1111_0101};
        Input input = Input.fromByteArray(array);
        Parser parser = Parser.newBuilder(input).build();
        Value value = parser.read(new SequenceGraph(1));

        assertEquals(MajorType.UnsignedInteger, value.majorType());
        assertEquals(501, value.uint());
    }

    @Test
    public void test_parse_graph_builder() throws Exception {
        byte[] array = new byte[]{0b000_11001, 0x0000_0001, (byte) 0b1111_0100, 0b000_11001, 0x0000_0001, (byte) 0b1111_0101};
        Input input = Input.fromByteArray(array);
        Parser parser = Parser.newBuilder(input).build();

        Graph graph = Graph.newBuilder().sequence(1).build();
        Value value = parser.read(graph);

        assertEquals(MajorType.UnsignedInteger, value.majorType());
        assertEquals(501, value.uint());
    }

    @Test
    public void test_parse_indefinite_string() throws Exception {
        byte[] array = new byte[]{0b010_11111, 0b010_00100, (byte) 0xaa, (byte) 0xbb, (byte) 0xcc, (byte) 0xdd, 0b010_00011,
                (byte) 0xee, (byte) 0xff, (byte) 0x99, (byte) 0b111_11111};

        String expected = new String(new byte[]{(byte) 0xaa, (byte) 0xbb, (byte) 0xcc, (byte) 0xdd, (byte) 0xee, (byte) 0xff,
                (byte) 0x99});

        Input input = Input.fromByteArray(array);
        Parser parser = Parser.newBuilder(input).build();

        Value value = parser.read(new SequenceGraph(0));

        assertEquals(expected, value.string());
    }


}
