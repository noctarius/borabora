package com.noctarius.borabora;

import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class DictionaryTestCase
        extends AbstractTestCase {

    @Test
    public void test_simple_map()
            throws Exception {

        Parser parser = buildParser("0xa201020304");
        Value value = parser.read(GraphQuery.newBuilder().build());

        assertEquals(MajorType.Dictionary, value.majorType());
        assertEquals(ValueTypes.Dictionary, value.valueType());

        //value.dictionary();
    }

    @Test
    public void test_simple_map_graph_access()
            throws Exception {

        Parser parser = buildParser("0xa201020304");
        GraphQuery query = GraphQuery.newBuilder().dictionary(this::matchNumber).build();
        Value value = parser.read(query);

        assertEquals(MajorType.UnsignedInteger, value.majorType());
        assertEquals(ValueTypes.Uint, value.valueType());

        assertEqualsNumber(4, value.number());
    }

    @Test
    public void test_mixed_dictionary()
            throws Exception {

        Parser parser = buildParser("0xbf6346756ef563416d7421ff");
        Value value = parser.read(GraphQuery.newBuilder().build());

        assertEquals(ValueTypes.Dictionary, value.valueType());

        Dictionary dictionary = value.dictionary();
    }

    private boolean matchNumber(Value value) {
        if (ValueTypes.Uint != value.valueType()) {
            return false;
        }
        Number number = value.number();
        if (number instanceof BigInteger) {
            return number.equals(BigInteger.valueOf(3));
        }
        return number.longValue() == 3;
    }

}
