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

abstract class AbstractValue
        implements Value {

    public static final String VALUE_TYPE_DOES_NOT_MATCH = "Requested value type does not match the read value: %s != %s";
    public static final String MAJOR_TYPE_DOES_NOT_MATCH = "Requested major type does not match the read value: %s != %s";
    public static final String VALUE_TYPE_NOT_A_DOUBLE = "Requested value type does not match the read value: {%s|%s} != %s";
    public static final String VALUE_TYPE_NOT_A_TRIPPLE = "Requested value type does not match the read value: {%s|%s|%s} != %s";

    private final MajorType majorType;
    private final ValueType valueType;

    protected AbstractValue(MajorType majorType, ValueType valueType) {
        this.majorType = majorType;
        this.valueType = valueType;
    }

    public MajorType majorType() {
        return majorType;
    }

    public ValueType valueType() {
        return valueType.identity();
    }

    protected void matchMajorType(MajorType expected) {
        if (expected != majorType) {
            String msg = String.format(MAJOR_TYPE_DOES_NOT_MATCH, expected, majorType);
            throw new WrongTypeException(msg);
        }
    }

    protected void matchValueType(ValueType expected) {
        ValueType identity = valueType.identity();
        if (expected != identity) {
            String msg = String.format(VALUE_TYPE_DOES_NOT_MATCH, expected, identity);
            throw new WrongTypeException(msg);
        }
    }

    protected void matchValueType(ValueType expected1, ValueType expected2, ValueType expected3) {
        ValueType identity = valueType.identity();
        if (expected1 != identity && expected2 != identity && expected3 != identity) {
            String msg = String.format(VALUE_TYPE_NOT_A_TRIPPLE, expected1, expected2, expected3, identity);
            throw new WrongTypeException(msg);
        }
    }

    protected void matchStringValueType() {
        ValueType identity = valueType.identity();
        if (ValueTypes.ByteString != identity && ValueTypes.TextString != identity) {

            String msg = String.format(VALUE_TYPE_NOT_A_DOUBLE, ValueTypes.ByteString, ValueTypes.TextString, identity);
            throw new WrongTypeException(msg);
        }
    }

    protected <T> T extract(Supplier<T> supplier) {
        return extract(null, supplier);
    }

    protected abstract <T> T extract(Validator validator, Supplier<T> supplier);

}
