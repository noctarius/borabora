package com.noctarius.borabora;

import org.junit.Test;

public class WrongTypeTestCase
        extends AbstractTestCase {

    @Test(expected = WrongTypeException.class)
    public void test_wrong_major_type_on_semantic_tag()
            throws Exception {

        Parser parser = buildParser("0xf7");
        Value value = parser.read(GraphQuery.newBuilder().build());
        value.tag();
    }

    @Test(expected = WrongTypeException.class)
    public void test_wrong_value_type_on_number()
            throws Exception {

        Parser parser = buildParser("0xf7");
        Value value = parser.read(GraphQuery.newBuilder().build());
        value.number();
    }

    @Test(expected = WrongTypeException.class)
    public void test_wrong_value_type_on_sequence()
            throws Exception {

        Parser parser = buildParser("0xf7");
        Value value = parser.read(GraphQuery.newBuilder().build());
        value.sequence();
    }

    @Test(expected = WrongTypeException.class)
    public void test_wrong_value_type_on_dictionary()
            throws Exception {

        Parser parser = buildParser("0xf7");
        Value value = parser.read(GraphQuery.newBuilder().build());
        value.dictionary();
    }

    @Test(expected = WrongTypeException.class)
    public void test_wrong_value_type_on_string()
            throws Exception {

        Parser parser = buildParser("0xf7");
        Value value = parser.read(GraphQuery.newBuilder().build());
        value.string();
    }

    @Test(expected = WrongTypeException.class)
    public void test_wrong_value_type_on_bool()
            throws Exception {

        Parser parser = buildParser("0x40");
        Value value = parser.read(GraphQuery.newBuilder().build());
        value.bool();
    }

}
