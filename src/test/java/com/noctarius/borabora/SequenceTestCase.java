package com.noctarius.borabora;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SequenceTestCase
        extends AbstractTestCase {

    @Test
    public void test_indefinite_sequence()
            throws Exception {

        Parser parser = buildParser("0x9f0102030405060708090a0b0c0d0e0f101112131415161718181819ff");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Sequence sequence = value.sequence();
        for (int i = 1; i < 26; i++) {
            Value element = sequence.get(i - 1);
            assertEquals(ValueTypes.Uint, element.valueType());
            assertEqualsNumber(i, element.number());

            GraphQuery query = GraphQuery.newBuilder().sequence(i - 1).build();
            element = parser.read(query);
            assertEquals(ValueTypes.Uint, element.valueType());
            assertEqualsNumber(i, element.number());
        }
    }

    @Test
    public void test_sequence_dictionary()
            throws Exception {

        Parser parser = buildParser("0x826161a161626163");
        Value value = parser.read(GraphQuery.newBuilder().build());

        Sequence sequence = value.sequence();
        assertEquals(2, sequence.size());

        Value element1 = sequence.get(0);
        assertEquals("a", element1.string());

        Value element2 = sequence.get(1);
        Dictionary dictionary = element2.dictionary();
        assertEquals("c", dictionary.get((v) -> "b".equals(v.string())).string());
    }

    @Test
    public void test_indefinite_empty_sequence()
            throws Exception {

        Parser parser = buildParser("0x9fff");
        Value value = parser.read(GraphQuery.newBuilder().build());

        assertEquals(0, value.sequence().size());
    }

    @Test
    public void test_indefinite_sequence_nested_sequence_sequence()
            throws Exception {

        test_sequence("0x9f01820203820405ff");
    }

    @Test
    public void test_indefinite_sequence_nested_sequence_indefinite_sequence()
            throws Exception {

        test_sequence("0x9f018202039f0405ffff");
    }

    @Test
    public void test_sequence_nested_sequence_sequence()
            throws Exception {

        test_sequence("0x8301820203820405");
    }

    @Test
    public void test_sequence_nested_sequence_indefinite_sequence()
            throws Exception {

        test_sequence("0x83018202039f0405ff");
    }

    @Test
    public void test_sequence_nested_indefinite_sequence_sequence()
            throws Exception {

        test_sequence("0x83019f0203ff820405");
    }

    private void test_sequence(String hex)
            throws Exception {

        Parser parser = buildParser(hex);
        test_using_sequence_graph(parser);
        test_using_sequence_traversal(parser);
    }

    private void test_using_sequence_graph(Parser parser) {
        GraphQuery i0e0 = GraphQuery.newBuilder().sequence(0).build();
        GraphQuery i1e0 = GraphQuery.newBuilder().sequence(1).sequence(0).build();
        GraphQuery i1e1 = GraphQuery.newBuilder().sequence(1).sequence(1).build();
        GraphQuery i2e0 = GraphQuery.newBuilder().sequence(2).sequence(0).build();
        GraphQuery i2e1 = GraphQuery.newBuilder().sequence(2).sequence(1).build();

        Value v1 = parser.read(i0e0);
        Value v2 = parser.read(i1e0);
        Value v3 = parser.read(i1e1);
        Value v4 = parser.read(i2e0);
        Value v5 = parser.read(i2e1);

        assertEqualsNumber(1, v1.number());
        assertEqualsNumber(2, v2.number());
        assertEqualsNumber(3, v3.number());
        assertEqualsNumber(4, v4.number());
        assertEqualsNumber(5, v5.number());
    }

    private void test_using_sequence_traversal(Parser parser) {
        Value value = parser.read(GraphQuery.newBuilder().sequence(-1).build());

        Sequence sequence = value.sequence();

        Value valueIndex0 = sequence.get(0);
        Value valueIndex1 = sequence.get(1);
        Value valueIndex2 = sequence.get(2);

        assertEquals(ValueTypes.Uint, valueIndex0.valueType());
        assertEquals(ValueTypes.Sequence, valueIndex1.valueType());
        assertEquals(ValueTypes.Sequence, valueIndex2.valueType());

        assertEqualsNumber(1, valueIndex0.number());

        assertEqualsNumber(2, valueIndex1.sequence().get(0).number());
        assertEqualsNumber(3, valueIndex1.sequence().get(1).number());

        assertEqualsNumber(4, valueIndex2.sequence().get(0).number());
        assertEqualsNumber(5, valueIndex2.sequence().get(1).number());
    }

}
