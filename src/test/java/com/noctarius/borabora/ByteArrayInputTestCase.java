package com.noctarius.borabora;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ByteArrayInputTestCase
        extends AbstractTestCase {

    @Test
    public void test_byte_extraction()
            throws Exception {

        byte[] data = new byte[]{(byte) 0xff};
        Input input = Input.fromByteArray(data);
        assertEquals(data[0], input.read(0));
    }

    @Test(expected = NoSuchByteException.class)
    public void test_byte_larger_than_bytearray()
            throws Exception {

        byte[] data = new byte[0];
        Input input = Input.fromByteArray(data);
        input.read(1);
    }

    @Test(expected = NoSuchByteException.class)
    public void test_byte_less_than_zero()
            throws Exception {

        byte[] data = new byte[0];
        Input input = Input.fromByteArray(data);
        input.read(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_byte_outside_legal_bytearray_range()
            throws Exception {

        byte[] data = new byte[0];
        Input input = Input.fromByteArray(data);
        input.read(Integer.MAX_VALUE + 1L);
    }

}
