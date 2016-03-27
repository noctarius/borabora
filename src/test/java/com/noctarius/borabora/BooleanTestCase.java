package com.noctarius.borabora;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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

    @Test
    public void test_boolean_null()
            throws Exception {

        Parser parser = buildParser("0xf6");

        Value value = parser.read(GraphQuery.newBuilder().build());

        assertEquals(ValueTypes.Null, value.valueType());
        assertNull(value.bool());
    }

}
