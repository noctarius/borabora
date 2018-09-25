/*
 * Copyright (c) 2016-2018, Christoph Engelbert (aka noctarius) and
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

import com.noctarius.borabora.Dictionary;
import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.Sequence;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.ValueType;
import com.noctarius.borabora.ValueTypes;
import com.noctarius.borabora.WrongTypeException;

import java.util.function.Supplier;

import static com.noctarius.borabora.ValueTypes.ASCII;
import static com.noctarius.borabora.ValueTypes.ByteString;
import static com.noctarius.borabora.ValueTypes.TextString;

/**
 * The <tt>AbstractValue</tt> abstract class is supposed to be the base class for all {@link Value}
 * implementations. It provides the basics for matching {@link MajorType} and {@link ValueType}
 * before trying to actually extract the real data item's value.
 */
public abstract class AbstractValue
        implements Value {

    private static final String VALUE_TYPE_DOES_NOT_MATCH = "Requested value type does not match the read value: %s != %s";
    private static final String MAJOR_TYPE_DOES_NOT_MATCH = "Requested major type does not match the read value: %s != %s";
    private static final String VALUE_TYPE_NOT_A_DOUBLE = "Requested value type does not match the read value: {%s|%s} != %s";
    private static final String VALUE_TYPE_NOT_A_TRIPPLE = "Requested value type does not match the read value: {%s|%s|%s} != %s";

    protected AbstractValue() {
    }

    @Override
    public String asString() {
        Object value = byValueType();
        String valueAsString;
        if (value instanceof Dictionary) {
            valueAsString = ((Dictionary) value).asString();
        } else if (value instanceof Sequence) {
            valueAsString = ((Sequence) value).asString();
        } else {
            valueAsString = value == null ? "null" : value.toString();
        }
        return valueType() + "{ " + valueAsString + " }";
    }

    protected void matchMajorType(MajorType actual, MajorType expected) {
        if (expected != actual) {
            String msg = String.format(MAJOR_TYPE_DOES_NOT_MATCH, expected, actual);
            throw new WrongTypeException(offset(), msg);
        }
    }

    protected void matchValueType(ValueType actual, ValueType expected) {
        if (!actual.matches(expected)) {
            String msg = String.format(VALUE_TYPE_DOES_NOT_MATCH, expected, actual);
            throw new WrongTypeException(offset(), msg);
        }
    }

    protected void matchValueType(ValueType actual, ValueType expected1, ValueType expected2) {
        if (!actual.matches(expected1) && !actual.matches(expected2)) {
            String msg = String.format(VALUE_TYPE_NOT_A_DOUBLE, expected1, expected2, actual);
            throw new WrongTypeException(offset(), msg);
        }
    }

    protected void matchStringValueType(ValueType actual) {
        ValueType identity = actual.identity();
        if (ValueTypes.String != identity) {
            String msg = String.format(VALUE_TYPE_NOT_A_TRIPPLE, ValueTypes.String, ASCII, TextString, identity);
            throw new WrongTypeException(offset(), msg);
        }
    }

    protected abstract <T> T extract(Validator validator, Supplier<T> supplier);

    protected interface Validator {
        void validate();
    }

}
