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

import com.noctarius.borabora.spi.codec.Decoder;
import com.noctarius.borabora.spi.codec.Encoder;
import com.noctarius.borabora.spi.query.QueryContext;
import com.noctarius.borabora.spi.query.QueryContextAware;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.function.Predicate;

public enum Predicates {
    ;

    private static final Predicate<Value> MATCH_ANY = (v) -> true;

    public static final Predicate<Value> any() {
        return MATCH_ANY;
    }

    public static Predicate<Value> matchStringIgnoreCase(String value) {
        return (v) -> {
            if (!v.valueType().matches(ValueTypes.String)) {
                return false;
            }

            return v.string().equalsIgnoreCase(value);
        };
    }

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

            if (v instanceof QueryContextAware) {
                QueryContext queryContext = ((QueryContextAware) v).queryContext();
                // TODO Match majorType ByteString vs TextString
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
            /* TODO not yet supported
            if (n instanceof BigDecimal) {
                return n.equals(BigDecimal.valueOf(value));
            }*/
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

    public static boolean predicateEquals(Predicate first, Predicate second) {
        String name = first.getClass().getName();
        String otherName = second.getClass().getName();

        if (name.contains("$$Lambda$") && !otherName.contains("$$Lambda$") //
                || !name.contains("$$Lambda$") && otherName.contains("$$Lambda$")) {

            return false;
        }

        if (!name.contains("$$Lambda$") && !otherName.contains("$$Lambda$")) {
            return first.equals(second);
        }

        int nameIndex = name.indexOf("$$Lambda$");
        int otherNameIndex = otherName.indexOf("$$Lambda$");

        int nameEndIndex = name.indexOf('/', nameIndex);
        int otherNameEndIndex = name.indexOf('/', otherNameIndex);

        return name.substring(nameIndex, nameEndIndex).equals(otherName.substring(otherNameIndex, otherNameEndIndex));
    }

}
