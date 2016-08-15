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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import sun.misc.Unsafe;

import java.util.Arrays;
import java.util.function.Function;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class InputTestCase
        extends AbstractTestCase {

    @Parameterized.Parameters(name = "{1}")
    public static Iterable<Object[]> parameters() {
        return Arrays.asList( //
                new Object[][]{ //
                                {input(Input::fromByteArray), "ByteArrayInput"}, //
                                {input(InputTestCase::unsafeInput), "UnsafeByteInput"}});
    }

    private static Function<byte[], Input> input(Function<byte[], Input> function) {
        return function;
    }

    private static Input unsafeInput(byte[] data) {
        Unsafe unsafe = UnsafeUtils.findUnsafe();
        long address = unsafe.allocateMemory(data.length);
        unsafe.copyMemory(data, Unsafe.ARRAY_BYTE_BASE_OFFSET, null, address, data.length);
        return Input.fromNative(address, data.length);
    }

    private final Function<byte[], Input> function;

    public InputTestCase(Function<byte[], Input> function, String name) {
        this.function = function;
    }

    @Test
    public void test_read_bytearray() {
        byte[] data = new byte[]{(byte) 0xff};
        Input input = function.apply(data);
        byte[] actual = new byte[1];
        assertEquals(1, input.read(actual, 0, 1));
        assertArrayEquals(data, actual);
    }

    @Test(expected = NoSuchByteException.class)
    public void test_read_bytearray_offset_larger_than_readable_data() {
        byte[] data = new byte[0];
        Input input = function.apply(data);
        input.read(new byte[0], 1, 0);
    }

    @Test(expected = NoSuchByteException.class)
    public void test_read_bytearray_length_larger_than_writeableable_data() {
        byte[] data = new byte[1];
        Input input = function.apply(data);
        input.read(new byte[0], 0, 1);
    }

    @Test(expected = NoSuchByteException.class)
    public void test_read_bytearray_offset_less_than_zero() {
        byte[] data = new byte[0];
        Input input = function.apply(data);
        input.read(new byte[0], -1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_read_bytearray_outside_legal_bytearray_range() {
        byte[] data = new byte[0];
        Input input = function.apply(data);
        input.read(new byte[0], Integer.MAX_VALUE + 1L, 0);
    }

    @Test
    public void test_read() {
        byte[] data = new byte[]{(byte) 0xff};
        Input input = function.apply(data);
        assertEquals(data[0], input.read(0));
    }

    @Test(expected = NoSuchByteException.class)
    public void test_read_offset_larger_than_bytearray() {
        byte[] data = new byte[0];
        Input input = function.apply(data);
        input.read(1);
    }

    @Test(expected = NoSuchByteException.class)
    public void test_read_offset_less_than_zero() {
        byte[] data = new byte[0];
        Input input = function.apply(data);
        input.read(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_read_outside_legal_bytearray_range() {
        byte[] data = new byte[0];
        Input input = function.apply(data);
        input.read(Integer.MAX_VALUE + 1L);
    }

    @Test
    public void test_offset_valid() {
        byte[] data = new byte[]{(byte) 0};
        Input input = function.apply(data);
        assertTrue(input.offsetValid(0));
    }

    @Test
    public void test_offset_invalid() {
        byte[] data = new byte[]{(byte) 0};
        Input input = function.apply(data);
        assertFalse(input.offsetValid(1));
    }

}
