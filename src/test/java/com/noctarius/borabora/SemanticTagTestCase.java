package com.noctarius.borabora;

import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.net.URI;

import static org.junit.Assert.assertEquals;

public class SemanticTagTestCase {

    @Test
    public void test_parse_uri()
            throws Exception {

        byte[] data = DatatypeConverter.parseHexBinary("d82076687474703a2f2f7777772e6578616d706c652e636f6d");
        Input input = Input.fromByteArray(data);
        Parser parser = Parser.newBuilder(input).build();
        Value value = parser.read(new SequenceGraphQuery(0));

        assertEquals(ValueTypes.URI, value.valueType());
        assertEquals(new URI("http://www.example.com"), value.tag());
    }

}
