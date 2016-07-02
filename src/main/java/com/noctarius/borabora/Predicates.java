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

import com.noctarius.borabora.spi.QueryContext;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.function.Predicate;

public enum Predicates {
    ;

    public static Predicate<Value> matchString(String value) {
        // If indefinite or large string or ObjectValue we have to go the slow path
        Predicate<Value> slowPathPredicate = matchString0(value);
        if (value.length() * 4L > Integer.MAX_VALUE) {
            return slowPathPredicate;
        }

        // Pre-encode matching value
        ByteArrayOutputStream baos = new ByteArrayOutputStream(value.length() * 2);
        Encoder.putString(value, 0, Output.toOutputStream(baos));

        byte[] expected = baos.toByteArray();
        int expectedLength = expected.length;

        return (v) -> {
            if (!v.valueType().matches(ValueTypes.String)) {
                return false;
            }

            if (v instanceof AbstractStreamValue) {
                QueryContext queryContext = ((AbstractStreamValue) v).queryContext();
                MajorType majorType = v.majorType();
                long offset = v.offset();

                Input input = queryContext.input();
                long length = Decoder.length(input, majorType, offset);
                if (length < expectedLength) {
                    return false;
                }

                if (length == expectedLength) {
                    byte[] data = new byte[expectedLength];
                    input.read(data, offset, expectedLength);
                    if (Arrays.equals(expected, data)) {
                        return true;
                    }
                }
            }

            return slowPathPredicate.test(v);
        };
    }

    private static Predicate<Value> matchString0(String value) {
        return (v) -> v.string().equals(value);
    }

    public static Predicate<Value> matchFloat(double value) {
        return (v) -> {
            if (!v.valueType().matches(ValueTypes.Float)) {
                return false;
            }
            Number n = v.number();
            if (n instanceof BigDecimal) {
                return n.equals(BigDecimal.valueOf(value));
            }
            return value == n.doubleValue();
        };
    }

    public static Predicate<Value> matchInt(long value) {
        return (v) -> {
            if (!v.valueType().matches(ValueTypes.Int)) {
                return false;
            }
            Number n = v.number();
            if (n instanceof BigInteger) {
                return n.equals(BigInteger.valueOf(value));
            }
            return value == n.longValue();
        };
    }

}
