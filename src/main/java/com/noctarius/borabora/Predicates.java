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

import static com.noctarius.borabora.spi.Constants.MATCH_STRING_FAST_PATH_TRESHOLD;

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
        Predicate<Value> slowPathPredicate = matchString0(value);

        // For more than 1024 chars we use the slow path for now
        if (value.length() > MATCH_STRING_FAST_PATH_TRESHOLD) {
            return slowPathPredicate;
        }

        // Predefine both possible matchers
        Predicate<Value> byteStringMatcher;
        Predicate<Value> textStringMatcher;

        // Pre-encode matching value
        byte[] expected = buildStringMatcherByteArray(value, Encoder::putString);

        MajorType currentMajorType = MajorType.findMajorType((short) (expected[0] & 0xFF));
        switch (currentMajorType) {
            case ByteString:
                byteStringMatcher = buildStringMatcher(expected);

                // Encode specifically as TextString
                expected = buildStringMatcherByteArray(value, Encoder::putTextString);
                textStringMatcher = buildStringMatcher(expected);
                break;

            default: // Always TextString
                byteStringMatcher = slowPathPredicate;
                textStringMatcher = buildStringMatcher(expected);
        }

        return (v) -> {
            if (!v.valueType().matches(ValueTypes.String)) {
                return false;
            }

            // Stream values can be tried to match them on an array level
            if (v instanceof QueryContextAware) {
                MajorType majorType = v.majorType();

                switch (majorType) {
                    case ByteString:
                        return byteStringMatcher.test(v);
                    case TextString:
                        return textStringMatcher.test(v);
                }
            }

            return slowPathPredicate.test(v);
        };
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

    private static byte[] buildStringMatcherByteArray(String value, StringPreencoder preencoder) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(value.length() * 2);
        preencoder.apply(value, 0, Output.toOutputStream(baos));
        return baos.toByteArray();
    }

    private static Predicate<Value> buildStringMatcher(byte[] expected) {
        int expectedLength = expected.length;
        return (v) -> {
            QueryContext queryContext = ((QueryContextAware) v).queryContext();

            MajorType majorType = v.majorType();
            long offset = v.offset();

            Input input = queryContext.input();
            long length = Decoder.length(input, majorType, offset);
            if (length < expectedLength) {
                return false;
            }

            byte[] data = new byte[expectedLength];
            input.read(data, offset, expectedLength);
            return Arrays.equals(expected, data);
        };
    }

    private static Predicate<Value> matchString0(String value) {
        return (v) -> {
            if (!v.valueType().matches(ValueTypes.String)) {
                return false;
            }

            return v.string().equals(value);
        };
    }

    private interface StringPreencoder {
        void apply(String value, int offset, Output output);
    }

}
