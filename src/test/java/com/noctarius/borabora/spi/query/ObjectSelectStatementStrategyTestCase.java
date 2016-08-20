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
package com.noctarius.borabora.spi.query;

import com.noctarius.borabora.AbstractTestCase;
import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.Sequence;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.ValueTypes;
import com.noctarius.borabora.spi.ObjectValue;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static com.noctarius.borabora.Predicates.matchString;
import static com.noctarius.borabora.spi.query.ObjectSelectStatementStrategy.ListBackedSequence;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ObjectSelectStatementStrategyTestCase
        extends AbstractTestCase {

    @Test
    public void test_listbackedsequence_asstring() {
        String expected = "[ByteString{ foo }, ByteString{ bar }]";

        List<Value> values = new ArrayList<>();
        values.add(new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, "foo"));
        values.add(new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, "bar"));

        Value value = asObjectSequence(values);
        Sequence sequence = value.sequence();
        assertEquals(expected, sequence.asString());
    }

    @Test
    public void test_listbackedsequence_tostring() {
        String expected = "[ObjectValue{valueType=ByteString, value=foo}, ObjectValue{valueType=ByteString, value=bar}]";

        List<Value> values = new ArrayList<>();
        values.add(new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, "foo"));
        values.add(new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, "bar"));

        Value value = asObjectSequence(values);
        Sequence sequence = value.sequence();
        assertEquals(expected, sequence.toString());
    }

    @Test
    public void test_listbackedsequence_get() {
        List<Value> values = new ArrayList<>();
        values.add(new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, "foo"));
        values.add(new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, "bar"));

        Value value = asObjectSequence(values);
        Sequence sequence = value.sequence();
        assertEquals("foo", sequence.get(0).string());
        assertEquals("bar", sequence.get(1).string());
    }

    @Test
    public void test_listbackedsequence_toarray() {
        List<Value> values = new ArrayList<>();
        values.add(new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, "foo"));
        values.add(new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, "bar"));

        Value value = asObjectSequence(values);
        Sequence sequence = value.sequence();
        Value[] array = sequence.toArray();

        assertEquals("foo", array[0].string());
        assertEquals("bar", array[1].string());
    }

    @Test
    public void test_listbackedsequence_iterator_hasnext_next() {
        List<Value> values = new ArrayList<>();
        values.add(new ObjectValue(MajorType.UnsignedInteger, ValueTypes.UInt, 0));
        values.add(new ObjectValue(MajorType.UnsignedInteger, ValueTypes.UInt, 1));
        values.add(new ObjectValue(MajorType.UnsignedInteger, ValueTypes.UInt, 2));

        Value value = asObjectSequence(values);
        Sequence sequence = value.sequence();
        Iterator<Value> iterator = sequence.iterator();

        int counter = 0;
        while (iterator.hasNext()) {
            int v = iterator.next().number().intValue();
            assertEquals(counter++, v);
        }
    }

    @Test
    public void test_listbackedsequence_iterator_next() {
        List<Value> values = new ArrayList<>();
        values.add(new ObjectValue(MajorType.UnsignedInteger, ValueTypes.UInt, 0));
        values.add(new ObjectValue(MajorType.UnsignedInteger, ValueTypes.UInt, 1));
        values.add(new ObjectValue(MajorType.UnsignedInteger, ValueTypes.UInt, 2));

        Value value = asObjectSequence(values);
        Sequence sequence = value.sequence();
        Iterator<Value> iterator = sequence.iterator();

        assertEquals(0, iterator.next().number().intValue());
        assertEquals(1, iterator.next().number().intValue());
        assertEquals(2, iterator.next().number().intValue());
    }

    @Test(expected = NoSuchElementException.class)
    public void fail_listbackedsequence_iterator_nosuchelement() {
        List<Value> values = new ArrayList<>();

        Value value = asObjectSequence(values);
        Sequence sequence = value.sequence();
        Iterator<Value> iterator = sequence.iterator();

        iterator.next().number().intValue();
    }

    @Test
    public void test_listbackedsequence_contains() {
        List<Value> values = new ArrayList<>();
        values.add(new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, "foo"));
        values.add(new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, "bar"));

        Value value = asObjectSequence(values);
        Sequence sequence = value.sequence();

        assertTrue(sequence.contains(matchString("foo")));
        assertTrue(sequence.contains(matchString("bar")));
        assertFalse(sequence.contains(matchString("bar2")));
    }

    @Test
    public void test_listbackedsequence_isempty() {
        List<Value> values = new ArrayList<>();
        Value value = asObjectSequence(values);
        Sequence sequence = value.sequence();
        assertTrue(sequence.isEmpty());

        List<Value> values2 = new ArrayList<>();
        values2.add(Value.NULL_VALUE);
        Value value2 = asObjectSequence(values2);
        Sequence sequence2 = value2.sequence();
        assertFalse(sequence2.isEmpty());
    }

    @Test
    public void test_listbackedsequence_size() {
        List<Value> values = new ArrayList<>();
        Value value = asObjectSequence(values);
        Sequence sequence = value.sequence();
        assertEquals(0, sequence.size());

        List<Value> values2 = new ArrayList<>();
        values2.add(Value.NULL_VALUE);
        Value value2 = asObjectSequence(values2);
        Sequence sequence2 = value2.sequence();
        assertEquals(1, sequence2.size());
    }

    private Value asObjectSequence(List<Value> values) {
        QueryContext queryContext = newQueryContext();
        return new ObjectValue(MajorType.Sequence, ValueTypes.Sequence, new ListBackedSequence(values, queryContext));
    }

}
