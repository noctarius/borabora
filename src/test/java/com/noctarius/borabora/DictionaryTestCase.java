package com.noctarius.borabora;

import org.junit.Test;

import java.math.BigInteger;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DictionaryTestCase
        extends AbstractTestCase {

    @Test
    public void test_empty_dictionary() throws Exception {

        Parser parser = buildParser("0xa0");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Dictionary dictionary = value.dictionary();
        assertEquals(0, dictionary.size());
    }

    @Test
    public void test_multi_element_dictionary()
            throws Exception {

        Parser parser = buildParser("0xa56161614161626142616361436164614461656145");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Dictionary dictionary = value.dictionary();
        assertEquals("A", dictionary.get((v) -> "a".equals(v.string())).string());
        assertEquals("B", dictionary.get((v) -> "b".equals(v.string())).string());
        assertEquals("C", dictionary.get((v) -> "c".equals(v.string())).string());
        assertEquals("D", dictionary.get((v) -> "d".equals(v.string())).string());
        assertEquals("E", dictionary.get((v) -> "e".equals(v.string())).string());
    }

    @Test
    public void test_double_element_dictionary()
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

        dictionary_uint_sequence("0xbf61610161629f0203ffff");
    }

    @Test
    public void test_dictionary_uint_sequence()
            throws Exception {

        dictionary_uint_sequence("0xa26161016162820203");
    }

    private void dictionary_uint_sequence(String hex) {
        Parser parser = buildParser(hex);
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
