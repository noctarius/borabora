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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BooleanTestCase
        extends AbstractTestCase {

    @Test
    public void test_boolean_false()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xf4");

        Value value = parser.read(parser.newQueryBuilder().build());

        assertEquals(ValueTypes.Bool, value.valueType());
        assertEquals(Boolean.FALSE, value.bool());
    }

    @Test
    public void test_boolean_true()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xf5");

        Value value = parser.read(parser.newQueryBuilder().build());

        assertEquals(ValueTypes.Bool, value.valueType());
        assertEquals(Boolean.TRUE, value.bool());
    }

}
