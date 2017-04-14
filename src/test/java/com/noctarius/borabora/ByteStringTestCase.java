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

import static org.junit.Assert.assertEquals;

public class ByteStringTestCase
        extends AbstractTestCase {

    private static final TestValueCollection<String> BYTE_STRING_TEST_VALUES = new TestValueCollection<>(
            new TestValue<>("", "0x40"), new TestValue<>("a", "0x4161"), new TestValue<>("IETF", "0x4449455446"),
            new TestValue<>("\"\\", "0x42225c"));

    private static final Charset ASCII = Charset.forName("ASCII");

    @Test
    public void test_byte_string()
            throws Exception {

        String expected = new String(hexToBytes("0x01020304"), ASCII);
        SimplifiedTestParser parser = buildParser("0x4401020304");
        Value value = parser.read(parser.newQueryBuilder().build());
        assertEquals(expected, value.string());
    }

    @Test
    public void test_indefinite_byte_string_1()
            throws Exception {

        String expected = new String(hexToBytes("0x0102030405"), ASCII);
        SimplifiedTestParser parser = buildParser("0x5f42010243030405ff");
        Value value = parser.read(parser.newQueryBuilder().build());
        assertEquals(expected, value.string());
    }

    @Test
    public void test_indefinite_byte_string_2()
            throws Exception {

        String expected = new String(hexToBytes("0xaabbccddeeff99"), ASCII);
        SimplifiedTestParser parser = buildParser("0x5f44aabbccdd43eeff99ff");
        Value value = parser.read(parser.newQueryBuilder().build());
        assertEquals(expected, value.string());
    }

    @Test
    public void test_parse_majortype2_bytestring()
            throws Exception {

        for (TestValue<String> testValue : BYTE_STRING_TEST_VALUES.getTestValues()) {
            testString(ValueTypes.ByteString, testValue);
        }
    }

    @Test
    public void test_multi_indefinite_bytestreams() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toOutputStream(baos);
        Writer writer = Writer.newWriter();
        writer.newGraphBuilder(output)

              .putIndefiniteAsciiString().putString("ab").putString("cd").endIndefiniteString()

              .putIndefiniteAsciiString().putString("ef").putString("gh").endIndefiniteString()

              .finishStream();

        Input input = Input.fromByteArray(baos.toByteArray());
        Parser parser = Parser.newParser();

        Value value1 = parser.read(input, parser.newQueryBuilder().stream(0).build());
        Value value2 = parser.read(input, parser.newQueryBuilder().stream(1).build());

        assertEquals("abcd", value1.string());
        assertEquals("efgh", value2.string());
    }

    private void testString(ValueType valueType, TestValue<String> testValue) {
        Input input = Input.fromByteArray(testValue.getValue2());
        Parser parser = Parser.newParser();
        Value value = parser.read(input, parser.newQueryBuilder().build());

        assertEquals(valueType, value.valueType());
        assertEquals(testValue.getValue1(), value.string());
    }

}
