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

import sun.misc.Unsafe;

import java.lang.reflect.Field;

final class UnsafeUtils {

    private static final Unsafe UNSAFE = findUnsafe(Unsafe.class);

    private UnsafeUtils() {
    }

    static Unsafe getUnsafe() {
        return UNSAFE;
    }

    private static Unsafe findUnsafe(Class<?> type) {
        try {
            try {
                return theUnsafe(type);

            } catch (Exception e) {
                Unsafe unsafe = searchField(type);
                if (unsafe == null) {
                    throw new Exception("No legal Unsafe field was found");
                }
                return unsafe;
            }
        } catch (Exception e) {
            throw new RuntimeException("Unsafe unavailable", e);
        }
    }

    private static Unsafe theUnsafe(Class<?> type)
            throws Exception {

        Field field = type.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        return Unsafe.class.cast(field.get(Unsafe.class));
    }

    private static Unsafe searchField(Class<?> type)
            throws Exception {

        for (Field field : type.getDeclaredFields()) {
            if (Unsafe.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                return Unsafe.class.cast(field.get(type));
            }
        }
        return null;
    }

}
