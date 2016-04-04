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

import com.noctarius.borabora.builder.DictionaryBuilder;
import com.noctarius.borabora.builder.SequenceBuilder;
import com.noctarius.borabora.builder.StreamGraphBuilder;
import com.noctarius.borabora.builder.ValueBuilder;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.Function;

import static com.noctarius.borabora.DictionaryGraphQuery.matchString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class StreamWriterTestCase
        extends AbstractTestCase {

    @Test
    public void test_write_immediate()
            throws Exception {

        SimplifiedTestParser parser = executeStreamWriterTest((sgb) -> {
            sgb.putString("foo") //
               .putString("äüö")

               .putBoolean(false) //
               .putBoolean(true) //
               .putBoolean(Boolean.FALSE) //
               .putBoolean(Boolean.TRUE)

               // nulls
               .putString(null).putBoolean(null);
        });

        Value value1 = parser.read(GraphQuery.newBuilder().stream(0).build());
        Value value2 = parser.read(GraphQuery.newBuilder().stream(1).build());
        Value value3 = parser.read(GraphQuery.newBuilder().stream(2).build());
        Value value4 = parser.read(GraphQuery.newBuilder().stream(3).build());
        Value value5 = parser.read(GraphQuery.newBuilder().stream(4).build());
        Value value6 = parser.read(GraphQuery.newBuilder().stream(5).build());
        Value valueN1 = parser.read(GraphQuery.newBuilder().stream(6).build());
        Value valueN2 = parser.read(GraphQuery.newBuilder().stream(7).build());

        assertEquals("foo", value1.string());
        assertEquals("äüö", value2.string());
        assertFalse(value3.bool());
        assertTrue(value4.bool());
        assertFalse(value5.bool());
        assertTrue(value6.bool());

        assertNull(valueN1.string());
        assertNull(valueN2.bool());
    }

    @Test
    public void test_write_boolean()
            throws Exception {

        SimplifiedTestParser parser = executeStreamWriterTest((sgb) -> {
            sgb.putBoolean(false) //
               .putBoolean(true) //
               .putBoolean(Boolean.FALSE) //
               .putBoolean(Boolean.TRUE) //
               .putBoolean(null);
        });

        Value value1 = parser.read(GraphQuery.newBuilder().stream(0).build());
        Value value2 = parser.read(GraphQuery.newBuilder().stream(1).build());
        Value value3 = parser.read(GraphQuery.newBuilder().stream(2).build());
        Value value4 = parser.read(GraphQuery.newBuilder().stream(3).build());
        Value value5 = parser.read(GraphQuery.newBuilder().stream(4).build());

        assertFalse(value1.bool());
        assertTrue(value2.bool());
        assertFalse(value3.bool());
        assertTrue(value4.bool());
        assertNull(value5.string());
    }

    @Test
    public void test_write_byte()
            throws Exception {

        SimplifiedTestParser parser = executeStreamWriterTest((sgb) -> {
            sgb.putNumber(Byte.MAX_VALUE);
            sgb.putNumber(Byte.MIN_VALUE);
            sgb.putNumber(Byte.valueOf(Byte.MAX_VALUE));
            sgb.putNumber(Byte.valueOf(Byte.MIN_VALUE));
            sgb.putNumber((Byte) null);
        });

        Value value1 = parser.read(GraphQuery.newBuilder().stream(0).build());
        Value value2 = parser.read(GraphQuery.newBuilder().stream(1).build());
        Value value3 = parser.read(GraphQuery.newBuilder().stream(2).build());
        Value value4 = parser.read(GraphQuery.newBuilder().stream(3).build());
        Value value5 = parser.read(GraphQuery.newBuilder().stream(4).build());

        assertEqualsNumber(Byte.MAX_VALUE, value1.number());
        assertEqualsNumber(Byte.MIN_VALUE, value2.number());
        assertEqualsNumber(Byte.MAX_VALUE, value3.number());
        assertEqualsNumber(Byte.MIN_VALUE, value4.number());
        assertNull(value5.number());
    }

    @Test
    public void test_write_short()
            throws Exception {

        SimplifiedTestParser parser = executeStreamWriterTest((sgb) -> {
            sgb.putNumber(Short.MAX_VALUE);
            sgb.putNumber(Short.MIN_VALUE);
            sgb.putNumber(Short.valueOf(Short.MAX_VALUE));
            sgb.putNumber(Short.valueOf(Short.MIN_VALUE));
            sgb.putNumber((Short) null);
        });

        Value value1 = parser.read(GraphQuery.newBuilder().stream(0).build());
        Value value2 = parser.read(GraphQuery.newBuilder().stream(1).build());
        Value value3 = parser.read(GraphQuery.newBuilder().stream(2).build());
        Value value4 = parser.read(GraphQuery.newBuilder().stream(3).build());
        Value value5 = parser.read(GraphQuery.newBuilder().stream(4).build());

        assertEqualsNumber(Short.MAX_VALUE, value1.number());
        assertEqualsNumber(Short.MIN_VALUE, value2.number());
        assertEqualsNumber(Short.MAX_VALUE, value3.number());
        assertEqualsNumber(Short.MIN_VALUE, value4.number());
        assertNull(value5.number());
    }

    @Test
    public void test_write_int()
            throws Exception {

        SimplifiedTestParser parser = executeStreamWriterTest((sgb) -> {
            sgb.putNumber(Integer.MAX_VALUE);
            sgb.putNumber(Integer.MIN_VALUE);
            sgb.putNumber(Integer.valueOf(Integer.MAX_VALUE));
            sgb.putNumber(Integer.valueOf(Integer.MIN_VALUE));
            sgb.putNumber((Integer) null);
        });

        Value value1 = parser.read(GraphQuery.newBuilder().stream(0).build());
        Value value2 = parser.read(GraphQuery.newBuilder().stream(1).build());
        Value value3 = parser.read(GraphQuery.newBuilder().stream(2).build());
        Value value4 = parser.read(GraphQuery.newBuilder().stream(3).build());
        Value value5 = parser.read(GraphQuery.newBuilder().stream(4).build());

        assertEqualsNumber(Integer.MAX_VALUE, value1.number());
        assertEqualsNumber(Integer.MIN_VALUE, value2.number());
        assertEqualsNumber(Integer.MAX_VALUE, value3.number());
        assertEqualsNumber(Integer.MIN_VALUE, value4.number());
        assertNull(value5.number());
    }

    @Test
    public void test_write_long()
            throws Exception {

        SimplifiedTestParser parser = executeStreamWriterTest((sgb) -> {
            sgb.putNumber(Long.MAX_VALUE);
            sgb.putNumber(Long.MIN_VALUE);
            sgb.putNumber(Long.valueOf(Long.MAX_VALUE));
            sgb.putNumber(Long.valueOf(Long.MIN_VALUE));
            sgb.putNumber((Long) null);
        });

        Value value1 = parser.read(GraphQuery.newBuilder().stream(0).build());
        Value value2 = parser.read(GraphQuery.newBuilder().stream(1).build());
        Value value3 = parser.read(GraphQuery.newBuilder().stream(2).build());
        Value value4 = parser.read(GraphQuery.newBuilder().stream(3).build());
        Value value5 = parser.read(GraphQuery.newBuilder().stream(4).build());

        assertEqualsNumber(Long.MAX_VALUE, value1.number());
        assertEqualsNumber(Long.MIN_VALUE, value2.number());
        assertEqualsNumber(Long.MAX_VALUE, value3.number());
        assertEqualsNumber(Long.MIN_VALUE, value4.number());
        assertNull(value5.number());
    }

    @Test
    public void test_write_half_precision()
            throws Exception {

        SimplifiedTestParser parser = executeStreamWriterTest((sgb) -> {
            sgb.putHalfPrecision(Float.POSITIVE_INFINITY);
            sgb.putHalfPrecision(Float.NEGATIVE_INFINITY);
            sgb.putHalfPrecision(Float.valueOf(Float.POSITIVE_INFINITY));
            sgb.putHalfPrecision(Float.valueOf(Float.NEGATIVE_INFINITY));
            sgb.putHalfPrecision(null);
        });

        Value value1 = parser.read(GraphQuery.newBuilder().stream(0).build());
        Value value2 = parser.read(GraphQuery.newBuilder().stream(1).build());
        Value value3 = parser.read(GraphQuery.newBuilder().stream(2).build());
        Value value4 = parser.read(GraphQuery.newBuilder().stream(3).build());
        Value value5 = parser.read(GraphQuery.newBuilder().stream(4).build());

        assertEqualsNumber(Float.POSITIVE_INFINITY, value1.number());
        assertEqualsNumber(Float.NEGATIVE_INFINITY, value2.number());
        assertEqualsNumber(Float.POSITIVE_INFINITY, value3.number());
        assertEqualsNumber(Float.NEGATIVE_INFINITY, value4.number());
        assertNull(value5.number());
    }

    @Test
    public void test_write_float()
            throws Exception {

        SimplifiedTestParser parser = executeStreamWriterTest((sgb) -> {
            sgb.putNumber(Float.MAX_VALUE);
            sgb.putNumber(Float.MIN_VALUE);
            sgb.putNumber(Float.valueOf(Float.MAX_VALUE));
            sgb.putNumber(Float.valueOf(Float.MIN_VALUE));
            sgb.putNumber((Float) null);
        });

        Value value1 = parser.read(GraphQuery.newBuilder().stream(0).build());
        Value value2 = parser.read(GraphQuery.newBuilder().stream(1).build());
        Value value3 = parser.read(GraphQuery.newBuilder().stream(2).build());
        Value value4 = parser.read(GraphQuery.newBuilder().stream(3).build());
        Value value5 = parser.read(GraphQuery.newBuilder().stream(4).build());

        assertEqualsNumber(Float.MAX_VALUE, value1.number());
        assertEqualsNumber(Float.MIN_VALUE, value2.number());
        assertEqualsNumber(Float.MAX_VALUE, value3.number());
        assertEqualsNumber(Float.MIN_VALUE, value4.number());
        assertNull(value5.number());
    }

    @Test
    public void test_write_double()
            throws Exception {

        SimplifiedTestParser parser = executeStreamWriterTest((sgb) -> {
            sgb.putNumber(Double.MAX_VALUE);
            sgb.putNumber(Double.MIN_VALUE);
            sgb.putNumber(Double.valueOf(Double.MAX_VALUE));
            sgb.putNumber(Double.valueOf(Double.MIN_VALUE));
            sgb.putNumber((Double) null);
        });

        Value value1 = parser.read(GraphQuery.newBuilder().stream(0).build());
        Value value2 = parser.read(GraphQuery.newBuilder().stream(1).build());
        Value value3 = parser.read(GraphQuery.newBuilder().stream(2).build());
        Value value4 = parser.read(GraphQuery.newBuilder().stream(3).build());
        Value value5 = parser.read(GraphQuery.newBuilder().stream(4).build());

        assertEqualsNumber(Double.MAX_VALUE, value1.number());
        assertEqualsNumber(Double.MIN_VALUE, value2.number());
        assertEqualsNumber(Double.MAX_VALUE, value3.number());
        assertEqualsNumber(Double.MIN_VALUE, value4.number());
        assertNull(value5.number());
    }

    @Test
    public void test_write_number_null()
            throws Exception {

        SimplifiedTestParser parser = executeStreamWriterTest((sgb) -> {
            sgb.putNumber((Number) null);
        });

        Value value = parser.read(GraphQuery.newBuilder().build());
        assertNull(value.number());
    }

    @Test
    public void test_write_number_byte()
            throws Exception {

        SimplifiedTestParser parser = executeStreamWriterTest((sgb) -> {
            sgb.putNumber((Number) Byte.MAX_VALUE);
        });

        Value value = parser.read(GraphQuery.newBuilder().build());
        assertEqualsNumber(Byte.MAX_VALUE, value.number());
    }

    @Test
    public void test_write_number_short()
            throws Exception {

        SimplifiedTestParser parser = executeStreamWriterTest((sgb) -> {
            sgb.putNumber((Number) Short.MAX_VALUE);
        });

        Value value = parser.read(GraphQuery.newBuilder().build());
        assertEqualsNumber(Short.MAX_VALUE, value.number());
    }

    @Test
    public void test_write_number_int()
            throws Exception {

        SimplifiedTestParser parser = executeStreamWriterTest((sgb) -> {
            sgb.putNumber((Number) Integer.MAX_VALUE);
        });

        Value value = parser.read(GraphQuery.newBuilder().build());
        assertEqualsNumber(Integer.MAX_VALUE, value.number());
    }

    @Test
    public void test_write_number_long()
            throws Exception {

        SimplifiedTestParser parser = executeStreamWriterTest((sgb) -> {
            sgb.putNumber((Number) Long.MAX_VALUE);
        });

        Value value = parser.read(GraphQuery.newBuilder().build());
        assertEqualsNumber(Long.MAX_VALUE, value.number());
    }

    @Test
    public void test_write_number_float()
            throws Exception {

        SimplifiedTestParser parser = executeStreamWriterTest((sgb) -> {
            sgb.putNumber((Number) Float.MAX_VALUE);
        });

        Value value = parser.read(GraphQuery.newBuilder().build());
        assertEqualsNumber(Float.MAX_VALUE, value.number());
    }

    @Test
    public void test_write_number_double()
            throws Exception {

        SimplifiedTestParser parser = executeStreamWriterTest((sgb) -> {
            sgb.putNumber((Number) Double.MAX_VALUE);
        });

        Value value = parser.read(GraphQuery.newBuilder().build());
        assertEqualsNumber(Double.MAX_VALUE, value.number());
    }

    @Test
    public void test_write_number_biginteger()
            throws Exception {

        SimplifiedTestParser parser = executeStreamWriterTest((sgb) -> {
            sgb.putNumber(Constants.BI_VAL_MAX_VALUE);
        });

        Value value = parser.read(GraphQuery.newBuilder().build());
        assertEqualsNumber(Constants.BI_VAL_MAX_VALUE, value.number());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_write_number_bigdecimal()
            throws Exception {

        SimplifiedTestParser parser = executeStreamWriterTest((sgb) -> {
            sgb.putNumber(BigDecimal.TEN);
        });
    }

    @Test
    public void test_write_indefinite_textstring()
            throws Exception {

        SimplifiedTestParser parser = executeStreamWriterTest((sgb) -> {
            sgb.putIndefiniteTextString() //
               .putString("abc") //
               .putString("def") //
               .putString("ghi") //
               .putString("üöä") //
               .endIndefiniteString();
        });

        Value value = parser.read(GraphQuery.newBuilder().build());
        assertEquals("abcdefghiüöä", value.string());
    }

    @Test(expected = NullPointerException.class)
    public void test_write_indefinite_string_null_parameter()
            throws Exception {

        executeStreamWriterTest((sgb) -> {
            sgb.putIndefiniteTextString() //
               .putString(null);
        });
    }

    @Test
    public void test_write_indefinite_bytestring()
            throws Exception {

        SimplifiedTestParser parser = executeStreamWriterTest((sgb) -> {
            sgb.putIndefiniteByteString() //
               .putString("abc") //
               .putString("def") //
               .putString("ghi") //
               .endIndefiniteString();
        });

        Value value = parser.read(GraphQuery.newBuilder().build());
        assertEquals("abcdefghi", value.string());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_write_indefinite_bytestring_fail()
            throws Exception {

        executeStreamWriterTest((sgb) -> {
            sgb.putIndefiniteByteString() //
               .putString("äöü");
        });
    }

    @Test
    public void test_write_indefinite_sequence()
            throws Exception {

        test_writing_sequence(ValueBuilder::putSequence);
    }

    @Test
    public void test_write_sequence()
            throws Exception {

        test_writing_sequence(builder -> builder.putSequence(2));
    }

    private void test_writing_sequence(Function<StreamGraphBuilder, SequenceBuilder<StreamGraphBuilder>> function) {

        SimplifiedTestParser parser = executeStreamWriterTest((sgb) -> {
            function.apply(sgb)

                    .putString("a") //
                    .putString("b") //
                    .endSequence();
        });

        Value value1 = parser.read(GraphQuery.newBuilder().sequence(0).build());
        Value value2 = parser.read(GraphQuery.newBuilder().sequence(1).build());

        assertEquals("a", value1.string());
        assertEquals("b", value2.string());
    }

    @Test(expected = IllegalStateException.class)
    public void test_write_sequence_fail_too_many_elements()
            throws Exception {

        executeStreamWriterTest((sgb) -> {
            sgb.putSequence(0) //
               .putString("a");
        });
    }

    @Test
    public void test_write_indefinite_dictionary()
            throws Exception {

        test_writing_dictionary(ValueBuilder::putDictionary);
    }

    @Test
    public void test_write_dictionary()
            throws Exception {

        test_writing_dictionary(builder -> builder.putDictionary(2));
    }

    private void test_writing_dictionary(Function<StreamGraphBuilder, DictionaryBuilder<StreamGraphBuilder>> function) {

        SimplifiedTestParser parser = executeStreamWriterTest((sgb) -> {
            function.apply(sgb)

                    .putEntry() //
                    .putString("a") //
                    .putString("A") //
                    .endEntry()

                    .putEntry() //
                    .putString("b") //
                    .putString("B") //
                    .endEntry()

                    .endDictionary();
        });

        Value value1 = parser.read(GraphQuery.newBuilder().dictionary(matchString("a")).build());
        Value value2 = parser.read(GraphQuery.newBuilder().dictionary(matchString("b")).build());

        assertEquals("A", value1.string());
        assertEquals("B", value2.string());
    }

    @Test(expected = IllegalStateException.class)
    public void test_write_dictionary_fail_too_many_elements()
            throws Exception {

        executeStreamWriterTest((sgb) -> {
            sgb.putDictionary(0) //
               .putEntry();
        });
    }

    @Test(expected = IllegalStateException.class)
    public void test_write_dictionary_fail_key_value_already_set()
            throws Exception {

        executeStreamWriterTest((sgb) -> {
            sgb.putDictionary(1) //
               .putEntry() //
               .putBoolean(true) //
               .putBoolean(true) //
               .putBoolean(true);
        });
    }

    @Test
    public void test_write_timestamp()
            throws Exception {

        long expected = 12345678;

        SimplifiedTestParser parser = executeStreamWriterTest((sgb) -> {
            sgb.putTimestamp(expected) //
               .putTimestamp(Instant.ofEpochSecond(expected)) //
               .putTimestamp(null);
        });

        Value value1 = parser.read(GraphQuery.newBuilder().stream(0).build());
        Value value2 = parser.read(GraphQuery.newBuilder().stream(1).build());
        Value value3 = parser.read(GraphQuery.newBuilder().stream(2).build());

        assertEquals(expected, (long) value1.tag());
        assertEquals(expected, (long) value2.tag());
        assertNull(value3.tag());
    }

}
