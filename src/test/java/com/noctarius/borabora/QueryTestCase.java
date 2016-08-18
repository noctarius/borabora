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

import java.util.ArrayList;
import java.util.List;

import static com.noctarius.borabora.Value.NULL_VALUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class QueryTestCase
        extends AbstractTestCase {

    @Test
    public void test_multistream_dictionary_query_first_element_break_second_found() {
        SimplifiedTestParser parser = buildParser(gb -> //
                gb.putDictionary(1).putEntry().putString("bar").putString("bar").endEntry().endDictionary() //
                  .putDictionary(1).putEntry().putString("foo").putString("foo").endEntry().endDictionary());

        List<Value> values = new ArrayList<>();
        Query query = Query.newBuilder().multiStream().dictionary("foo").build();
        parser.read(query, values::add);

        assertEquals(2, values.size());
        assertEquals(Value.NULL_VALUE, values.get(0));
        assertEquals("foo", values.get(1).string());
    }

    @Test(expected = WrongTypeException.class)
    public void test_not_a_dictionary()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x83010203");
        parser.read(Query.newBuilder().dictionary((v) -> "b".equals(v.string())).build());
    }

    @Test
    public void test_sequence_index_not_found()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x83010203");
        Value value = parser.read(Query.newBuilder().sequence(4).build());
        assertSame(NULL_VALUE, value);
    }

    @Test
    public void test_dictionary_property_not_found()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xbf61610161629f0203ffff");
        Value value = parser.read(Query.newBuilder().dictionary((v) -> "c".equals(v.string())).sequence(0).build());
        assertSame(NULL_VALUE, value);
    }

    @Test(expected = WrongTypeException.class)
    public void test_not_a_sequence()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xbf61610161629f0203ffff");
        parser.read(Query.newBuilder().sequence(0).build());
    }

    @Test
    public void code_coverage_for_unused_but_generated_methods()
            throws Exception {

        Query query = Query.newBuilder().stream(1).dictionary("b").sequence(1).build();
        query.toString();
    }

}
