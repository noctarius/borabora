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

import static org.junit.Assert.assertEquals;

public class StreamingParserTestCase
        extends AbstractTestCase {

    @Test
    public void test_parse_majortype1_signedinteger()
            throws Exception {

        byte[] array = new byte[]{0b001_11001, 0x0000_0001, (byte) 0b1111_0011};
        Input input = Input.fromByteArray(array);
        Parser parser = Parser.newBuilder().build();
        Value value = parser.read(input, parser.newQueryBuilder().stream(0).build());

        assertEquals(MajorType.NegativeInteger, value.majorType());
        assertEqualsNumber(-500, value.number());
    }

    @Test
    public void test_parse_unsignedinteger_with_index()
            throws Exception {

        byte[] array = new byte[]{0b000_11001, 0x0000_0001, (byte) 0b1111_0100, 0b000_11001, 0x0000_0001, (byte) 0b1111_0101};
        Input input = Input.fromByteArray(array);
        Parser parser = Parser.newBuilder().build();
        Value value = parser.read(input, parser.newQueryBuilder().stream(1).build());

        assertEquals(MajorType.UnsignedInteger, value.majorType());
        assertEqualsNumber(501, value.number());
    }

    @Test
    public void test_parse_graph_builder()
            throws Exception {

        byte[] array = new byte[]{0b000_11001, 0x0000_0001, (byte) 0b1111_0100, 0b000_11001, 0x0000_0001, (byte) 0b1111_0101};
        Input input = Input.fromByteArray(array);
        Parser parser = Parser.newBuilder().build();

        Query query = parser.newQueryBuilder().stream(1).build();
        Value value = parser.read(input, query);

        assertEquals(MajorType.UnsignedInteger, value.majorType());
        assertEqualsNumber(501, value.number());
    }

}
