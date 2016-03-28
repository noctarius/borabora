package com.noctarius.borabora;

import org.junit.Test;

import java.math.BigInteger;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DictionaryTestCase
        extends AbstractTestCase {

    @Test
    public void test_simple_map()
            throws Exception {

        Parser parser = buildParser("0xa201020304");
        Value value = parser.read(GraphQuery.newBuilder().build());

        assertEquals(MajorType.Dictionary, value.majorType());
        assertEquals(ValueTypes.Dictionary, value.valueType());

        Dictionary dictionary = value.dictionary();
        assertEquals(2, dictionary.size());
        assertEqualsNumber(2, dictionary.get(matchNumber(1)).number());
        assertEqualsNumber(4, dictionary.get(matchNumber(3)).number());
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
    public void test_indefinite_dictionary_bool_number()
            throws Exception {

        Parser parser = buildParser("0xbf6346756ef563416d7421ff");
        Value value = parser.read(GraphQuery.newBuilder().build());

        assertEquals(ValueTypes.Dictionary, value.valueType());

        Dictionary dictionary = value.dictionary();

        assertEquals(2, dictionary.size());

        assertTrue(dictionary.get((v) -> "Fun".equals(v.string())).bool());
        assertEqualsNumber(-2, dictionary.get((v) -> "Amt".equals(v.string())).number());
    }

    @Test
    public void test_indefinite_dictionary_uint_indefinite_sequence()
            throws Exception {

        Parser parser = buildParser("0xbf61610161629f0203ffff");
        Value value = parser.read(GraphQuery.newBuilder().build());

        assertEquals(ValueTypes.Dictionary, value.valueType());

        Dictionary dictionary = value.dictionary();

        assertEquals(2, dictionary.size());
        assertEqualsNumber(1, dictionary.get((v) -> "a".equals(v.string())).number());

        Sequence sequence = dictionary.get((v) -> "b".equals(v.string())).sequence();
        assertEquals(2, sequence.size());
        assertEqualsNumber(2, sequence.get(0).number());
        assertEqualsNumber(3, sequence.get(1).number());
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

    private Predicate<Value> matchNumber(long v) {
        return (value) -> {
            if (ValueTypes.Uint != value.valueType()) {
                return false;
            }
            Number number = value.number();
            if (number instanceof BigInteger) {
                return number.equals(BigInteger.valueOf(v));
            }
            return number.longValue() == v;
        };
    }

}
