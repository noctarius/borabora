package com.noctarius.borabora;

import com.noctarius.borabora.spi.Constants;
import com.noctarius.borabora.spi.codec.Encoder;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
/*
 * Copyright (c) 2008-2016, Hazelcast, Inc. All Rights Reserved.
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

public class ValueTypesTest
        extends AbstractTestCase {

    @Test
    public void test_value_number() {
        Value value = asStreamValue(gb -> gb.putNumber(Byte.MAX_VALUE).finishStream());
        assertEquals(Byte.MAX_VALUE, ((Number) ValueTypes.Number.value(value, true)).intValue());
    }

    @Test
    public void test_value_int() {
        Value value = asStreamValue(gb -> gb.putNumber(Integer.MAX_VALUE).finishStream());
        assertEquals(Integer.MAX_VALUE, ((Number) ValueTypes.Int.value(value, true)).intValue());
    }

    @Test
    public void test_value_uint() {
        Value value = asStreamValue(gb -> gb.putNumber(Integer.MAX_VALUE).finishStream());
        assertEquals(Integer.MAX_VALUE, ((Number) ValueTypes.UInt.value(value, true)).intValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_value_uint_nonpositive() {
        Value value = asStreamValue(gb -> gb.putNumber(Integer.MIN_VALUE).finishStream());
        assertEquals(Integer.MAX_VALUE, ((Number) ValueTypes.UInt.value(value, true)).intValue());
    }

    @Test
    public void test_value_nint() {
        Value value = asStreamValue(gb -> gb.putNumber(Integer.MIN_VALUE).finishStream());
        assertEquals(Integer.MIN_VALUE, ((Number) ValueTypes.NInt.value(value, true)).intValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_value_nint_nonnegative() {
        Value value = asStreamValue(gb -> gb.putNumber(Integer.MAX_VALUE).finishStream());
        assertEquals(Integer.MIN_VALUE, ((Number) ValueTypes.NInt.value(value, true)).intValue());
    }

    @Test
    public void test_value_string_bytestring() {
        Value value = asStreamValue(gb -> gb.putByteString("foo").finishStream());
        assertEquals("foo", ValueTypes.String.value(value, true));
    }

    @Test
    public void test_value_string_textstring() {
        Value value = asStreamValue(gb -> gb.putTextString("foo").finishStream());
        assertEquals("foo", ValueTypes.String.value(value, true));
    }

    @Test
    public void test_value_bytestring() {
        Value value = asStreamValue(gb -> gb.putByteString("foo").finishStream());
        assertEquals("foo", ValueTypes.ByteString.value(value, true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_value_bytestring_textstring() {
        Value value = asStreamValue(gb -> gb.putTextString("foo").finishStream());
        assertEquals("foo", ValueTypes.ByteString.value(value, true));
    }

    @Test
    public void test_value_textstring() {
        Value value = asStreamValue(gb -> gb.putTextString("foo").finishStream());
        assertEquals("foo", ValueTypes.TextString.value(value, true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_value_textstring_bytestring() {
        Value value = asStreamValue(gb -> gb.putByteString("foo").finishStream());
        assertEquals("foo", ValueTypes.TextString.value(value, true));
    }

    @Test
    public void test_value_sequence() {
        Value value = asStreamValue(gb -> gb.putSequence(1).putString("foo").endSequence().finishStream());
        assertEquals(ValueTypes.Sequence, value.valueType());
        Sequence sequence = ValueTypes.Sequence.value(value, true);
        assertEquals(1, sequence.size());
    }

    @Test
    public void test_value_indefinite_sequence() {
        Value value = asStreamValue(gb -> gb.putSequence().putString("foo").endSequence().finishStream());
        assertEquals(ValueTypes.Sequence, value.valueType());
        Sequence sequence = ValueTypes.Sequence.value(value, true);
        assertEquals(1, sequence.size());
    }

    @Test
    public void test_value_dictionary() {
        Value value = asStreamValue(gb -> gb.putDictionary(1)

                                            .putEntry().putString("key").putString("foo").endEntry()

                                            .endDictionary().finishStream());
        assertEquals(ValueTypes.Dictionary, value.valueType());
        Dictionary dictionary = ValueTypes.Dictionary.value(value, true);
        assertEquals(1, dictionary.size());
    }

    @Test
    public void test_value_indefinite_dictionary() {
        Value value = asStreamValue(gb -> gb.putDictionary()

                                            .putEntry().putString("key").putString("foo").endEntry()

                                            .endDictionary().finishStream());
        assertEquals(ValueTypes.Dictionary, value.valueType());
        Dictionary dictionary = ValueTypes.Dictionary.value(value, true);
        assertEquals(1, dictionary.size());
    }

    @Test
    public void test_value_float_halffloat() {
        Value value = asStreamValue(gb -> gb.putHalfPrecision(1.0f).finishStream());
        assertEquals(HalfPrecisionFloat.valueOf(1.0f), ValueTypes.Float.value(value, true));
    }

    @Test
    public void test_value_float_float() {
        Value value = asStreamValue(gb -> gb.putNumber(1.0f).finishStream());
        assertEquals(Float.valueOf(1.0f), ValueTypes.Float.value(value, true));
    }

    @Test
    public void test_value_float_double() {
        Value value = asStreamValue(gb -> gb.putNumber(1.0d).finishStream());
        assertEquals(Double.valueOf(1.0), ValueTypes.Float.value(value, true));
    }

    @Test
    public void test_value_bool() {
        Value value = asStreamValue(gb -> gb.putBoolean(true).finishStream());
        assertEquals(true, ValueTypes.Bool.value(value, true));
    }

    @Test
    public void test_value_null() {
        Value value = asStreamValue(gb -> gb.putString(null).finishStream());
        assertNull(ValueTypes.Null.value(value, true));
    }

    @Test
    public void test_value_undefined() {
        byte[] bytes = new byte[]{(byte) ((MajorType.FloatingPointOrSimple.typeId() << 5) | Constants.FP_VALUE_UNDEF)};
        Value value = asStreamValue(bytes);
        assertNull(ValueTypes.Undefined.value(value, true));
    }

    @Test
    public void test_value_datetime_date() {
        Date date = new Date();
        Instant expected = date.toInstant();
        Value value = asStreamValue(gb -> gb.putDateTime(date).finishStream());
        assertEquals(expected, ValueTypes.DateTime.value(value, true));
    }

    @Test
    public void test_value_datetime_instant() {
        Instant instant = Instant.now();
        Value value = asStreamValue(gb -> gb.putDateTime(instant).finishStream());
        assertEquals(instant, ValueTypes.DateTime.value(value, true));
    }

    @Test
    public void test_value_timestamp_long() {
        long expected = Instant.now().getEpochSecond();
        Value value = asStreamValue(gb -> gb.putTimestamp(expected).finishStream());
        assertEquals(expected, ((Long) ValueTypes.Timestamp.value(value, true)).longValue());
    }

    @Test
    public void test_value_timestamp_instant() {
        Instant instant = Instant.now();
        Value value = asStreamValue(gb -> gb.putTimestamp(instant).finishStream());
        assertEquals(instant.getEpochSecond(), ((Long) ValueTypes.Timestamp.value(value, true)).longValue());
    }

    @Test
    public void test_value_ubignum() {
        BigInteger expected = new BigInteger("1234");
        Value value = asStreamValue(gb -> gb.putBigInteger(expected).finishStream());
        assertEquals(expected, ValueTypes.UBigNum.value(value, true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_value_ubignum_nonpositive() {
        BigInteger expected = new BigInteger("-1234");
        Value value = asStreamValue(gb -> gb.putBigInteger(expected).finishStream());
        assertEquals(expected, ValueTypes.UBigNum.value(value, true));
    }

    @Test
    public void test_value_nbignum() {
        BigInteger expected = new BigInteger("-1234");
        Value value = asStreamValue(gb -> gb.putBigInteger(expected).finishStream());
        assertEquals(expected, ValueTypes.NBigNum.value(value, true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_value_nbignum_nonnegative() {
        BigInteger expected = new BigInteger("1234");
        Value value = asStreamValue(gb -> gb.putBigInteger(expected).finishStream());
        assertEquals(expected, ValueTypes.NBigNum.value(value, true));
    }

    @Test
    public void test_value_cborenc() {
        String expected = new String(hexToBytes("0x6449455446"), Constants.ASCII);
        byte[] bytes = hexToBytes("0xd818456449455446");
        Value value = asStreamValue(bytes);
        assertEquals(expected, ((Value) ValueTypes.EncCBOR.value(value, true)).string());
    }

    @Test
    public void test_value_uri()
            throws Exception {

        URI expected = new URI("www.noctarius.com");
        Value value = asStreamValue(gb -> gb.putURI(expected).finishStream());
        assertEquals(expected, ValueTypes.URI.value(value, true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_value_semantic_tag_ubignum() {
        Value value = asStreamValue(gb -> gb.putBigInteger(new BigInteger("1234")).finishStream());
        ValueTypes.URI.value(value, true);
    }

    @Test
    public void test_value_unknown_semantic_tag() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toOutputStream(baos);
        long offset = Encoder.putSemanticTag(128, 0, output);
        Encoder.putByteString("foo", offset, output);
        byte[] bytes = baos.toByteArray();
        byte[] expected = Arrays.copyOfRange(bytes, 2, 6);
        Value value = asStreamValue(bytes);
        assertArrayEquals(expected, ValueTypes.Unknown.value(value, true));
    }

    @Test
    public void test_valueType_uint() {
        Input input = input(MajorType.UnsignedInteger, ValueTypes.UInt);
        ValueType valueType = ValueTypes.valueType(input, 0);
        assertEquals(ValueTypes.UInt, valueType);
    }

    @Test
    public void test_valueType_nint() {
        Input input = input(MajorType.NegativeInteger, ValueTypes.NInt);
        ValueType valueType = ValueTypes.valueType(input, 0);
        assertEquals(ValueTypes.NInt, valueType);
    }

    @Test
    public void test_valueType_bytestring() {
        Input input = input(MajorType.ByteString, ValueTypes.ByteString);
        ValueType valueType = ValueTypes.valueType(input, 0);
        assertEquals(ValueTypes.ByteString, valueType);
    }

    @Test
    public void test_valueType_textstring() {
        Input input = input(MajorType.TextString, ValueTypes.TextString);
        ValueType valueType = ValueTypes.valueType(input, 0);
        assertEquals(ValueTypes.TextString, valueType);
    }

    @Test
    public void test_valueType_sequence() {
        Input input = input(MajorType.Sequence, ValueTypes.Sequence);
        ValueType valueType = ValueTypes.valueType(input, 0);
        assertEquals(ValueTypes.Sequence, valueType);
    }

    @Test
    public void test_valueType_dictionary() {
        Input input = input(MajorType.Dictionary, ValueTypes.Dictionary);
        ValueType valueType = ValueTypes.valueType(input, 0);
        assertEquals(ValueTypes.Dictionary, valueType);
    }

    @Test
    public void test_valueType_floatOrSimple_null() {
        Input input = input(MajorType.FloatingPointOrSimple, ValueTypes.Null);
        ValueType valueType = ValueTypes.valueType(input, 0);
        assertEquals(ValueTypes.Null, valueType);
    }

    @Test
    public void test_valueType_floatOrSimple_bool() {
        Input input = input(MajorType.FloatingPointOrSimple, ValueTypes.Bool);
        ValueType valueType = ValueTypes.valueType(input, 0);
        assertEquals(ValueTypes.Bool, valueType);
    }

    @Test
    public void test_valueType_floatOrSimple_float() {
        Input input = input(MajorType.FloatingPointOrSimple, ValueTypes.Float);
        ValueType valueType = ValueTypes.valueType(input, 0);
        assertEquals(ValueTypes.Float, valueType);
    }

    @Test
    public void test_valueType_semtag_datetime() {
        Input input = semanticTag(ValueTypes.DateTime);
        ValueType valueType = ValueTypes.valueType(input, 0);
        assertEquals(ValueTypes.DateTime, valueType);
    }

    @Test
    public void test_valueType_semtag_timestamp() {
        Input input = semanticTag(ValueTypes.Timestamp);
        ValueType valueType = ValueTypes.valueType(input, 0);
        assertEquals(ValueTypes.Timestamp, valueType);
    }

    @Test
    public void test_valueType_semtag_ubigint() {
        Input input = semanticTag(ValueTypes.UBigNum);
        ValueType valueType = ValueTypes.valueType(input, 0);
        assertEquals(ValueTypes.UBigNum, valueType);
    }

    @Test
    public void test_valueType_semtag_nbigint() {
        Input input = semanticTag(ValueTypes.NBigNum);
        ValueType valueType = ValueTypes.valueType(input, 0);
        assertEquals(ValueTypes.NBigNum, valueType);
    }

    @Test
    public void test_valueType_semtag_enccbor() {
        Input input = semanticTag(ValueTypes.EncCBOR);
        ValueType valueType = ValueTypes.valueType(input, 0);
        assertEquals(ValueTypes.EncCBOR, valueType);
    }

    @Test
    public void test_valueType_semtag_uri() {
        Input input = semanticTag(ValueTypes.URI);
        ValueType valueType = ValueTypes.valueType(input, 0);
        assertEquals(ValueTypes.URI, valueType);
    }

    @Test(expected = IllegalStateException.class)
    public void fail_valueType_semtag_unimplemented_bigfloat() {
        Input input = semanticTag(Constants.TAG_BIGFLOAT);
        ValueTypes.valueType(input, 0);
    }

    @Test
    public void fail_valueType_semtag_fraction() {
        Input input = semanticTag(Constants.TAG_FRACTION);
        ValueTypes.valueType(input, 0);
    }

    @Test(expected = IllegalStateException.class)
    public void fail_valueType_semtag_unimplemented_regex() {
        Input input = semanticTag(Constants.TAG_REGEX);
        ValueTypes.valueType(input, 0);
    }

    @Test(expected = IllegalStateException.class)
    public void fail_valueType_semtag_unimplemented_mime() {
        Input input = semanticTag(Constants.TAG_MIME);
        ValueTypes.valueType(input, 0);
    }

    @Test
    public void test_valueType_obj_uint() {
        ValueType valueType = ValueTypes.valueType(1);
        assertEquals(ValueTypes.UInt, valueType);
    }

    @Test
    public void test_valueType_obj_nint() {
        ValueType valueType = ValueTypes.valueType(-1);
        assertEquals(ValueTypes.NInt, valueType);
    }

    @Test
    public void test_valueType_obj_bytestring() {
        ValueType valueType = ValueTypes.valueType("foo");
        assertEquals(ValueTypes.ByteString, valueType);
    }

    @Test
    public void test_valueType_obj_textstring() {
        ValueType valueType = ValueTypes.valueType("üöä");
        assertEquals(ValueTypes.TextString, valueType);
    }

    @Test
    public void test_valueType_obj_sequence() {
        ValueType valueType = ValueTypes.valueType(new ArrayList<>());
        assertEquals(ValueTypes.Sequence, valueType);
    }

    @Test
    public void test_valueType_obj_dictionary() {
        ValueType valueType = ValueTypes.valueType(new HashMap<>());
        assertEquals(ValueTypes.Dictionary, valueType);
    }

    @Test
    public void test_valueType_obj_floatOrSimple_null() {
        ValueType valueType = ValueTypes.valueType(null);
        assertEquals(ValueTypes.Null, valueType);
    }

    @Test
    public void test_valueType_obj_floatOrSimple_bool() {
        ValueType valueType = ValueTypes.valueType(true);
        assertEquals(ValueTypes.Bool, valueType);
    }

    @Test
    public void test_valueType_obj_floatOrSimple_float() {
        ValueType valueType = ValueTypes.valueType(Float.valueOf(12.0f));
        assertEquals(ValueTypes.Float, valueType);
    }

    @Test
    public void test_valueType_obj_semtag_datetime() {
        ValueType valueType = ValueTypes.valueType(new Date());
        assertEquals(ValueTypes.DateTime, valueType);
    }

    @Test
    public void test_valueType_obj_semtag_timestamp() {
        Input input = semanticTag(ValueTypes.Timestamp);
        ValueType valueType = ValueTypes.valueType(input, 0);
        assertEquals(ValueTypes.Timestamp, valueType);
    }

    @Test
    public void test_valueType_obj_semtag_ubigint() {
        ValueType valueType = ValueTypes.valueType(new BigInteger("1234"));
        assertEquals(ValueTypes.UBigNum, valueType);
    }

    @Test
    public void test_valueType_obj_semtag_nbigint() {
        ValueType valueType = ValueTypes.valueType(new BigInteger("-1234"));
        assertEquals(ValueTypes.NBigNum, valueType);
    }

    @Test
    public void test_valueType_obj_semtag_fraction() {
        ValueType valueType = ValueTypes.valueType(new BigDecimal(12.d));
        assertEquals(ValueTypes.Fraction, valueType);
    }

    @Test
    @Ignore("Not yet implemented")
    public void test_valueType_obj_semtag_enccbor() {
        Input input = semanticTag(ValueTypes.EncCBOR);
        ValueType valueType = ValueTypes.valueType(input, 0);
        assertEquals(ValueTypes.EncCBOR, valueType);
    }

    @Test
    public void test_valueType_obj_semtag_uri()
            throws Exception {

        ValueType valueType = ValueTypes.valueType(new URI("www.noctarius.com"));
        assertEquals(ValueTypes.URI, valueType);
    }

    private Input input(MajorType majorType, ValueType valueType) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toOutputStream(baos);

        if (valueType == ValueTypes.Bool) {
            Encoder.putBoolean(true, 0, output);
        } else if (valueType == ValueTypes.Null) {
            Encoder.putNull(0, output);
        } else {
            Encoder.encodeLengthAndValue(majorType, 1, 0, output);
        }
        return Input.fromByteArray(baos.toByteArray());
    }

    private Input semanticTag(ValueTypes valueType) {
        switch (valueType) {
            case DateTime:
                return semanticTag(Constants.TAG_DATE_TIME);
            case Timestamp:
                return semanticTag(Constants.TAG_TIMESTAMP);
            case UBigNum:
                return semanticTag(Constants.TAG_UNSIGNED_BIGNUM);
            case NBigNum:
                return semanticTag(Constants.TAG_SIGNED_BIGNUM);
            case EncCBOR:
                return semanticTag(Constants.TAG_ENCCBOR);
            case URI:
                return semanticTag(Constants.TAG_URI);
            default:
                throw new IllegalArgumentException("Not a semanticTag value type");
        }
    }

    private Input semanticTag(int tagId) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toOutputStream(baos);
        Encoder.putSemanticTag(tagId, 0, output);
        return Input.fromByteArray(baos.toByteArray());
    }

}