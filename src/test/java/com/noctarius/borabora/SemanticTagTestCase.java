/*
 * Copyright (c) 2016, Christoph Engelbert (aka noctarius) and
 * contributors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.noctarius.borabora;

import org.junit.Test;

import java.net.URI;
import java.util.Calendar;
import java.util.TimeZone;

import static com.noctarius.borabora.spi.Constants.ASCII;
import static org.junit.Assert.assertEquals;

public class SemanticTagTestCase
        extends AbstractTestCase {

    @Test
    public void test_semantic_tag_uri()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xd82076687474703a2f2f7777772e6578616d706c652e636f6d");
        Value value = parser.read(Query.newBuilder().build());

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

        SimplifiedTestParser parser = buildParser("0xc074323031332d30332d32315432303a30343a30305a");
        Value value = parser.read(Query.newBuilder().build());

        assertEquals(ValueTypes.DateTime, value.valueType());
        assertEquals(calendar.getTime(), value.tag());
    }

    @Test
    public void test_semantic_tag_timestamp_uint()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xc11a514b67b0");
        Value value = parser.read(Query.newBuilder().build());

        assertEquals(ValueTypes.Timestamp, value.valueType());
        assertEquals((Long) 1363896240L, value.tag());
    }

    @Test
    public void test_semantic_tag_timestamp_float()
            throws Exception {

        SimplifiedTestParser parser = buildParser("0xc1fb41d452d9ec200000");
        Value value = parser.read(Query.newBuilder().build());

        assertEquals(ValueTypes.Timestamp, value.valueType());
        assertEquals((Double) 1363896240.5D, value.tag());
    }

    @Test
    public void test_semantic_tag_enc_cbor()
            throws Exception {

        String expected = new String(hexToBytes("0x6449455446"), ASCII);
        SimplifiedTestParser parser = buildParser("0xd818456449455446");
        Value value = parser.read(Query.newBuilder().build());
        Value enc = value.tag();
        String actual = enc.string();
        assertEquals(expected, actual);
    }

}
