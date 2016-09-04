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
package com.noctarius.borabora.impl;

import com.noctarius.borabora.AbstractTestCase;
import com.noctarius.borabora.Input;
import com.noctarius.borabora.Parser;
import com.noctarius.borabora.Query;
import com.noctarius.borabora.QueryParserException;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.spi.query.pipeline.PipelineStage;
import com.noctarius.borabora.spi.query.pipeline.PipelineStageFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParserImplTestCase
        extends AbstractTestCase {

    @Test
    public void test_prepare_query() {
        Parser parser = Parser.newBuilder().build();
        Query query = parser.prepareQuery("#");
        assertEquals(Query.newBuilder().build(), query);
    }

    @Test(expected = QueryParserException.class)
    public void fail_prepare_query_parser_error() {
        Parser parser = Parser.newBuilder().build();
        parser.prepareQuery("1");
    }

    @Test(expected = QueryParserException.class)
    public void fail_prepare_query_other_error() {
        PipelineStageFactory mock = mock(PipelineStageFactory.class);
        when(mock.newPipelineStage(any(PipelineStage.class), any(PipelineStage.class), any())).thenThrow(new RuntimeException());

        Parser parser = Parser.newBuilder().withPipelineStageFactory(mock).build();
        parser.prepareQuery("#");
    }

    @Test
    public void test_read_single_value_consumer_multiple_results() {
        Input input = Input.fromByteArray(new byte[]{(byte) 0x1, (byte) 0x2});
        Parser parser = Parser.newBuilder().build();
        Value value = parser.read(input, Query.newBuilder().multiStream().build());
        assertEqualsNumber(1, value.number());
    }

    @Test
    public void test_read_input_query() {
        Input input = Input.fromByteArray(new byte[]{(byte) 0x1});
        Parser parser = Parser.newBuilder().build();
        Value value = parser.read(input, Query.newBuilder().build());
        assertEqualsNumber(1, value.number());
    }

    @Test
    public void test_read_input_query_string() {
        Input input = Input.fromByteArray(new byte[]{(byte) 0x1});
        Parser parser = Parser.newBuilder().build();
        Value value = parser.read(input, "#");
        assertEqualsNumber(1, value.number());
    }

    @Test
    public void test_read_input_offset() {
        Input input = Input.fromByteArray(new byte[]{(byte) 0x1});
        Parser parser = Parser.newBuilder().build();
        Value value = parser.read(input, 0);
        assertEqualsNumber(1, value.number());
    }

    @Test
    public void test_read_input_query_consumer() {
        Input input = Input.fromByteArray(new byte[]{(byte) 0x1});
        Parser parser = Parser.newBuilder().build();
        List<Value> values = new ArrayList<>();
        parser.read(input, Query.newBuilder().build(), values::add);
        assertEquals(1, values.size());
        assertEqualsNumber(1, values.get(0).number());
    }

    @Test
    public void test_read_input_query_string_consumer() {
        Input input = Input.fromByteArray(new byte[]{(byte) 0x1});
        Parser parser = Parser.newBuilder().build();
        List<Value> values = new ArrayList<>();
        parser.read(input, "#", values::add);
        assertEquals(1, values.size());
        assertEqualsNumber(1, values.get(0).number());
    }

    @Test
    public void test_extract_input_query() {
        byte[] bytes = new byte[]{(byte) 0x1, (byte) 0x2};
        byte[] expected1 = new byte[]{(byte) 0x1};
        byte[] expected2 = new byte[]{(byte) 0x2};
        Input input = Input.fromByteArray(bytes);
        Parser parser = Parser.newBuilder().build();
        byte[] actual1 = parser.extract(input, Query.newBuilder().stream(0).build());
        byte[] actual2 = parser.extract(input, Query.newBuilder().stream(1).build());
        assertArrayEquals(expected1, actual1);
        assertArrayEquals(expected2, actual2);
    }

    @Test
    public void test_extract_input_query_string() {
        byte[] bytes = new byte[]{(byte) 0x1, (byte) 0x2};
        byte[] expected1 = new byte[]{(byte) 0x1};
        byte[] expected2 = new byte[]{(byte) 0x2};
        Input input = Input.fromByteArray(bytes);
        Parser parser = Parser.newBuilder().build();
        byte[] actual1 = parser.extract(input, "#");
        byte[] actual2 = parser.extract(input, "#1");
        assertArrayEquals(expected1, actual1);
        assertArrayEquals(expected2, actual2);
    }

    @Test
    public void test_extract_input_offset() {
        byte[] bytes = new byte[]{(byte) 0x1, (byte) 0x2};
        byte[] expected1 = new byte[]{(byte) 0x1};
        byte[] expected2 = new byte[]{(byte) 0x2};
        Input input = Input.fromByteArray(bytes);
        Parser parser = Parser.newBuilder().build();
        byte[] actual1 = parser.extract(input, 0);
        byte[] actual2 = parser.extract(input, 1);
        assertArrayEquals(expected1, actual1);
        assertArrayEquals(expected2, actual2);
    }

}
