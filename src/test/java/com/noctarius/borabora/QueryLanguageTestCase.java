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

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class QueryLanguageTestCase
        extends AbstractTestCase {

    // >#{'b'}>>"foo", #{'c'}>>"bar"<
    // select(#{'b'} as "foo", #{'c'} as "bar")
    // select(foo: #{'b'}, bar: #{'c'})
    // (foo: #{'b'}, bar: #{'c'})
    // { "foo": ..., "bar": ... }

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

    @Test
    public void test_query_combinations()
            throws Exception {

        GraphQuery stream = GraphQuery.newBuilder().build();
        GraphQuery stream_1 = GraphQuery.newBuilder().stream(1).build();
        GraphQuery stream_sequence_one = GraphQuery.newBuilder().sequence(1).build();
        GraphQuery stream_1_seq_1 = GraphQuery.newBuilder().stream(1).sequence(1).build();
        GraphQuery stream_dic_1 = GraphQuery.newBuilder().dictionary(1).build();
        GraphQuery stream_1_dic_1 = GraphQuery.newBuilder().stream(1).dictionary(1).build();
        GraphQuery stream_dic_n1 = GraphQuery.newBuilder().dictionary(-1).build();
        GraphQuery stream_1_dic_n1 = GraphQuery.newBuilder().stream(1).dictionary(-1).build();
        GraphQuery stream_dic_11 = GraphQuery.newBuilder().dictionary(1.1).build();
        GraphQuery stream_1_dic_11 = GraphQuery.newBuilder().stream(1).dictionary(1.1).build();
        GraphQuery stream_dic_n11 = GraphQuery.newBuilder().dictionary(-1.1).build();
        GraphQuery stream_1_dic_n11 = GraphQuery.newBuilder().stream(1).dictionary(-1.1).build();
        GraphQuery stream_dic_s = GraphQuery.newBuilder().dictionary("test").build();
        GraphQuery stream_1_dic_s = GraphQuery.newBuilder().stream(1).dictionary("test").build();

        GraphQuery stream_tc_number = GraphQuery.newBuilder().requireType(TypeSpecs.Number).build();
        GraphQuery stream_tc_uint = GraphQuery.newBuilder().requireType(TypeSpecs.UInt).build();
        GraphQuery stream_tc_nint = GraphQuery.newBuilder().requireType(TypeSpecs.NInt).build();
        GraphQuery stream_tc_int = GraphQuery.newBuilder().requireType(TypeSpecs.Int).build();
        GraphQuery stream_tc_ufloat = GraphQuery.newBuilder().requireType(TypeSpecs.UFloat).build();
        GraphQuery stream_tc_nfloat = GraphQuery.newBuilder().requireType(TypeSpecs.NFloat).build();
        GraphQuery stream_tc_float = GraphQuery.newBuilder().requireType(TypeSpecs.Float).build();
        GraphQuery stream_tc_string = GraphQuery.newBuilder().requireType(TypeSpecs.String).build();
        GraphQuery stream_tc_dictionary = GraphQuery.newBuilder().requireType(TypeSpecs.Dictionary).build();
        GraphQuery stream_tc_sequence = GraphQuery.newBuilder().requireType(TypeSpecs.Sequence).build();
        GraphQuery stream_tc_tag = GraphQuery.newBuilder().requireType(TypeSpecs.SemanticTag).build();
        GraphQuery stream_tc_tag_0 = GraphQuery.newBuilder().requireType(TypeSpecs.DateTime).build();
        GraphQuery stream_tc_tag_1 = GraphQuery.newBuilder().requireType(TypeSpecs.Timstamp).build();
        GraphQuery stream_tc_tag_24 = GraphQuery.newBuilder().requireType(TypeSpecs.EncCBOR).build();
        GraphQuery stream_tc_tag_32 = GraphQuery.newBuilder().requireType(TypeSpecs.URI).build();
        GraphQuery stream_tc_bool = GraphQuery.newBuilder().requireType(TypeSpecs.Bool).build();
        GraphQuery stream_tc_datatime = GraphQuery.newBuilder().requireType(TypeSpecs.DateTime).build();
        GraphQuery stream_tc_timestamp = GraphQuery.newBuilder().requireType(TypeSpecs.Timstamp).build();
        GraphQuery stream_tc_uri = GraphQuery.newBuilder().requireType(TypeSpecs.URI).build();
        GraphQuery stream_tc_enccbor = GraphQuery.newBuilder().requireType(TypeSpecs.EncCBOR).build();

        GraphQuery stream_tc_optional = GraphQuery.newBuilder().nullOrType(TypeSpecs.EncCBOR).build();

        Parser parser = Parser.newBuilder().build();
        assertGraphQuery(stream, parser.prepareQuery("#"));
        assertGraphQuery(stream_1, parser.prepareQuery("#1"));
        assertGraphQuery(stream_sequence_one, parser.prepareQuery("#(1)"));
        assertGraphQuery(stream_1_seq_1, parser.prepareQuery("#1(1)"));
        assertGraphQuery(stream_dic_1, parser.prepareQuery("#{1}"));
        assertGraphQuery(stream_1_dic_1, parser.prepareQuery("#1{1}"));
        assertGraphQuery(stream_dic_n1, parser.prepareQuery("#{-1}"));
        assertGraphQuery(stream_1_dic_n1, parser.prepareQuery("#1{-1}"));
        assertGraphQuery(stream_dic_11, parser.prepareQuery("#{1.1}"));
        assertGraphQuery(stream_1_dic_11, parser.prepareQuery("#1{1.1}"));
        assertGraphQuery(stream_dic_n11, parser.prepareQuery("#{-1.1}"));
        assertGraphQuery(stream_1_dic_n11, parser.prepareQuery("#1{-1.1}"));
        assertGraphQuery(stream_dic_s, parser.prepareQuery("#{'test'}"));
        assertGraphQuery(stream_1_dic_s, parser.prepareQuery("#1{'test'}"));

        assertGraphQuery(stream_tc_number, parser.prepareQuery("#->number"));
        assertGraphQuery(stream_tc_uint, parser.prepareQuery("#->uint"));
        assertGraphQuery(stream_tc_nint, parser.prepareQuery("#->nint"));
        assertGraphQuery(stream_tc_int, parser.prepareQuery("#->int"));
        assertGraphQuery(stream_tc_ufloat, parser.prepareQuery("#->ufloat"));
        assertGraphQuery(stream_tc_nfloat, parser.prepareQuery("#->nfloat"));
        assertGraphQuery(stream_tc_float, parser.prepareQuery("#->float"));
        assertGraphQuery(stream_tc_string, parser.prepareQuery("#->string"));
        assertGraphQuery(stream_tc_dictionary, parser.prepareQuery("#->dictionary"));
        assertGraphQuery(stream_tc_sequence, parser.prepareQuery("#->sequence"));
        assertGraphQuery(stream_tc_tag, parser.prepareQuery("#->tag"));
        assertGraphQuery(stream_tc_tag_0, parser.prepareQuery("#->tag$0"));
        assertGraphQuery(stream_tc_tag_1, parser.prepareQuery("#->tag$1"));
        assertGraphQuery(stream_tc_tag_24, parser.prepareQuery("#->tag$24"));
        assertGraphQuery(stream_tc_tag_32, parser.prepareQuery("#->tag$32"));
        assertGraphQuery(stream_tc_bool, parser.prepareQuery("#->bool"));
        assertGraphQuery(stream_tc_datatime, parser.prepareQuery("#->datetime"));
        assertGraphQuery(stream_tc_timestamp, parser.prepareQuery("#->timestamp"));
        assertGraphQuery(stream_tc_uri, parser.prepareQuery("#->uri"));
        assertGraphQuery(stream_tc_enccbor, parser.prepareQuery("#->enccbor"));

        assertGraphQuery(stream_tc_optional, parser.prepareQuery("#->?enccbor"));
    }

    @Test(expected = QueryParserException.class)
    public void test_query_parsing_fail_unknown_tagid()
            throws Exception {

        Parser parser = Parser.newBuilder().build();
        parser.prepareQuery("#->tag$111");
    }

    @Test(expected = QueryParserException.class)
    public void test_query_parsing_fail()
            throws Exception {

        Parser parser = Parser.newBuilder().build();
        parser.prepareQuery("");
    }

    @Test(expected = QueryParserException.class)
    public void test_query_parsing_fail_broken_dictionary()
            throws Exception {

        Parser parser = Parser.newBuilder().build();
        parser.prepareQuery("#{}");
    }

    @Test(expected = QueryParserException.class)
    public void test_query_parsing_fail_token_error_default()
            throws Exception {

        Parser parser = Parser.newBuilder().build();
        parser.prepareQuery("#{1.1.}");
    }

    private void assertGraphQuery(GraphQuery expected, GraphQuery actual) {
        ChainGraphQuery exp = (ChainGraphQuery) expected;
        ChainGraphQuery act = (ChainGraphQuery) actual;

        List<GraphQuery> n1 = exp.nodes();
        List<GraphQuery> n2 = act.nodes();

        assertEquals(n1.size(), n2.size());
        assertEquals(n1, n2);
        assertEquals(n1.hashCode(), n2.hashCode());
    }

    @Test
    public void code_coverage_for_unused_but_generated_methods()
            throws Exception {

        ByteArrayInputStream stream = new ByteArrayInputStream(new byte[0]);
        QueryParser qp = new QueryParser(stream);
        qp.ReInit(stream);

        qp = new QueryParser(new StringReader(""));
        Field token_source = QueryParser.class.getDeclaredField("token_source");
        token_source.setAccessible(true);
        token_source.set(qp, null);
        qp.ReInit(new StringReader(""));

        QueryParserTokenManager tm = new QueryParserTokenManager(new SimpleCharStream(new StringReader("")));
        qp = new QueryParser(tm);
        qp.ReInit(tm);
        qp.enable_tracing();
        qp.disable_tracing();

        qp.ReInit(new StringReader("#"));
        qp.getToken(0);
        qp.getToken(1);

        qp.ReInit(new StringReader("#"));
        Token token;
        while ((token = qp.getNextToken()) != null) {
            if (token.next == null) {
                break;
            }
        }
    }

}
