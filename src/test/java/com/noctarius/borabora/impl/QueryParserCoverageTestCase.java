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

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.lang.reflect.Field;

public class QueryParserCoverageTestCase {

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

        QueryParserTokenManager tm = new QueryParserTokenManager(new JavaCharStream(new StringReader("")));
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
