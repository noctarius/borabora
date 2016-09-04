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

import com.noctarius.borabora.builder.encoder.GraphBuilder;
import com.noctarius.borabora.spi.io.CompositeBuffer;
import org.junit.Test;
import sun.misc.Unsafe;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class OutputTestCase
        extends AbstractTestCase {

    @Test
    public void test_output_bytearray() {
        byte[] bytes = new byte[3];
        Output output = Output.toByteArray(bytes);
        Writer writer = Writer.newBuilder().build();
        GraphBuilder graphBuilder = writer.newGraphBuilder(output);
        graphBuilder.putNumber(1).putNumber(2).putNumber(3).finishStream();

        Input input = Input.fromByteArray(bytes);
        Parser parser = Parser.newBuilder().build();

        List<Value> values = new ArrayList<>();
        parser.read(input, Query.newBuilder().multiStream().build(), values::add);

        assertEqualsNumber(1, values.get(0).number());
        assertEqualsNumber(2, values.get(1).number());
        assertEqualsNumber(3, values.get(2).number());
    }

    @Test
    public void test_output_native() {
        Unsafe unsafe = UnsafeUtils.getUnsafe();
        long address = unsafe.allocateMemory(3);

        Output output = Output.toNative(address, 3);
        Writer writer = Writer.newBuilder().build();
        GraphBuilder graphBuilder = writer.newGraphBuilder(output);
        graphBuilder.putNumber(1).putNumber(2).putNumber(3).finishStream();

        Input input = Input.fromNative(address, 3);
        Parser parser = Parser.newBuilder().build();

        List<Value> values = new ArrayList<>();
        parser.read(input, Query.newBuilder().multiStream().build(), values::add);

        assertEqualsNumber(1, values.get(0).number());
        assertEqualsNumber(2, values.get(1).number());
        assertEqualsNumber(3, values.get(2).number());
    }

    @Test
    public void test_output_outputstream() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toOutputStream(baos);
        Writer writer = Writer.newBuilder().build();
        GraphBuilder graphBuilder = writer.newGraphBuilder(output);
        graphBuilder.putNumber(1).putNumber(2).putNumber(3).finishStream();

        Input input = Input.fromByteArray(baos.toByteArray());
        Parser parser = Parser.newBuilder().build();

        List<Value> values = new ArrayList<>();
        parser.read(input, Query.newBuilder().multiStream().build(), values::add);

        assertEqualsNumber(1, values.get(0).number());
        assertEqualsNumber(2, values.get(1).number());
        assertEqualsNumber(3, values.get(2).number());
    }

    @Test
    public void test_output_compositebuffer() {
        CompositeBuffer compositeBuffer = CompositeBuffer.newCompositeBuffer();
        Output output = Output.toCompositeBuffer(compositeBuffer);
        Writer writer = Writer.newBuilder().build();
        GraphBuilder graphBuilder = writer.newGraphBuilder(output);
        graphBuilder.putNumber(1).putNumber(2).putNumber(3).finishStream();

        Input input = Input.fromByteArray(compositeBuffer.toByteArray());
        Parser parser = Parser.newBuilder().build();

        List<Value> values = new ArrayList<>();
        parser.read(input, Query.newBuilder().multiStream().build(), values::add);

        assertEqualsNumber(1, values.get(0).number());
        assertEqualsNumber(2, values.get(1).number());
        assertEqualsNumber(3, values.get(2).number());
    }

}
