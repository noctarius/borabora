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
package com.noctarius.borabora.impl.codec;

import com.noctarius.borabora.AbstractTestCase;
import com.noctarius.borabora.Input;
import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.Output;
import com.noctarius.borabora.spi.io.ByteSizes;
import com.noctarius.borabora.spi.io.Encoder;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertEquals;

public class ByteSizesTestCase
        extends AbstractTestCase {

    @Test
    public void call_constructor() {
        callConstructor(ByteSizes.class);
    }

    @Test
    public void test_bytesizebymajortype_unsignedinteger() {
        Input input = input(MajorType.UnsignedInteger);
        long size = ByteSizes.byteSizeByMajorType(MajorType.UnsignedInteger, input, 0);
        assertEquals(1, size);
    }

    @Test
    public void test_bytesizebymajortype_negativeinteger() {
        Input input = input(MajorType.NegativeInteger);
        long size = ByteSizes.byteSizeByMajorType(MajorType.NegativeInteger, input, 0);
        assertEquals(1, size);
    }

    @Test
    public void test_bytesizebymajortype_bytestring() {
        Input input = input(MajorType.ByteString);
        long size = ByteSizes.byteSizeByMajorType(MajorType.ByteString, input, 0);
        assertEquals(2, size);
    }

    @Test
    public void test_bytesizebymajortype_textstring() {
        Input input = input(MajorType.TextString);
        long size = ByteSizes.byteSizeByMajorType(MajorType.TextString, input, 0);
        assertEquals(2, size);
    }

    @Test
    public void test_bytesizebymajortype_sequence() {
        Input input = input(MajorType.Sequence);
        long size = ByteSizes.byteSizeByMajorType(MajorType.Sequence, input, 0);
        assertEquals(2, size);
    }

    @Test
    public void test_bytesizebymajortype_dictionary() {
        Input input = input(MajorType.Dictionary);
        long size = ByteSizes.byteSizeByMajorType(MajorType.Dictionary, input, 0);
        assertEquals(3, size);
    }

    @Test
    public void test_bytesizebymajortype_floatorsimple() {
        Input input = input(MajorType.FloatingPointOrSimple);
        long size = ByteSizes.byteSizeByMajorType(MajorType.FloatingPointOrSimple, input, 0);
        assertEquals(1, size);
    }

    @Test
    public void test_bytesizebymajortype_semantictag() {
        Input input = input(MajorType.SemanticTag);
        long size = ByteSizes.byteSizeByMajorType(MajorType.SemanticTag, input, 0);
        assertEquals(2, size);
    }

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

    private Input input(MajorType majorType) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toOutputStream(baos);
        long offset = Encoder.encodeLengthAndValue(majorType, 1, 0, output);
        if (majorType == MajorType.Sequence) {
            Encoder.encodeLengthAndValue(MajorType.UnsignedInteger, 1, offset, output);
        } else if (majorType == MajorType.Dictionary) {
            offset = Encoder.encodeLengthAndValue(MajorType.UnsignedInteger, 1, offset, output);
            Encoder.encodeLengthAndValue(MajorType.UnsignedInteger, 1, offset, output);
        } else if (majorType == MajorType.SemanticTag) {
            Encoder.encodeLengthAndValue(MajorType.UnsignedInteger, 1, offset, output);
        }
        return Input.fromByteArray(baos.toByteArray());
    }
}
