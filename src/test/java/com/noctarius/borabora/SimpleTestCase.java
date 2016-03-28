package com.noctarius.borabora;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class SimpleTestCase
        extends AbstractTestCase {

    @Test
    public void test_raw_extraction()
            throws Exception {

        Parser parser = buildParser("0xf0");
        Value value = parser.read(GraphQuery.newBuilder().build());

        assertArrayEquals(new byte[]{(byte) 0xf0}, value.raw());
    }

}
