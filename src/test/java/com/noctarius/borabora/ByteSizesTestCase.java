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

import com.noctarius.borabora.spi.ByteSizes;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ByteSizesTestCase {

    @Test
    public void test_stringsize_in_addinfo() {
        Input input = Input.fromByteArray(new byte[]{23});
        assertEquals(24, ByteSizes.stringByteSize(input, 0));
    }

    @Test
    public void test_stringsize_one_byte() {
        Input input = Input.fromByteArray(new byte[]{24, (byte) 0xff});
        assertEquals(257, ByteSizes.stringByteSize(input, 0));
    }

    @Test
    public void test_stringsize_two_byte() {
        Input input = Input.fromByteArray(new byte[]{25, (byte) 0xff, (byte) 0xff});
        assertEquals(65538, ByteSizes.stringByteSize(input, 0));
    }

    @Test
    public void test_stringsize_four_byte() {
        Input input = Input.fromByteArray(new byte[]{26, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff});
        assertEquals(4294967300L, ByteSizes.stringByteSize(input, 0));
    }

    @Test(expected = IllegalStateException.class)
    public void test_stringsize_64bit() {
        Input input = Input.fromByteArray(new byte[]{27});
        ByteSizes.stringByteSize(input, 0);
    }

    @Test
    public void test_stringsize_indefinite() {
        Input input = Input.fromByteArray(new byte[]{31, 0, 0, 0, 0, 0, (byte) 0xff});
        ByteSizes.stringByteSize(input, 0);
        assertEquals(7, ByteSizes.stringByteSize(input, 0));
    }

    @Test(expected = IllegalStateException.class)
    public void test_stringsize_reserved1() {
        Input input = Input.fromByteArray(new byte[]{28});
        ByteSizes.stringByteSize(input, 0);
    }

    @Test(expected = IllegalStateException.class)
    public void test_stringsize_reserved2() {
        Input input = Input.fromByteArray(new byte[]{29});
        ByteSizes.stringByteSize(input, 0);
    }

    @Test(expected = IllegalStateException.class)
    public void test_stringsize_reserved3() {
        Input input = Input.fromByteArray(new byte[]{30});
        ByteSizes.stringByteSize(input, 0);
    }

    @Test
    public void test_datasize_in_addinfo() {
        Input input = Input.fromByteArray(new byte[]{23});
        assertEquals(23, ByteSizes.stringDataSize(input, 0));
    }

    @Test
    public void test_datasize_one_byte() {
        Input input = Input.fromByteArray(new byte[]{24, (byte) 0xff});
        assertEquals(255, ByteSizes.stringDataSize(input, 0));
    }

    @Test
    public void test_datasize_two_byte() {
        Input input = Input.fromByteArray(new byte[]{25, (byte) 0xff, (byte) 0xff});
        assertEquals(65535, ByteSizes.stringDataSize(input, 0));
    }

    @Test
    public void test_datasize_four_byte() {
        Input input = Input.fromByteArray(new byte[]{26, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff});
        assertEquals(4294967295L, ByteSizes.stringDataSize(input, 0));
    }

    @Test(expected = IllegalStateException.class)
    public void test_datasize_64bit() {
        Input input = Input.fromByteArray(new byte[]{27});
        ByteSizes.stringDataSize(input, 0);
    }

    @Test(expected = IllegalStateException.class)
    public void test_datasize_reserved1() {
        Input input = Input.fromByteArray(new byte[]{28});
        ByteSizes.stringDataSize(input, 0);
    }

    @Test(expected = IllegalStateException.class)
    public void test_datasize_reserved2() {
        Input input = Input.fromByteArray(new byte[]{29});
        ByteSizes.stringDataSize(input, 0);
    }

    @Test(expected = IllegalStateException.class)
    public void test_datasize_reserved3() {
        Input input = Input.fromByteArray(new byte[]{30});
        ByteSizes.stringDataSize(input, 0);
    }

    @Test
    public void test_headsize_in_addinfo() {
        Input input = Input.fromByteArray(new byte[]{23});
        assertEquals(1, ByteSizes.headByteSize(input, 0));
    }

    @Test
    public void test_headsize_one_byte() {
        Input input = Input.fromByteArray(new byte[]{24, (byte) 0xff});
        assertEquals(2, ByteSizes.headByteSize(input, 0));
    }

    @Test
    public void test_headsize_two_byte() {
        Input input = Input.fromByteArray(new byte[]{25, (byte) 0xff, (byte) 0xff});
        assertEquals(3, ByteSizes.headByteSize(input, 0));
    }

    @Test
    public void test_headsize_four_byte() {
        Input input = Input.fromByteArray(new byte[]{26, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff});
        assertEquals(5, ByteSizes.headByteSize(input, 0));
    }

    @Test
    public void test_headsize_64bit() {
        Input input = Input.fromByteArray(new byte[]{27});
        ByteSizes.headByteSize(input, 0);
        assertEquals(9, ByteSizes.headByteSize(input, 0));
    }

    @Test(expected = IllegalStateException.class)
    public void test_headsize_reserved1() {
        Input input = Input.fromByteArray(new byte[]{28});
        ByteSizes.headByteSize(input, 0);
    }

    @Test(expected = IllegalStateException.class)
    public void test_headsize_reserved2() {
        Input input = Input.fromByteArray(new byte[]{29});
        ByteSizes.headByteSize(input, 0);
    }

    @Test(expected = IllegalStateException.class)
    public void test_headsize_reserved3() {
        Input input = Input.fromByteArray(new byte[]{30});
        ByteSizes.headByteSize(input, 0);
    }

    @Test
    public void test_floatingpoint_or_simplebytesize_in_addinfo() {
        Input input = Input.fromByteArray(new byte[]{23});
        assertEquals(1, ByteSizes.floatOrSimpleByteSize(input, 0));
    }

    @Test
    public void test_floatingpoint_or_simplebytesize_one_byte() {
        Input input = Input.fromByteArray(new byte[]{24, (byte) 0xff});
        assertEquals(2, ByteSizes.floatOrSimpleByteSize(input, 0));
    }

    @Test
    public void test_floatingpoint_or_simplebytesize_two_byte() {
        Input input = Input.fromByteArray(new byte[]{25, (byte) 0xff, (byte) 0xff});
        assertEquals(3, ByteSizes.floatOrSimpleByteSize(input, 0));
    }

    @Test
    public void test_floatingpoint_or_simplebytesize_four_byte() {
        Input input = Input.fromByteArray(new byte[]{26, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff});
        assertEquals(5, ByteSizes.floatOrSimpleByteSize(input, 0));
    }

    @Test
    public void test_floatingpoint_or_simplebytesize_64bit() {
        Input input = Input.fromByteArray(new byte[]{27});
        ByteSizes.floatOrSimpleByteSize(input, 0);
        assertEquals(9, ByteSizes.floatOrSimpleByteSize(input, 0));
    }

    @Test
    public void test_floatingpoint_or_simplebytesize_indefinite() {
        Input input = Input.fromByteArray(new byte[]{31, 0, 0, 0, 0, 0, (byte) 0xff});
        ByteSizes.floatOrSimpleByteSize(input, 0);
        assertEquals(7, ByteSizes.floatOrSimpleByteSize(input, 0));
    }

    @Test(expected = IllegalStateException.class)
    public void test_floatingpoint_or_simplebytesize_reserved1() {
        Input input = Input.fromByteArray(new byte[]{28});
        ByteSizes.floatOrSimpleByteSize(input, 0);
    }

    @Test(expected = IllegalStateException.class)
    public void test_floatingpoint_or_simplebytesize_reserved2() {
        Input input = Input.fromByteArray(new byte[]{29});
        ByteSizes.floatOrSimpleByteSize(input, 0);
    }

    @Test(expected = IllegalStateException.class)
    public void test_floatingpoint_or_simplebytesize_reserved3() {
        Input input = Input.fromByteArray(new byte[]{30});
        ByteSizes.floatOrSimpleByteSize(input, 0);
    }

}
