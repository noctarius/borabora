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

import com.noctarius.borabora.spi.query.TypeSpecs;
import org.junit.Ignore;
import org.junit.Test;

import static com.noctarius.borabora.Predicates.matchString;

public class QueryLanguageAcceptanceTestCase
        extends AbstractTestCase {

    private final Parser parser = Parser.newParser();

    @Test(expected = QueryParserException.class)
    public void fail_empty_query()
            throws Exception {

        parser.prepareQuery("");
    }

    @Test(expected = QueryParserException.class)
    public void fail_stream_nint() {
        parser.prepareQuery("#-1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_query_stream_nint() {
        parser.newQueryBuilder().stream(-1).build();
    }

    @Test(expected = QueryParserException.class)
    public void fail_stream_ufloat() {
        parser.prepareQuery("#1.0");
    }

    @Test(expected = QueryParserException.class)
    public void fail_stream_nfloat() {
        parser.prepareQuery("#-1.0");
    }

    @Test(expected = QueryParserException.class)
    public void fail_stream_ufloat_comma() {
        parser.prepareQuery("#1,0");
    }

    @Test(expected = QueryParserException.class)
    public void fail_stream_nfloat_comma() {
        parser.prepareQuery("#-1,0");
    }

    @Test
    public void test_stream_uint() {
        Query query = parser.newQueryBuilder().stream(0).build();
        evaluate(query, "#0");

        query = parser.newQueryBuilder().build();
        evaluate(query, "#0");

        query = parser.newQueryBuilder().stream(123).build();
        evaluate(query, "#123");
    }

    @Test
    public void test_stream_base() {
        Query query = parser.newQueryBuilder().build();
        evaluate(query, "#");
    }

    @Test
    public void test_stream_sequence_int() {
        Query query = parser.newQueryBuilder().stream(1).sequence(1).build();
        evaluate(query, "#1(1)");
    }

    @Test(expected = QueryParserException.class)
    public void fail_stream_sequence_nint() {
        parser.prepareQuery("#1(-1)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_query_stream_sequence_nint() {
        parser.newQueryBuilder().stream(1).sequence(-1).build();
    }

    @Test
    public void test_stream_dictionary_int() {
        Query query = parser.newQueryBuilder().stream(1).dictionary(1).build();
        evaluate(query, "#1{1}");
    }

    @Test
    public void test_stream_dictionary_nint() {
        Query query = parser.newQueryBuilder().stream(1).dictionary(-1).build();
        evaluate(query, "#1{-1}");
    }

    @Test(expected = QueryParserException.class)
    public void fail_type_match_missing_type_sped() {
        parser.prepareQuery("#->");
    }

    @Test(expected = QueryParserException.class)
    public void fail_type_match_unknown_type() {
        parser.prepareQuery("#->gg");
    }

    @Test(expected = NullPointerException.class)
    public void fail_type_match_null() {
        parser.newQueryBuilder().requireType(null).build();
    }

    @Test(expected = QueryParserException.class)
    public void fail_type_match_wrong_optional_sign() {
        parser.prepareQuery("#->!number");
    }

    @Test
    public void test_type_match() {
        Query query = parser.newQueryBuilder().requireType(TypeSpecs.Number).build();
        evaluate(query, "#->number");

        query = parser.newQueryBuilder().nullOrType(TypeSpecs.Number).build();
        evaluate(query, "#->?number");
    }

    @Test
    public void test_dictionary_access_string() {
        Query query = parser.newQueryBuilder().dictionary(matchString("test")).build();
        evaluate(query, "#{'test'}");
    }

    @Test
    public void test_stream_dictionary_access_string() {
        Query query = parser.newQueryBuilder().stream(1).dictionary(matchString("test")).build();
        evaluate(query, "#1{'test'}");
    }

    @Test
    public void test_dictionary_access_uint() {
        Query query = parser.newQueryBuilder().dictionary(Predicates.matchInt(123)).build();
        evaluate(query, "#{123}");
    }

    @Test
    public void test_dictionary_access_nint() {
        Query query = parser.newQueryBuilder().dictionary(Predicates.matchInt(-123)).build();
        evaluate(query, "#{-123}");
    }

    @Test
    public void test_dictionary_access_ufloat() {
        Query query = parser.newQueryBuilder().dictionary(Predicates.matchFloat(123.0)).build();
        evaluate(query, "#{123.0}");
    }

    @Test
    public void test_stream_dictionary_access_ufloat() {
        Query query = parser.newQueryBuilder().stream(1).dictionary(Predicates.matchFloat(123.0)).build();
        evaluate(query, "#1{123.0}");
    }

    @Test
    public void test_dictionary_access_nfloar() {
        Query query = parser.newQueryBuilder().dictionary(Predicates.matchFloat(-123.0)).build();
        evaluate(query, "#{-123.0}");
    }

    @Test
    public void test_stream_dictionary_access_nfloar() {
        Query query = parser.newQueryBuilder().stream(1).dictionary(Predicates.matchFloat(-123.0)).build();
        evaluate(query, "#1{-123.0}");
    }

    @Test(expected = QueryParserException.class)
    public void fail_dictionary_access_illegal_argument() {
        parser.prepareQuery("#{test}");
    }

    @Test
    public void test_sequence_access_uint() {
        Query query = parser.newQueryBuilder().sequence(123).build();
        evaluate(query, "#(123)");
    }

    @Test(expected = QueryParserException.class)
    public void fail_sequence_access_nint() {
        parser.prepareQuery("#(-123)");
    }

    @Test
    public void test_sequence_select() {
        Query query = parser.newQueryBuilder().asSequence() //
                            .putElement().stream(0).endEntry() //
                            .putElement().stream(0).endEntry().endSequence().build();
        evaluate(query, "(#, #)");
    }

    @Test
    public void test_sequence_select_subsequence() {
        Query query = parser.newQueryBuilder().asSequence() //
                            .putElement().stream(0).endEntry() //
                            .putElement().asSequence().putElement().stream(0).endEntry() //
                            .putElement().stream(0).endEntry().endSequence().endEntry() //
                            .endSequence().build();
        evaluate(query, "(#, (#, #))");
    }

    @Test
    public void test_sequence_select_subdictionary_string() {
        Query query = parser.newQueryBuilder().asSequence() //
                            .putElement().stream(0).endEntry() //
                            .putElement().asDictionary() //
                            .putEntry("a").stream(0).endEntry() //
                            .putEntry("b").stream(0).endEntry() //
                            .endDictionary().endEntry() //
                            .endSequence().build();
        evaluate(query, "(#, (a: #, b: #))");
    }

    @Test
    public void test_sequence_select_subdictionary_float() {
        Query query = parser.newQueryBuilder().asSequence() //
                            .putElement().stream(0).endEntry() //
                            .putElement().asDictionary() //
                            .putEntry(1.0).stream(0).endEntry() //
                            .putEntry(2.0).stream(0).endEntry() //
                            .endDictionary().endEntry() //
                            .endSequence().build();
        evaluate(query, "(#, (1.0: #, 2.0: #))");
    }

    @Test
    public void test_sequence_select_subdictionary_int() {
        Query query = parser.newQueryBuilder().asSequence() //
                            .putElement().stream(0).endEntry() //
                            .putElement().asDictionary() //
                            .putEntry(1).stream(0).endEntry() //
                            .putEntry(2).stream(0).endEntry() //
                            .endDictionary().endEntry() //
                            .endSequence().build();
        evaluate(query, "(#, (1: #, 2: #))");
    }

    @Test(expected = QueryParserException.class)
    public void fail_sequence_dictionary_select_string_mixed() {
        parser.prepareQuery("(#, c: (a: #, b: #))");
    }

    @Test(expected = QueryParserException.class)
    public void fail_sequence_dictionary_select_float_mixed() {
        parser.prepareQuery("(#, 3.0: (1.0: #, 2.0: #))");
    }

    @Test(expected = QueryParserException.class)
    public void fail_sequence_dictionary_select_int_mixed() {
        parser.prepareQuery("(#, 3: (1: #, 2: #))");
    }

    @Test
    public void test_type_check_uint() {
        Query query = parser.newQueryBuilder().requireType(TypeSpecs.UInt).build();
        assertQueryEquals(query, parser.prepareQuery("#->uint"));
    }

    @Test
    public void test_type_check_nint() {
        Query query = parser.newQueryBuilder().requireType(TypeSpecs.NInt).build();
        assertQueryEquals(query, parser.prepareQuery("#->nint"));
    }

    @Test
    public void test_type_check_int() {
        Query query = parser.newQueryBuilder().requireType(TypeSpecs.Int).build();
        assertQueryEquals(query, parser.prepareQuery("#->int"));
    }

    @Test
    public void test_type_check_float() {
        Query query = parser.newQueryBuilder().requireType(TypeSpecs.Float).build();
        assertQueryEquals(query, parser.prepareQuery("#->float"));
    }

    @Test
    public void test_type_check_string() {
        Query query = parser.newQueryBuilder().requireType(TypeSpecs.String).build();
        assertQueryEquals(query, parser.prepareQuery("#->string"));
    }

    @Test
    public void test_type_check_dictionary() {
        Query query = parser.newQueryBuilder().requireType(TypeSpecs.Dictionary).build();
        assertQueryEquals(query, parser.prepareQuery("#->dictionary"));
    }

    @Test
    public void test_type_check_sequence() {
        Query query = parser.newQueryBuilder().requireType(TypeSpecs.Sequence).build();
        assertQueryEquals(query, parser.prepareQuery("#->sequence"));
    }

    @Test
    public void test_type_check_tag() {
        Query query = parser.newQueryBuilder().requireType(TypeSpecs.SemanticTag).build();
        assertQueryEquals(query, parser.prepareQuery("#->tag"));
    }

    @Test(expected = QueryParserException.class)
    public void fail_type_check_unknown_tag() {
        parser.prepareQuery("#->tag$111");
    }

    @Test
    public void test_type_check_date_time() {
        Query query = parser.newQueryBuilder().requireType(TypeSpecs.DateTime).build();
        assertQueryEquals(query, parser.prepareQuery("#->tag$0"));
        assertQueryEquals(query, parser.prepareQuery("#->datetime"));
    }

    @Test
    public void test_type_check_timestamp() {
        Query query = parser.newQueryBuilder().requireType(TypeSpecs.Timstamp).build();
        assertQueryEquals(query, parser.prepareQuery("#->tag$1"));
        assertQueryEquals(query, parser.prepareQuery("#->timestamp"));
    }

    @Test
    public void test_type_check_enccbor() {
        Query query = parser.newQueryBuilder().requireType(TypeSpecs.EncCBOR).build();
        assertQueryEquals(query, parser.prepareQuery("#->tag$24"));
        assertQueryEquals(query, parser.prepareQuery("#->enccbor"));
    }

    @Test
    public void test_type_check_optional_enccbor() {
        Query query = parser.newQueryBuilder().nullOrType(TypeSpecs.EncCBOR).build();
        assertQueryEquals(query, parser.prepareQuery("#->?tag$24"));
        assertQueryEquals(query, parser.prepareQuery("#->?enccbor"));
    }

    @Test
    public void test_type_check_uri() {
        Query query = parser.newQueryBuilder().requireType(TypeSpecs.URI).build();
        assertQueryEquals(query, parser.prepareQuery("#->tag$32"));
        assertQueryEquals(query, parser.prepareQuery("#->uri"));
    }

    @Test
    public void test_type_check_bool() {
        Query query = parser.newQueryBuilder().requireType(TypeSpecs.Bool).build();
        assertQueryEquals(query, parser.prepareQuery("#->bool"));
    }

    @Test
    public void test_dictionary_select_typecheck() {
        Query query = parser.newQueryBuilder().asDictionary() //
                            .putEntry("a").stream(0).requireType(TypeSpecs.Number).endEntry() //
                            .putEntry("b").stream(0).nullOrType(TypeSpecs.Number).endEntry() //
                            .endDictionary().build();
        evaluate(query, "(a: #->number, b: #->?number)");
    }

    @Test
    public void test_dictionary_select_stream_number_typecheck() {
        Query query = parser.newQueryBuilder().asDictionary() //
                            .putEntry("a").stream(1).requireType(TypeSpecs.Number).endEntry() //
                            .putEntry("b").stream(1).nullOrType(TypeSpecs.Number).endEntry() //
                            .endDictionary().build();
        evaluate(query, "(a: #1->number, b: #1->?number)");
    }

    @Test
    public void test_dictionary_select_dictionary_lookup() {
        Query query = parser.newQueryBuilder().asDictionary() //
                            .putEntry("a").stream(0).dictionary(matchString("foo")).endEntry() //
                            .putEntry("b").stream(1).dictionary(matchString("foo")).endEntry() //
                            .endDictionary().build();
        evaluate(query, "(a: #{'foo'}, b: #1{'foo'})");
    }

    @Test
    public void test_dictionary_select_sequence_lookup() {
        Query query = parser.newQueryBuilder().asDictionary() //
                            .putEntry("a").stream(0).sequence(1).endEntry() //
                            .putEntry("b").stream(1).sequence(1).endEntry() //
                            .endDictionary().build();
        evaluate(query, "(a: #(1), b: #1(1))");
    }

    @Test
    public void test_dictionary_select_string() {
        Query query = parser.newQueryBuilder().asDictionary() //
                            .putEntry("a").stream(0).endEntry() //
                            .putEntry("b").stream(0).endEntry() //
                            .endDictionary().build();
        evaluate(query, "(a: #, b: #)");
    }

    @Test
    public void test_dictionary_select_float() {
        Query query = parser.newQueryBuilder().asDictionary() //
                            .putEntry(1.0).stream(0).endEntry() //
                            .putEntry(2.0).stream(0).endEntry() //
                            .endDictionary().build();
        evaluate(query, "(1.0: #, 2.0: #)");
    }

    @Test
    public void test_dictionary_select_int() {
        Query query = parser.newQueryBuilder().asDictionary() //
                            .putEntry(1).stream(0).endEntry() //
                            .putEntry(2).stream(0).endEntry() //
                            .endDictionary().build();
        evaluate(query, "(1: #, 2: #)");
    }

    @Test
    public void test_dictionary_select_string_subsequence() {
        Query query = parser.newQueryBuilder().asDictionary() //
                            .putEntry("a").stream(0).endEntry() //
                            .putEntry("b").asSequence() //
                            .putElement().stream(0).endEntry() //
                            .putElement().stream(0).endEntry() //
                            .endSequence().endEntry() //
                            .endDictionary().build();
        evaluate(query, "(a: #, b: (#, #))");
    }

    @Test
    public void test_dictionary_select_float_subsequence() {
        Query query = parser.newQueryBuilder().asDictionary() //
                            .putEntry(1.0).stream(0).endEntry() //
                            .putEntry(2.0).asSequence() //
                            .putElement().stream(0).endEntry() //
                            .putElement().stream(0).endEntry() //
                            .endSequence().endEntry() //
                            .endDictionary().build();
        evaluate(query, "(1.0: #, 2.0: (#, #))");
    }

    @Test
    public void test_dictionary_select_int_subsequence() {
        Query query = parser.newQueryBuilder().asDictionary() //
                            .putEntry(1).stream(0).endEntry() //
                            .putEntry(2).asSequence() //
                            .putElement().stream(0).endEntry() //
                            .putElement().stream(0).endEntry() //
                            .endSequence().endEntry() //
                            .endDictionary().build();
        evaluate(query, "(1: #, 2: (#, #))");
    }

    @Test
    public void test_dictionary_select_string_subdictionary() {
        Query query = parser.newQueryBuilder().asDictionary() //
                            .putEntry("c").stream(0).endEntry() //
                            .putEntry("d").asDictionary() //
                            .putEntry("a").stream(0).endEntry() //
                            .putEntry("b").stream(0).endEntry() //
                            .endDictionary().endEntry() //
                            .endDictionary().build();
        evaluate(query, "(c: #, d: (a: #, b: #))");
    }

    @Test
    public void test_dictionary_select_float_subdictionary() {
        Query query = parser.newQueryBuilder().asDictionary() //
                            .putEntry(3.0).stream(0).endEntry() //
                            .putEntry(4.0).asDictionary() //
                            .putEntry(1.0).stream(0).endEntry() //
                            .putEntry(2.0).stream(0).endEntry() //
                            .endDictionary().endEntry() //
                            .endDictionary().build();
        evaluate(query, "(3.0: #, 4.0: (1.0: #, 2.0: #))");
    }

    @Test
    public void test_dictionary_select_int_subdictionary() {
        Query query = parser.newQueryBuilder().asDictionary() //
                            .putEntry(3).stream(0).endEntry() //
                            .putEntry(4).asDictionary() //
                            .putEntry(1).stream(0).endEntry() //
                            .putEntry(2).stream(0).endEntry() //
                            .endDictionary().endEntry() //
                            .endDictionary().build();
        evaluate(query, "(3: #, 4: (1: #, 2: #))");
    }

    @Test(expected = QueryParserException.class)
    public void fail_dictionary_sequence_string_select_mixed() {
        parser.prepareQuery("(c: #, (a: #, b: #))");
    }

    @Test(expected = QueryParserException.class)
    public void fail_dictionary_sequence_float_select_mixed() {
        parser.prepareQuery("(3.0: #, (1.0: #, 2.0: #))");
    }

    @Test(expected = QueryParserException.class)
    public void fail_dictionary_sequence_int_select_mixed() {
        parser.prepareQuery("(3: #, (1: #, 2: #))");
    }

    @Test(expected = QueryParserException.class)
    public void fail_broken_dictionary_query() {
        parser.prepareQuery("#{}");
    }

    @Test(expected = QueryParserException.class)
    public void fail_broken_sequence_query() {
        parser.prepareQuery("#()");
    }

    @Test(expected = QueryParserException.class)
    public void fail_broken_dictionary_float_query() {
        parser.prepareQuery("#{1.1.}");
    }

    @Test(expected = QueryParserException.class)
    public void fail_broken_sequence_float_query() {
        parser.prepareQuery("#(1.1.)");
    }

    @Test
    @Ignore
    public void test_sequence_dictionary_if_string() {
        Query query = null; // TODO
        evaluate(query, "(?(0){'foo'=='bar'}>>1)");
    }

    @Test
    @Ignore
    public void test_sequence_dictionary_if_uint() {
        Query query = null; // TODO
        evaluate(query, "(?(0){'foo'==1}>>1)");
        evaluate(query, "(?(0){'foo'>=1}>>1)");
        evaluate(query, "(?(0){'foo'>1}>>1)");
        evaluate(query, "(?(0){'foo'<=1}>>1)");
        evaluate(query, "(?(0){'foo'<1}>>1)");
    }

    @Test
    @Ignore
    public void test_sequence_dictionary_if_nint() {
        Query query = null; // TODO
        evaluate(query, "(?(0){'foo'==-1}>>1)");
        evaluate(query, "(?(0){'foo'>=-1}>>1)");
        evaluate(query, "(?(0){'foo'>-1}>>1)");
        evaluate(query, "(?(0){'foo'<=-1}>>1)");
        evaluate(query, "(?(0){'foo'<-1}>>1)");
    }

    @Test
    @Ignore
    public void test_sequence_dictionary_if_ufloat() {
        Query query = null; // TODO
        evaluate(query, "(?(0){'foo'==1.0}>>1)");
        evaluate(query, "(?(0){'foo'>=1.0}>>1)");
        evaluate(query, "(?(0){'foo'>1.0}>>1)");
        evaluate(query, "(?(0){'foo'<=1.0}>>1)");
        evaluate(query, "(?(0){'foo'<1.0}>>1)");
    }

    @Test
    @Ignore
    public void test_sequence_dictionary_if_nfloat() {
        Query query = null; // TODO
        evaluate(query, "(?(0){'foo'==-1.0}>>1)");
        evaluate(query, "(?(0){'foo'>=-1.0}>>1)");
        evaluate(query, "(?(0){'foo'>-1.0}>>1)");
        evaluate(query, "(?(0){'foo'<=-1.0}>>1)");
        evaluate(query, "(?(0){'foo'<-1.0}>>1)");
    }

    @Test
    @Ignore
    public void test_sequence_sequence_if_string() {
        Query query = null; // TODO
        evaluate(query, "(?(1=='bar')>>1)");
    }

    @Test
    @Ignore
    public void test_sequence_sequence_if_uint() {
        Query query = null; // TODO
        evaluate(query, "(?(1==1)>>1)");
        evaluate(query, "(?(1>=1)>>1)");
        evaluate(query, "(?(1>1)>>1)");
        evaluate(query, "(?(1<=1)>>1)");
        evaluate(query, "(?(1<1)>>1)");
    }

    @Test
    @Ignore
    public void test_sequence_sequence_if_nint() {
        Query query = null; // TODO
        evaluate(query, "(?(1==-1)>>1)");
        evaluate(query, "(?(1>=-1)>>1)");
        evaluate(query, "(?(1>-1)>>1)");
        evaluate(query, "(?(1<=-1)>>1)");
        evaluate(query, "(?(1<-1)>>1)");
    }

    @Test
    @Ignore
    public void test_sequence_sequence_if_ufloat() {
        Query query = null; // TODO
        evaluate(query, "(?(1==1.0)>>1)");
        evaluate(query, "(?(1>=1.0)>>1)");
        evaluate(query, "(?(1>1.0)>>1)");
        evaluate(query, "(?(1<=1.0)>>1)");
        evaluate(query, "(?(1<1.0)>>1)");
    }

    @Test
    @Ignore
    public void test_sequence_sequence_if_nfloat() {
        Query query = null; // TODO
        evaluate(query, "(?(1==-1.0)>>1)");
        evaluate(query, "(?(1>=-1.0)>>1)");
        evaluate(query, "(?(1>-1.0)>>1)");
        evaluate(query, "(?(1<=-1.0)>>1)");
        evaluate(query, "(?(1<-1.0)>>1)");
    }

    @Test
    @Ignore
    public void test_dictionary_dictionary_if_string() {
        Query query = null; // TODO
        evaluate(query, "#{?{'foo'=='bar'}>>'foo'}");
    }

    @Test
    @Ignore
    public void test_dictionary_dictionary_if_uint() {
        Query query = null; // TODO
        evaluate(query, "{?{'foo'==1}>>'foo'}");
        evaluate(query, "{?{'foo'>=1}>>'foo'}");
        evaluate(query, "{?{'foo'>1}>>'foo'}");
        evaluate(query, "{?{'foo'<=1}>>'foo'}");
        evaluate(query, "{?{'foo'<1}>>'foo'}");
    }

    @Test
    @Ignore
    public void test_dictionary_dictionary_if_nint() {
        Query query = null; // TODO
        evaluate(query, "{?{'foo'==-1}>>'foo'}");
        evaluate(query, "{?{'foo'>=-1}>>'foo'}");
        evaluate(query, "{?{'foo'>-1}>>'foo'}");
        evaluate(query, "{?{'foo'<=-1}>>'foo'}");
        evaluate(query, "{?{'foo'<-1}>>'foo'}");
    }

    @Test
    @Ignore
    public void test_dictionary_dictionary_if_ufloat() {
        Query query = null; // TODO
        evaluate(query, "{?{'foo'==1.0}>>'foo'}");
        evaluate(query, "{?{'foo'>=1.0}>>'foo'}");
        evaluate(query, "{?{'foo'>1.0}>>'foo'}");
        evaluate(query, "{?{'foo'<=1.0}>>'foo'}");
        evaluate(query, "{?{'foo'<1.0}>>'foo'}");
    }

    @Test
    @Ignore
    public void test_dictionary_dictionary_if_nfloat() {
        Query query = null; // TODO
        evaluate(query, "{?{'foo'==-1.0}>>'foo'}");
        evaluate(query, "{?{'foo'>=-1.0}>>'foo'}");
        evaluate(query, "{?{'foo'>-1.0}>>'foo'}");
        evaluate(query, "{?{'foo'<=-1.0}>>'foo'}");
        evaluate(query, "{?{'foo'<-1.0}>>'foo'}");
        // TODO Maybe that way? String test = ":{name=='foo'}-->{'bar'}:";
    }

    @Test
    public void test_any_sequence_index() {
        Query query = parser.newQueryBuilder().sequenceMatch(Predicates.any()).build();
        evaluate(query, "#(?)");

        query = parser.newQueryBuilder().multiStream().sequenceMatch(Predicates.any()).build();
        evaluate(query, "$(?)");
    }

    @Test
    public void test_match_any_stream_element() {
        Query query = parser.newQueryBuilder().multiStream().build();
        evaluate(query, "$");
    }

    private void evaluate(Query query, String queryString) {
        Query parsedQuery = parser.prepareQuery(queryString);
        assertQueryEquals(query, parsedQuery);
    }

}
