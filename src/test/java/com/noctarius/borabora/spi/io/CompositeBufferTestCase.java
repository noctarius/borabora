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
package com.noctarius.borabora.spi.io;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class CompositeBufferTestCase {

    @Test
    public void test_read_multiple_chunks() {
        CompositeBuffer compositeBuffer = CompositeBuffer.newCompositeBuffer();
        byte[] expected = randomByteArray(400000);
        for (int i = 0; i < expected.length; i++) {
            compositeBuffer.write(i, expected[i]);
        }

        byte[] actual = new byte[expected.length];
        for (int i = 0; i < expected.length; i++) {
            actual[i] = compositeBuffer.read(i);
        }
        assertArrayEquals(expected, actual);
    }

    @Test
    public void test_write_verify_returned_offset() {
        CompositeBuffer compositeBuffer = CompositeBuffer.newCompositeBuffer(16);
        long offset = compositeBuffer.write(5, (byte) 0x1);
        assertEquals(6, offset);
    }

    @Test
    public void test_read_bytearray_multiple_chunks() {
        CompositeBuffer compositeBuffer = CompositeBuffer.newCompositeBuffer(16);
        byte[] expected = randomByteArray(32);
        for (int i = 0; i < expected.length; i++) {
            compositeBuffer.write(i, expected[i]);
        }
        byte[] actual = new byte[32];
        compositeBuffer.read(actual, 0, 32);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void test_read_bytearray_multiple_chunks_read_over_internal_border() {
        CompositeBuffer compositeBuffer = CompositeBuffer.newCompositeBuffer(16);
        byte[] expected = randomByteArray(32);
        compositeBuffer.write(expected, 0, expected.length);

        byte[] actual = new byte[16];
        compositeBuffer.read(actual, 8, 16);
        for (int i = 0; i < 16; i++) {
            assertEquals(expected[i + 8], actual[i]);
        }
    }

    @Test
    public void test_write_byte() {
        CompositeBuffer compositeBuffer = CompositeBuffer.newCompositeBuffer(16);
        byte[] expected = randomByteArray(32);
        for (int i = 0; i < expected.length; i++) {
            compositeBuffer.write(i, expected[i]);
        }
        byte[] actual = compositeBuffer.toByteArray();
        assertArrayEquals(expected, actual);
    }

    @Test
    public void test_write_bytearray() {
        CompositeBuffer compositeBuffer = CompositeBuffer.newCompositeBuffer(16);
        byte[] expected = randomByteArray(1024);
        compositeBuffer.write(expected, 0, expected.length);
        byte[] actual = compositeBuffer.toByteArray();
        assertArrayEquals(expected, actual);
    }

    @Test(expected = IllegalStateException.class)
    public void fail_write_tobytearray_highestoffset_too_large_for_bytearray() {
        CompositeBuffer compositeBuffer = CompositeBuffer.newCompositeBuffer();
        fakeHighestOffset(compositeBuffer, Integer.MAX_VALUE + 1L);
        compositeBuffer.toByteArray();
    }

    @Test
    public void test_write_array_tooutputstream()
            throws Exception {

        CompositeBuffer compositeBuffer = CompositeBuffer.newCompositeBuffer(16);
        byte[] expected = randomByteArray(1099);
        compositeBuffer.write(expected, 0, expected.length);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compositeBuffer.writeToOutputStream(baos);
        byte[] actual = baos.toByteArray();
        assertArrayEquals(expected, actual);
    }

    @Test
    public void test_write_tooutputstream()
            throws Exception {

        CompositeBuffer compositeBuffer = CompositeBuffer.newCompositeBuffer(16);
        byte[] expected = randomByteArray(1024);
        for (int i = 0; i < expected.length; i++) {
            compositeBuffer.write(i, expected[i]);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compositeBuffer.writeToOutputStream(baos);
        byte[] actual = baos.toByteArray();
        assertArrayEquals(expected, actual);
    }

    @Test
    public void test_write_tobytebuffer_heap()
            throws Exception {

        CompositeBuffer compositeBuffer = CompositeBuffer.newCompositeBuffer(16);
        byte[] expected = randomByteArray(1024);
        compositeBuffer.write(expected, 0, expected.length);
        ByteBuffer byteBuffer = compositeBuffer.toByteBuffer();
        byte[] actual = byteBuffer.array();
        assertArrayEquals(expected, actual);
    }

    @Test(expected = IllegalStateException.class)
    public void fail_write_tobytebuffer_heap_highestoffset_too_large_for_bytearray()
            throws Exception {

        CompositeBuffer compositeBuffer = CompositeBuffer.newCompositeBuffer();
        fakeHighestOffset(compositeBuffer, Integer.MAX_VALUE + 1L);
        compositeBuffer.toByteBuffer();
    }

    @Test
    public void test_write_tobytebuffer_direct()
            throws Exception {

        CompositeBuffer compositeBuffer = CompositeBuffer.newCompositeBuffer(16);
        byte[] expected = randomByteArray(1024);
        compositeBuffer.write(expected, 0, expected.length);
        ByteBuffer byteBuffer = compositeBuffer.toByteBuffer(true);
        byte[] actual = new byte[byteBuffer.position()];
        byteBuffer.flip();
        byteBuffer.get(actual, 0, actual.length);
        assertArrayEquals(expected, actual);
    }

    @Test(expected = IllegalStateException.class)
    public void fail_write_tobytebuffer_direct_highestoffset_too_large_for_bytearray()
            throws Exception {

        CompositeBuffer compositeBuffer = CompositeBuffer.newCompositeBuffer();
        fakeHighestOffset(compositeBuffer, Integer.MAX_VALUE + 1L);
        compositeBuffer.toByteBuffer(true);
    }

    private void fakeHighestOffset(CompositeBuffer compositeBuffer, long highestOffset) {
        try {
            Field field = CompositeBuffer.class.getDeclaredField("highestOffset");
            field.setAccessible(true);
            field.set(compositeBuffer, highestOffset);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] randomByteArray(int size) {
        byte[] bytes = new byte[size];
        Random random = new Random();
        random.nextBytes(bytes);
        return bytes;
    }

}