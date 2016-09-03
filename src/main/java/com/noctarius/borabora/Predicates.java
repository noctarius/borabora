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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

import static com.noctarius.borabora.spi.Constants.MATCH_STRING_FAST_PATH_TRESHOLD;

/**
 * This class contains parsing optimized utility methods to provide {@link Predicate}
 * matchers against {@link Dictionary} keys or values or {@link Sequence} values. The methods
 * provided here can be passed to the following dictionary, sequence or query methods:
 * <ul>
 * <li>{@link Dictionary#containsKey(Predicate)}</li>
 * <li>{@link Dictionary#containsValue(Predicate)}</li>
 * <li>{@link com.noctarius.borabora.builder.QueryTokenBuilder#dictionary(Predicate)}</li>
 * <li>{@link com.noctarius.borabora.builder.QueryTokenBuilder#sequenceMatch(Predicate)}</li>
 * </ul>
 */
public final class Predicates {

    private static final Predicate<Value> MATCH_ANY = (v) -> true;

    private Predicates() {
    }

    /**
     * Matches any given {@link Value}
     *
     * @return always true
     */
    public static final Predicate<Value> any() {
        return MATCH_ANY;
    }

    /**
     * Matches strings against the provided <tt>value</tt> with ignored case sensitivity. Anyhow
     * as an optimization, the given value at first is tried to be matched in a case sensitive mode
     * in case the stream actually matches directly (see {@link #matchString(String)} for further
     * details). Only afterwards the string from the stream is actually deserialized and matched
     * using {@link String#equalsIgnoreCase(String)}.
     *
     * @param value the value to match
     * @return true is the value in the CBOR stream matches the given value, otherwise false
     */
    public static Predicate<Value> matchStringIgnoreCase(String value) {
        Objects.requireNonNull(value, "value must not be null");
        return (v) -> {
            if (!v.valueType().matches(ValueTypes.String)) {
                return false;
            }

            return v.string().equalsIgnoreCase(value);
        };
    }

    /**
     * Matches strings against the provided <tt>value</tt> with case sensitivity. As an optimization,
     * the given value is serialized into a CBOR encoded value and is tried to be matched on a
     * byte-array level, without actually deserializing the CBOR data stream string itself. That
     * provides the query matcher with way better performance. Only in case the byte-array based
     * matching the string is deserialized and matched using {@link String#equals(Object)}}.
     *
     * @param value the value to match
     * @return true is the value in the CBOR stream matches the given value, otherwise false
     */
    public static Predicate<Value> matchString(String value) {
        Objects.requireNonNull(value, "value must not be null");
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
                // If the matcher string must be a TextString the matched string
                // can never match if a ByteString (only ASCII)
                byteStringMatcher = (v) -> false;
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

            // Match ObjectValue instances
            return slowPathPredicate.test(v);
        };
    }

    /**
     * Matches any kind of floating point data type (<tt>float</tt>, <tt>double</tt>, {@link BigDecimal}
     * against the provided <tt>value</tt>. This method does not support matching against {@link BigDecimal}
     * as a user provided value.
     *
     * @param value the value to match
     * @return true is the value in the CBOR stream matches the given value, otherwise false
     */
    public static Predicate<Value> matchFloat(double value) {
        return (v) -> {
            if (!v.valueType().matches(ValueTypes.Float)) {
                return false;
            }
            Number n = v.number();
            if (n instanceof BigDecimal) {
                return n.equals(BigDecimal.valueOf(value));
            }
            return Double.compare(value, n.doubleValue()) == 0;
        };
    }

    /**
     * Matches any kind of integer data type (<tt>byte</tt>, <tt>short</tt>, <tt>int</tt>, <tt>long</tt> or
     * {@link BigInteger} against the provided <tt>value</tt>. This method does not support matching against
     * {@link BigInteger} as a user provided value.
     *
     * @param value the value to match
     * @return true is the value in the CBOR stream matches the given value, otherwise false
     */
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
            if (length != expectedLength) {
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
