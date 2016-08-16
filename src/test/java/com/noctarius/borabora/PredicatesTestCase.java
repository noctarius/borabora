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

import com.noctarius.borabora.spi.ObjectValue;
import org.junit.Test;

import java.util.function.Predicate;

import static com.noctarius.borabora.Predicates.matchString;
import static com.noctarius.borabora.Predicates.matchStringIgnoreCase;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class PredicatesTestCase {

    @Test
    public void test_matchignorecase_bytestring() {
        Predicate<Value> predicate = matchStringIgnoreCase("foo");

        Value value = new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, "foo");
        assertTrue(predicate.test(value));

        Value value2 = new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, "Foo");
        assertTrue(predicate.test(value2));

        Value value3 = new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, "Foo2");
        assertFalse(predicate.test(value3));
    }

    @Test
    public void test_matchignorecase_bytestring_string() {
        Predicate<Value> predicate = matchStringIgnoreCase("foo");

        Value value = new ObjectValue(MajorType.ByteString, ValueTypes.String, "foo");
        assertTrue(predicate.test(value));

        Value value2 = new ObjectValue(MajorType.ByteString, ValueTypes.String, "Foo");
        assertTrue(predicate.test(value2));

        Value value3 = new ObjectValue(MajorType.ByteString, ValueTypes.String, "Foo2");
        assertFalse(predicate.test(value3));
    }

    @Test
    public void test_matchignorecase_textstring() {
        Predicate<Value> predicate = matchStringIgnoreCase("foo");

        Value value = new ObjectValue(MajorType.TextString, ValueTypes.TextString, "foo");
        assertTrue(predicate.test(value));

        Value value2 = new ObjectValue(MajorType.TextString, ValueTypes.TextString, "Foo");
        assertTrue(predicate.test(value2));

        Value value3 = new ObjectValue(MajorType.TextString, ValueTypes.TextString, "Foo2");
        assertFalse(predicate.test(value3));
    }

    @Test
    public void test_matchignorecase_textstring_string() {
        Predicate<Value> predicate = matchStringIgnoreCase("foo");

        Value value = new ObjectValue(MajorType.TextString, ValueTypes.String, "foo");
        assertTrue(predicate.test(value));

        Value value2 = new ObjectValue(MajorType.TextString, ValueTypes.String, "Foo");
        assertTrue(predicate.test(value2));

        Value value3 = new ObjectValue(MajorType.TextString, ValueTypes.String, "Foo2");
        assertFalse(predicate.test(value3));
    }

    @Test
    public void test_matchignorecase_non_string() {
        Value value = new ObjectValue(MajorType.Unknown, ValueTypes.Unknown, null);
        Predicate<Value> predicate = matchStringIgnoreCase("foo");
        assertFalse(predicate.test(value));
    }

    @Test
    public void test_matchstring_large_string() {
        String largeString = new String(new char[1025]);
        Predicate<Value> predicate = matchString(largeString);

        Value value = new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, "foo");
        assertFalse(predicate.test(value));
        value = new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, largeString);
        assertTrue(predicate.test(value));

        Value value2 = new ObjectValue(MajorType.ByteString, ValueTypes.String, "foo");
        assertFalse(predicate.test(value2));
        value2 = new ObjectValue(MajorType.ByteString, ValueTypes.String, largeString);
        assertTrue(predicate.test(value2));

        Value value3 = new ObjectValue(MajorType.TextString, ValueTypes.TextString, "foo");
        assertFalse(predicate.test(value3));
        value3 = new ObjectValue(MajorType.TextString, ValueTypes.TextString, largeString);
        assertTrue(predicate.test(value3));

        Value value4 = new ObjectValue(MajorType.TextString, ValueTypes.String, "foo");
        assertFalse(predicate.test(value4));
        value4 = new ObjectValue(MajorType.TextString, ValueTypes.String, largeString);
        assertTrue(predicate.test(value4));

        Value value5 = new ObjectValue(MajorType.Unknown, ValueTypes.Unknown, null);
        assertFalse(predicate.test(value5));
    }

}
