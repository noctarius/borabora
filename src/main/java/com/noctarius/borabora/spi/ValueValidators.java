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

import com.noctarius.borabora.HalfPrecisionFloat;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.ValueTypes;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/**
 * The <tt>ValueValidators</tt> class provides a set of helper methods to quickly test values
 * for a specific criteria.
 */
public final class ValueValidators {

    private ValueValidators() {
    }

    /**
     * If the given <tt>value</tt> is either <tt>ByteString</tt> or a <tt>TextString</tt> the
     * method returns gracefully, otherwise an {@link IllegalArgumentException} is thrown.
     *
     * @param value     the value to test
     * @param extracted the extracted value
     */
    public static void isString(Value value, Object extracted) {
        Objects.requireNonNull(value, "value must not be null");
        if (!ValueTypes.ByteString.matches(value.valueType()) //
                && !ValueTypes.TextString.matches(value.valueType())) {

            throw new IllegalArgumentException("value is neither a bytestring nor a textstring");
        }
    }

    /**
     * If the given <tt>value</tt> is a <tt>ByteString</tt> the method returns gracefully,
     * otherwise an {@link IllegalArgumentException} is thrown.
     *
     * @param value     the value to test
     * @param extracted the extracted value
     */
    public static void isByteString(Value value, Object extracted) {
        Objects.requireNonNull(value, "value must not be null");
        if (!ValueTypes.ByteString.matches(value.valueType())) {
            throw new IllegalArgumentException("value is not a bytestring");
        }
    }

    /**
     * If the given <tt>value</tt> is a <tt>TextString</tt> the method returns gracefully,
     * otherwise an {@link IllegalArgumentException} is thrown.
     *
     * @param value     the value to test
     * @param extracted the extracted value
     */
    public static void isTextString(Value value, Object extracted) {
        Objects.requireNonNull(value, "value must not be null");
        if (!ValueTypes.TextString.matches(value.valueType())) {
            throw new IllegalArgumentException("value is not a textstring");
        }
    }

    /**
     * If the given <tt>extracted</tt> value is a <tt>Number</tt> and the number is positive,
     * the method returns gracefully, otherwise an {@link IllegalArgumentException} is thrown
     * if the value is negative.
     *
     * @param value     the value to test
     * @param extracted the extracted value
     */
    public static void isPositive(Value value, Object extracted) {
        Number number = (Number) extracted;
        if (isNegative(number)) {
            throw new IllegalArgumentException("extracted value is not positive");
        }
    }

    /**
     * If the given <tt>extracted</tt> value is a <tt>Number</tt> and the number is negative,
     * the method returns gracefully, otherwise an {@link IllegalArgumentException} is thrown
     * if the value is positive.
     *
     * @param value     the value to test
     * @param extracted the extracted value
     */
    public static void isNegative(Value value, Object extracted) {
        Number number = (Number) extracted;
        if (!isNegative(number)) {
            throw new IllegalArgumentException("extracted value is not negative");
        }
    }

    private static boolean isNegative(Number number) {
        Objects.requireNonNull(number, "number must not be null");
        if (number instanceof BigInteger) {
            return ((BigInteger) number).signum() < 0;
        }
        if (number instanceof BigDecimal) {
            return ((BigDecimal) number).signum() < 0;
        }
        if (number instanceof HalfPrecisionFloat //
                || number instanceof Float //
                || number instanceof Double) {

            return Double.compare(number.doubleValue(), 0d) < 0;
        }
        return number.longValue() < 0;
    }

}
