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

import com.noctarius.borabora.builder.StreamGraphBuilder;
import com.noctarius.borabora.spi.Dictionary;
import com.noctarius.borabora.spi.QueryContext;
import com.noctarius.borabora.spi.Sequence;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.noctarius.borabora.StreamPredicates.matchInt;
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
        Value value = parser.read(GraphQuery.newBuilder().build());
        Sequence sequence = value.sequence();
        assertEquals(0, sequence.size());
    }

    @Test
    public void test_sequence_get_outside_range()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x80");
        Value value = parser.read(GraphQuery.newBuilder().build());
        Sequence sequence = value.sequence();
        assertNull(sequence.get(1));
    }

    @Test
    public void test_sequence_get_negative_outside_range()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x80");
        Value value = parser.read(GraphQuery.newBuilder().build());
        Sequence sequence = value.sequence();
        assertNull(sequence.get(-1));
    }

    @Test
    public void test_sequence_isempty_true()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x80");
        Value value = parser.read(GraphQuery.newBuilder().build());
        Sequence sequence = value.sequence();
        assertTrue(sequence.isEmpty());
    }

    @Test
    public void test_indefinite_sequence_isempty_true()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x9fff");
        Value value = parser.read(GraphQuery.newBuilder().build());
        Sequence sequence = value.sequence();
        assertTrue(sequence.isEmpty());
    }

    @Test
    public void test_sequence_isempty_false()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x83010203");
        Value value = parser.read(GraphQuery.newBuilder().build());
        Sequence sequence = value.sequence();
        assertFalse(sequence.isEmpty());
    }

    @Test
    public void test_indefinite_sequence_isempty_false()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x9f010203ff");
        Value value = parser.read(GraphQuery.newBuilder().build());
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
        Value value = parser.read(GraphQuery.newBuilder().build());

        Sequence sequence = value.sequence();
        for (int i = 1; i < 26; i++) {
            Value element = sequence.get(i - 1);
            assertEquals(ValueTypes.UInt, element.valueType());
            assertEqualsNumber(i, element.number());

            GraphQuery query = GraphQuery.newBuilder().sequence(i - 1).build();
            element = parser.read(query);
            assertEquals(ValueTypes.UInt, element.valueType());
            assertEqualsNumber(i, element.number());
        }
    }

    @Test
    public void long_sequence_iterator()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x9f0102030405060708090a0b0c0d0e0f101112131415161718181819ff");
        Value value = parser.read(GraphQuery.newBuilder().build());

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
        Value value = parser.read(GraphQuery.newBuilder().build());

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
        Value value = parser.read(GraphQuery.newBuilder().build());

        Sequence sequence = value.sequence();
        Value[] values = sequence.toArray();

        for (int i = 0; i < values.length; i++) {
            assertEqualsNumber(i + 1, values[i].number());
        }
    }

    @Test(expected = IllegalStateException.class)
    public void sequence_to_array_size_to_large()
            throws Exception {

        Sequence sequence = new SequenceImpl( //
                Long.MAX_VALUE, new long[0][0], new QueryContextImpl(Input.fromByteArray(new byte[0]), Collections.emptyList()));

        sequence.toArray();
    }

    @Test
    public void test_contains_value_true()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x9f0102030405060708090a0b0c0d0e0f101112131415161718181819ff");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Sequence sequence = value.sequence();

        assertTrue(sequence.contains((v) -> v.number().longValue() == 2));
    }

    @Test
    public void test_stream_contains_value_true()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x9f0102030405060708090a0b0c0d0e0f101112131415161718181819ff");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Sequence sequence = value.sequence();

        assertTrue(sequence.contains(matchInt(2)));
    }

    @Test
    public void test_contains_value_false()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x9f0102030405060708090a0b0c0d0e0f101112131415161718181819ff");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Sequence sequence = value.sequence();

        assertFalse(sequence.contains((v) -> v.number().longValue() == 30));
    }

    @Test
    public void test_stream_contains_value_false()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x9f0102030405060708090a0b0c0d0e0f101112131415161718181819ff");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Sequence sequence = value.sequence();

        assertFalse(sequence.contains(matchInt(30)));
    }

    @Test
    public void test_sequence_dictionary()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x826161a161626163");
        Value value = parser.read(GraphQuery.newBuilder().build());

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
        Value value = parser.read(GraphQuery.newBuilder().build());

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
        Value value = parser.read(GraphQuery.newBuilder().build());
        Sequence sequence = value.sequence();
        for (int i = 0; i < 3; i++) {
            assertEqualsNumber(i + 1, parser.read(GraphQuery.newBuilder().sequence(i).build()).number());
            assertEqualsNumber(i + 1, sequence.get(i).number());
        }
    }

    @Test
    public void test_indefinite_sequence_dictionary_indexes()
            throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        StreamWriter writer = StreamWriter.newBuilder().build();
        StreamGraphBuilder graphBuilder = writer.newStreamGraphBuilder(Output.toOutputStream(baos));

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
        Parser parser = Parser.newBuilder().build();

        Value value = parser.read(input, GraphQuery.newBuilder().build());
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

        StreamWriter writer = StreamWriter.newBuilder().build();
        StreamGraphBuilder graphBuilder = writer.newStreamGraphBuilder(Output.toOutputStream(baos));

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
        Parser parser = Parser.newBuilder().build();

        Value value = parser.read(input, GraphQuery.newBuilder().build());
        assertTrue(value.valueType().matches(ValueTypes.Sequence));

        Sequence sequence = value.sequence();
        assertEquals(2, sequence.size());

        Value entry1 = sequence.get(0);
        Value entry2 = sequence.get(1);

        assertTrue(entry1.valueType().matches(ValueTypes.Dictionary));
        assertTrue(entry2.valueType().matches(ValueTypes.Dictionary));
    }

    private void test_sequence(String hex)
            throws Exception {

        SimplifiedTestParser parser = buildParser(hex);
        test_using_sequence_graph(parser);
        test_using_sequence_traversal(parser);
    }

    private void test_using_sequence_graph(SimplifiedTestParser parser) {
        GraphQuery i0e0 = GraphQuery.newBuilder().sequence(0).build();
        GraphQuery i1e0 = GraphQuery.newBuilder().sequence(1).sequence(0).build();
        GraphQuery i1e1 = GraphQuery.newBuilder().sequence(1).sequence(1).build();
        GraphQuery i2e0 = GraphQuery.newBuilder().sequence(2).sequence(0).build();
        GraphQuery i2e1 = GraphQuery.newBuilder().sequence(2).sequence(1).build();

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
        Value value = parser.read(GraphQuery.newBuilder().build());

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
