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

import java.lang.reflect.Method;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class UnsafeUtilsTestCase
        extends AbstractTestCase {

    @Test
    public void call_constructor() {
        callConstructor(UnsafeUtils.class);
    }

    @Test
    public void test_getunsafe_findunsafe()
            throws Exception {

        Method method = UnsafeUtils.class.getDeclaredMethod("getUnsafe");
        method.setAccessible(true);
        assertNotNull(method.invoke(UnsafeUtils.class));
    }

    @Test
    public void test_findunsafe()
            throws Exception {

        Method method = UnsafeUtils.class.getDeclaredMethod("findUnsafe");
        method.setAccessible(true);
        assertNotNull(method.invoke(UnsafeUtils.class));
    }

    @Test
    public void test_searchfield_available()
            throws Exception {

        Method method = UnsafeUtils.class.getDeclaredMethod("searchField", Class.class);
        method.setAccessible(true);
        assertNotNull(method.invoke(UnsafeUtils.class, UnsafeUtils.class));
    }

    @Test
    public void test_searchfield_not_available()
            throws Exception {

        Method method = UnsafeUtils.class.getDeclaredMethod("searchField", Class.class);
        method.setAccessible(true);
        assertNull(method.invoke(UnsafeUtils.class, this.getClass()));
    }

}
