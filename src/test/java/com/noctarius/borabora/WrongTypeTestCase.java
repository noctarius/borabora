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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class WrongTypeTestCase
        extends AbstractTestCase {

    @Test
    public void test_wrong_major_type_offset_and_message() {
        String expected = "Requested major type does not match the read value: SemanticTag != FloatingPointOrSimple[offset=0]";
        SimplifiedTestParser parser = buildParser("0xf7");
        Value value = parser.read(parser.newQueryBuilder().build());
        try {
            value.tag();
        } catch (WrongTypeException e) {
            assertEquals(0, e.getOffset());
            assertEquals(expected, e.getMessage());
            return;
        }
        fail();
    }

    @Test(expected = WrongTypeException.class)
    public void test_wrong_major_type_on_semantic_tag()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xf7");
        Value value = parser.read(parser.newQueryBuilder().build());
        value.tag();
    }

    @Test(expected = WrongTypeException.class)
    public void test_wrong_value_type_on_number()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xf7");
        Value value = parser.read(parser.newQueryBuilder().build());
        value.number();
    }

    @Test(expected = WrongTypeException.class)
    public void test_wrong_value_type_on_sequence()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xf7");
        Value value = parser.read(parser.newQueryBuilder().build());
        value.sequence();
    }

    @Test(expected = WrongTypeException.class)
    public void test_wrong_value_type_on_dictionary()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xf7");
        Value value = parser.read(parser.newQueryBuilder().build());
        value.dictionary();
    }

    @Test(expected = WrongTypeException.class)
    public void test_wrong_value_type_on_string()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xf7");
        Value value = parser.read(parser.newQueryBuilder().build());
        value.string();
    }

    @Test(expected = WrongTypeException.class)
    public void test_wrong_value_type_on_bool()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0x40");
        Value value = parser.read(parser.newQueryBuilder().build());
        value.bool();
    }

}
