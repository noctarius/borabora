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
import com.noctarius.borabora.impl.DefaultQueryContextFactory;
import com.noctarius.borabora.impl.query.QueryImpl;
import com.noctarius.borabora.spi.io.Constants;
import com.noctarius.borabora.spi.ObjectValue;
import com.noctarius.borabora.spi.StreamValue;
import com.noctarius.borabora.spi.codec.TagStrategies;
import com.noctarius.borabora.spi.codec.TagStrategy;
import com.noctarius.borabora.spi.io.Decoder;
import com.noctarius.borabora.spi.io.Encoder;
import com.noctarius.borabora.spi.query.BinaryProjectionStrategy;
import com.noctarius.borabora.spi.query.ProjectionStrategy;
import com.noctarius.borabora.spi.query.QueryContext;
import com.noctarius.borabora.spi.query.QueryContextFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import static com.noctarius.borabora.spi.io.Constants.EMPTY_BYTE_ARRAY;
import static com.noctarius.borabora.spi.io.Constants.EMPTY_QUERY_CONSUMER;
import static org.junit.Assert.assertEquals;

public abstract class AbstractTestCase {

    private static final Random RANDOM = new Random();

    public static void callConstructor(Class<?> type) {
        try {
            Constructor<?> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String buildString(int nbOfLetters) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nbOfLetters; i++) {
            sb.append((char) (RANDOM.nextInt(91) + 32));
        }
        return sb.toString();
    }

    public static SimplifiedTestParser buildParser(Consumer<GraphBuilder> test) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toOutputStream(baos);

        Writer writer = Writer.newBuilder().build();

        GraphBuilder graphBuilder = writer.newGraphBuilder(output);
        test.accept(graphBuilder);
        graphBuilder.finishStream();

        byte[] bytes = baos.toByteArray();
        Input input = Input.fromByteArray(bytes);

        return new SimplifiedTestParser(Parser.newBuilder().build(), input);
    }

    public static void assertEqualsNumber(Number n1, Number n2) {
        if (n1.getClass().equals(n2.getClass())) {
            assertEquals(n1, n2);
            return;
        }

        BigInteger b1 = n1 instanceof BigInteger ? (BigInteger) n1 : BigInteger.valueOf(n1.longValue());
        BigInteger b2 = n2 instanceof BigInteger ? (BigInteger) n2 : BigInteger.valueOf(n2.longValue());
        assertEquals(b1, b2);
    }

    public static byte[] hexToBytes(String hex) {
        hex = hex.toLowerCase();
        if (hex.startsWith("0x")) {
            hex = hex.substring(2);
        }
        return DatatypeConverter.parseHexBinary(hex);
    }

    public static SimplifiedTestParser buildParser(String hex) {
        byte[] data = hexToBytes(hex);
        Input input = Input.fromByteArray(data);
        return new SimplifiedTestParser(Parser.newBuilder().build(), input);
    }

    public static void assertQueryEquals(Query expected, Query actual) {
        QueryImpl exp = (QueryImpl) expected;
        QueryImpl act = (QueryImpl) actual;
        assertEquals(exp, act);
    }

    public static QueryContext newQueryContext() {
        return newQueryContext(Input.fromByteArray(EMPTY_BYTE_ARRAY));
    }

    public static QueryContext newQueryContext(Input input) {
        List<TagStrategy> tagStrategies = Arrays.asList(TagStrategies.values());
        ProjectionStrategy projectionStrategy = BinaryProjectionStrategy.INSTANCE;
        return newQueryContext(input, tagStrategies, projectionStrategy);
    }

    public static QueryContext newQueryContext(Input input, List<TagStrategy> tagStrategies,
                                               ProjectionStrategy projectionStrategy) {

        QueryContextFactory queryContextFactory = DefaultQueryContextFactory.INSTANCE;
        return queryContextFactory.newQueryContext(input, EMPTY_QUERY_CONSUMER, tagStrategies, projectionStrategy);
    }

    public static Value asObjectValue(MajorType majorType, ValueType valueType, String value) {
        return new ObjectValue(majorType, valueType, value);
    }

    public static Value asStreamValue(Consumer<GraphBuilder> consumer) {
        Writer writer = Writer.newBuilder().build();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toOutputStream(baos);
        GraphBuilder graphBuilder = writer.newGraphBuilder(output);
        consumer.accept(graphBuilder);
        return asStreamValue(baos.toByteArray());
    }

    public static Value asStreamValue(byte[] bytes) {
        Input input = Input.fromByteArray(bytes);
        short head = Decoder.readUInt8(input, 0);
        MajorType majorType = MajorType.findMajorType(head);

        List<TagStrategy> tagStrategies = Arrays.asList(TagStrategies.values());
        ProjectionStrategy projectionStrategy = BinaryProjectionStrategy.INSTANCE;

        QueryContextFactory queryContextFactory = DefaultQueryContextFactory.INSTANCE;
        QueryContext queryContext = queryContextFactory
                .newQueryContext(input, Constants.EMPTY_QUERY_CONSUMER, tagStrategies, projectionStrategy);

        ValueType valueType = queryContext.valueType(0);
        return new StreamValue(majorType, valueType, 0, queryContext);
    }

    public static Value asStreamValue(String value) {
        return asStreamValue(encode(value));
    }

    public static byte[] encode(String value) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Encoder.putString(value, 0, Output.toOutputStream(baos));
        return baos.toByteArray();
    }

    public static class SimplifiedTestParser {

        private final Parser parser;
        private final Input input;

        private SimplifiedTestParser(Parser parser, Input input) {
            this.parser = parser;
            this.input = input;
        }

        public Value read(Query query) {
            return parser.read(input, query);
        }

        public Value read(String query) {
            return parser.read(input, query);
        }

        public void read(Query query, Consumer<Value> callback) {
            parser.read(input, query, callback);
        }

        public void read(String query, Consumer<Value> callback) {
            parser.read(input, query, callback);
        }

        public Query prepareQuery(String query) {
            return parser.prepareQuery(query);
        }
    }

}
