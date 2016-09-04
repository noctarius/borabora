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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class WriteNullTestCase
        extends AbstractTestCase {

    @Parameterized.Parameters(name = "test_write_null_value_{0}")
    public static Collection<Object[]> suppliers() {
        return Arrays.asList(new Object[][]{{"biginteger", consumer(gb -> gb.putBigInteger(null))}, //
                                            {"boolean", consumer(gb -> gb.putBoolean(null))}, //
                                            {"bytestring", consumer(gb -> gb.putByteString(null))}, //
                                            {"datetime_date", consumer(gb -> gb.putDateTime((Date) null))}, //
                                            {"datetime_instant", consumer(gb -> gb.putDateTime((Instant) null))}, //
                                            {"halffloat", consumer(gb -> gb.putHalfPrecision(null))}, //
                                            {"byte", consumer(gb -> gb.putNumber((Byte) null))}, //
                                            {"short", consumer(gb -> gb.putNumber((Short) null))}, //
                                            {"integer", consumer(gb -> gb.putNumber((Integer) null))}, //
                                            {"long", consumer(gb -> gb.putNumber((Long) null))}, //
                                            {"float", consumer(gb -> gb.putNumber((Float) null))}, //
                                            {"double", consumer(gb -> gb.putNumber((Double) null))}, //
                                            {"string", consumer(gb -> gb.putString(null))}, //
                                            {"textstring", consumer(gb -> gb.putTextString(null))}, //
                                            {"timestamp", consumer(gb -> gb.putTimestamp(null))}, //
                                            {"uri", consumer(gb -> gb.putURI(null))}});
    }

    private static final Parser PARSER = Parser.newBuilder().build();
    private static final Query QUERY = PARSER.newQueryBuilder().build();

    private final Consumer<GraphBuilder> consumer;

    public WriteNullTestCase(String name, Consumer<GraphBuilder> consumer) {
        this.consumer = consumer;
    }

    @Test
    public void test_null()
            throws Exception {

        Writer writer = Writer.newBuilder().build();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toOutputStream(baos);

        GraphBuilder graphBuilder = writer.newGraphBuilder(output);
        consumer.accept(graphBuilder);
        graphBuilder.finishStream();

        Input input = Input.fromByteArray(baos.toByteArray());
        Value value = PARSER.read(input, QUERY);
        assertEquals(ValueTypes.Null, value.valueType());
    }

    private static Consumer<GraphBuilder> consumer(Consumer<GraphBuilder> consumer) {
        return consumer;
    }

}
