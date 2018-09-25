/*
 * Copyright (c) 2008-2016-2018, Hazelcast, Inc. All Rights Reserved.
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

import com.noctarius.borabora.spi.ValueValidators;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ValueValidatorsTest
        extends AbstractTestCase {

    @Test
    public void call_constructor() {
        callConstructor(ValueValidators.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isString_nonstring() {
        ValueValidators.isString(Value.NULL_VALUE, null);
    }

    @Test
    public void isString_bytestring() {
        Value value = asObjectValue(MajorType.ByteString, ValueTypes.ByteString, "foo");
        ValueValidators.isString(value, "foo");
    }

    @Test
    public void isString_textstring() {
        Value value = asObjectValue(MajorType.TextString, ValueTypes.TextString, "foo");
        ValueValidators.isString(value, "foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void isByteString_nonstring() {
        ValueValidators.isByteString(Value.NULL_VALUE, null);
    }

    @Test
    public void isByteString_bytestring() {
        Value value = asObjectValue(MajorType.ByteString, ValueTypes.ByteString, "foo");
        ValueValidators.isByteString(value, "foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void isByteString_textstring() {
        Value value = asObjectValue(MajorType.TextString, ValueTypes.TextString, "foo");
        ValueValidators.isByteString(value, "foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void isTextString_nonstring() {
        ValueValidators.isTextString(Value.NULL_VALUE, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isTextString_bytestring() {
        Value value = asObjectValue(MajorType.ByteString, ValueTypes.ByteString, "foo");
        ValueValidators.isTextString(value, "foo");
    }

    @Test
    public void isTextString_textstring() {
        Value value = asObjectValue(MajorType.TextString, ValueTypes.TextString, "foo");
        ValueValidators.isTextString(value, "foo");
    }

    @Test
    public void isPositive_biginteger() {
        ValueValidators.isPositive(Value.NULL_VALUE, new BigInteger("1234"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isPositive_biginteger_nonpositive() {
        ValueValidators.isPositive(Value.NULL_VALUE, new BigInteger("-1234"));
    }

    @Test
    public void isPositive_bigdecimal() {
        ValueValidators.isPositive(Value.NULL_VALUE, new BigDecimal(1234.d));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isPositive_bigdecimal_nonpositive() {
        ValueValidators.isPositive(Value.NULL_VALUE, new BigDecimal(-1234.d));
    }

    @Test
    public void isPositive_halfprecisionfloat() {
        ValueValidators.isPositive(Value.NULL_VALUE, HalfPrecisionFloat.valueOf(12.f));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isPositive_halfprecisionfloat_nonpositive() {
        ValueValidators.isPositive(Value.NULL_VALUE, HalfPrecisionFloat.valueOf(-12.f));
    }

    @Test
    public void isPositive_float() {
        ValueValidators.isPositive(Value.NULL_VALUE, 12.f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isPositive_float_nonpositive() {
        ValueValidators.isPositive(Value.NULL_VALUE, -12.f);
    }

    @Test
    public void isPositive_double() {
        ValueValidators.isPositive(Value.NULL_VALUE, 12.d);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isPositive_double_nonpositive() {
        ValueValidators.isPositive(Value.NULL_VALUE, -12.d);
    }

    @Test
    public void isPositive_int() {
        ValueValidators.isPositive(Value.NULL_VALUE, 12);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isPositive_int_nonpositive() {
        ValueValidators.isPositive(Value.NULL_VALUE, -12);
    }

    @Test
    public void isNegative_biginteger() {
        ValueValidators.isNegative(Value.NULL_VALUE, new BigInteger("-1234"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isNegative_biginteger_nonnegative() {
        ValueValidators.isNegative(Value.NULL_VALUE, new BigInteger("1234"));
    }

    @Test
    public void isNegative_bigdecimal() {
        ValueValidators.isNegative(Value.NULL_VALUE, new BigDecimal(-1234.d));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isNegative_bigdecimal_nonnegative() {
        ValueValidators.isNegative(Value.NULL_VALUE, new BigDecimal(1234.d));
    }

    @Test
    public void isNegative_halfprecisionfloat() {
        ValueValidators.isNegative(Value.NULL_VALUE, HalfPrecisionFloat.valueOf(-12.f));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isNegative_halfprecisionfloat_nonnegative() {
        ValueValidators.isNegative(Value.NULL_VALUE, HalfPrecisionFloat.valueOf(12.f));
    }

    @Test
    public void isNegative_float() {
        ValueValidators.isNegative(Value.NULL_VALUE, -12.f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isNegative_float_nonnegative() {
        ValueValidators.isNegative(Value.NULL_VALUE, 12.f);
    }

    @Test
    public void isNegative_double() {
        ValueValidators.isNegative(Value.NULL_VALUE, -12.d);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isNegative_double_nonnegative() {
        ValueValidators.isNegative(Value.NULL_VALUE, 12.d);
    }

    @Test
    public void isNegative_int() {
        ValueValidators.isNegative(Value.NULL_VALUE, -12);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isNegative_int_nonnegative() {
        ValueValidators.isNegative(Value.NULL_VALUE, 12);
    }

}