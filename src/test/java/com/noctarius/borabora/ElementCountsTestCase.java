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

public class ElementCountsTestCase
        extends AbstractTestCase {

    @Test
    public void test_sequence_elementcount_head_byte() {
        Input input = Input.fromByteArray(hexToBytes("0x81"));
        assertEquals(1, ElementCounts.sequenceElementCount(input, 0));
    }

    @Test
    public void test_sequence_elementcount_1_byte() {
        Input input = Input.fromByteArray(hexToBytes("0x98ff"));
        assertEquals(255, ElementCounts.sequenceElementCount(input, 0));
    }

    @Test
    public void test_sequence_elementcount_2_byte() {
        Input input = Input.fromByteArray(hexToBytes("0x99ffff"));
        assertEquals(65535, ElementCounts.sequenceElementCount(input, 0));
    }

    @Test
    public void test_sequence_elementcount_4_byte() {
        Input input = Input.fromByteArray(hexToBytes("0x9affffffff"));
        assertEquals(Integer.MAX_VALUE * 2L + 1, ElementCounts.sequenceElementCount(input, 0));
    }

    @Test
    public void test_sequence_elementcount_8_byte() {
        Input input = Input.fromByteArray(hexToBytes("0x9b7fffffffffffffff"));
        assertEquals(Long.MAX_VALUE, ElementCounts.sequenceElementCount(input, 0));
    }

}
