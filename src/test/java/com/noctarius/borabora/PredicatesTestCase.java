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
import com.noctarius.borabora.spi.StreamValue;
import com.noctarius.borabora.spi.codec.Encoder;
import com.noctarius.borabora.spi.query.BinarySelectStatementStrategy;
import com.noctarius.borabora.spi.query.QueryContext;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Collections;
import java.util.function.Predicate;

import static com.noctarius.borabora.Predicates.any;
import static com.noctarius.borabora.Predicates.matchFloat;
import static com.noctarius.borabora.Predicates.matchInt;
import static com.noctarius.borabora.Predicates.matchString;
import static com.noctarius.borabora.Predicates.matchStringIgnoreCase;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class PredicatesTestCase
        extends AbstractTestCase {

    @Test
    public void call_constructor() {
        callConstructor(Predicates.class);
    }

    @Test
    public void test_any() {
        Predicate<Value> predicate = any();
        assertTrue(predicate.test(Value.NULL_VALUE));
    }

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

    @Test
    public void test_matchstring_bytestring() {
        Predicate<Value> predicate = matchString("foo");

        Value ascii = asStreamValue("foo");
        assertTrue(predicate.test(ascii));

        Value utf8 = asStreamValue("äöü");
        assertFalse(predicate.test(utf8));

        Value ascii_non_match = asStreamValue("oof");
        assertFalse(predicate.test(ascii_non_match));

        Value ascii_diff_length = asStreamValue("fooo");
        assertFalse(predicate.test(ascii_diff_length));
    }

    @Test
    public void test_matchstring_textstring() {
        Predicate<Value> predicate = matchString("äöü");

        Value ascii = asStreamValue("abc");
        assertFalse(predicate.test(ascii));

        Value utf8 = asStreamValue("äöü");
        assertTrue(predicate.test(utf8));

        Value utf8_non_match = asStreamValue("üöä");
        assertFalse(predicate.test(utf8_non_match));

        Value utf8_diff_length = asStreamValue("üöää");
        assertFalse(predicate.test(utf8_diff_length));
    }

    @Test
    public void test_matchstring_nonstring() {
        Predicate<Value> predicate = matchString("foo");
        assertFalse(predicate.test(Value.NULL_VALUE));
    }

    @Test
    public void test_matchstring_objectvalue_match_bytestring() {
        Predicate<Value> predicate = matchString("foo");
        Value value = new ObjectValue(MajorType.ByteString, ValueTypes.ByteString, "foo");
        assertTrue(predicate.test(value));
    }

    @Test
    public void test_matchstring_objectvalue_match_textstring() {
        Predicate<Value> predicate = matchString("foo");
        Value value = new ObjectValue(MajorType.TextString, ValueTypes.TextString, "foo");
        assertTrue(predicate.test(value));
    }

    @Test
    public void test_matchstring_objectvalue_match_string() {
        Predicate<Value> predicate = matchString("foo");
        Value value = new ObjectValue(MajorType.TextString, ValueTypes.String, "foo");
        assertTrue(predicate.test(value));
    }

    @Test
    public void test_matchfloat_nonfloat() {
        Predicate<Value> predicate = matchFloat(12.d);
        assertFalse(predicate.test(Value.NULL_VALUE));
    }

    @Test
    public void test_matchfloat_float() {
        Predicate<Value> predicate = matchFloat(12.f);
        Value value = new ObjectValue(MajorType.FloatingPointOrSimple, ValueTypes.Float, 12.f);
        assertTrue(predicate.test(value));

        Value value2 = new ObjectValue(MajorType.FloatingPointOrSimple, ValueTypes.Float, 12.d);
        assertTrue(predicate.test(value2));

        HalfPrecisionFloat halfPrecisionFloat = HalfPrecisionFloat.valueOf(12.f);
        Value value3 = new ObjectValue(MajorType.FloatingPointOrSimple, ValueTypes.Float, halfPrecisionFloat);
        assertTrue(predicate.test(value3));
    }

    @Test
    public void test_matchfloat_double() {
        Predicate<Value> predicate = matchFloat(12.d);
        Value value = new ObjectValue(MajorType.FloatingPointOrSimple, ValueTypes.Float, 12.f);
        assertTrue(predicate.test(value));

        Value value2 = new ObjectValue(MajorType.FloatingPointOrSimple, ValueTypes.Float, 12.d);
        assertTrue(predicate.test(value2));

        HalfPrecisionFloat halfPrecisionFloat = HalfPrecisionFloat.valueOf(12.f);
        Value value3 = new ObjectValue(MajorType.FloatingPointOrSimple, ValueTypes.Float, halfPrecisionFloat);
        assertTrue(predicate.test(value3));
    }

    @Test
    public void test_matchint_nonint() {
        Predicate<Value> predicate = matchInt(12);
        assertFalse(predicate.test(Value.NULL_VALUE));
    }

    @Test
    public void test_matchint_int() {
        Predicate<Value> predicate = matchInt(12);
        Value value = new ObjectValue(MajorType.UnsignedInteger, ValueTypes.UInt, 12.f);
        assertTrue(predicate.test(value));

        Value value2 = new ObjectValue(MajorType.UnsignedInteger, ValueTypes.UInt, 12.d);
        assertTrue(predicate.test(value2));

        Value value3 = new ObjectValue(MajorType.SemanticTag, ValueTypes.UBigNum, new BigInteger("12"));
        assertTrue(predicate.test(value3));
    }

    @Test
    public void test_matchint_long() {
        Predicate<Value> predicate = matchInt(12L);
        Value value = new ObjectValue(MajorType.UnsignedInteger, ValueTypes.UInt, 12);
        assertTrue(predicate.test(value));

        Value value2 = new ObjectValue(MajorType.UnsignedInteger, ValueTypes.UInt, 12L);
        assertTrue(predicate.test(value2));

        Value value3 = new ObjectValue(MajorType.SemanticTag, ValueTypes.UBigNum, new BigInteger("12"));
        assertTrue(predicate.test(value3));
    }

}
