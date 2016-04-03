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

import com.noctarius.borabora.builder.DictionaryBuilder;
import com.noctarius.borabora.builder.SequenceBuilder;
import com.noctarius.borabora.builder.StreamGraphBuilder;
import com.noctarius.borabora.builder.ValueBuilder;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static com.noctarius.borabora.DictionaryGraphQuery.*;

public class WriterTestCase {

    @Test
    public void test_write_lazy()
            throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toByteArrayOutputStream(baos);

        Writer writer = Writer.newBuilder(output).build();

        ObjectGraph graph = ObjectGraph.newBuilder()
                .putNumber(1)
                    .putString("foo")
                .putSequence()
                    .putNumber(2)
                    .putString("bar")
                .endSequence()
                .putSequence(2)
//                    .putIndefiniteByteString()
//                        .putString("uff")
//                        .putString("tata")
//                    .endIndefiniteString()
//                    .putIndefiniteByteString()
//                        .putString("lala")
//                        .putString("lulu")
//                    .endIndefiniteString()
                    .putBoolean(true)
                .endSequence()
                .putDictionary(2)
                    .putEntry()
                        .putString("key1")
                        .putBoolean(true)
                    .endEntry()
                    .putEntry()
                        .putString("key2")
                        .putBoolean(false)
                    .endEntry()
                .endDictionary()
                .putDictionary()
                .endDictionary().build();

        writer.write(graph);
    }


    @Test
    public void test_write_immediate()
            throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toByteArrayOutputStream(baos);

        Writer writer = Writer.newBuilder(output).build();

        writer.newStreamGraphBuilder()
              .putString("foo")
              .putString("äüö")
              .putBoolean(false)
              .putBoolean(true)

              // nulls
              .putString(null)
              .putBoolean(null)

              .finishStream();

        byte[] bytes = baos.toByteArray();
        Input input = Input.fromByteArray(bytes);

        Parser parser = Parser.newBuilder().build();

        Value value1 = parser.read(input, GraphQuery.newBuilder().stream(0).build());
        Value value2 = parser.read(input, GraphQuery.newBuilder().stream(1).build());
        Value value3 = parser.read(input, GraphQuery.newBuilder().stream(2).build());
        Value value4 = parser.read(input, GraphQuery.newBuilder().stream(3).build());
        Value valueN1 = parser.read(input, GraphQuery.newBuilder().stream(4).build());
        Value valueN2 = parser.read(input, GraphQuery.newBuilder().stream(5).build());

        assertEquals("foo", value1.string());
        assertEquals("äüö", value2.string());
        assertFalse(value3.bool());
        assertTrue(value4.bool());

        assertNull(valueN1.string());
        assertNull(valueN2.bool());
    }

    @Test
    public void test_write_indefinite_textstring()
            throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toByteArrayOutputStream(baos);

        Writer writer = Writer.newBuilder(output).build();

        writer.newStreamGraphBuilder()
              .putIndefiniteTextString()
                  .putString("abc")
                  .putString("def")
                  .putString("ghi")
                  .putString("üöä")
              .endIndefiniteString()
              .finishStream();

        byte[] bytes = baos.toByteArray();
        Input input = Input.fromByteArray(bytes);

        Parser parser = Parser.newBuilder().build();

        Value value = parser.read(input, GraphQuery.newBuilder().build());
        assertEquals("abcdefghiüöä", value.string());
    }

    @Test
    public void test_write_indefinite_bytestring()
            throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toByteArrayOutputStream(baos);

        Writer writer = Writer.newBuilder(output).build();

        writer.newStreamGraphBuilder()
                .putIndefiniteByteString()
                    .putString("abc")
                    .putString("def")
                    .putString("ghi")
                .endIndefiniteString()
                .finishStream();

        byte[] bytes = baos.toByteArray();
        Input input = Input.fromByteArray(bytes);

        Parser parser = Parser.newBuilder().build();

        Value value = parser.read(input, GraphQuery.newBuilder().build());
        assertEquals("abcdefghi", value.string());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_write_indefinite_bytestring_fail()
            throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toByteArrayOutputStream(baos);

        Writer writer = Writer.newBuilder(output).build();

        writer.newStreamGraphBuilder()
              .putIndefiniteByteString()
                  .putString("äöü");
    }

    @Test
    public void test_write_indefinite_sequence()
            throws Exception {

        test_writing_sequence(ValueBuilder::putSequence);
    }

    @Test
    public void test_write_sequence()
            throws Exception {

        test_writing_sequence(builder -> builder.putSequence(2));
    }

    private void test_writing_sequence(Function<StreamGraphBuilder, SequenceBuilder<StreamGraphBuilder>> function) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toByteArrayOutputStream(baos);

        Writer writer = Writer.newBuilder(output).build();

        function.apply(writer.newStreamGraphBuilder())
                  .putString("a")
                  .putString("b")
              .endSequence()
              .finishStream();

        byte[] bytes = baos.toByteArray();
        Input input = Input.fromByteArray(bytes);

        Parser parser = Parser.newBuilder().build();

        Value value1 = parser.read(input, GraphQuery.newBuilder().sequence(0).build());
        Value value2 = parser.read(input, GraphQuery.newBuilder().sequence(1).build());

        assertEquals("a", value1.string());
        assertEquals("b", value2.string());
    }

    @Test(expected = IllegalStateException.class)
    public void test_write_sequence_fail_too_many_elements()
            throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toByteArrayOutputStream(baos);

        Writer writer = Writer.newBuilder(output).build();

        writer.newStreamGraphBuilder()
              .putSequence(0)
              .putString("a");
    }

    @Test
    public void test_write_indefinite_dictionary()
            throws Exception {

        test_writing_dictionary(ValueBuilder::putDictionary);
    }

    @Test
    public void test_write_dictionary()
            throws Exception {

        test_writing_dictionary(builder -> builder.putDictionary(2));
    }

    private void test_writing_dictionary(Function<StreamGraphBuilder, DictionaryBuilder<StreamGraphBuilder>> function) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toByteArrayOutputStream(baos);

        Writer writer = Writer.newBuilder(output).build();

        function.apply(writer.newStreamGraphBuilder())
                .putEntry()
                    .putString("a")
                    .putString("A")
                .endEntry()
                .putEntry()
                    .putString("b")
                    .putString("B")
                .endEntry()
            .endDictionary()
            .finishStream();

        byte[] bytes = baos.toByteArray();
        Input input = Input.fromByteArray(bytes);

        Parser parser = Parser.newBuilder().build();

        Value value1 = parser.read(input, GraphQuery.newBuilder().dictionary(matchString("a")).build());
        Value value2 = parser.read(input, GraphQuery.newBuilder().dictionary(matchString("b")).build());

        assertEquals("A", value1.string());
        assertEquals("B", value2.string());
    }

    @Test(expected = IllegalStateException.class)
    public void test_write_dictionary_fail_too_many_elements()
            throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toByteArrayOutputStream(baos);

        Writer writer = Writer.newBuilder(output).build();

        writer.newStreamGraphBuilder()
              .putDictionary(0)
              .putEntry();
    }
}
