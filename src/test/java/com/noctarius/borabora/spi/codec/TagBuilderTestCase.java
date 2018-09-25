/*
 * Copyright (c) 2016-2018, Christoph Engelbert (aka noctarius) and
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
package com.noctarius.borabora.spi.codec;

import com.noctarius.borabora.AbstractTestCase;
import com.noctarius.borabora.Dictionary;
import com.noctarius.borabora.Input;
import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.Output;
import com.noctarius.borabora.Parser;
import com.noctarius.borabora.Sequence;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.ValueTypes;
import com.noctarius.borabora.Writer;
import com.noctarius.borabora.builder.encoder.GraphBuilder;
import com.noctarius.borabora.builder.encoder.semantictag.CBORBuilder;
import com.noctarius.borabora.builder.encoder.semantictag.DateTimeBuilder;
import com.noctarius.borabora.builder.encoder.semantictag.FractionBuilder;
import com.noctarius.borabora.builder.encoder.semantictag.NBigNumberBuilder;
import com.noctarius.borabora.builder.encoder.semantictag.TimestampBuilder;
import com.noctarius.borabora.builder.encoder.semantictag.UBigNumberBuilder;
import com.noctarius.borabora.builder.encoder.semantictag.URIBuilder;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

import static com.noctarius.borabora.Predicates.matchString;
import static com.noctarius.borabora.spi.builder.TagSupport.semanticTag;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TagBuilderTestCase
        extends AbstractTestCase {

    @Test
    public void call_constructor() {
        callConstructor(TagBuilders.class);
    }

    @Test
    public void test_write_semtag_datetime() {
        Instant expected1 = Instant.now();
        Date expected2 = Date.from(expected1);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer writer = Writer.newWriter();

        GraphBuilder graphBuilder = writer.newGraphBuilder(Output.toOutputStream(baos));

        graphBuilder.putTag( //
                semanticTag(DateTimeBuilder.class).putDateTime(expected1).endSemanticTag() //
        ).putTag( //
                semanticTag(DateTimeBuilder.class).putDateTime(expected2).endSemanticTag() //
        ).putTag( //
                semanticTag(DateTimeBuilder.class).putDateTime((Instant) null).endSemanticTag() //
        ).putTag( //
                semanticTag(DateTimeBuilder.class).putDateTime((Date) null).endSemanticTag() //
        ).finishStream();

        Parser parser = Parser.newParser();
        Input input = Input.fromByteArray(baos.toByteArray());

        Value value1 = parser.read(input, parser.newQueryBuilder().stream(0).build());
        Value value2 = parser.read(input, parser.newQueryBuilder().stream(1).build());
        Value value3 = parser.read(input, parser.newQueryBuilder().stream(2).build());
        Value value4 = parser.read(input, parser.newQueryBuilder().stream(3).build());
        assertEquals(expected1, value1.tag());
        assertEquals(expected1, value2.tag());
        assertNull(value3.tag());
        assertNull(value4.tag());
    }

    @Test
    public void test_write_semtag_timestamp() {
        Instant expected1 = Instant.now();
        long expected2 = expected1.getEpochSecond();
        Timestamp expected3 = Timestamp.from(expected1);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer writer = Writer.newWriter();

        GraphBuilder graphBuilder = writer.newGraphBuilder(Output.toOutputStream(baos));

        graphBuilder.putTag( //
                semanticTag(TimestampBuilder.class).putTimestamp(expected1).endSemanticTag() //
        ).putTag( //
                semanticTag(TimestampBuilder.class).putTimestamp(expected2).endSemanticTag() //
        ).putTag( //
                semanticTag(TimestampBuilder.class).putTimestamp(expected3).endSemanticTag() //
        ).putTag( //
                semanticTag(TimestampBuilder.class).putTimestamp((Instant) null).endSemanticTag() //
        ).putTag( //
                semanticTag(TimestampBuilder.class).putTimestamp((Timestamp) null).endSemanticTag() //
        ).finishStream();

        Parser parser = Parser.newParser();
        Input input = Input.fromByteArray(baos.toByteArray());

        Value value1 = parser.read(input, parser.newQueryBuilder().stream(0).build());
        Value value2 = parser.read(input, parser.newQueryBuilder().stream(1).build());
        Value value3 = parser.read(input, parser.newQueryBuilder().stream(2).build());
        Value value4 = parser.read(input, parser.newQueryBuilder().stream(3).build());
        Value value5 = parser.read(input, parser.newQueryBuilder().stream(4).build());

        assertEquals(expected2, ((Long) value1.tag()).longValue());
        assertEquals(expected2, ((Long) value2.tag()).longValue());
        assertEquals(expected2, ((Long) value3.tag()).longValue());
        assertNull(value4.tag());
        assertNull(value5.tag());
    }

    @Test
    public void test_write_semtag_ubignum() {
        BigInteger expected = BigInteger.TEN;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer writer = Writer.newWriter();

        GraphBuilder graphBuilder = writer.newGraphBuilder(Output.toOutputStream(baos));

        graphBuilder.putTag( //
                semanticTag(UBigNumberBuilder.class).putBigInteger(expected).endSemanticTag() //
        ).putTag( //
                semanticTag(UBigNumberBuilder.class).putBigInteger(null).endSemanticTag() //
        ).finishStream();

        Parser parser = Parser.newParser();
        Input input = Input.fromByteArray(baos.toByteArray());

        Value value1 = parser.read(input, parser.newQueryBuilder().stream(0).build());
        Value value2 = parser.read(input, parser.newQueryBuilder().stream(1).build());
        assertEquals(expected, value1.tag());
        assertNull(value2.tag());
    }

    @Test
    public void test_write_semtag_nbignum() {
        BigInteger expected = BigInteger.valueOf(-10);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer writer = Writer.newWriter();

        GraphBuilder graphBuilder = writer.newGraphBuilder(Output.toOutputStream(baos));

        graphBuilder.putTag( //
                semanticTag(NBigNumberBuilder.class).putBigInteger(expected).endSemanticTag() //
        ).putTag( //
                semanticTag(NBigNumberBuilder.class).putBigInteger(null).endSemanticTag() //
        ).finishStream();

        Parser parser = Parser.newParser();
        Input input = Input.fromByteArray(baos.toByteArray());

        Value value1 = parser.read(input, parser.newQueryBuilder().stream(0).build());
        Value value2 = parser.read(input, parser.newQueryBuilder().stream(1).build());
        assertEquals(expected, value1.tag());
        assertNull(value2.tag());
    }

    @Test
    public void test_write_semtag_fraction() {
        BigDecimal expected = BigDecimal.valueOf(12.d);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer writer = Writer.newWriter();

        GraphBuilder graphBuilder = writer.newGraphBuilder(Output.toOutputStream(baos));

        graphBuilder.putTag( //
                semanticTag(FractionBuilder.class).putFraction(expected).endSemanticTag() //
        ).putTag( //
                semanticTag(FractionBuilder.class).putFraction(null).endSemanticTag() //
        ).finishStream();

        Parser parser = Parser.newParser();
        Input input = Input.fromByteArray(baos.toByteArray());

        Value value1 = parser.read(input, parser.newQueryBuilder().stream(0).build());
        Value value2 = parser.read(input, parser.newQueryBuilder().stream(1).build());
        assertEquals(expected, value1.tag());
        assertNull(value2.tag());
    }

    @Test
    public void test_write_semtag_uri()
            throws Exception {

        URL expected1 = new URL("http://www.noctarius.com");
        URI expected2 = expected1.toURI();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer writer = Writer.newWriter();

        GraphBuilder graphBuilder = writer.newGraphBuilder(Output.toOutputStream(baos));

        graphBuilder.putTag( //
                semanticTag(URIBuilder.class).putURL(expected1).endSemanticTag() //
        ).putTag( //
                semanticTag(URIBuilder.class).putURI(expected2).endSemanticTag() //
        ).putTag( //
                semanticTag(URIBuilder.class).putURL(null).endSemanticTag() //
        ).putTag( //
                semanticTag(URIBuilder.class).putURI(null).endSemanticTag() //
        ).finishStream();

        Parser parser = Parser.newParser();
        Input input = Input.fromByteArray(baos.toByteArray());

        Value value1 = parser.read(input, parser.newQueryBuilder().stream(0).build());
        Value value2 = parser.read(input, parser.newQueryBuilder().stream(1).build());
        Value value3 = parser.read(input, parser.newQueryBuilder().stream(2).build());
        Value value4 = parser.read(input, parser.newQueryBuilder().stream(3).build());
        assertEquals(expected2, value1.tag());
        assertEquals(expected2, value2.tag());
        assertNull(value3.tag());
        assertNull(value4.tag());
    }

    @Test
    public void test_write_semtag_cbor()
            throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer writer = Writer.newWriter();

        GraphBuilder graphBuilder = writer.newGraphBuilder(Output.toOutputStream(baos));

        graphBuilder.putTag( //
                semanticTag(CBORBuilder.class).putSequence(2) //
                                              .putString("foo").putString("bar") //
                                              .endSequence().endCBOR().endSemanticTag() //
        ).finishStream();

        Parser parser = Parser.newParser();
        Input input = Input.fromByteArray(baos.toByteArray());

        Value value = parser.read(input, parser.newQueryBuilder().build());
        Value encodedValue = value.tag();
        assertEquals(ValueTypes.Sequence, encodedValue.valueType());
        Sequence sequence = encodedValue.sequence();
        assertEquals(2, sequence.size());
        assertEquals("foo", sequence.get(0).string());
        assertEquals("bar", sequence.get(1).string());
    }

    @Test
    public void test_write_putvalue_on_dictionary()
            throws Exception {

        SimplifiedTestParser parser = buildParser(sgb -> {
            sgb.putDictionary(1).putEntry().putString("key")

               .putTag(semanticTag(UBigNumberBuilder.class).putBigInteger(BigInteger.ONE).endSemanticTag())

               .endEntry().endDictionary();
        });

        Value value = parser.read(parser.newQueryBuilder().build());
        assertEquals(MajorType.Dictionary, value.majorType());
        Dictionary dictionary = value.dictionary();
        assertEquals(BigInteger.ONE, dictionary.get(matchString("key")).tag());
    }

    @Test
    public void test_write_putvalue_on_sequence()
            throws Exception {

        SimplifiedTestParser parser = buildParser(sgb -> {
            sgb.putSequence(1)

               .putTag(semanticTag(UBigNumberBuilder.class).putBigInteger(BigInteger.ONE).endSemanticTag())

               .endSequence();
        });

        Value value = parser.read(parser.newQueryBuilder().build());
        assertEquals(MajorType.Sequence, value.majorType());
        Sequence sequence = value.sequence();
        assertEquals(BigInteger.ONE, sequence.get(0).tag());
    }

}
