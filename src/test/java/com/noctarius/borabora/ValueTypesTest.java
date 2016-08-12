package com.noctarius.borabora;

import com.noctarius.borabora.builder.GraphBuilder;
import com.noctarius.borabora.spi.CommonTagCodec;
import com.noctarius.borabora.spi.Decoder;
import com.noctarius.borabora.spi.Encoder;
import com.noctarius.borabora.spi.HalfPrecisionFloat;
import com.noctarius.borabora.spi.QueryContext;
import com.noctarius.borabora.spi.SelectStatementStrategy;
import com.noctarius.borabora.spi.StreamValue;
import com.noctarius.borabora.spi.TagDecoder;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import static com.noctarius.borabora.MajorType.FloatingPointOrSimple;
import static com.noctarius.borabora.spi.Constants.ASCII;
import static com.noctarius.borabora.spi.Constants.EMPTY_QUERY_CONSUMER;
import static com.noctarius.borabora.spi.Constants.FP_VALUE_UNDEF;
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
    public void test_value_number()
            throws Exception {

        Value value = asValue(gb -> gb.putNumber(Byte.MAX_VALUE).finishStream());
        assertEquals(Byte.MAX_VALUE, ((Number) ValueTypes.Number.value(value, true)).intValue());
    }

    @Test
    public void test_value_int()
            throws Exception {

        Value value = asValue(gb -> gb.putNumber(Integer.MAX_VALUE).finishStream());
        assertEquals(Integer.MAX_VALUE, ((Number) ValueTypes.Int.value(value, true)).intValue());
    }

    @Test
    public void test_value_uint()
            throws Exception {

        Value value = asValue(gb -> gb.putNumber(Integer.MAX_VALUE).finishStream());
        assertEquals(Integer.MAX_VALUE, ((Number) ValueTypes.UInt.value(value, true)).intValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_value_uint_nonpositive()
            throws Exception {

        Value value = asValue(gb -> gb.putNumber(Integer.MIN_VALUE).finishStream());
        assertEquals(Integer.MAX_VALUE, ((Number) ValueTypes.UInt.value(value, true)).intValue());
    }

    @Test
    public void test_value_nint()
            throws Exception {

        Value value = asValue(gb -> gb.putNumber(Integer.MIN_VALUE).finishStream());
        assertEquals(Integer.MIN_VALUE, ((Number) ValueTypes.NInt.value(value, true)).intValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_value_nint_nonnegative()
            throws Exception {

        Value value = asValue(gb -> gb.putNumber(Integer.MAX_VALUE).finishStream());
        assertEquals(Integer.MIN_VALUE, ((Number) ValueTypes.NInt.value(value, true)).intValue());
    }

    @Test
    public void test_value_string_bytestring()
            throws Exception {

        Value value = asValue(gb -> gb.putByteString("foo").finishStream());
        assertEquals("foo", ValueTypes.String.value(value, true));
    }

    @Test
    public void test_value_string_textstring()
            throws Exception {

        Value value = asValue(gb -> gb.putTextString("foo").finishStream());
        assertEquals("foo", ValueTypes.String.value(value, true));
    }

    @Test
    public void test_value_bytestring()
            throws Exception {

        Value value = asValue(gb -> gb.putByteString("foo").finishStream());
        assertEquals("foo", ValueTypes.ByteString.value(value, true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_value_bytestring_textstring()
            throws Exception {

        Value value = asValue(gb -> gb.putTextString("foo").finishStream());
        assertEquals("foo", ValueTypes.ByteString.value(value, true));
    }

    @Test
    public void test_value_textstring()
            throws Exception {

        Value value = asValue(gb -> gb.putTextString("foo").finishStream());
        assertEquals("foo", ValueTypes.TextString.value(value, true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_value_textstring_bytestring()
            throws Exception {

        Value value = asValue(gb -> gb.putByteString("foo").finishStream());
        assertEquals("foo", ValueTypes.TextString.value(value, true));
    }

    @Test
    public void test_value_sequence()
            throws Exception {

        Value value = asValue(gb -> gb.putSequence(1).putString("foo").endSequence().finishStream());
        assertEquals(ValueTypes.Sequence, value.valueType());
        Sequence sequence = ValueTypes.Sequence.value(value, true);
        assertEquals(1, sequence.size());
    }

    @Test
    public void test_value_indefinite_sequence()
            throws Exception {

        Value value = asValue(gb -> gb.putSequence().putString("foo").endSequence().finishStream());
        assertEquals(ValueTypes.Sequence, value.valueType());
        Sequence sequence = ValueTypes.Sequence.value(value, true);
        assertEquals(1, sequence.size());
    }

    @Test
    public void test_value_dictionary()
            throws Exception {

        Value value = asValue(gb -> gb.putDictionary(1)

                                      .putEntry().putString("key").putString("foo").endEntry()

                                      .endDictionary().finishStream());
        assertEquals(ValueTypes.Dictionary, value.valueType());
        Dictionary dictionary = ValueTypes.Dictionary.value(value, true);
        assertEquals(1, dictionary.size());
    }

    @Test
    public void test_value_indefinite_dictionary()
            throws Exception {

        Value value = asValue(gb -> gb.putDictionary()

                                      .putEntry().putString("key").putString("foo").endEntry()

                                      .endDictionary().finishStream());
        assertEquals(ValueTypes.Dictionary, value.valueType());
        Dictionary dictionary = ValueTypes.Dictionary.value(value, true);
        assertEquals(1, dictionary.size());
    }

    @Test
    public void test_value_float_halffloat()
            throws Exception {

        Value value = asValue(gb -> gb.putHalfPrecision(1.0f).finishStream());
        assertEquals(HalfPrecisionFloat.valueOf(1.0f), ValueTypes.Float.value(value, true));
    }

    @Test
    public void test_value_float_float()
            throws Exception {

        Value value = asValue(gb -> gb.putNumber(1.0f).finishStream());
        assertEquals(Float.valueOf(1.0f), ValueTypes.Float.value(value, true));
    }

    @Test
    public void test_value_float_double()
            throws Exception {

        Value value = asValue(gb -> gb.putNumber(1.0d).finishStream());
        assertEquals(Double.valueOf(1.0), ValueTypes.Float.value(value, true));
    }

    @Test
    public void test_value_bool()
            throws Exception {

        Value value = asValue(gb -> gb.putBoolean(true).finishStream());
        assertEquals(true, ValueTypes.Bool.value(value, true));
    }

    @Test
    public void test_value_null()
            throws Exception {

        Value value = asValue(gb -> gb.putString(null).finishStream());
        assertNull(ValueTypes.Null.value(value, true));
    }

    @Test
    public void test_value_undefined()
            throws Exception {

        byte[] bytes = new byte[]{(byte) ((FloatingPointOrSimple.typeId() << 5) | FP_VALUE_UNDEF)};
        Value value = asValue(bytes);
        assertNull(ValueTypes.Undefined.value(value, true));
    }

    @Test
    public void test_value_datetime_date()
            throws Exception {

        Date expected = new Date();
        Value value = asValue(gb -> gb.putDateTime(expected).finishStream());
        assertEquals(expected, ValueTypes.DateTime.value(value, true));
    }

    @Test
    public void test_value_datetime_instant()
            throws Exception {

        Instant instant = Instant.now();
        Value value = asValue(gb -> gb.putDateTime(instant).finishStream());
        assertEquals(instant, ((Date) ValueTypes.DateTime.value(value, true)).toInstant());
    }

    @Test
    public void test_value_timestamp_long()
            throws Exception {

        long expected = Instant.now().getEpochSecond();
        Value value = asValue(gb -> gb.putTimestamp(expected).finishStream());
        assertEquals(expected, ((Long) ValueTypes.Timestamp.value(value, true)).longValue());
    }

    @Test
    public void test_value_timestamp_instant()
            throws Exception {

        Instant instant = Instant.now();
        Value value = asValue(gb -> gb.putTimestamp(instant).finishStream());
        assertEquals(instant.getEpochSecond(), ((Long) ValueTypes.Timestamp.value(value, true)).longValue());
    }

    @Test
    public void test_value_ubignum()
            throws Exception {

        BigInteger expected = new BigInteger("1234");
        Value value = asValue(gb -> gb.putBigInteger(expected).finishStream());
        assertEquals(expected, ValueTypes.UBigNum.value(value, true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_value_ubignum_nonpositive()
            throws Exception {

        BigInteger expected = new BigInteger("-1234");
        Value value = asValue(gb -> gb.putBigInteger(expected).finishStream());
        assertEquals(expected, ValueTypes.UBigNum.value(value, true));
    }

    @Test
    public void test_value_nbignum()
            throws Exception {

        BigInteger expected = new BigInteger("-1234");
        Value value = asValue(gb -> gb.putBigInteger(expected).finishStream());
        assertEquals(expected, ValueTypes.NBigNum.value(value, true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_value_nbignum_nonnegative()
            throws Exception {

        BigInteger expected = new BigInteger("1234");
        Value value = asValue(gb -> gb.putBigInteger(expected).finishStream());
        assertEquals(expected, ValueTypes.NBigNum.value(value, true));
    }

    @Test
    public void test_value_cborenc()
            throws Exception {

        String expected = new String(hexToBytes("0x6449455446"), ASCII);
        byte[] bytes = hexToBytes("0xd818456449455446");
        Value value = asValue(bytes);
        assertEquals(expected, ((Value) ValueTypes.EncCBOR.value(value, true)).string());
    }

    @Test
    public void test_value_uri()
            throws Exception {

        URI expected = new URI("www.noctarius.com");
        Value value = asValue(gb -> gb.putURI(expected).finishStream());
        assertEquals(expected, ValueTypes.URI.value(value, true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_value_semantic_tag_ubignum()
            throws Exception {

        Value value = asValue(gb -> gb.putBigInteger(new BigInteger("1234")).finishStream());
        ValueTypes.URI.value(value, true);
    }

    @Test
    public void test_value_unknown_semantic_tag()
            throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toOutputStream(baos);
        long offset = Encoder.putSemanticTag(128, 0, output);
        Encoder.putByteString("foo", offset, output);
        byte[] bytes = baos.toByteArray();
        byte[] expected = Arrays.copyOfRange(bytes, 2, 6);
        Value value = asValue(bytes);
        assertArrayEquals(expected, ValueTypes.Unknown.value(value, true));
    }

    private Value asValue(Consumer<GraphBuilder> consumer) {
        Writer writer = Writer.newBuilder().build();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toOutputStream(baos);
        GraphBuilder graphBuilder = writer.newGraphBuilder(output);
        consumer.accept(graphBuilder);
        return asValue(baos.toByteArray());
    }

    private Value asValue(byte[] bytes) {
        Input input = Input.fromByteArray(bytes);
        short head = Decoder.readUInt8(input, 0);
        MajorType majorType = MajorType.findMajorType(head);
        ValueType valueType = ValueTypes.valueType(input, 0);

        List<TagDecoder> tagDecoders = Collections.singletonList(CommonTagCodec.INSTANCE);
        SelectStatementStrategy selectStatementStrategy = BinarySelectStatementStrategy.INSTANCE;

        QueryContext queryContext = new QueryContextImpl(input, EMPTY_QUERY_CONSUMER, tagDecoders, selectStatementStrategy);
        return new StreamValue(majorType, valueType, 0, queryContext);
    }

}