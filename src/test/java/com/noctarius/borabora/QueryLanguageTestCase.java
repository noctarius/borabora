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

import com.noctarius.borabora.builder.GraphBuilder;
import com.noctarius.borabora.spi.TypeSpecs;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.noctarius.borabora.Predicates.matchString;
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

        Parser parser = Parser.newBuilder()/*.asObjectSelectStatement()*/.build();

        Query query1 = parser.prepareQuery(queryString1);
        Query query2 = parser.prepareQuery(queryString2);

        Input input = Input.fromByteArray(baos.toByteArray());

        Query query3 = Query.newBuilder(ObjectSelectStatementStrategy.INSTANCE) //
                            .asDictionary().putEntry("a").stream(0).endEntry().endDictionary().build();

        Query query4 = Query.newBuilder(ObjectSelectStatementStrategy.INSTANCE) //
                            .asSequence().putEntry().stream(0).endEntry().endSequence().build();

        Value value3 = parser.read(input, query3);
        Value value4 = parser.read(input, query4);

        Value value1 = parser.read(input, query1);
        Value value2 = parser.read(input, query2);

        assertEquals(ValueTypes.Dictionary, value1.valueType());

        Dictionary v1 = value1.dictionary();
        Value v1v1 = v1.get(matchString("a"));
        Value v1v2 = v1.get(matchString("b"));
        Value v1v3 = v1.get(matchString("c"));
        Value v1v4 = v1.get(matchString("f"));
        Value v1v5 = v1.get(matchString("g"));

        assertEquals(ValueTypes.UInt, v1v1.valueType());
        assertEquals(ValueTypes.UInt, v1v2.valueType());
        assertEquals(ValueTypes.Dictionary, v1v3.valueType());
        assertEquals(ValueTypes.UInt, v1v4.valueType());
        assertEquals(ValueTypes.Sequence, v1v5.valueType());

        assertEqualsNumber(100, v1v1.number());
        assertEqualsNumber(100, v1v2.number());

        Dictionary v1v3d = v1v3.dictionary();
        Value v1v3v1 = v1v3d.get(matchString("d"));
        Value v1v3v2 = v1v3d.get(matchString("e"));

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

        System.out.println(ValuePrettyPrinter.asStringPrettyPrint(value1));
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

    @Test
    public void test_query_combinations()
            throws Exception {

        Query stream = Query.newBuilder().build();
        Query stream_1 = Query.newBuilder().stream(1).build();
        Query stream_sequence_one = Query.newBuilder().sequence(1).build();
        Query stream_1_seq_1 = Query.newBuilder().stream(1).sequence(1).build();
        Query stream_dic_1 = Query.newBuilder().dictionary(1).build();
        Query stream_1_dic_1 = Query.newBuilder().stream(1).dictionary(1).build();
        Query stream_dic_n1 = Query.newBuilder().dictionary(-1).build();
        Query stream_1_dic_n1 = Query.newBuilder().stream(1).dictionary(-1).build();
        Query stream_dic_11 = Query.newBuilder().dictionary(1.1).build();
        Query stream_1_dic_11 = Query.newBuilder().stream(1).dictionary(1.1).build();
        Query stream_dic_n11 = Query.newBuilder().dictionary(-1.1).build();
        Query stream_1_dic_n11 = Query.newBuilder().stream(1).dictionary(-1.1).build();
        Query stream_dic_s = Query.newBuilder().dictionary("test").build();
        Query stream_1_dic_s = Query.newBuilder().stream(1).dictionary("test").build();

        Query stream_tc_number = Query.newBuilder().requireType(TypeSpecs.Number).build();
        Query stream_tc_uint = Query.newBuilder().requireType(TypeSpecs.UInt).build();
        Query stream_tc_nint = Query.newBuilder().requireType(TypeSpecs.NInt).build();
        Query stream_tc_int = Query.newBuilder().requireType(TypeSpecs.Int).build();
        Query stream_tc_ufloat = Query.newBuilder().requireType(TypeSpecs.UFloat).build();
        Query stream_tc_nfloat = Query.newBuilder().requireType(TypeSpecs.NFloat).build();
        Query stream_tc_float = Query.newBuilder().requireType(TypeSpecs.Float).build();
        Query stream_tc_string = Query.newBuilder().requireType(TypeSpecs.String).build();
        Query stream_tc_dictionary = Query.newBuilder().requireType(TypeSpecs.Dictionary).build();
        Query stream_tc_sequence = Query.newBuilder().requireType(TypeSpecs.Sequence).build();
        Query stream_tc_tag = Query.newBuilder().requireType(TypeSpecs.SemanticTag).build();
        Query stream_tc_tag_0 = Query.newBuilder().requireType(TypeSpecs.DateTime).build();
        Query stream_tc_tag_1 = Query.newBuilder().requireType(TypeSpecs.Timstamp).build();
        Query stream_tc_tag_24 = Query.newBuilder().requireType(TypeSpecs.EncCBOR).build();
        Query stream_tc_tag_32 = Query.newBuilder().requireType(TypeSpecs.URI).build();
        Query stream_tc_bool = Query.newBuilder().requireType(TypeSpecs.Bool).build();
        Query stream_tc_datatime = Query.newBuilder().requireType(TypeSpecs.DateTime).build();
        Query stream_tc_timestamp = Query.newBuilder().requireType(TypeSpecs.Timstamp).build();
        Query stream_tc_uri = Query.newBuilder().requireType(TypeSpecs.URI).build();
        Query stream_tc_enccbor = Query.newBuilder().requireType(TypeSpecs.EncCBOR).build();

        Query stream_tc_optional = Query.newBuilder().nullOrType(TypeSpecs.EncCBOR).build();

        Parser parser = Parser.newBuilder().build();
        assertQueryEquals(stream, parser.prepareQuery("#"));
        assertQueryEquals(stream_1, parser.prepareQuery("#1"));
        assertQueryEquals(stream_sequence_one, parser.prepareQuery("#(1)"));
        assertQueryEquals(stream_1_seq_1, parser.prepareQuery("#1(1)"));
        assertQueryEquals(stream_dic_1, parser.prepareQuery("#{1}"));
        assertQueryEquals(stream_1_dic_1, parser.prepareQuery("#1{1}"));
        assertQueryEquals(stream_dic_n1, parser.prepareQuery("#{-1}"));
        assertQueryEquals(stream_1_dic_n1, parser.prepareQuery("#1{-1}"));
        assertQueryEquals(stream_dic_11, parser.prepareQuery("#{1.1}"));
        assertQueryEquals(stream_1_dic_11, parser.prepareQuery("#1{1.1}"));
        assertQueryEquals(stream_dic_n11, parser.prepareQuery("#{-1.1}"));
        assertQueryEquals(stream_1_dic_n11, parser.prepareQuery("#1{-1.1}"));
        assertQueryEquals(stream_dic_s, parser.prepareQuery("#{'test'}"));
        assertQueryEquals(stream_1_dic_s, parser.prepareQuery("#1{'test'}"));

        assertQueryEquals(stream_tc_number, parser.prepareQuery("#->number"));
        assertQueryEquals(stream_tc_uint, parser.prepareQuery("#->uint"));
        assertQueryEquals(stream_tc_nint, parser.prepareQuery("#->nint"));
        assertQueryEquals(stream_tc_int, parser.prepareQuery("#->int"));
        assertQueryEquals(stream_tc_ufloat, parser.prepareQuery("#->ufloat"));
        assertQueryEquals(stream_tc_nfloat, parser.prepareQuery("#->nfloat"));
        assertQueryEquals(stream_tc_float, parser.prepareQuery("#->float"));
        assertQueryEquals(stream_tc_string, parser.prepareQuery("#->string"));
        assertQueryEquals(stream_tc_dictionary, parser.prepareQuery("#->dictionary"));
        assertQueryEquals(stream_tc_sequence, parser.prepareQuery("#->sequence"));
        assertQueryEquals(stream_tc_tag, parser.prepareQuery("#->tag"));
        assertQueryEquals(stream_tc_tag_0, parser.prepareQuery("#->tag$0"));
        assertQueryEquals(stream_tc_tag_1, parser.prepareQuery("#->tag$1"));
        assertQueryEquals(stream_tc_tag_24, parser.prepareQuery("#->tag$24"));
        assertQueryEquals(stream_tc_tag_32, parser.prepareQuery("#->tag$32"));
        assertQueryEquals(stream_tc_bool, parser.prepareQuery("#->bool"));
        assertQueryEquals(stream_tc_datatime, parser.prepareQuery("#->datetime"));
        assertQueryEquals(stream_tc_timestamp, parser.prepareQuery("#->timestamp"));
        assertQueryEquals(stream_tc_uri, parser.prepareQuery("#->uri"));
        assertQueryEquals(stream_tc_enccbor, parser.prepareQuery("#->enccbor"));

        assertQueryEquals(stream_tc_optional, parser.prepareQuery("#->?enccbor"));
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
