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
import java.net.URI;

import static org.junit.Assert.assertEquals;

public class ParserTestCase
        extends AbstractTestCase {

    private static final TestValueCollection<String> SPECIAL_TEST_VALUES = new TestValueCollection<>(
            new TestValue<>("1(1363896240)", "c11a514b67b0"), new TestValue<>("1(1363896240.5)", "c1fb41d452d9ec200000"),
            new TestValue<>("23(h'01020304')", "d74401020304"), new TestValue<>("24(h'6449455446')", "d818456449455446"),
            new TestValue<>("32(\"http://www.example.com\")", "d82076687474703a2f2f7777772e6578616d706c652e636f6d"));

    @Test
    public void test_parse_majortype1_signedinteger()
            throws Exception {

        byte[] array = new byte[]{0b001_11001, 0x0000_0001, (byte) 0b1111_0011};
        Input input = Input.fromByteArray(array);
        Parser parser = Parser.newBuilder(input).build();
        Value value = parser.read(new SequenceGraphQuery(0));

        assertEquals(MajorType.NegativeInteger, value.majorType());
        assertEqualsNumber(-500, value.number());
    }

    @Test
    public void test_parse_unsignedinteger_with_index()
            throws Exception {

        byte[] array = new byte[]{0b000_11001, 0x0000_0001, (byte) 0b1111_0100, 0b000_11001, 0x0000_0001, (byte) 0b1111_0101};
        Input input = Input.fromByteArray(array);
        Parser parser = Parser.newBuilder(input).build();
        Value value = parser.read(new SequenceGraphQuery(1));

        assertEquals(MajorType.UnsignedInteger, value.majorType());
        assertEqualsNumber(501, value.number());
    }

    @Test
    public void test_parse_graph_builder()
            throws Exception {

        byte[] array = new byte[]{0b000_11001, 0x0000_0001, (byte) 0b1111_0100, 0b000_11001, 0x0000_0001, (byte) 0b1111_0101};
        Input input = Input.fromByteArray(array);
        Parser parser = Parser.newBuilder(input).build();

        GraphQuery graphQuery = GraphQuery.newBuilder().sequence(1).build();
        Value value = parser.read(graphQuery);

        assertEquals(MajorType.UnsignedInteger, value.majorType());
        assertEqualsNumber(501, value.number());
    }

}
