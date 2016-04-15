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

import java.util.function.Supplier;

import static com.noctarius.borabora.ValueTypes.ByteString;
import static com.noctarius.borabora.ValueTypes.TextString;

abstract class AbstractValue
        implements Value {

    public static final String VALUE_TYPE_DOES_NOT_MATCH = "Requested value type does not match the read value: %s != %s";
    public static final String MAJOR_TYPE_DOES_NOT_MATCH = "Requested major type does not match the read value: %s != %s";
    public static final String VALUE_TYPE_NOT_A_DOUBLE = "Requested value type does not match the read value: {%s|%s} != %s";
    public static final String VALUE_TYPE_NOT_A_TRIPPLE = "Requested value type does not match the read value: {%s|%s|%s} != %s";

    protected void matchMajorType(MajorType actual, MajorType expected) {
        if (expected != actual) {
            String msg = String.format(MAJOR_TYPE_DOES_NOT_MATCH, expected, actual);
            throw new WrongTypeException(msg);
        }
    }

    protected void matchValueType(ValueType actual, ValueType expected) {
        if (!actual.matches(expected)) {
            String msg = String.format(VALUE_TYPE_DOES_NOT_MATCH, expected, actual);
            throw new WrongTypeException(msg);
        }
    }

    protected void matchValueType(ValueType actual, ValueType expected1, ValueType expected2, ValueType expected3) {
        if (!actual.matches(expected1) && !actual.matches(expected2) && !actual.matches(expected3)) {
            String msg = String.format(VALUE_TYPE_NOT_A_TRIPPLE, expected1, expected2, expected3, actual);
            throw new WrongTypeException(msg);
        }
    }

    protected void matchStringValueType(ValueType actual) {
        ValueType identity = actual.identity();
        if (ValueTypes.String != identity) {
            String msg = String.format(VALUE_TYPE_NOT_A_TRIPPLE, ValueTypes.String, ByteString, TextString, identity);
            throw new WrongTypeException(msg);
        }
    }

    protected <T> T extract(Supplier<T> supplier) {
        return extract(null, supplier);
    }

    protected abstract <T> T extract(Validator validator, Supplier<T> supplier);

}
