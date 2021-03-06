/*
 * Copyright (c) 2008-2016-2018, Hazelcast, Inc. All Rights Reserved.
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
package com.noctarius.borabora.impl;

import org.junit.Test;

public class TokenMgrExceptionTestCase {

    @Test
    public void testcoverage_addEscapes() {

        TokenMgrException.addEscapes("\b\t\n\f\r\"\'\\" + (char) 0x19 + (char) 0x7f);
    }

    @Test
    public void testcoverage_useless_constructor() {
        new TokenMgrException();
    }

}