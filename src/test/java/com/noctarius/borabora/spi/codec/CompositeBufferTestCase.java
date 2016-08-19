package com.noctarius.borabora.spi.codec;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
/*
 * Copyright (c) 2008-2016, Hazelcast, Inc. All Rights Reserved.
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

public class CompositeBufferTestCase {

    @Test
    public void test_write_bytearray() {
        CompositeBuffer compositeBuffer = CompositeBuffer.newCompositeBuffer(16);
        byte[] expected = randomByteArray(1024);
        compositeBuffer.write(expected, 0, expected.length);
        byte[] actual = compositeBuffer.toByteArray();
        assertArrayEquals(expected, actual);
    }

    private byte[] randomByteArray(int size) {
        byte[] bytes = new byte[size];
        Random random = new Random();
        random.nextBytes(bytes);
        return bytes;
    }

}