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

public class QueryLanguageTestCase
        extends AbstractTestCase {

    @Test
    public void test_simple_sequence_access()
            throws Exception {

        String query = "#{'b'}(1)->number";
        Input input = Input.fromByteArray(hexToBytes("0xbf61610161629f0203ffff"));
        Parser parser = Parser.newBuilder(input).build();
        Value value = parser.read(query);
        assertEqualsNumber(3, value.number());
    }

}
