/*
 * Copyright (c) 2016-2018, Christoph Engelbert (aka noctarius) and
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

import com.noctarius.borabora.AbstractTestCase;
import com.noctarius.borabora.Dictionary;
import com.noctarius.borabora.Input;
import com.noctarius.borabora.Output;
import com.noctarius.borabora.Parser;
import com.noctarius.borabora.Predicates;
import com.noctarius.borabora.Query;
import com.noctarius.borabora.Sequence;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.ValueTypes;
import com.noctarius.borabora.Writer;
import com.noctarius.borabora.builder.encoder.GraphBuilder;
import com.noctarius.borabora.spi.query.ObjectProjectionStrategy;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SequenceTestCase
        extends AbstractTestCase {

    @Test
    public void test_empty_sequence()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x80");
        Value value = parser.read(parser.newQueryBuilder().build());
        Sequence sequence = value.sequence();
        assertEquals(0, sequence.size());
    }

    @Test
    public void test_sequence_get_outside_range()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x80");
        Value value = parser.read(parser.newQueryBuilder().build());
        Sequence sequence = value.sequence();
        assertNull(sequence.get(1));
    }

    @Test
    public void test_sequence_get_negative_outside_range()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x80");
        Value value = parser.read(parser.newQueryBuilder().build());
        Sequence sequence = value.sequence();
        assertNull(sequence.get(-1));
    }

    @Test
    public void test_sequence_isempty_true()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x80");
        Value value = parser.read(parser.newQueryBuilder().build());
        Sequence sequence = value.sequence();
        assertTrue(sequence.isEmpty());
    }

    @Test
    public void test_indefinite_sequence_isempty_true()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x9fff");
        Value value = parser.read(parser.newQueryBuilder().build());
        Sequence sequence = value.sequence();
        assertTrue(sequence.isEmpty());
    }

    @Test
    public void test_sequence_isempty_false()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x83010203");
        Value value = parser.read(parser.newQueryBuilder().build());
        Sequence sequence = value.sequence();
        assertFalse(sequence.isEmpty());
    }

    @Test
    public void test_indefinite_sequence_isempty_false()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x9f010203ff");
        Value value = parser.read(parser.newQueryBuilder().build());
        Sequence sequence = value.sequence();
        assertFalse(sequence.isEmpty());
    }

    @Test
    public void test_sequence()
            throws Exception {

        long_sequence("0x98190102030405060708090a0b0c0d0e0f101112131415161718181819");
    }

    @Test
    public void test_indefinite_sequence()
            throws Exception {

        long_sequence("0x9f0102030405060708090a0b0c0d0e0f101112131415161718181819ff");
    }

    private void long_sequence(String hex) {
        SimplifiedTestParser parser = buildParser(hex);
        Value value = parser.read(parser.newQueryBuilder().build());

        Sequence sequence = value.sequence();
        for (int i = 1; i < 26; i++) {
            Value element = sequence.get(i - 1);
            Assert.assertEquals(ValueTypes.UInt, element.valueType());
            assertEqualsNumber(i, element.number());

            Query query = parser.newQueryBuilder().sequence(i - 1).build();
            element = parser.read(query);
            assertEquals(ValueTypes.UInt, element.valueType());
            assertEqualsNumber(i, element.number());
        }
    }

    @Test
    public void long_sequence_stream()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x9f0102030405060708090a0b0c0d0e0f101112131415161718181819ff");
        Value value = parser.read(parser.newQueryBuilder().build());

        Sequence sequence = value.sequence();
        List<Number> numbers = sequence.stream().map(v -> v.number()).collect(toList());

        Iterator<Number> iterator = numbers.iterator();

        int position = 1;
        while (iterator.hasNext()) {
            Number v = iterator.next();
            assertEqualsNumber(position++, v.longValue());
        }
    }

    @Test
    public void long_sequence_parallel_stream()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x9f0102030405060708090a0b0c0d0e0f101112131415161718181819ff");
        Value value = parser.read(parser.newQueryBuilder().build());

        Sequence sequence = value.sequence();
        List<Long> numbers = sequence.parallelStream().map(v -> v.number().longValue())
                                     .collect(toCollection(CopyOnWriteArrayList::new));

        numbers.sort(Comparator.naturalOrder());
        Iterator<Long> iterator = numbers.iterator();

        int position = 1;
        while (iterator.hasNext()) {
            Number v = iterator.next();
            assertEqualsNumber(position++, v);
        }
    }

    @Test
    public void long_sequence_iterator()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x9f0102030405060708090a0b0c0d0e0f101112131415161718181819ff");
        Value value = parser.read(parser.newQueryBuilder().build());

        Sequence sequence = value.sequence();
        Iterator<Value> iterator = sequence.iterator();

        int position = 1;
        while (iterator.hasNext()) {
            Value v = iterator.next();
            assertEqualsNumber(position++, v.number());
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void long_sequence_iterator_no_such_element()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x9f0102030405060708090a0b0c0d0e0f101112131415161718181819ff");
        Value value = parser.read(parser.newQueryBuilder().build());

        Sequence sequence = value.sequence();
        Iterator<Value> iterator = sequence.iterator();

        for (int i = 0; i < sequence.size(); i++) {
            try {
                Value v = iterator.next();
                assertEqualsNumber(i + 1, v.number());
            } catch (NoSuchElementException e) {
                throw new AssertionError(e);
            }
        }

        iterator.next();
    }

    @Test
    public void sequence_to_array()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x9f0102030405060708090a0b0c0d0e0f101112131415161718181819ff");
        Value value = parser.read(parser.newQueryBuilder().build());

        Sequence sequence = value.sequence();
        Value[] values = sequence.toArray();

        for (int i = 0; i < values.length; i++) {
            assertEqualsNumber(i + 1, values[i].number());
        }
    }

    @Test(expected = IllegalStateException.class)
    public void sequence_to_array_size_to_large()
            throws Exception {

        Sequence sequence = new SequenceImpl(Long.MAX_VALUE, new long[0][0], //
                newQueryContext(Input.fromByteArray(new byte[0]), Collections.emptyList(), ObjectProjectionStrategy.INSTANCE));

        sequence.toArray();
    }

    @Test
    public void test_contains_value_true()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x9f0102030405060708090a0b0c0d0e0f101112131415161718181819ff");
        Value value = parser.read(parser.newQueryBuilder().build());

        Sequence sequence = value.sequence();

        assertTrue(sequence.contains((v) -> v.number().longValue() == 2));
    }

    @Test
    public void test_stream_contains_value_true()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x9f0102030405060708090a0b0c0d0e0f101112131415161718181819ff");
        Value value = parser.read(parser.newQueryBuilder().build());

        Sequence sequence = value.sequence();

        assertTrue(sequence.contains(Predicates.matchInt(2)));
    }

    @Test
    public void test_contains_value_false()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x9f0102030405060708090a0b0c0d0e0f101112131415161718181819ff");
        Value value = parser.read(parser.newQueryBuilder().build());

        Sequence sequence = value.sequence();

        assertFalse(sequence.contains((v) -> v.number().longValue() == 30));
    }

    @Test
    public void test_stream_contains_value_false()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x9f0102030405060708090a0b0c0d0e0f101112131415161718181819ff");
        Value value = parser.read(parser.newQueryBuilder().build());

        Sequence sequence = value.sequence();

        assertFalse(sequence.contains(Predicates.matchInt(30)));
    }

    @Test
    public void test_sequence_dictionary()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x826161a161626163");
        Value value = parser.read(parser.newQueryBuilder().build());

        Sequence sequence = value.sequence();
        assertEquals(2, sequence.size());

        Value element1 = sequence.get(0);
        assertEquals("a", element1.string());

        Value element2 = sequence.get(1);
        Dictionary dictionary = element2.dictionary();
        assertEquals("c", dictionary.get((v) -> "b".equals(v.string())).string());
    }

    @Test
    public void test_indefinite_empty_sequence()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x9fff");
        Value value = parser.read(parser.newQueryBuilder().build());

        assertEquals(0, value.sequence().size());
    }

    @Test
    public void test_indefinite_sequence_nested_sequence_sequence()
            throws Exception {

        test_sequence("0x9f01820203820405ff");
    }

    @Test
    public void test_indefinite_sequence_nested_sequence_indefinite_sequence()
            throws Exception {

        test_sequence("0x9f018202039f0405ffff");
    }

    @Test
    public void test_sequence_nested_sequence_sequence()
            throws Exception {

        test_sequence("0x8301820203820405");
    }

    @Test
    public void test_sequence_nested_sequence_indefinite_sequence()
            throws Exception {

        test_sequence("0x83018202039f0405ff");
    }

    @Test
    public void test_sequence_nested_indefinite_sequence_sequence()
            throws Exception {

        test_sequence("0x83019f0203ff820405");
    }

    @Test
    public void test_small_sequence()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x83010203");
        Value value = parser.read(parser.newQueryBuilder().build());
        Sequence sequence = value.sequence();
        for (int i = 0; i < 3; i++) {
            assertEqualsNumber(i + 1, parser.read(parser.newQueryBuilder().sequence(i).build()).number());
            assertEqualsNumber(i + 1, sequence.get(i).number());
        }
    }

    @Test
    public void test_sequence_asstring() {
        String expected = "[UInt{ 1 }, UInt{ 2 }, UInt{ 3 }]";
        SimplifiedTestParser parser = buildParser("0x83010203");
        Value value = parser.read(parser.newQueryBuilder().build());
        Sequence sequence = value.sequence();
        String actual = sequence.asString();
        assertEquals(expected, actual);
    }

    @Test
    public void test_sequence_tostring() {
        String expected = "[StreamValue{valueType=UInt, offset=1, value=1}, " //
                + "StreamValue{valueType=UInt, offset=2, value=2}, " //
                + "StreamValue{valueType=UInt, offset=3, value=3}]";
        SimplifiedTestParser parser = buildParser("0x83010203");
        Value value = parser.read(parser.newQueryBuilder().build());
        Sequence sequence = value.sequence();
        String actual = sequence.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void test_indefinite_sequence_dictionary_indexes()
            throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Writer writer = Writer.newWriter();
        GraphBuilder graphBuilder = writer.newGraphBuilder(Output.toOutputStream(baos));

        graphBuilder.putSequence()

                    .putDictionary(2) //
                    .putEntry().putString("key-1").putString("value-1").endEntry() //
                    .putEntry().putString("key-2").putString("value-2").endEntry() //
                    .endDictionary()

                    .putDictionary(2) //
                    .putEntry().putString("key-3").putString("value-3").endEntry() //
                    .putEntry().putString("key-4").putString("value-4").endEntry() //
                    .endDictionary()

                    .endSequence().finishStream();

        Input input = Input.fromByteArray(baos.toByteArray());
        Parser parser = Parser.newParser();

        Value value = parser.read(input, parser.newQueryBuilder().build());
        assertTrue(value.valueType().matches(ValueTypes.Sequence));

        Sequence sequence = value.sequence();
        assertEquals(2, sequence.size());

        Value entry1 = sequence.get(0);
        Value entry2 = sequence.get(1);

        assertTrue(entry1.valueType().matches(ValueTypes.Dictionary));
        assertTrue(entry2.valueType().matches(ValueTypes.Dictionary));
    }

    @Test
    public void test_sequence_dictionary_indexes()
            throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Writer writer = Writer.newWriter();
        GraphBuilder graphBuilder = writer.newGraphBuilder(Output.toOutputStream(baos));

        graphBuilder.putSequence(2)

                    .putDictionary(2) //
                    .putEntry().putString("key-1").putString("value-1").endEntry() //
                    .putEntry().putString("key-2").putString("value-2").endEntry() //
                    .endDictionary()

                    .putDictionary(2) //
                    .putEntry().putString("key-3").putString("value-3").endEntry() //
                    .putEntry().putString("key-4").putString("value-4").endEntry() //
                    .endDictionary()

                    .endSequence().finishStream();

        Input input = Input.fromByteArray(baos.toByteArray());
        Parser parser = Parser.newParser();

        Value value = parser.read(input, parser.newQueryBuilder().build());
        assertTrue(value.valueType().matches(ValueTypes.Sequence));

        Sequence sequence = value.sequence();
        assertEquals(2, sequence.size());

        Value entry1 = sequence.get(0);
        Value entry2 = sequence.get(1);

        assertTrue(entry1.valueType().matches(ValueTypes.Dictionary));
        assertTrue(entry2.valueType().matches(ValueTypes.Dictionary));
    }

    @Test
    public void test_match_any_sequence_element() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Writer writer = Writer.newWriter();
        GraphBuilder graphBuilder = writer.newGraphBuilder(Output.toOutputStream(baos));

        graphBuilder.putSequence()

                    .putString("a").putString("b").putString("c")

                    .endSequence().finishStream();

        Input input = Input.fromByteArray(baos.toByteArray());
        Parser parser = Parser.newParser();

        Query query = parser.newQueryBuilder().sequenceMatch(Predicates.any()).build();

        List<Value> result = new ArrayList<>();
        parser.read(input, query, result::add);

        assertEquals(3, result.size());
        assertEquals("a", result.get(0).string());
        assertEquals("b", result.get(1).string());
        assertEquals("c", result.get(2).string());
    }

    private void test_sequence(String hex)
            throws Exception {

        SimplifiedTestParser parser = buildParser(hex);
        test_using_sequence_graph(parser);
        test_using_sequence_traversal(parser);
    }

    private void test_using_sequence_graph(SimplifiedTestParser parser) {
        Query i0e0 = parser.newQueryBuilder().sequence(0).build();
        Query i1e0 = parser.newQueryBuilder().sequence(1).sequence(0).build();
        Query i1e1 = parser.newQueryBuilder().sequence(1).sequence(1).build();
        Query i2e0 = parser.newQueryBuilder().sequence(2).sequence(0).build();
        Query i2e1 = parser.newQueryBuilder().sequence(2).sequence(1).build();

        Value v1 = parser.read(i0e0);
        Value v2 = parser.read(i1e0);
        Value v3 = parser.read(i1e1);
        Value v4 = parser.read(i2e0);
        Value v5 = parser.read(i2e1);

        assertEqualsNumber(1, v1.number());
        assertEqualsNumber(2, v2.number());
        assertEqualsNumber(3, v3.number());
        assertEqualsNumber(4, v4.number());
        assertEqualsNumber(5, v5.number());
    }

    private void test_using_sequence_traversal(SimplifiedTestParser parser) {
        Value value = parser.read(parser.newQueryBuilder().build());

        Sequence sequence = value.sequence();

        Value valueIndex0 = sequence.get(0);
        Value valueIndex1 = sequence.get(1);
        Value valueIndex2 = sequence.get(2);

        assertEquals(ValueTypes.UInt, valueIndex0.valueType());
        assertEquals(ValueTypes.Sequence, valueIndex1.valueType());
        assertEquals(ValueTypes.Sequence, valueIndex2.valueType());

        assertEqualsNumber(1, valueIndex0.number());

        assertEqualsNumber(2, valueIndex1.sequence().get(0).number());
        assertEqualsNumber(3, valueIndex1.sequence().get(1).number());

        assertEqualsNumber(4, valueIndex2.sequence().get(0).number());
        assertEqualsNumber(5, valueIndex2.sequence().get(1).number());
    }

}
