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
package com.noctarius.borabora.spi;

import com.noctarius.borabora.AbstractTestCase;
import com.noctarius.borabora.Dictionary;
import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.Sequence;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.ValueTypes;
import com.noctarius.borabora.WrongTypeException;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ObjectValueTestCase
        extends AbstractTestCase {

    @Test
    public void test_majortype() {
        Value value = new ObjectValue(MajorType.SemanticTag, ValueTypes.UBigNum, BigInteger.ONE);
        assertEquals(MajorType.SemanticTag, value.majorType());
    }

    @Test
    public void test_tag() {
        Value value = new ObjectValue(MajorType.SemanticTag, ValueTypes.UBigNum, BigInteger.ONE);
        assertEquals(BigInteger.ONE, value.tag());
    }

    @Test
    public void test_number() {
        Value value = new ObjectValue(MajorType.UnsignedInteger, ValueTypes.UInt, 1);
        assertEquals(1, value.number().intValue());
    }

    @Test
    public void test_sequence()
            throws Exception {

        List<Value> values = new ArrayList<>();
        values.add(new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, "foo"));
        values.add(new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, "bar"));

        Value value = asObjectSequence(values);

        Sequence sequence = value.sequence();
        assertEquals(2, sequence.size());
    }

    @Test
    public void test_dictionary()
            throws Exception {

        Value foo = new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, "foo");
        Value bar = new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, "bar");

        Map<Value, Value> values = new HashMap<>();
        values.put(foo, bar);
        values.put(bar, foo);

        Value value = asObjectDictionary(values);

        Dictionary dictionary = value.dictionary();
        assertEquals(2, dictionary.size());
    }

    @Test
    public void test_string() {
        Value value = new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, "foo");
        assertEquals("foo", value.string());
        Value value2 = new ObjectValue(MajorType.TextString, ValueTypes.TextString, "bar");
        assertEquals("bar", value2.string());
    }

    @Test
    public void test_bool() {
        Value value = new ObjectValue(MajorType.FloatingPointOrSimple, ValueTypes.Bool, Boolean.TRUE);
        assertEquals(Boolean.TRUE, value.bool());
    }

    @Test(expected = WrongTypeException.class)
    public void test_raw() {
        Value value = new ObjectValue(MajorType.SemanticTag, ValueTypes.UBigNum, BigInteger.ONE);
        value.raw();
    }

    @Test
    public void test_byvaluetype() {
        Value value = new ObjectValue(MajorType.SemanticTag, ValueTypes.UBigNum, BigInteger.ONE);
        assertEquals(BigInteger.ONE, value.byValueType());
    }

    @Test
    public void test_offset() {
        Value value = new ObjectValue(MajorType.SemanticTag, ValueTypes.UBigNum, BigInteger.ONE);
        assertEquals(-1, value.offset());
    }

    @Test
    public void test_tostring() {
        Value value = new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, "foo");
        assertEquals("ObjectValue{valueType=ByteString, value=foo}", value.toString());
    }

    @Test
    public void test_asstring() {
        Value value = new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, "foo");
        assertEquals("ByteString{ foo }", value.asString());
    }

    @Test
    public void test_asstring_sequence()
            throws Exception {

        List<Value> values = new ArrayList<>();
        values.add(new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, "foo"));
        values.add(new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, "bar"));

        Value value = asObjectSequence(values);
        assertEquals("Sequence{ [ByteString{ foo }, ByteString{ bar }] }", value.asString());
    }

    @Test
    public void test_asstring_dictionary()
            throws Exception {

        Value foo = new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, "foo");
        Value bar = new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, "bar");

        Map<Value, Value> mapValues = new HashMap<>();
        mapValues.put(foo, bar);

        Value value = asObjectDictionary(mapValues);
        assertEquals("Dictionary{ [ByteString{ foo }=ByteString{ bar }] }", value.asString());
    }

    private Value asObjectSequence(List<Value> values)
            throws Exception {

        Class<?> type = Class.forName("com.noctarius.borabora.spi.query.ObjectProjectionStrategy$ListBackedSequence");
        Constructor<Sequence> constructor = (Constructor) type.getDeclaredConstructor(List.class);
        constructor.setAccessible(true);

        Sequence sequence = constructor.newInstance(values);

        return new ObjectValue(MajorType.Sequence, ValueTypes.Sequence, sequence);
    }

    private Value asObjectDictionary(Map<Value, Value> values)
            throws Exception {

        Class<?> type = Class.forName("com.noctarius.borabora.spi.query.ObjectProjectionStrategy$MapBackedDictionary");
        Constructor<Dictionary> constructor = (Constructor) type.getDeclaredConstructor(Map.class);
        constructor.setAccessible(true);

        Dictionary dictionary = constructor.newInstance(values);

        return new ObjectValue(MajorType.Dictionary, ValueTypes.Dictionary, dictionary);
    }

}
