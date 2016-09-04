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

import com.noctarius.borabora.spi.query.TypeSpecs;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TypeSpecsTestCase
        extends AbstractTestCase {

    @Test(expected = WrongTypeException.class)
    public void test_invalid_typespec() {
        TypeSpecs.typeSpec("", Collections.emptyList());
    }

    @Test
    public void test_match_illegal_value_type() {
        Input input = Input.fromByteArray(hexToBytes("0x40"));
        assertFalse(TypeSpecs.Bool.valid(MajorType.ByteString, newQueryContext(input), 0));
    }

    @Test
    public void test_valid_uint() {
        Input input = Input.fromByteArray(hexToBytes("0x01"));
        assertTrue(TypeSpecs.Int.valid(MajorType.UnsignedInteger, newQueryContext(input), 0));
        assertTrue(TypeSpecs.UInt.valid(MajorType.UnsignedInteger, newQueryContext(input), 0));
        assertTrue(TypeSpecs.Number.valid(MajorType.UnsignedInteger, newQueryContext(input), 0));
    }

    @Test
    public void test_valid_nint() {
        Input input = Input.fromByteArray(hexToBytes("0x21"));
        assertTrue(TypeSpecs.Int.valid(MajorType.NegativeInteger, newQueryContext(input), 0));
        assertTrue(TypeSpecs.NInt.valid(MajorType.NegativeInteger, newQueryContext(input), 0));
        assertTrue(TypeSpecs.Number.valid(MajorType.NegativeInteger, newQueryContext(input), 0));
    }

    @Test
    public void test_matches_number() {
        assertTrue(TypeSpecs.Number.matches(TypeSpecs.Number));
        assertTrue(TypeSpecs.Int.matches(TypeSpecs.Number));
        assertTrue(TypeSpecs.UInt.matches(TypeSpecs.Number));
        assertTrue(TypeSpecs.NInt.matches(TypeSpecs.Number));
        assertTrue(TypeSpecs.Float.matches(TypeSpecs.Number));
    }

    @Test
    public void test_matches_int() {
        assertTrue(TypeSpecs.Int.matches(TypeSpecs.Int));
        assertTrue(TypeSpecs.UInt.matches(TypeSpecs.Int));
        assertTrue(TypeSpecs.NInt.matches(TypeSpecs.Int));
    }

    @Test
    public void test_matches_float() {
        assertTrue(TypeSpecs.Float.matches(TypeSpecs.Float));
    }

    @Test
    public void test_matches_semantic_tag() {
        assertTrue(TypeSpecs.SemanticTag.matches(TypeSpecs.SemanticTag));
        assertTrue(TypeSpecs.SpecializedSemanticTag.matches(TypeSpecs.SemanticTag));
        assertTrue(TypeSpecs.DateTime.matches(TypeSpecs.SemanticTag));
        assertTrue(TypeSpecs.Timstamp.matches(TypeSpecs.SemanticTag));
        assertTrue(TypeSpecs.URI.matches(TypeSpecs.SemanticTag));
        assertTrue(TypeSpecs.EncCBOR.matches(TypeSpecs.SemanticTag));
    }

    @Test
    public void test_matches_semantic_tag_unequals() {
        assertFalse(TypeSpecs.EncCBOR.matches(TypeSpecs.DateTime));
    }

    @Test
    public void test_matches_no_match() {
        assertFalse(TypeSpecs.Number.matches(TypeSpecs.Bool));
    }

}
