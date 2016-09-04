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

import com.noctarius.borabora.builder.encoder.GraphBuilder;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class QueryLanguageTestCase
        extends AbstractTestCase {

    // (foo: #{'b'}, bar: #{'c'})

    @Test
    public void test_any_stream_element()
            throws Exception {

        Writer writer = Writer.newBuilder().build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GraphBuilder graphBuilder = writer.newGraphBuilder(Output.toOutputStream(baos));

        graphBuilder.putString("test").putString("foo").putString("bar").finishStream();

        Input input = Input.fromByteArray(baos.toByteArray());

        Parser parser = Parser.newBuilder().build();

        Query query = parser.prepareQuery("$");

        List<Value> list = new ArrayList<>();
        parser.read(input, query, list::add);

        assertEquals(3, list.size());
        assertEquals("test", list.get(0).string());
        assertEquals("foo", list.get(1).string());
        assertEquals("bar", list.get(2).string());
    }

    @Test
    public void test_first_stream_element()
            throws Exception {

        Writer writer = Writer.newBuilder().build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GraphBuilder graphBuilder = writer.newGraphBuilder(Output.toOutputStream(baos));

        graphBuilder.putString("test").putString("foo").putString("bar").finishStream();

        Input input = Input.fromByteArray(baos.toByteArray());

        Parser parser = Parser.newBuilder().build();

        Query query = parser.prepareQuery("#");

        List<Value> list = new ArrayList<>();
        parser.read(input, query, list::add);

        assertEquals(1, list.size());
        assertEquals("test", list.get(0).string());
    }

    @Test
    public void test_select_statement()
            throws Exception {

        Writer writer = Writer.newBuilder().build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GraphBuilder graphBuilder = writer.newGraphBuilder(Output.toOutputStream(baos));
        graphBuilder.putNumber(100).putNumber(101).putNumber(102).putNumber(103).putNumber(104).finishStream();

        String queryString1 = "(a: #0, b: #0, c: (d: #0, e: #0), f: #4, g: (#0, #1, #2))";
        String queryString2 = "(#0, #1, #2, #3)";

        Parser parser = Parser.newBuilder().asObjectProjectionStrategy().build();

        Query query1 = parser.prepareQuery(queryString1);
        Query query2 = parser.prepareQuery(queryString2);

        Input input = Input.fromByteArray(baos.toByteArray());

        Query query3 = parser.newQueryBuilder() //
                             .asDictionary().putEntry("a").stream(0).endEntry().endDictionary().build();

        Query query4 = parser.newQueryBuilder() //
                             .asSequence().putEntry().stream(0).endEntry().endSequence().build();

        Value value3 = parser.read(input, query3);
        Value value4 = parser.read(input, query4);

        Value value1 = parser.read(input, query1);
        Value value2 = parser.read(input, query2);

        assertEquals(ValueTypes.Dictionary, value1.valueType());

        Dictionary v1 = value1.dictionary();
        Value v1v1 = v1.get(Predicates.matchString("a"));
        Value v1v2 = v1.get(Predicates.matchString("b"));
        Value v1v3 = v1.get(Predicates.matchString("c"));
        Value v1v4 = v1.get(Predicates.matchString("f"));
        Value v1v5 = v1.get(Predicates.matchString("g"));

        assertEquals(ValueTypes.UInt, v1v1.valueType());
        assertEquals(ValueTypes.UInt, v1v2.valueType());
        assertEquals(ValueTypes.Dictionary, v1v3.valueType());
        assertEquals(ValueTypes.UInt, v1v4.valueType());
        assertEquals(ValueTypes.Sequence, v1v5.valueType());

        assertEqualsNumber(100, v1v1.number());
        assertEqualsNumber(100, v1v2.number());

        Dictionary v1v3d = v1v3.dictionary();
        Value v1v3v1 = v1v3d.get(Predicates.matchString("d"));
        Value v1v3v2 = v1v3d.get(Predicates.matchString("e"));

        assertEqualsNumber(100, v1v3v1.number());
        assertEqualsNumber(100, v1v3v2.number());

        assertEqualsNumber(104, v1v4.number());

        Sequence v1v5s = v1v5.sequence();
        Value v1v5v1 = v1v5s.get(0);
        Value v1v5v2 = v1v5s.get(1);
        Value v1v5v3 = v1v5s.get(2);

        assertEqualsNumber(100, v1v5v1.number());
        assertEqualsNumber(101, v1v5v2.number());
        assertEqualsNumber(102, v1v5v3.number());

        assertEquals(ValueTypes.Sequence, value2.valueType());

        assertEquals(ValueTypes.Dictionary, value3.valueType());
        assertEquals(ValueTypes.Sequence, value4.valueType());
    }

    @Test
    public void test_simple_sequence_access()
            throws Exception {

        String query = "#{'b'}->sequence(1)->number";
        SimplifiedTestParser parser = buildParser("0xbf61610161629f0203ffff");
        Value value = parser.read(query);
        assertEqualsNumber(3, value.number());
    }

    @Test
    public void test_dictionary_matchint_long_type_not_match()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xa56161614161626142616361436164614461656145");
        Value value = parser.read("#{123}");
        assertSame(Value.NULL_VALUE, value);
    }

    @Test
    public void test_dictionary_matchint_long_type_match()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xa201020304");
        Value value = parser.read("#{1}");
        assertEqualsNumber(2, value.number());
    }

}
