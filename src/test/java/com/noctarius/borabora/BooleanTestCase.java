package com.noctarius.borabora;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BooleanTestCase
        extends AbstractTestCase {

    @Test
    public void test_boolean_false()
            throws Exception {

        Parser parser = buildParser("0xf4");

        Value value = parser.read(GraphQuery.newBuilder().build());

        assertEquals(ValueTypes.Bool, value.valueType());
        assertEquals(Boolean.FALSE, value.bool());
    }

    @Test
    public void test_boolean_true()
            throws Exception {

        Parser parser = buildParser("0xf5");

        Value value = parser.read(GraphQuery.newBuilder().build());

        assertEquals(ValueTypes.Bool, value.valueType());
        assertEquals(Boolean.TRUE, value.bool());
    }

}
