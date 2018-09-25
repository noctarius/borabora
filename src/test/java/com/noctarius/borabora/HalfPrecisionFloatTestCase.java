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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HalfPrecisionFloatTestCase {

    @Test
    public void test_int_value() {
        HalfPrecisionFloat f = HalfPrecisionFloat.valueOf(12.f);
        assertEquals(12, f.intValue());
    }

    @Test
    public void test_long_value() {
        HalfPrecisionFloat f = HalfPrecisionFloat.valueOf(12.f);
        assertEquals(12L, f.longValue());
    }

    @Test
    public void test_float_value() {
        HalfPrecisionFloat f = HalfPrecisionFloat.valueOf(12.f);
        assertEquals(0, Float.compare(12.f, f.floatValue()));
    }

    @Test
    public void test_double_value() {
        HalfPrecisionFloat f = HalfPrecisionFloat.valueOf(12.f);
        assertEquals(0, Double.compare(12.d, f.doubleValue()));
    }

    @Test
    public void test_equals() {
        HalfPrecisionFloat f = HalfPrecisionFloat.valueOf(12.f);
        assertTrue(f.equals(f));
        assertFalse(f.equals(new Object()));
        assertFalse(f.equals(HalfPrecisionFloat.valueOf(11.f)));
        assertTrue(f.equals(HalfPrecisionFloat.valueOf(12.f)));
    }

    @Test
    public void test_hashcode() {
        HalfPrecisionFloat f = HalfPrecisionFloat.valueOf(12.f);
        assertEquals(Float.floatToIntBits(f.floatValue()), f.hashCode());

        HalfPrecisionFloat f2 = HalfPrecisionFloat.valueOf(+0.f);
        assertEquals(0, f2.hashCode());
    }

    @Test
    public void test_tostring() {
        HalfPrecisionFloat f = HalfPrecisionFloat.valueOf(12.f);
        assertEquals("HalfPrecisionFloat{value=12.0}", f.toString());
    }

}
