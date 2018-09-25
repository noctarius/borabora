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
package com.noctarius.borabora;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(Parameterized.class)
public class ExtractNullTestCase
        extends AbstractTestCase {

    @Parameterized.Parameters(name = "test_extract_null_value_{0}")
    public static Collection<Object[]> suppliers() {
        return Arrays.asList(new Object[][]{{"tag", function(Value::tag)}, //
                                            {"number", function(Value::number)}, //
                                            {"sequence", function(Value::sequence)}, //
                                            {"dictionary", function(Value::dictionary)}, //
                                            {"string", function(Value::string)}, //
                                            {"bool", function(Value::bool)}});
    }

    private final Function<Value, Object> function;

    public ExtractNullTestCase(String name, Function<Value, Object> function) {
        this.function = function;
    }

    @Test
    public void test_null()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xf6");

        Value value = parser.read(parser.newQueryBuilder().build());

        assertEquals(ValueTypes.Null, value.valueType());
        assertNull(function.apply(value));
    }

    private static Function<Value, Object> function(Function<Value, Object> function) {
        return function;
    }

}
