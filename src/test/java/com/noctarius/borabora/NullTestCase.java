package com.noctarius.borabora;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(Parameterized.class)
public class NullTestCase
        extends AbstractTestCase {

    @Parameterized.Parameters(name = "test_null_value_{0}")
    public static Collection<Object[]> suppliers() {
        return Arrays.asList(new Object[][]{{"tag", function(Value::tag)}, //
                                            {"number", function(Value::number)}, //
                                            {"sequence", function(Value::sequence)}, //
                                            {"dictionary", function(Value::dictionary)}, //
                                            {"string", function(Value::string)}, //
                                            {"bool", function(Value::bool)}});
    }

    private final Function<Value, Object> function;

    public NullTestCase(String name, Function<Value, Object> function) {
        this.function = function;
    }

    @Test
    public void test_null()
            throws Exception {

        Parser parser = buildParser("0xf6");

        Value value = parser.read(GraphQuery.newBuilder().build());

        assertEquals(ValueTypes.Null, value.valueType());
        assertNull(function.apply(value));
    }

    private static Function<Value, Object> function(Function<Value, Object> function) {
        return function;
    }

}
