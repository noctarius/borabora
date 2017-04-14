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

import com.noctarius.borabora.builder.encoder.DictionaryBuilder;
import com.noctarius.borabora.builder.encoder.GraphBuilder;
import com.noctarius.borabora.builder.encoder.SequenceBuilder;
import com.noctarius.borabora.builder.encoder.ValueBuilder;
import com.noctarius.borabora.spi.io.Constants;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.noctarius.borabora.Predicates.matchString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class WriterTestCase
        extends AbstractTestCase {

    @Test
    public void test_write_immediate()
            throws Exception {

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putString("foo") //
               .putString("äüö")

               .putBoolean(false) //
               .putBoolean(true) //
               .putBoolean(Boolean.FALSE) //
               .putBoolean(Boolean.TRUE)

               // nulls
               .putString(null).putBoolean(null);
        });

        Value value1 = parser.read(parser.newQueryBuilder().stream(0).build());
        Value value2 = parser.read(parser.newQueryBuilder().stream(1).build());
        Value value3 = parser.read(parser.newQueryBuilder().stream(2).build());
        Value value4 = parser.read(parser.newQueryBuilder().stream(3).build());
        Value value5 = parser.read(parser.newQueryBuilder().stream(4).build());
        Value value6 = parser.read(parser.newQueryBuilder().stream(5).build());
        Value valueN1 = parser.read(parser.newQueryBuilder().stream(6).build());
        Value valueN2 = parser.read(parser.newQueryBuilder().stream(7).build());

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

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putBoolean(false) //
               .putBoolean(true) //
               .putBoolean(Boolean.FALSE) //
               .putBoolean(Boolean.TRUE) //
               .putBoolean(null);
        });

        Value value1 = parser.read(parser.newQueryBuilder().stream(0).build());
        Value value2 = parser.read(parser.newQueryBuilder().stream(1).build());
        Value value3 = parser.read(parser.newQueryBuilder().stream(2).build());
        Value value4 = parser.read(parser.newQueryBuilder().stream(3).build());
        Value value5 = parser.read(parser.newQueryBuilder().stream(4).build());

        assertFalse(value1.bool());
        assertTrue(value2.bool());
        assertFalse(value3.bool());
        assertTrue(value4.bool());
        assertNull(value5.string());
    }

    @Test
    public void test_write_byte()
            throws Exception {

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putNumber(Byte.MAX_VALUE);
            sgb.putNumber(Byte.MIN_VALUE);
            sgb.putNumber(Byte.valueOf(Byte.MAX_VALUE));
            sgb.putNumber(Byte.valueOf(Byte.MIN_VALUE));
            sgb.putNumber((Byte) null);
        });

        Value value1 = parser.read(parser.newQueryBuilder().stream(0).build());
        Value value2 = parser.read(parser.newQueryBuilder().stream(1).build());
        Value value3 = parser.read(parser.newQueryBuilder().stream(2).build());
        Value value4 = parser.read(parser.newQueryBuilder().stream(3).build());
        Value value5 = parser.read(parser.newQueryBuilder().stream(4).build());

        assertEqualsNumber(Byte.MAX_VALUE, value1.number());
        assertEqualsNumber(Byte.MIN_VALUE, value2.number());
        assertEqualsNumber(Byte.MAX_VALUE, value3.number());
        assertEqualsNumber(Byte.MIN_VALUE, value4.number());
        assertNull(value5.number());
    }

    @Test
    public void test_write_short()
            throws Exception {

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putNumber(Short.MAX_VALUE);
            sgb.putNumber(Short.MIN_VALUE);
            sgb.putNumber(Short.valueOf(Short.MAX_VALUE));
            sgb.putNumber(Short.valueOf(Short.MIN_VALUE));
            sgb.putNumber((Short) null);
        });

        Value value1 = parser.read(parser.newQueryBuilder().stream(0).build());
        Value value2 = parser.read(parser.newQueryBuilder().stream(1).build());
        Value value3 = parser.read(parser.newQueryBuilder().stream(2).build());
        Value value4 = parser.read(parser.newQueryBuilder().stream(3).build());
        Value value5 = parser.read(parser.newQueryBuilder().stream(4).build());

        assertEqualsNumber(Short.MAX_VALUE, value1.number());
        assertEqualsNumber(Short.MIN_VALUE, value2.number());
        assertEqualsNumber(Short.MAX_VALUE, value3.number());
        assertEqualsNumber(Short.MIN_VALUE, value4.number());
        assertNull(value5.number());
    }

    @Test
    public void test_write_int()
            throws Exception {

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putNumber(Integer.MAX_VALUE);
            sgb.putNumber(Integer.MIN_VALUE);
            sgb.putNumber(Integer.valueOf(Integer.MAX_VALUE));
            sgb.putNumber(Integer.valueOf(Integer.MIN_VALUE));
            sgb.putNumber((Integer) null);
        });

        Value value1 = parser.read(parser.newQueryBuilder().stream(0).build());
        Value value2 = parser.read(parser.newQueryBuilder().stream(1).build());
        Value value3 = parser.read(parser.newQueryBuilder().stream(2).build());
        Value value4 = parser.read(parser.newQueryBuilder().stream(3).build());
        Value value5 = parser.read(parser.newQueryBuilder().stream(4).build());

        assertEqualsNumber(Integer.MAX_VALUE, value1.number());
        assertEqualsNumber(Integer.MIN_VALUE, value2.number());
        assertEqualsNumber(Integer.MAX_VALUE, value3.number());
        assertEqualsNumber(Integer.MIN_VALUE, value4.number());
        assertNull(value5.number());
    }

    @Test
    public void test_write_long()
            throws Exception {

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putNumber(Long.MAX_VALUE);
            sgb.putNumber(Long.MIN_VALUE);
            sgb.putNumber(Long.valueOf(Long.MAX_VALUE));
            sgb.putNumber(Long.valueOf(Long.MIN_VALUE));
            sgb.putNumber((Long) null);
        });

        Value value1 = parser.read(parser.newQueryBuilder().stream(0).build());
        Value value2 = parser.read(parser.newQueryBuilder().stream(1).build());
        Value value3 = parser.read(parser.newQueryBuilder().stream(2).build());
        Value value4 = parser.read(parser.newQueryBuilder().stream(3).build());
        Value value5 = parser.read(parser.newQueryBuilder().stream(4).build());

        assertEqualsNumber(Long.MAX_VALUE, value1.number());
        assertEqualsNumber(Long.MIN_VALUE, value2.number());
        assertEqualsNumber(Long.MAX_VALUE, value3.number());
        assertEqualsNumber(Long.MIN_VALUE, value4.number());
        assertNull(value5.number());
    }

    @Test
    public void test_write_half_precision()
            throws Exception {

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putHalfPrecision(Float.POSITIVE_INFINITY);
            sgb.putHalfPrecision(Float.NEGATIVE_INFINITY);
            sgb.putHalfPrecision(Float.valueOf(Float.POSITIVE_INFINITY));
            sgb.putHalfPrecision(Float.valueOf(Float.NEGATIVE_INFINITY));
            sgb.putHalfPrecision(null);
        });

        Value value1 = parser.read(parser.newQueryBuilder().stream(0).build());
        Value value2 = parser.read(parser.newQueryBuilder().stream(1).build());
        Value value3 = parser.read(parser.newQueryBuilder().stream(2).build());
        Value value4 = parser.read(parser.newQueryBuilder().stream(3).build());
        Value value5 = parser.read(parser.newQueryBuilder().stream(4).build());

        assertEqualsNumber(Float.POSITIVE_INFINITY, value1.number());
        assertEqualsNumber(Float.NEGATIVE_INFINITY, value2.number());
        assertEqualsNumber(Float.POSITIVE_INFINITY, value3.number());
        assertEqualsNumber(Float.NEGATIVE_INFINITY, value4.number());
        assertNull(value5.number());
    }

    @Test
    public void test_write_float()
            throws Exception {

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putNumber(Float.MAX_VALUE);
            sgb.putNumber(Float.MIN_VALUE);
            sgb.putNumber(Float.valueOf(Float.MAX_VALUE));
            sgb.putNumber(Float.valueOf(Float.MIN_VALUE));
            sgb.putNumber((Float) null);
        });

        Value value1 = parser.read(parser.newQueryBuilder().stream(0).build());
        Value value2 = parser.read(parser.newQueryBuilder().stream(1).build());
        Value value3 = parser.read(parser.newQueryBuilder().stream(2).build());
        Value value4 = parser.read(parser.newQueryBuilder().stream(3).build());
        Value value5 = parser.read(parser.newQueryBuilder().stream(4).build());

        assertEqualsNumber(Float.MAX_VALUE, value1.number());
        assertEqualsNumber(Float.MIN_VALUE, value2.number());
        assertEqualsNumber(Float.MAX_VALUE, value3.number());
        assertEqualsNumber(Float.MIN_VALUE, value4.number());
        assertNull(value5.number());
    }

    @Test
    public void test_write_double()
            throws Exception {

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putNumber(Double.MAX_VALUE);
            sgb.putNumber(Double.MIN_VALUE);
            sgb.putNumber(Double.valueOf(Double.MAX_VALUE));
            sgb.putNumber(Double.valueOf(Double.MIN_VALUE));
            sgb.putNumber((Double) null);
        });

        Value value1 = parser.read(parser.newQueryBuilder().stream(0).build());
        Value value2 = parser.read(parser.newQueryBuilder().stream(1).build());
        Value value3 = parser.read(parser.newQueryBuilder().stream(2).build());
        Value value4 = parser.read(parser.newQueryBuilder().stream(3).build());
        Value value5 = parser.read(parser.newQueryBuilder().stream(4).build());

        assertEqualsNumber(Double.MAX_VALUE, value1.number());
        assertEqualsNumber(Double.MIN_VALUE, value2.number());
        assertEqualsNumber(Double.MAX_VALUE, value3.number());
        assertEqualsNumber(Double.MIN_VALUE, value4.number());
        assertNull(value5.number());
    }

    @Test
    public void test_write_number_null()
            throws Exception {

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putNumber((Number) null);
        });

        Value value = parser.read(parser.newQueryBuilder().build());
        assertNull(value.number());
    }

    @Test
    public void test_write_number_byte()
            throws Exception {

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putNumber((Number) Byte.MAX_VALUE);
        });

        Value value = parser.read(parser.newQueryBuilder().build());
        assertEqualsNumber(Byte.MAX_VALUE, value.number());
    }

    @Test
    public void test_write_number_short()
            throws Exception {

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putNumber((Number) Short.MAX_VALUE);
        });

        Value value = parser.read(parser.newQueryBuilder().build());
        assertEqualsNumber(Short.MAX_VALUE, value.number());
    }

    @Test
    public void test_write_number_int()
            throws Exception {

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putNumber((Number) Integer.MAX_VALUE);
        });

        Value value = parser.read(parser.newQueryBuilder().build());
        assertEqualsNumber(Integer.MAX_VALUE, value.number());
    }

    @Test
    public void test_write_number_long()
            throws Exception {

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putNumber((Number) Long.MAX_VALUE);
        });

        Value value = parser.read(parser.newQueryBuilder().build());
        assertEqualsNumber(Long.MAX_VALUE, value.number());
    }

    @Test
    public void test_write_number_float()
            throws Exception {

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putNumber((Number) Float.MAX_VALUE);
        });

        Value value = parser.read(parser.newQueryBuilder().build());
        assertEqualsNumber(Float.MAX_VALUE, value.number());
    }

    @Test
    public void test_write_number_double()
            throws Exception {

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putNumber((Number) Double.MAX_VALUE);
        });

        Value value = parser.read(parser.newQueryBuilder().build());
        assertEqualsNumber(Double.MAX_VALUE, value.number());
    }

    @Test
    public void test_write_number_biginteger()
            throws Exception {

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putNumber(Constants.BI_VAL_MAX_VALUE);
        });

        Value value = parser.read(parser.newQueryBuilder().build());
        assertEqualsNumber(Constants.BI_VAL_MAX_VALUE, value.number());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_write_number_bigdecimal()
            throws Exception {

        buildParser((sgb) -> {
            sgb.putNumber(BigDecimal.TEN);
        });
    }

    @Test
    public void test_write_indefinite_textstring()
            throws Exception {

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putIndefiniteTextString() //
               .putString("abc") //
               .putString("def") //
               .putString("ghi") //
               .putString("üöä") //
               .endIndefiniteString();
        });

        Value value = parser.read(parser.newQueryBuilder().build());
        assertEquals("abcdefghiüöä", value.string());
    }

    @Test(expected = NullPointerException.class)
    public void test_write_indefinite_string_null_parameter()
            throws Exception {

        buildParser((sgb) -> {
            sgb.putIndefiniteTextString() //
               .putString(null);
        });
    }

    @Test
    public void test_write_indefinite_bytestring()
            throws Exception {

        String a = buildString(3);
        String b = buildString(24);
        String c = buildString(256);
        String d = buildString(65356);
        String e = buildString(100000);

        String expected = a + b + c + d + e;

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putIndefiniteAsciiString() //
               .putString(a) //
               .putString(b) //
               .putString(c) //
               .putString(d) //
               .putString(e) //
               .endIndefiniteString();
        });

        Value value = parser.read(parser.newQueryBuilder().build());
        assertEquals(expected, value.string());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_write_indefinite_bytestring_fail()
            throws Exception {

        buildParser((sgb) -> {
            sgb.putIndefiniteAsciiString() //
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

    private void test_writing_sequence(Function<GraphBuilder, SequenceBuilder<GraphBuilder>> function) {

        SimplifiedTestParser parser = buildParser((sgb) -> {
            function.apply(sgb)

                    .putString("a") //
                    .putString("b") //
                    .endSequence();
        });

        Value value1 = parser.read(parser.newQueryBuilder().sequence(0).build());
        Value value2 = parser.read(parser.newQueryBuilder().sequence(1).build());

        assertEquals("a", value1.string());
        assertEquals("b", value2.string());
    }

    @Test(expected = IllegalStateException.class)
    public void test_write_sequence_fail_too_many_elements()
            throws Exception {

        buildParser((sgb) -> {
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

    private void test_writing_dictionary(Function<GraphBuilder, DictionaryBuilder<GraphBuilder>> function) {

        SimplifiedTestParser parser = buildParser((sgb) -> {
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

        Value value1 = parser.read(parser.newQueryBuilder().dictionary("a").build());
        Value value2 = parser.read(parser.newQueryBuilder().dictionary("b").build());

        assertEquals("A", value1.string());
        assertEquals("B", value2.string());
    }

    @Test(expected = IllegalStateException.class)
    public void fail_write_dictionary_fail_too_many_elements()
            throws Exception {

        buildParser((sgb) -> {
            sgb.putDictionary(0) //
               .putEntry();
        });
    }

    @Test(expected = IllegalStateException.class)
    public void fail_write_dictionary_fail_key_value_already_set()
            throws Exception {

        buildParser((sgb) -> {
            sgb.putDictionary(1) //
               .putEntry() //
               .putBoolean(true) //
               .putBoolean(true) //
               .putBoolean(true);
        });
    }

    @Test(expected = IllegalStateException.class)
    public void fail_write_dictionary_entry_no_key() {
        buildParser(sgb -> sgb.putDictionary(1).putEntry().endEntry().endDictionary());
    }

    @Test(expected = IllegalStateException.class)
    public void fail_write_dictionary_entry_no_value() {
        buildParser(sgb -> sgb.putDictionary(1).putEntry().putString("key").endEntry().endDictionary());
    }

    @Test
    public void test_write_timestamp()
            throws Exception {

        long expected = 12345678;

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putTimestamp(expected) //
               .putTimestamp(Instant.ofEpochSecond(expected)) //
               .putTimestamp(null);
        });

        Value value1 = parser.read(parser.newQueryBuilder().stream(0).build());
        Value value2 = parser.read(parser.newQueryBuilder().stream(1).build());
        Value value3 = parser.read(parser.newQueryBuilder().stream(2).build());

        assertEquals(expected, (long) value1.tag());
        assertEquals(expected, (long) value2.tag());
        assertNull(value3.tag());
    }

    @Test
    public void test_write_fraction()
            throws Exception {

        BigDecimal expected = BigDecimal.valueOf(12.d);

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putFraction(expected) //
               .putFraction(null);
        });

        Value value1 = parser.read(parser.newQueryBuilder().stream(0).build());
        Value value2 = parser.read(parser.newQueryBuilder().stream(1).build());

        assertEquals(expected, value1.tag());
        assertNull(value2.tag());
    }

    @Test
    public void test_write_uri()
            throws Exception {

        URI expected = URI.create("file://test-äüö.dat");

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putURI(expected) //
               .putURI(null);
        });

        Value value1 = parser.read(parser.newQueryBuilder().stream(0).build());
        Value value2 = parser.read(parser.newQueryBuilder().stream(1).build());

        assertEquals(expected, value1.tag());
        assertNull(value2.tag());
    }

    @Test
    public void test_write_datetime()
            throws Exception {

        Instant expected = Instant.now();
        Date date = Date.from(expected);

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putDateTime(expected) //
               .putDateTime(date) //
               .putDateTime((Instant) null).putDateTime((Date) null);
        });

        Value value1 = parser.read(parser.newQueryBuilder().stream(0).build());
        Value value2 = parser.read(parser.newQueryBuilder().stream(1).build());
        Value value3 = parser.read(parser.newQueryBuilder().stream(2).build());

        assertEquals(expected, value1.tag());
        assertEquals(expected, value2.tag());
        assertNull(value3.tag());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_write_unknown_number_type()
            throws Exception {

        buildParser((sgb) -> sgb.putNumber(new Number() {
            @Override
            public int intValue() {
                return 0;
            }

            @Override
            public long longValue() {
                return 0;
            }

            @Override
            public float floatValue() {
                return 0;
            }

            @Override
            public double doubleValue() {
                return 0;
            }
        }));
    }

    @Test(expected = IllegalStateException.class)
    public void test_write_unfinished_sequence()
            throws Exception {

        buildParser((sgb) -> sgb.putSequence(2).putString("test").endSequence());
    }

    @Test(expected = IllegalStateException.class)
    public void test_write_unfinished_dictionary()
            throws Exception {

        buildParser((sgb) -> sgb.putDictionary(2).putEntry().putString("te").putString("st").endEntry().endDictionary());
    }

    @Test(expected = IllegalStateException.class)
    public void test_write_unfinished_dictionary_entry()
            throws Exception {

        buildParser((sgb) -> sgb.putDictionary(2).putEntry().putString("te").endEntry());
    }

    @Test(expected = WrongTypeException.class)
    public void fail_write_putvalue_puttag_object()
            throws Exception {

        buildParser((sgb) -> sgb.putValue(new Object()));
    }

    @Test
    public void test_write_putvalue_string()
            throws Exception {

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putValue("foo");
        });

        Value value1 = parser.read(parser.newQueryBuilder().stream(0).build());

        assertEquals("foo", value1.string());
    }

    @Test
    public void test_write_puttag_null()
            throws Exception {

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putTag(null);
        });

        Value value1 = parser.read(parser.newQueryBuilder().stream(0).build());

        assertNull(value1.tag());
    }

    @Test
    public void test_write_putvalue_number()
            throws Exception {

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putValue(1L);
        });

        Value value1 = parser.read(parser.newQueryBuilder().stream(0).build());

        assertEquals(1L, value1.number().longValue());
    }

    @Test
    public void test_write_putvalue_boolean()
            throws Exception {

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putValue(Boolean.TRUE);
        });

        Value value1 = parser.read(parser.newQueryBuilder().stream(0).build());

        assertEquals(Boolean.TRUE, value1.bool());
    }

    @Test
    public void test_write_putvalue_puttag_datetime()
            throws Exception {

        Instant expected = Instant.now();
        Date date = Date.from(expected);

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putValue(date) //
               .putValue(null);
        });

        Value value1 = parser.read(parser.newQueryBuilder().stream(0).build());
        Value value2 = parser.read(parser.newQueryBuilder().stream(1).build());

        assertEquals(expected, value1.tag());
        assertNull(value2.tag());
    }

    @Test
    public void test_write_putvalue_puttag_timestamp()
            throws Exception {

        Instant expected = Instant.now();
        Timestamp timestamp = Timestamp.from(expected);

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putValue(expected) //
               .putValue(timestamp) //
               .putValue(null);
        });

        Value value1 = parser.read(parser.newQueryBuilder().stream(0).build());
        Value value2 = parser.read(parser.newQueryBuilder().stream(1).build());
        Value value3 = parser.read(parser.newQueryBuilder().stream(2).build());

        assertEquals(expected.getEpochSecond(), (long) value1.tag());
        assertEquals(expected.getEpochSecond(), (long) value2.tag());
        assertNull(value3.tag());
    }

    @Test
    public void test_write_putvalue_puttag_value()
            throws Exception {

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putDictionary(1) //
               .putEntry().putString("non-used-key1").putString("non-used-value-1").endEntry() //
               .endDictionary()

               .putDictionary(2) //
               .putEntry().putString("key-1").putString("value-1").endEntry() //
               .putEntry().putString("key-2").putString("value-2").endEntry() //
               .endDictionary()

               .putDictionary(1) //
               .putEntry().putString("non-used-key2").putString("non-used-value-2").endEntry() //
               .endDictionary();
        });

        Value expected = parser.read(parser.newQueryBuilder().stream(1).build());

        SimplifiedTestParser result = buildParser((sgb) -> sgb.putValue(expected));
        Value value = result.read(result.newQueryBuilder().build()).byValueType();

        assertEquals(MajorType.Dictionary, value.majorType());

        Dictionary dictionary = value.dictionary();
        assertEquals(2, dictionary.size());
        assertEquals("value-1", dictionary.get(matchString("key-1")).string());
        assertEquals("value-2", dictionary.get(matchString("key-2")).string());
    }

    @Test
    public void test_write_putvalue_puttag_object_value_dictionary()
            throws Exception {

        Map<Value, Value> values = new HashMap<Value, Value>() {
            {
                put(asObjectValue(MajorType.SemanticTag, ValueTypes.ASCII, "key-1"),
                        asObjectValue(MajorType.SemanticTag, ValueTypes.ASCII, "value-1"));
                put(asObjectValue(MajorType.SemanticTag, ValueTypes.ASCII, "key-2"),
                        asObjectValue(MajorType.SemanticTag, ValueTypes.ASCII, "value-2"));
            }
        };
        Value expected = asObjectValue(values);

        SimplifiedTestParser result = buildParser((sgb) -> sgb.putValue(expected));
        Value value = result.read(result.newQueryBuilder().build()).byValueType();

        assertEquals(MajorType.Dictionary, value.majorType());

        Dictionary dictionary = value.dictionary();
        assertEquals(2, dictionary.size());
        assertEquals("value-1", dictionary.get(matchString("key-1")).string());
        assertEquals("value-2", dictionary.get(matchString("key-2")).string());
    }

    @Test
    public void test_write_putvalue_puttag_object_value_sequence()
            throws Exception {

        List<Value> values = new ArrayList<Value>() {
            {
                add(asObjectValue(MajorType.ByteString, ValueTypes.ByteString, "value-1"));
                add(asObjectValue(MajorType.ByteString, ValueTypes.ByteString, "value-2"));
            }
        };
        Value expected = asObjectValue(values);

        SimplifiedTestParser result = buildParser((sgb) -> sgb.putValue(expected));
        Value value = result.read(result.newQueryBuilder().build()).byValueType();

        assertEquals(MajorType.Sequence, value.majorType());

        Sequence sequence = value.sequence();
        assertEquals(2, sequence.size());
        assertEquals("value-1", sequence.get(0).string());
        assertEquals("value-2", sequence.get(1).string());
    }

    @Test
    public void test_write_putvalue_puttag_object_value_biguint()
            throws Exception {

        Value expected = asObjectValue(MajorType.SemanticTag, ValueTypes.UBigNum, BigInteger.valueOf(123L));

        SimplifiedTestParser result = buildParser((sgb) -> sgb.putValue(expected));
        Value value = result.read(result.newQueryBuilder().build()).byValueType();

        assertEquals(MajorType.SemanticTag, value.majorType());
        assertEquals(ValueTypes.UBigNum, value.valueType());

        assertEquals(BigInteger.valueOf(123L), value.tag());
    }

    @Test
    public void test_write_putvalue_puttag_object_value_fraction()
            throws Exception {

        Value expected = asObjectValue(MajorType.SemanticTag, ValueTypes.Fraction, BigDecimal.valueOf(123.f));

        SimplifiedTestParser result = buildParser((sgb) -> sgb.putValue(expected));
        Value value = result.read(result.newQueryBuilder().build()).byValueType();

        assertEquals(MajorType.SemanticTag, value.majorType());
        assertEquals(ValueTypes.Fraction, value.valueType());

        assertEquals(BigDecimal.valueOf(123.f), value.tag());
    }

    @Test
    public void test_write_putvalue_puttag_object_value_float()
            throws Exception {

        Value expected = asObjectValue(MajorType.FloatingPointOrSimple, ValueTypes.Float, 123.f);

        SimplifiedTestParser result = buildParser((sgb) -> sgb.putValue(expected));
        Value value = result.read(result.newQueryBuilder().build()).byValueType();

        assertEquals(MajorType.FloatingPointOrSimple, value.majorType());
        assertEquals(ValueTypes.Float, value.valueType());

        assertEqualsNumber(123.f, value.number());
    }

    @Test
    public void test_write_putvalue_puttag_object_value_double()
            throws Exception {

        Value expected = asObjectValue(MajorType.FloatingPointOrSimple, ValueTypes.Float, 123.d);

        SimplifiedTestParser result = buildParser((sgb) -> sgb.putValue(expected));
        Value value = result.read(result.newQueryBuilder().build()).byValueType();

        assertEquals(MajorType.FloatingPointOrSimple, value.majorType());
        assertEquals(ValueTypes.Float, value.valueType());

        assertEqualsNumber(123.d, value.number());
    }

    @Test
    public void test_write_putvalue_puttag_object_value_uint()
            throws Exception {

        Value expected = asObjectValue(MajorType.UnsignedInteger, ValueTypes.UInt, 123);

        SimplifiedTestParser result = buildParser((sgb) -> sgb.putValue(expected));
        Value value = result.read(result.newQueryBuilder().build()).byValueType();

        assertEquals(MajorType.UnsignedInteger, value.majorType());
        assertEquals(ValueTypes.UInt, value.valueType());

        assertEqualsNumber(123, value.number());
    }

    @Test
    public void test_write_putvalue_puttag_object_value_date()
            throws Exception {

        Instant expected = Instant.now();
        Value original = asObjectValue(MajorType.SemanticTag, ValueTypes.DateTime, expected);

        SimplifiedTestParser result = buildParser((sgb) -> sgb.putValue(original));
        Value value = result.read(result.newQueryBuilder().build()).byValueType();

        assertEquals(MajorType.SemanticTag, value.majorType());
        assertEquals(ValueTypes.DateTime, value.valueType());

        assertEquals(expected, value.tag());
    }

    @Test(expected = IllegalStateException.class)
    public void test_write_putvalue_puttag_object_value_unknown()
            throws Exception {

        Value original = asObjectValue(MajorType.SemanticTag, ValueTypes.Unknown, new Object());

        buildParser((sgb) -> sgb.putValue(original));
    }

    @Test(expected = IllegalStateException.class)
    public void test_write_putvalue_puttag_object_value_unknown_2()
            throws Exception {

        Value original = asObjectValue(MajorType.Unknown, ValueTypes.Unknown, new Object());

        buildParser((sgb) -> sgb.putValue(original));
    }

    @Test
    public void test_write_putvalue_puttag_uri()
            throws Exception {

        URI expected = new URI("www.noctarius.com");

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putValue(expected);
        });

        Value value1 = parser.read(parser.newQueryBuilder().stream(0).build());

        assertEquals(expected, value1.tag());
    }

    @Test
    public void test_write_putvalue_puttag_ubignum()
            throws Exception {

        BigInteger expected = Constants.BI_VAL_MAX_VALUE.add(BigInteger.ONE);

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putValue(expected);
        });

        Value value1 = parser.read(parser.newQueryBuilder().stream(0).build());

        assertEquals(expected, value1.tag());
    }

    @Test
    public void test_write_putvalue_puttag_fraction()
            throws Exception {

        BigDecimal expected = BigDecimal.valueOf(12.d);

        SimplifiedTestParser parser = buildParser((sgb) -> {
            sgb.putValue(expected);
        });

        Value value1 = parser.read(parser.newQueryBuilder().stream(0).build());

        assertEquals(expected, value1.tag());
    }

}
