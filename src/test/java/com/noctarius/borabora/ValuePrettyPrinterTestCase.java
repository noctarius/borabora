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
package com.noctarius.borabora;

import com.noctarius.borabora.builder.encoder.GraphBuilder;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

public class ValuePrettyPrinterTestCase
        extends AbstractTestCase {

    private static final byte[] bytes = buildBytes();

    private static byte[] buildBytes() {
        Writer writer = Writer.newWriter();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toOutputStream(baos);

        GraphBuilder graphBuilder = writer.newGraphBuilder(output);

        graphBuilder.putSequence()

                    .putSequence().putNumber(1).putNumber(2).endSequence()

                    .putDictionary().putEntry().putString("key").putNumber(1).endEntry().endDictionary()

                    .endSequence().finishStream();

        return baos.toByteArray();
    }

    @Test
    public void call_constructor() {
        callConstructor(ValuePrettyPrinter.class);
    }

    @Test
    public void testcoverage_toStringPrettyPrint()
            throws Exception {

        Input input = Input.fromByteArray(bytes);
        Parser parser = Parser.newParser();

        Value value = parser.read(input, parser.newQueryBuilder().build());
        ValuePrettyPrinter.toStringPrettyPrint(value);
    }

    @Test
    public void testcoverage_asStringPrettyPrint()
            throws Exception {

        Input input = Input.fromByteArray(bytes);
        Parser parser = Parser.newParser();

        Value value = parser.read(input, parser.newQueryBuilder().build());
        ValuePrettyPrinter.asStringPrettyPrint(value);
    }

}