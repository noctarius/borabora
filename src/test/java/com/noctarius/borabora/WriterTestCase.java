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

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WriterTestCase {

    @Test
    public void test_write_lazy() throws Exception {
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
    public void test_write_immediate() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toByteArrayOutputStream(baos);

        Writer writer = Writer.newBuilder(output).build();

        writer.newStreamGraphBuilder()
              .putString("foo")
              .putString("äüö")
              .putBoolean(false)
              .putBoolean(true)
              .finishStream();

        byte[] bytes = baos.toByteArray();
        Input input = Input.fromByteArray(bytes);

        Parser parser = Parser.newBuilder().build();

        Value value1 = parser.read(input, GraphQuery.newBuilder().stream(0).build());
        Value value2 = parser.read(input, GraphQuery.newBuilder().stream(1).build());
        Value value3 = parser.read(input, GraphQuery.newBuilder().stream(2).build());
        Value value4 = parser.read(input, GraphQuery.newBuilder().stream(3).build());

        assertEquals("foo", value1.string());
        assertEquals("äüö", value2.string());
        assertFalse(value3.bool());
        assertTrue(value4.bool());
    }

}
