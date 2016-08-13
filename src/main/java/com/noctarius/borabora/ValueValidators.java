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

import java.math.BigInteger;

enum ValueValidators {
    ;

    public static void isByteString(Value value, Object extracted) {
        if (!ValueTypes.ByteString.matches(value.valueType())) {
            throw new IllegalArgumentException("value is not a bytestring");
        }
    }

    public static void isTextString(Value value, Object extracted) {
        if (!ValueTypes.TextString.matches(value.valueType())) {
            throw new IllegalArgumentException("value is not a textstring");
        }
    }

    public static void isPositive(Value value, Object extracted) {
        Number number = (Number) extracted;
        if (isNegative(number)) {
            throw new IllegalArgumentException("extracted value is not positive");
        }
    }

    public static void isNegative(Value value, Object extracted) {
        Number number = (Number) extracted;
        if (!isNegative(number)) {
            throw new IllegalArgumentException("extracted value is not negative");
        }
    }

    private static boolean isNegative(Number number) {
        if (number instanceof BigInteger) {
            return ((BigInteger) number).signum() < 0;
        }
        if (number instanceof HalfPrecisionFloat //
                || number instanceof Float //
                || number instanceof Double) {

            return Double.compare(number.doubleValue(), 0d) < 0;
        }
        return number.longValue() < 0;
    }

}
