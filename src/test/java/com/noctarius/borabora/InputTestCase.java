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

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class InputTestCase
        extends AbstractTestCase {

    @Parameterized.Parameters(name = "{1}")
    public static Iterable<Object[]> parameters() {
        return Arrays.asList( //
                new Object[][]{ //
                                {input((d) -> Input.fromByteArray(d)), "ByteArrayInput"}, //
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
    public void test_byte_extraction()
            throws Exception {

        byte[] data = new byte[]{(byte) 0xff};
        Input input = function.apply(data);
        assertEquals(data[0], input.read(0));
    }

    @Test(expected = NoSuchByteException.class)
    public void test_byte_larger_than_bytearray()
            throws Exception {

        byte[] data = new byte[0];
        Input input = function.apply(data);
        input.read(1);
    }

    @Test(expected = NoSuchByteException.class)
    public void test_byte_less_than_zero()
            throws Exception {

        byte[] data = new byte[0];
        Input input = function.apply(data);
        input.read(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_byte_outside_legal_bytearray_range()
            throws Exception {

        byte[] data = new byte[0];
        Input input = function.apply(data);
        input.read(Integer.MAX_VALUE + 1L);
    }

}
