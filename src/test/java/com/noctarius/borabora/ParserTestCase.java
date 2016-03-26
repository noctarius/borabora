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

import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import java.net.URI;

import static org.junit.Assert.assertEquals;

public class ParserTestCase {

    private static final TestValueCollection<Number> NUMBER_TEST_VALUES = new TestValueCollection<>(
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
            new TestValue<>(new BigInteger("18446744073709551616"), "c249010000000000000000"),
            new TestValue<>(new BigInteger("-18446744073709551616"), "3bffffffffffffffff"),
            new TestValue<>(new BigInteger("-18446744073709551617"), "c349010000000000000000"),
            new TestValue<>(-1, (byte) 0x20),
            new TestValue<>(-10, (byte) 0x29),
            new TestValue<>(-100, (byte) 0x38, (byte) 0x63),
            new TestValue<>(-1000, "3903e7")
    );

    private static final TestValueCollection<String> SPECIAL_TEST_VALUES = new TestValueCollection<>(
            new TestValue<>("1(1363896240)", "c11a514b67b0"),
            new TestValue<>("1(1363896240.5)", "c1fb41d452d9ec200000"),
            new TestValue<>("23(h'01020304')", "d74401020304"),
            new TestValue<>("24(h'6449455446')", "d818456449455446"),
            new TestValue<>("32(\"http://www.example.com\")", "d82076687474703a2f2f7777772e6578616d706c652e636f6d")
    );

    private static final TestValueCollection<String> BYTE_STRING_TEST_VALUES = new TestValueCollection<>(
            new TestValue<>("", (byte) 0x40),
            new TestValue<>("a", "4161"),
            new TestValue<>("IETF", "4449455446"),
            new TestValue<>("\"\\", "42225c")
    );

    private static final TestValueCollection<String> TEXT_STRING_TEST_VALUES = new TestValueCollection<>(
            new TestValue<>("", (byte) 0x60),
            new TestValue<>("a", "6161"),
            new TestValue<>("IETF", "6449455446"),
            new TestValue<>("\"\\", "62225c"),
            new TestValue<>("\u00fc", "62c3bc"),
            new TestValue<>("\u6c34", "63e6b0b4"),
            new TestValue<>("\ud800\udd51", "64f0908591")
    );

    @Test
    public void test_parse_majortype0_majortype1_numbers() throws Exception {
        for (TestValue<Number> testValue : NUMBER_TEST_VALUES.getTestValues()) {
            Input input = Input.fromByteArray(testValue.getInputData());
            Parser parser = Parser.newBuilder(input).build();
            Value value = parser.read(new SequenceGraph(0));

            Number result = value.number();
            assertEqualsNumber(testValue.getExpectedValue(), result);
        }
    }

    @Test
    public void test_parse_uri() throws Exception {
        byte[] data = DatatypeConverter.parseHexBinary("d82076687474703a2f2f7777772e6578616d706c652e636f6d");
        Input input = Input.fromByteArray(data);
        Parser parser = Parser.newBuilder(input).build();
        Value value = parser.read(new SequenceGraph(0));

        assertEquals(ValueTypes.URI, value.valueType());
        assertEquals(new URI("http://www.example.com"), value.tag());
    }

    @Test
    public void test_parse_majortype1_signedinteger() throws Exception {
        byte[] array = new byte[]{0b001_11001, 0x0000_0001, (byte) 0b1111_0011};
        Input input = Input.fromByteArray(array);
        Parser parser = Parser.newBuilder(input).build();
        Value value = parser.read(new SequenceGraph(0));

        assertEquals(MajorType.NegativeInteger, value.majorType());
        assertEqualsNumber(-500, value.number());
    }

    @Test
    public void test_parse_majortype2_bytestring() throws Exception {
        for (TestValue<String> testValue : BYTE_STRING_TEST_VALUES.getTestValues()) {
            testString(ValueTypes.ByteString, testValue);
        }
    }

    @Test
    public void test_parse_majortype3_textstring() throws Exception {
        for (TestValue<String> testValue : TEXT_STRING_TEST_VALUES.getTestValues()) {
            testString(ValueTypes.TextString, testValue);
        }
    }

    private void testString(ValueType valueType, TestValue<String> testValue) {
        Input input = Input.fromByteArray(testValue.getInputData());
        Parser parser = Parser.newBuilder(input).build();
        Value value = parser.read(new SequenceGraph(0));

        assertEquals(valueType, value.valueType());
        assertEquals(testValue.getExpectedValue(), value.string());
    }

    @Test
    public void test_parse_unsignedinteger_with_index() throws Exception {
        byte[] array = new byte[]{0b000_11001, 0x0000_0001, (byte) 0b1111_0100, 0b000_11001, 0x0000_0001, (byte) 0b1111_0101};
        Input input = Input.fromByteArray(array);
        Parser parser = Parser.newBuilder(input).build();
        Value value = parser.read(new SequenceGraph(1));

        assertEquals(MajorType.UnsignedInteger, value.majorType());
        assertEqualsNumber(501, value.number());
    }

    @Test
    public void test_parse_graph_builder() throws Exception {
        byte[] array = new byte[]{0b000_11001, 0x0000_0001, (byte) 0b1111_0100, 0b000_11001, 0x0000_0001, (byte) 0b1111_0101};
        Input input = Input.fromByteArray(array);
        Parser parser = Parser.newBuilder(input).build();

        Graph graph = Graph.newBuilder().sequence(1).build();
        Value value = parser.read(graph);

        assertEquals(MajorType.UnsignedInteger, value.majorType());
        assertEqualsNumber(501, value.number());
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

    @Test
    public void test_parse_majortype4_sequence() throws Exception {
        byte[] data = DatatypeConverter.parseHexBinary("8301820203820405");

        Input input = Input.fromByteArray(data);
        Parser parser = Parser.newBuilder(input).build();

        Value value = parser.read(new SequenceGraph(-1));

        SequenceImpl sequence = value.sequence();
        assertEqualsNumber(1, sequence.get(0).number());
    }

    private void assertEqualsNumber(Number n1, Number n2) {
        if (n1.getClass().equals(n2.getClass())) {
            assertEquals(n1, n2);
        }

        BigInteger b1 = n1 instanceof BigInteger ? (BigInteger) n1 : BigInteger.valueOf(n1.longValue());
        BigInteger b2 = n2 instanceof BigInteger ? (BigInteger) n2 : BigInteger.valueOf(n2.longValue());
        assertEquals(b1, b2);
    }

}
