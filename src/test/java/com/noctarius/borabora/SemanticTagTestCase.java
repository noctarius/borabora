package com.noctarius.borabora;

import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class SemanticTagTestCase
        extends AbstractTestCase {

    @Test
    public void test_parse_uri()
            throws Exception {

        Parser parser = buildParser("0xd82076687474703a2f2f7777772e6578616d706c652e636f6d");
        Value value = parser.read(new SequenceGraphQuery(0));

        assertEquals(ValueTypes.URI, value.valueType());
        assertEquals(new URI("http://www.example.com"), value.tag());
    }

}
