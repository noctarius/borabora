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
package com.noctarius.borabora.spi;

import com.noctarius.borabora.AbstractTestCase;
import org.junit.Test;

import java.util.function.Predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EqualsSupportTestCase
        extends AbstractTestCase {

    private static final Predicate PREDICATE = new PredicateTestImpl();

    @Test
    public void call_constructor() {
        callConstructor(EqualsSupport.class);
    }

    @Test
    public void test_equals_lambda_nonlambda() {
        Predicate lambda = (o) -> false;

        assertFalse(EqualsSupport.equals(lambda, PREDICATE));
        assertFalse(EqualsSupport.equals(PREDICATE, lambda));
        assertTrue(EqualsSupport.equals(PREDICATE, PREDICATE));
        assertTrue(EqualsSupport.equals(lambda, lambda));

        Predicate lambda2 = (o) -> true;
        assertFalse(EqualsSupport.equals(lambda, lambda2));
    }

    private static class PredicateTestImpl
            implements Predicate {

        @Override
        public boolean test(Object o) {
            return false;
        }
    }

}
