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
import sun.misc.Unsafe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class UnsafeUtilsTestCase
        extends AbstractTestCase {

    private static final Object theUnsafe = new Object();

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

        Method method = UnsafeUtils.class.getDeclaredMethod("findUnsafe", Class.class);
        method.setAccessible(true);
        assertNotNull(method.invoke(UnsafeUtils.class, Unsafe.class));
    }

    @Test
    public void test_findunsafe_myunsafe()
            throws Exception {

        Method method = UnsafeUtils.class.getDeclaredMethod("findUnsafe", Class.class);
        method.setAccessible(true);
        assertNotNull(method.invoke(UnsafeUtils.class, MyUnsafe.class));
    }

    @Test(expected = RuntimeException.class)
    public void fail_findunsafe()
            throws Throwable {

        Method method = UnsafeUtils.class.getDeclaredMethod("findUnsafe", Class.class);
        method.setAccessible(true);

        try {
            method.invoke(UnsafeUtils.class, this.getClass());
        } catch (InvocationTargetException e) {
            // Unwrap
            throw e.getCause();
        }
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

    private static class MyUnsafe {
        private static final Object theUnsafe = new Object();
        private static final Unsafe THE_ONE = UnsafeUtils.getUnsafe();
    }

}
