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
package com.noctarius.borabora.spi.codec;

import com.noctarius.borabora.AbstractTestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HalfPrecisionTestCase
        extends AbstractTestCase {

    @Test
    public void call_constructor() {
        callConstructor(HalfPrecision.class);
    }

    @Test
    public void test_posinf() {
        int value = HalfPrecision.fromFloat(Float.POSITIVE_INFINITY);
        float actual = HalfPrecision.toFloat(value);
        assertEquals(Float.POSITIVE_INFINITY, actual, 0f);
    }

    @Test
    public void test_close_to_inf() {
        int value = HalfPrecision.fromFloat(2139095039f);
        float actual = HalfPrecision.toFloat(value);
        assertEquals(Float.POSITIVE_INFINITY, actual, 0f);
    }

    @Test
    public void test_neginf() {
        int value = HalfPrecision.fromFloat(Float.NEGATIVE_INFINITY);
        float actual = HalfPrecision.toFloat(value);
        assertEquals(Float.NEGATIVE_INFINITY, actual, 0f);
    }

    @Test
    public void test_nan() {
        int value = HalfPrecision.fromFloat(Float.NaN);
        float actual = HalfPrecision.toFloat(value);
        assertEquals(Float.NaN, actual, 0f);
    }

}
