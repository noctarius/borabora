package com.noctarius.borabora;

import org.junit.Test;

import java.net.URI;
import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class SemanticTagTestCase
        extends AbstractTestCase {

    @Test
    public void test_semantic_tag_uri()
            throws Exception {

        Parser parser = buildParser("0xd82076687474703a2f2f7777772e6578616d706c652e636f6d");
        Value value = parser.read(GraphQuery.newBuilder().build());

        assertEquals(ValueTypes.URI, value.valueType());
        assertEquals(new URI("http://www.example.com"), value.tag());
    }

    @Test
    public void test_semantic_tag_datatime()
            throws Exception {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2013);
        calendar.set(Calendar.MONTH, 2);
        calendar.set(Calendar.DAY_OF_MONTH, 21);
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 04);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        Parser parser = buildParser("0xc074323031332d30332d32315432303a30343a30305a");
        Value value = parser.read(GraphQuery.newBuilder().build());

        assertEquals(ValueTypes.DateTime, value.valueType());
        assertEquals(calendar.getTime(), value.tag());
    }

    @Test
    public void test_semantic_tag_timestamp_uint()
            throws Exception {

        Parser parser = buildParser("0xc11a514b67b0");
        Value value = parser.read(GraphQuery.newBuilder().build());

        assertEquals(ValueTypes.Timestamp, value.valueType());
        assertEquals((Long) 1363896240L, value.tag());
    }

    @Test
    public void test_semantic_tag_timestamp_float()
            throws Exception {

        Parser parser = buildParser("0xc1fb41d452d9ec200000");
        Value value = parser.read(GraphQuery.newBuilder().build());

        assertEquals(ValueTypes.Timestamp, value.valueType());
        assertEquals((Double) 1363896240.5D, value.tag());
    }

}
