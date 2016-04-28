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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Predicate;

public enum Predicates {
    ;

    public static Predicate<Value> matchString(String value) {
        return (v) -> v.string().equals(value);
    }

    public static Predicate<Value> matchFloat(double value) {
        return (v) -> {
            Number n = v.number();
            if (n instanceof BigDecimal) {
                return n.equals(BigDecimal.valueOf(value));
            }
            return value == n.doubleValue();
        };
    }

    public static Predicate<Value> matchInt(long value) {
        return (v) -> {
            Number n = v.number();
            if (n instanceof BigInteger) {
                return n.equals(BigInteger.valueOf(value));
            }
            return value == n.longValue();
        };
    }

}
