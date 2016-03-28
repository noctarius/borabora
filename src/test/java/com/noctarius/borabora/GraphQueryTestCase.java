package com.noctarius.borabora;

import org.junit.Test;

import static com.noctarius.borabora.Value.NULL_VALUE;
import static org.junit.Assert.assertSame;

public class GraphQueryTestCase
        extends AbstractTestCase {

    @Test(expected = WrongTypeException.class)
    public void test_not_a_dictionary()
            throws Exception {

        Parser parser = buildParser("0x83010203");
        parser.read(GraphQuery.newBuilder().dictionary((v) -> "b".equals(v.string())).build());
    }

    @Test
    public void test_sequence_index_not_found()
            throws Exception {

        Parser parser = buildParser("0x83010203");
        Value value = parser.read(GraphQuery.newBuilder().sequence(4).build());
        assertSame(NULL_VALUE, value);
    }

    @Test
    public void test_dictionary_property_not_found()
            throws Exception {

        Parser parser = buildParser("0xbf61610161629f0203ffff");
        Value value = parser.read(GraphQuery.newBuilder().dictionary((v) -> "c".equals(v.string())).sequence(0).build());
        assertSame(NULL_VALUE, value);
    }

    @Test(expected = WrongTypeException.class)
    public void test_not_a_sequence()
            throws Exception {

        Parser parser = buildParser("0xbf61610161629f0203ffff");
        parser.read(GraphQuery.newBuilder().sequence(0).build());
    }

}
