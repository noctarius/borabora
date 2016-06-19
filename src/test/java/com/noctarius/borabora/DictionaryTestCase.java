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

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import static com.noctarius.borabora.StreamPredicates.matchString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DictionaryTestCase
        extends AbstractTestCase {

    @Test
    public void test_empty_dictionary()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xa0");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Dictionary dictionary = value.dictionary();
        assertEquals(0, dictionary.size());
    }

    @Test
    public void test_multi_element_dictionary()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xa56161614161626142616361436164614461656145");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Dictionary dictionary = value.dictionary();
        assertEquals("A", dictionary.get((v) -> "a".equals(v.string())).string());
        assertEquals("B", dictionary.get((v) -> "b".equals(v.string())).string());
        assertEquals("C", dictionary.get((v) -> "c".equals(v.string())).string());
        assertEquals("D", dictionary.get((v) -> "d".equals(v.string())).string());
        assertEquals("E", dictionary.get((v) -> "e".equals(v.string())).string());
    }

    @Test
    public void test_stream_multi_element_dictionary()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xa56161614161626142616361436164614461656145");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Dictionary dictionary = value.dictionary();
        assertEquals("A", dictionary.get(matchString("a")).string());
        assertEquals("B", dictionary.get(matchString("b")).string());
        assertEquals("C", dictionary.get(matchString("c")).string());
        assertEquals("D", dictionary.get(matchString("d")).string());
        assertEquals("E", dictionary.get(matchString("e")).string());
    }

    @Test
    public void test_dictionary_key_iterator()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xa56161614161626142616361436164614461656145");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Dictionary dictionary = value.dictionary();
        Iterable<Value> keys = dictionary.keys();
        Iterator<Value> iterator = keys.iterator();

        assertEquals("a", iterator.next().string());
        assertEquals("b", iterator.next().string());
        assertEquals("c", iterator.next().string());
        assertEquals("d", iterator.next().string());
        assertEquals("e", iterator.next().string());
    }

    @Test
    public void test_dictionary_key_iterator_hasnext()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xa56161614161626142616361436164614461656145");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Dictionary dictionary = value.dictionary();
        Iterable<Value> keys = dictionary.keys();
        Iterator<Value> iterator = keys.iterator();

        assertTrue(iterator.hasNext());
        assertEquals("a", iterator.next().string());

        assertTrue(iterator.hasNext());
        assertEquals("b", iterator.next().string());

        assertTrue(iterator.hasNext());
        assertEquals("c", iterator.next().string());

        assertTrue(iterator.hasNext());
        assertEquals("d", iterator.next().string());

        assertTrue(iterator.hasNext());
        assertEquals("e", iterator.next().string());

        assertFalse(iterator.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void test_dictionary_key_iterator_next_nosuchelement()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xa0");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Dictionary dictionary = value.dictionary();
        Iterable<Value> keys = dictionary.keys();
        Iterator<Value> iterator = keys.iterator();
        iterator.next();
    }

    @Test
    public void test_dictionary_value_iterator()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xa56161614161626142616361436164614461656145");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Dictionary dictionary = value.dictionary();
        Iterable<Value> values = dictionary.values();
        Iterator<Value> iterator = values.iterator();

        assertEquals("A", iterator.next().string());
        assertEquals("B", iterator.next().string());
        assertEquals("C", iterator.next().string());
        assertEquals("D", iterator.next().string());
        assertEquals("E", iterator.next().string());
    }

    @Test
    public void test_dictionary_value_iterator_hasnext()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xa56161614161626142616361436164614461656145");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Dictionary dictionary = value.dictionary();
        Iterable<Value> values = dictionary.values();
        Iterator<Value> iterator = values.iterator();

        assertTrue(iterator.hasNext());
        assertEquals("A", iterator.next().string());

        assertTrue(iterator.hasNext());
        assertEquals("B", iterator.next().string());

        assertTrue(iterator.hasNext());
        assertEquals("C", iterator.next().string());

        assertTrue(iterator.hasNext());
        assertEquals("D", iterator.next().string());

        assertTrue(iterator.hasNext());
        assertEquals("E", iterator.next().string());

        assertFalse(iterator.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void test_dictionary_value_iterator_next_nosuchelement()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xa0");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Dictionary dictionary = value.dictionary();
        Iterable<Value> values = dictionary.values();
        Iterator<Value> iterator = values.iterator();
        iterator.next();
    }

    @Test
    public void test_dictionary_entries_iterator()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xa56161614161626142616361436164614461656145");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Dictionary dictionary = value.dictionary();
        Iterator<Map.Entry<Value, Value>> iterator = dictionary.iterator();

        Map.Entry<Value, Value> entry = iterator.next();
        assertEquals("a", entry.getKey().string());
        assertEquals("A", entry.getValue().string());

        entry = iterator.next();
        assertEquals("b", entry.getKey().string());
        assertEquals("B", entry.getValue().string());

        entry = iterator.next();
        assertEquals("c", entry.getKey().string());
        assertEquals("C", entry.getValue().string());

        entry = iterator.next();
        assertEquals("d", entry.getKey().string());
        assertEquals("D", entry.getValue().string());

        entry = iterator.next();
        assertEquals("e", entry.getKey().string());
        assertEquals("E", entry.getValue().string());
    }

    @Test
    public void test_dictionary_entries_iterator_hasnext()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xa56161614161626142616361436164614461656145");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Dictionary dictionary = value.dictionary();
        Iterator<Map.Entry<Value, Value>> iterator = dictionary.iterator();

        assertTrue(iterator.hasNext());
        Map.Entry<Value, Value> entry = iterator.next();
        assertEquals("a", entry.getKey().string());
        assertEquals("A", entry.getValue().string());

        assertTrue(iterator.hasNext());
        entry = iterator.next();
        assertEquals("b", entry.getKey().string());
        assertEquals("B", entry.getValue().string());

        assertTrue(iterator.hasNext());
        entry = iterator.next();
        assertEquals("c", entry.getKey().string());
        assertEquals("C", entry.getValue().string());

        assertTrue(iterator.hasNext());
        entry = iterator.next();
        assertEquals("d", entry.getKey().string());
        assertEquals("D", entry.getValue().string());

        assertTrue(iterator.hasNext());
        entry = iterator.next();
        assertEquals("e", entry.getKey().string());
        assertEquals("E", entry.getValue().string());

        assertFalse(iterator.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void test_dictionary_entries_iterator_next_nosuchelement()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xa0");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Dictionary dictionary = value.dictionary();
        Iterator<Map.Entry<Value, Value>> iterator = dictionary.iterator();
        iterator.next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test_dictionary_entries_iterator_next_setvalue_unsupportedoperation()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xa56161614161626142616361436164614461656145");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Dictionary dictionary = value.dictionary();
        Iterator<Map.Entry<Value, Value>> iterator = dictionary.iterator();
        Map.Entry<Value, Value> entry = iterator.next();
        entry.setValue(null);
    }

    @Test
    public void test_dictionary_isempty_false()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xa56161614161626142616361436164614461656145");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Dictionary dictionary = value.dictionary();
        assertFalse(dictionary.isEmpty());
    }

    @Test
    public void test_indefinite_dictionary_isempty_false()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xbf6161614161626142616361436164614461656145ff");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Dictionary dictionary = value.dictionary();
        assertFalse(dictionary.isEmpty());
    }

    @Test
    public void test_dictionary_isempty_true()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xa0");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Dictionary dictionary = value.dictionary();
        assertTrue(dictionary.isEmpty());
    }

    @Test
    public void test_indefinite_dictionary_isempty_true()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xbfff");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Dictionary dictionary = value.dictionary();
        assertTrue(dictionary.isEmpty());
    }

    @Test
    public void test_dictionary_get_null_result()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xa0");
        Value value = parser.read(GraphQuery.newBuilder().build());
        Dictionary dictionary = value.dictionary();
        assertNull(dictionary.get((v) -> false));
    }

    @Test
    public void test_dictionary_stream_get_null_result()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xa0");
        Value value = parser.read(GraphQuery.newBuilder().build());
        Dictionary dictionary = value.dictionary();
        assertNull(dictionary.get((m, v, o, q) -> false));
    }

    @Test
    public void test_dictionary_contains_key()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xa56161614161626142616361436164614461656145");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Dictionary dictionary = value.dictionary();

        assertTrue(dictionary.containsKey((v) -> "b".equals(v.string())));
        assertFalse(dictionary.containsKey((v) -> "z".equals(v.string())));
    }

    @Test
    public void test_dictionary_stream_contains_key()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xa56161614161626142616361436164614461656145");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Dictionary dictionary = value.dictionary();

        assertTrue(dictionary.containsKey(matchString("b")));
        assertFalse(dictionary.containsKey(matchString("z")));
    }

    @Test
    public void test_dictionary_contains_value()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xa56161614161626142616361436164614461656145");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Dictionary dictionary = value.dictionary();

        assertTrue(dictionary.containsValue((v) -> "B".equals(v.string())));
        assertFalse(dictionary.containsValue((v) -> "Z".equals(v.string())));
    }

    @Test
    public void test_dictionary_stream_contains_value()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xa56161614161626142616361436164614461656145");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Dictionary dictionary = value.dictionary();

        assertTrue(dictionary.containsValue(matchString("B")));
        assertFalse(dictionary.containsValue(matchString("Z")));
    }

    @Test
    public void test_double_element_dictionary()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xa201020304");
        Value value = parser.read(GraphQuery.newBuilder().build());

        assertEquals(MajorType.Dictionary, value.majorType());
        assertEquals(ValueTypes.Dictionary, value.valueType());

        Dictionary dictionary = value.dictionary();
        assertEquals(2, dictionary.size());
        assertEqualsNumber(2, dictionary.get(matchNumber(1)).number());
        assertEqualsNumber(4, dictionary.get(matchNumber(3)).number());
    }

    @Test
    public void test_simple_map_graph_access()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xa201020304");
        GraphQuery query = GraphQuery.newBuilder().dictionary(this::matchNumber).build();
        Value value = parser.read(query);

        assertEquals(MajorType.UnsignedInteger, value.majorType());
        assertEquals(ValueTypes.UInt, value.valueType());

        assertEqualsNumber(4, value.number());
    }

    @Test
    public void test_indefinite_dictionary_bool_number()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xbf6346756ef563416d7421ff");
        Value value = parser.read(GraphQuery.newBuilder().build());

        assertEquals(ValueTypes.Dictionary, value.valueType());

        Dictionary dictionary = value.dictionary();

        assertEquals(2, dictionary.size());

        assertTrue(dictionary.get((v) -> "Fun".equals(v.string())).bool());
        assertEqualsNumber(-2, dictionary.get((v) -> "Amt".equals(v.string())).number());
    }

    @Test
    public void test_indefinite_dictionary_stream_bool_number()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xbf6346756ef563416d7421ff");
        Value value = parser.read(GraphQuery.newBuilder().build());

        assertEquals(ValueTypes.Dictionary, value.valueType());

        Dictionary dictionary = value.dictionary();

        assertEquals(2, dictionary.size());

        assertTrue(dictionary.get(matchString("Fun")).bool());
        assertEqualsNumber(-2, dictionary.get(matchString("Amt")).number());
    }

    @Test
    public void test_indefinite_dictionary_uint_indefinite_sequence()
            throws Exception {

        dictionary_uint_sequence("0xbf61610161629f0203ffff");
    }

    @Test
    public void test_dictionary_uint_sequence()
            throws Exception {

        dictionary_uint_sequence("0xa26161016162820203");
    }

    private void dictionary_uint_sequence(String hex) {
        SimplifiedTestParser parser = buildParser(hex);
        Value value = parser.read(GraphQuery.newBuilder().build());
        assertEquals(ValueTypes.Dictionary, value.valueType());

        Dictionary dictionary = value.dictionary();

        assertEquals(2, dictionary.size());
        assertEqualsNumber(1, dictionary.get((v) -> "a".equals(v.string())).number());

        Sequence sequence = dictionary.get((v) -> "b".equals(v.string())).sequence();
        assertEquals(2, sequence.size());
        assertEqualsNumber(2, sequence.get(0).number());
        assertEqualsNumber(3, sequence.get(1).number());
    }

    private boolean matchNumber(Value value) {
        if (ValueTypes.UInt != value.valueType()) {
            return false;
        }
        Number number = value.number();
        if (number instanceof BigInteger) {
            return number.equals(BigInteger.valueOf(3));
        }
        return number.longValue() == 3;
    }

    private Predicate<Value> matchNumber(long v) {
        return (value) -> {
            if (ValueTypes.UInt != value.valueType()) {
                return false;
            }
            Number number = value.number();
            if (number instanceof BigInteger) {
                return number.equals(BigInteger.valueOf(v));
            }
            return number.longValue() == v;
        };
    }

}
