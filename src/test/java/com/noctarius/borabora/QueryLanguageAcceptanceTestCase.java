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

import org.junit.Ignore;
import org.junit.Test;

public class QueryLanguageAcceptanceTestCase {

    private final Parser parser = Parser.newBuilder().build();

    @Test(expected = QueryParserException.class)
    public void fail_stream_nint() {
        parser.prepareQuery("#-1");
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
        parser.prepareQuery("#0");
        parser.prepareQuery("#123");
    }

    @Test
    public void test_stream_base() {
        parser.prepareQuery("#");
    }

    @Test(expected = QueryParserException.class)
    public void fail_type_match_unknown_type() {
        parser.prepareQuery("#->gg");
    }

    @Test(expected = QueryParserException.class)
    public void fail_type_match_wrong_optional_sign() {
        parser.prepareQuery("#->!gg");
    }

    @Test
    public void test_type_match() {
        parser.prepareQuery("#->number");
        parser.prepareQuery("#->?number");
    }

    @Test
    public void test_dictionary_access_string() {
        parser.prepareQuery("#{'test'}");
    }

    @Test
    public void test_dictionary_access_uint() {
        parser.prepareQuery("#{123}");
    }

    @Test
    public void test_dictionary_access_nint() {
        parser.prepareQuery("#{-123}");
    }

    @Test
    public void test_dictionary_access_ufloat() {
        parser.prepareQuery("#{123.0}");
    }

    @Test
    public void test_dictionary_access_nfloar() {
        parser.prepareQuery("#{-123.0}");
    }

    @Test(expected = QueryParserException.class)
    public void fail_dictionary_access_illegal_argument() {
        parser.prepareQuery("#{test}");
    }

    @Test
    public void test_sequence_access_uint() {
        parser.prepareQuery("#(123)");
    }

    @Test(expected = QueryParserException.class)
    public void fail_sequence_access_nint() {
        parser.prepareQuery("#(-123)");
    }

    @Test
    public void test_sequence_select() {
        parser.prepareQuery("(#, #)");
    }

    @Test
    public void test_sequence_select_subsequence() {
        parser.prepareQuery("(#, (#, #))");
    }

    @Test
    public void test_sequence_select_subdictionary_string() {
        parser.prepareQuery("(#, (a: #, b: #))");
    }

    @Test
    public void test_sequence_select_subdictionary_float() {
        parser.prepareQuery("(#, (1.0: #, 2.0: #))");
    }

    @Test
    public void test_sequence_select_subdictionary_int() {
        parser.prepareQuery("(#, (1: #, 2: #))");
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
    public void test_dictionary_select_string() {
        parser.prepareQuery("(a: #, b: #)");
    }

    @Test
    public void test_dictionary_select_float() {
        parser.prepareQuery("(1.0: #, 2.0: #)");
    }

    @Test
    public void test_dictionary_select_int() {
        parser.prepareQuery("(1: #, 2: #)");
    }

    @Test
    public void test_dictionary_select_string_subsequence() {
        parser.prepareQuery("(a: #, b: (#, #))");
    }

    @Test
    public void test_dictionary_select_float_subsequence() {
        parser.prepareQuery("(1.0: #, 2.0: (#, #))");
    }

    @Test
    public void test_dictionary_select_int_subsequence() {
        parser.prepareQuery("(1: #, 2: (#, #))");
    }

    @Test
    public void test_dictionary_select_string_subdictionary() {
        parser.prepareQuery("(c: #, d: (a: #, b: #))");
    }

    @Test
    public void test_dictionary_select_float_subdictionary() {
        parser.prepareQuery("(3.0: #, 4.0: (1.0: #, 2.0: #))");
    }

    @Test
    public void test_dictionary_select_int_subdictionary() {
        parser.prepareQuery("(3: #, 4: (1: #, 2: #))");
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

    @Test
    @Ignore
    public void test_sequence_dictionary_if_string() {
        parser.prepareQuery("(?(0){'foo'=='bar'}>>1)");
    }

    @Test
    @Ignore
    public void test_sequence_dictionary_if_uint() {
        parser.prepareQuery("(?(0){'foo'==1}>>1)");
        parser.prepareQuery("(?(0){'foo'>=1}>>1)");
        parser.prepareQuery("(?(0){'foo'>1}>>1)");
        parser.prepareQuery("(?(0){'foo'<=1}>>1)");
        parser.prepareQuery("(?(0){'foo'<1}>>1)");
    }

    @Test
    @Ignore
    public void test_sequence_dictionary_if_nint() {
        parser.prepareQuery("(?(0){'foo'==-1}>>1)");
        parser.prepareQuery("(?(0){'foo'>=-1}>>1)");
        parser.prepareQuery("(?(0){'foo'>-1}>>1)");
        parser.prepareQuery("(?(0){'foo'<=-1}>>1)");
        parser.prepareQuery("(?(0){'foo'<-1}>>1)");
    }

    @Test
    @Ignore
    public void test_sequence_dictionary_if_ufloat() {
        parser.prepareQuery("(?(0){'foo'==1.0}>>1)");
        parser.prepareQuery("(?(0){'foo'>=1.0}>>1)");
        parser.prepareQuery("(?(0){'foo'>1.0}>>1)");
        parser.prepareQuery("(?(0){'foo'<=1.0}>>1)");
        parser.prepareQuery("(?(0){'foo'<1.0}>>1)");
    }

    @Test
    @Ignore
    public void test_sequence_dictionary_if_nfloat() {
        parser.prepareQuery("(?(0){'foo'==-1.0}>>1)");
        parser.prepareQuery("(?(0){'foo'>=-1.0}>>1)");
        parser.prepareQuery("(?(0){'foo'>-1.0}>>1)");
        parser.prepareQuery("(?(0){'foo'<=-1.0}>>1)");
        parser.prepareQuery("(?(0){'foo'<-1.0}>>1)");
    }

    @Test
    @Ignore
    public void test_sequence_sequence_if_string() {
        parser.prepareQuery("(?(1=='bar')>>1)");
    }

    @Test
    @Ignore
    public void test_sequence_sequence_if_uint() {
        parser.prepareQuery("(?(1==1)>>1)");
        parser.prepareQuery("(?(1>=1)>>1)");
        parser.prepareQuery("(?(1>1)>>1)");
        parser.prepareQuery("(?(1<=1)>>1)");
        parser.prepareQuery("(?(1<1)>>1)");
    }

    @Test
    @Ignore
    public void test_sequence_sequence_if_nint() {
        parser.prepareQuery("(?(1==-1)>>1)");
        parser.prepareQuery("(?(1>=-1)>>1)");
        parser.prepareQuery("(?(1>-1)>>1)");
        parser.prepareQuery("(?(1<=-1)>>1)");
        parser.prepareQuery("(?(1<-1)>>1)");
    }

    @Test
    @Ignore
    public void test_sequence_sequence_if_ufloat() {
        parser.prepareQuery("(?(1==1.0)>>1)");
        parser.prepareQuery("(?(1>=1.0)>>1)");
        parser.prepareQuery("(?(1>1.0)>>1)");
        parser.prepareQuery("(?(1<=1.0)>>1)");
        parser.prepareQuery("(?(1<1.0)>>1)");
    }

    @Test
    @Ignore
    public void test_sequence_sequence_if_nfloat() {
        parser.prepareQuery("(?(1==-1.0)>>1)");
        parser.prepareQuery("(?(1>=-1.0)>>1)");
        parser.prepareQuery("(?(1>-1.0)>>1)");
        parser.prepareQuery("(?(1<=-1.0)>>1)");
        parser.prepareQuery("(?(1<-1.0)>>1)");
    }

    @Test
    @Ignore
    public void test_dictionary_dictionary_if_string() {
        parser.prepareQuery("{?{'foo'=='bar'}>>'foo'}");
    }

    @Test
    @Ignore
    public void test_dictionary_dictionary_if_uint() {
        parser.prepareQuery("{?{'foo'==1}>>'foo'}");
        parser.prepareQuery("{?{'foo'>=1}>>'foo'}");
        parser.prepareQuery("{?{'foo'>1}>>'foo'}");
        parser.prepareQuery("{?{'foo'<=1}>>'foo'}");
        parser.prepareQuery("{?{'foo'<1}>>'foo'}");
    }

    @Test
    @Ignore
    public void test_dictionary_dictionary_if_nint() {
        parser.prepareQuery("{?{'foo'==-1}>>'foo'}");
        parser.prepareQuery("{?{'foo'>=-1}>>'foo'}");
        parser.prepareQuery("{?{'foo'>-1}>>'foo'}");
        parser.prepareQuery("{?{'foo'<=-1}>>'foo'}");
        parser.prepareQuery("{?{'foo'<-1}>>'foo'}");
    }

    @Test
    @Ignore
    public void test_dictionary_dictionary_if_ufloat() {
        parser.prepareQuery("{?{'foo'==1.0}>>'foo'}");
        parser.prepareQuery("{?{'foo'>=1.0}>>'foo'}");
        parser.prepareQuery("{?{'foo'>1.0}>>'foo'}");
        parser.prepareQuery("{?{'foo'<=1.0}>>'foo'}");
        parser.prepareQuery("{?{'foo'<1.0}>>'foo'}");
    }

    @Test
    @Ignore
    public void test_dictionary_dictionary_if_nfloat() {
        parser.prepareQuery("{?{'foo'==-1.0}>>'foo'}");
        parser.prepareQuery("{?{'foo'>=-1.0}>>'foo'}");
        parser.prepareQuery("{?{'foo'>-1.0}>>'foo'}");
        parser.prepareQuery("{?{'foo'<=-1.0}>>'foo'}");
        parser.prepareQuery("{?{'foo'<-1.0}>>'foo'}");
    }

    @Test
    @Ignore
    public void test_match_any_stream_element() {
        parser.prepareQuery("$");
    }

}
