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

}
