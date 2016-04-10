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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MajorTypeTestCase {

    @Test
    public void test_indefinite() {
        assertFalse(MajorType.UnsignedInteger.indefinite());
        assertFalse(MajorType.NegativeInteger.indefinite());
        assertFalse(MajorType.SemanticTag.indefinite());
        assertFalse(MajorType.FloatingPointOrSimple.indefinite());
        assertFalse(MajorType.Unknown.indefinite());

        assertTrue(MajorType.ByteString.indefinite());
        assertTrue(MajorType.TextString.indefinite());
        assertTrue(MajorType.Sequence.indefinite());
        assertTrue(MajorType.Dictionary.indefinite());
    }

    @Test
    public void test_match_majortype() {
        assertTrue(MajorType.UnsignedInteger.match((short) (Constants.MT_UNSINGED_INT << 5)));
        assertTrue(MajorType.NegativeInteger.match((short) (Constants.MT_NEGATIVE_INT << 5)));
        assertTrue(MajorType.ByteString.match((short) (Constants.MT_BYTESTRING << 5)));
        assertTrue(MajorType.TextString.match((short) (Constants.MT_TEXTSTRING << 5)));
        assertTrue(MajorType.Sequence.match((short) (Constants.MT_SEQUENCE << 5)));
        assertTrue(MajorType.Dictionary.match((short) (Constants.MT_DICTIONARY << 5)));
        assertTrue(MajorType.SemanticTag.match((short) (Constants.MT_SEMANTIC_TAG << 5)));
        assertTrue(MajorType.FloatingPointOrSimple.match((short) (Constants.MT_FLOAT_SIMPLE << 5)));
    }

}
