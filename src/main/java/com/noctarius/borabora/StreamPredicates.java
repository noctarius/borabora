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

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.function.Predicate;

public enum StreamPredicates {
    ;

    public static StreamPredicate matchInt(long value) {
        return matchValue(ValueTypes.Int, Predicates.matchInt(value));
    }

    public static StreamPredicate matchFloat(double value) {
        return matchValue(ValueTypes.Float, Predicates.matchFloat(value));
    }

    public static StreamPredicate matchString(String value) {
        // If indefinite or large string we have to go the slow path
        StreamPredicate slowPathPredicate = matchValue(ValueTypes.String, Predicates.matchString(value));
        if (value.length() * 4L > Integer.MAX_VALUE) {
            return slowPathPredicate;
        }

        // Pre-encode matching value
        ByteArrayOutputStream baos = new ByteArrayOutputStream(value.length() * 2);
        Encoder.putString(value, 0, Output.toOutputStream(baos));

        byte[] expected = baos.toByteArray();
        int expectedLength = expected.length;

        return (majorType, valueType, offset, queryContext) -> {
            if (!valueType.matches(ValueTypes.String)) {
                return false;
            }

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

            return slowPathPredicate.test(majorType, valueType, offset, queryContext);
        };
    }

    public static StreamPredicate matchValue(Predicate<Value> predicate) {
        return matchValue(null, predicate);
    }

    public static StreamPredicate matchValue(ValueType vt, Predicate<Value> predicate) {
        return new StreamPredicate() {

            private RelocatableStreamValue streamValue = null;

            @Override
            public boolean test(MajorType majorType, ValueType valueType, long offset, QueryContext queryContext) {
                if (vt != null && !valueType.matches(vt)) {
                    return false;
                }

                if (streamValue == null) {
                    streamValue = new RelocatableStreamValue(queryContext);
                }

                streamValue.relocate(majorType, valueType, offset);
                return predicate.test(streamValue);
            }
        };
    }

}
