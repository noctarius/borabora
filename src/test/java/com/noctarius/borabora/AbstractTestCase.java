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

import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public abstract class AbstractTestCase {

    public static void assertEqualsNumber(Number n1, Number n2) {
        if (n1.getClass().equals(n2.getClass())) {
            assertEquals(n1, n2);
        }

        BigInteger b1 = n1 instanceof BigInteger ? (BigInteger) n1 : BigInteger.valueOf(n1.longValue());
        BigInteger b2 = n2 instanceof BigInteger ? (BigInteger) n2 : BigInteger.valueOf(n2.longValue());
        assertEquals(b1, b2);
    }

    public static byte[] hexToBytes(String hex) {
        hex = hex.toLowerCase();
        if (hex.startsWith("0x")) {
            hex = hex.substring(2);
        }
        return DatatypeConverter.parseHexBinary(hex);
    }

    public static SimplifiedTestParser buildParser(String hex) {
        byte[] data = hexToBytes(hex);
        Input input = Input.fromByteArray(data);
        return new SimplifiedTestParser(com.noctarius.borabora.Parser.newBuilder().build(), input);
    }

    protected static class SimplifiedTestParser {

        private final com.noctarius.borabora.Parser parser;
        private final Input input;

        private SimplifiedTestParser(com.noctarius.borabora.Parser parser, Input input) {
            this.parser = parser;
            this.input = input;
        }

        Value read(GraphQuery graphQuery) {
            return parser.read(input, graphQuery);
        }

        Value read(String query) {
            return parser.read(input, query);
        }

        GraphQuery prepareQuery(String query) {
            return parser.prepareQuery(query);
        }

    }

}
