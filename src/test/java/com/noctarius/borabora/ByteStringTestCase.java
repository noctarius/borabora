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

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ByteStringTestCase
        extends AbstractTestCase {

    private static final Charset ASCII = Charset.forName("ASCII");

    private static final TestValueCollection<byte[]> BYTE_STRING_TEST_VALUES = new TestValueCollection<>(
            new TestValue<>("".getBytes(ASCII), "0x40"), //
            new TestValue<>("a".getBytes(ASCII), "0x4161"), //
            new TestValue<>("IETF".getBytes(ASCII), "0x4449455446"), //
            new TestValue<>("\"\\".getBytes(ASCII), "0x42225c"));

    @Test
    public void test_byte_string()
            throws Exception {

        byte[] expected = hexToBytes("0x01020304");
        SimplifiedTestParser parser = buildParser("0x4401020304");
        Value value = parser.read(parser.newQueryBuilder().build());
        assertArrayEquals(expected, value.bytes());
    }

    @Test
    public void test_indefinite_byte_string_1()
            throws Exception {

        byte[] expected = hexToBytes("0x0102030405");
        SimplifiedTestParser parser = buildParser("0x5f42010243030405ff");
        Value value = parser.read(parser.newQueryBuilder().build());
        assertArrayEquals(expected, value.bytes());
    }

    @Test
    public void test_indefinite_byte_string_2()
            throws Exception {

        byte[] expected = hexToBytes("0xaabbccddeeff99");
        SimplifiedTestParser parser = buildParser("0x5f44aabbccdd43eeff99ff");
        Value value = parser.read(parser.newQueryBuilder().build());
        assertArrayEquals(expected, value.bytes());
    }

    @Test
    public void test_parse_majortype2_bytestring()
            throws Exception {

        for (TestValue<byte[]> testValue : BYTE_STRING_TEST_VALUES.getTestValues()) {
            testByteString(ValueTypes.ByteString, testValue);
        }
    }

    @Test
    public void test_multi_indefinite_bytestreams() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toOutputStream(baos);
        Writer writer = Writer.newWriter();
        writer.newGraphBuilder(output)

              .putIndefiniteByteString() //
              .putByteString(bytes("ab")).putByteString(bytes("cd")).endIndefiniteByteString()

              .putIndefiniteByteString() //
              .putByteString(bytes("ef")).putByteString(bytes("gh")).endIndefiniteByteString()

              .finishStream();

        Input input = Input.fromByteArray(baos.toByteArray());
        Parser parser = Parser.newParser();

        Value value1 = parser.read(input, parser.newQueryBuilder().stream(0).build());
        Value value2 = parser.read(input, parser.newQueryBuilder().stream(1).build());

        assertArrayEquals(bytes("abcd"), value1.bytes());
        assertArrayEquals(bytes("efgh"), value2.bytes());
    }

    private void testByteString(ValueType valueType, TestValue<byte[]> testValue) {
        Input input = Input.fromByteArray(testValue.getValue2());
        Parser parser = Parser.newParser();
        Value value = parser.read(input, parser.newQueryBuilder().build());

        assertEquals(valueType, value.valueType());
        assertArrayEquals(testValue.getValue1(), value.bytes());
    }

    private static byte[] bytes(String value) {
        return value.getBytes(ASCII);
    }

}
