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

public class WrongTypeTestCase
        extends AbstractTestCase {

    @Test(expected = WrongTypeException.class)
    public void test_wrong_major_type_on_semantic_tag()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xf7");
        Value value = parser.read(GraphQuery.newBuilder().build());
        value.tag();
    }

    @Test(expected = WrongTypeException.class)
    public void test_wrong_value_type_on_number()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xf7");
        Value value = parser.read(GraphQuery.newBuilder().build());
        value.number();
    }

    @Test(expected = WrongTypeException.class)
    public void test_wrong_value_type_on_sequence()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xf7");
        Value value = parser.read(GraphQuery.newBuilder().build());
        value.sequence();
    }

    @Test(expected = WrongTypeException.class)
    public void test_wrong_value_type_on_dictionary()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xf7");
        Value value = parser.read(GraphQuery.newBuilder().build());
        value.dictionary();
    }

    @Test(expected = WrongTypeException.class)
    public void test_wrong_value_type_on_string()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xf7");
        Value value = parser.read(GraphQuery.newBuilder().build());
        value.string();
    }

    @Test(expected = WrongTypeException.class)
    public void test_wrong_value_type_on_bool()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x40");
        Value value = parser.read(GraphQuery.newBuilder().build());
        value.bool();
    }

}
