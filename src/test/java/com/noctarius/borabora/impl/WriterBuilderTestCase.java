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
package com.noctarius.borabora.impl;

import com.noctarius.borabora.Writer;
import com.noctarius.borabora.spi.SemanticTagBuilderFactory;
import com.noctarius.borabora.spi.codec.CommonTagCodec;
import com.noctarius.borabora.spi.codec.EncoderContext;
import com.noctarius.borabora.spi.codec.TagEncoder;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class WriterBuilderTestCase {

    private static final TagEncoder TE_1 = new TagEncoderTestImpl();
    private static final TagEncoder TE_2 = new TagEncoderTestImpl();
    private static final TagEncoder TE_3 = new TagEncoderTestImpl();
    private static final TagEncoder TE_4 = new TagEncoderTestImpl();

    private static final SemanticTagBuilderFactory STBF_1 = new SemanticTagBuilderFactory1TestImpl();
    private static final SemanticTagBuilderFactory STBF_2 = new SemanticTagBuilderFactory2TestImpl();
    private static final SemanticTagBuilderFactory STBF_3 = new SemanticTagBuilderFactory3TestImpl();
    private static final SemanticTagBuilderFactory STBF_4 = new SemanticTagBuilderFactory4TestImpl();

    @Test
    public void test_addtagencoder_single() {
        Writer writer = Writer.newBuilder().addTagEncoder(TE_1).build();
        List<TagEncoder> tagEncoders = extractTagEncoders(writer);
        assertEquals(2, tagEncoders.size());
        Iterator<TagEncoder> iterator = tagEncoders.iterator();
        assertSame(CommonTagCodec.INSTANCE, iterator.next());
        assertSame(TE_1, iterator.next());
    }

    @Test
    public void test_addtagencoder_double() {
        Writer writer = Writer.newBuilder().addTagEncoders(TE_1, TE_2).build();
        List<TagEncoder> tagEncoders = extractTagEncoders(writer);
        assertEquals(3, tagEncoders.size());
        Iterator<TagEncoder> iterator = tagEncoders.iterator();
        assertSame(CommonTagCodec.INSTANCE, iterator.next());
        assertSame(TE_1, iterator.next());
        assertSame(TE_2, iterator.next());
    }

    @Test
    public void test_addtagencoder_array() {
        Writer writer = Writer.newBuilder().addTagEncoders(TE_1, TE_2, TE_3, TE_4).build();
        List<TagEncoder> tagEncoders = extractTagEncoders(writer);
        assertEquals(5, tagEncoders.size());
        Iterator<TagEncoder> iterator = tagEncoders.iterator();
        assertSame(CommonTagCodec.INSTANCE, iterator.next());
        assertSame(TE_1, iterator.next());
        assertSame(TE_2, iterator.next());
        assertSame(TE_3, iterator.next());
        assertSame(TE_4, iterator.next());
    }

    @Test
    public void test_addtagencoder_iterable() {
        List<TagEncoder> encoders = Stream.of(TE_1, TE_2, TE_3, TE_4).collect(Collectors.toList());
        Writer writer = Writer.newBuilder().addTagEncoders(encoders).build();
        List<TagEncoder> tagEncoders = extractTagEncoders(writer);
        assertEquals(5, tagEncoders.size());
        Iterator<TagEncoder> iterator = tagEncoders.iterator();
        assertSame(CommonTagCodec.INSTANCE, iterator.next());
        assertSame(TE_1, iterator.next());
        assertSame(TE_2, iterator.next());
        assertSame(TE_3, iterator.next());
        assertSame(TE_4, iterator.next());
    }

    @Test
    public void test_addsemantictagbuilderfactory_single() {
        Writer writer = Writer.newBuilder().addSemanticTagBuilderFactory(STBF_1).build();
        List<SemanticTagBuilderFactory> semanticTagBuilderFactories = extractSemanticTagBuilderFactories(writer);
        assertEquals(1, semanticTagBuilderFactories.size());
        Iterator<SemanticTagBuilderFactory> iterator = semanticTagBuilderFactories.iterator();
        assertSame(STBF_1, iterator.next());
    }

    @Test
    public void test_addsemantictagbuilderfactory_double() {
        Writer writer = Writer.newBuilder().addSemanticTagBuilderFactories(STBF_1, STBF_2).build();
        List<SemanticTagBuilderFactory> semanticTagBuilderFactories = extractSemanticTagBuilderFactories(writer);
        assertEquals(2, semanticTagBuilderFactories.size());
        assertTrue(semanticTagBuilderFactories.contains(STBF_1));
        assertTrue(semanticTagBuilderFactories.contains(STBF_2));
    }

    @Test
    public void test_addsemantictagbuilderfactory_array() {
        Writer writer = Writer.newBuilder().addSemanticTagBuilderFactories(STBF_1, STBF_2, STBF_3, STBF_4).build();
        List<SemanticTagBuilderFactory> semanticTagBuilderFactories = extractSemanticTagBuilderFactories(writer);
        assertEquals(4, semanticTagBuilderFactories.size());
        assertTrue(semanticTagBuilderFactories.contains(STBF_1));
        assertTrue(semanticTagBuilderFactories.contains(STBF_2));
        assertTrue(semanticTagBuilderFactories.contains(STBF_3));
        assertTrue(semanticTagBuilderFactories.contains(STBF_4));
    }

    @Test
    public void test_addsemantictagbuilderfactory_iterable() {
        List<SemanticTagBuilderFactory> factories = Stream.of(STBF_1, STBF_2, STBF_3, STBF_4).collect(Collectors.toList());
        Writer writer = Writer.newBuilder().addSemanticTagBuilderFactories(factories).build();
        List<SemanticTagBuilderFactory> semanticTagBuilderFactories = extractSemanticTagBuilderFactories(writer);
        assertEquals(4, semanticTagBuilderFactories.size());
        assertTrue(semanticTagBuilderFactories.contains(STBF_1));
        assertTrue(semanticTagBuilderFactories.contains(STBF_2));
        assertTrue(semanticTagBuilderFactories.contains(STBF_3));
        assertTrue(semanticTagBuilderFactories.contains(STBF_4));
    }

    private List<TagEncoder> extractTagEncoders(Writer writer) {
        try {
            Field field = WriterImpl.class.getDeclaredField("tagEncoders");
            field.setAccessible(true);
            return (List<TagEncoder>) field.get(writer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<SemanticTagBuilderFactory> extractSemanticTagBuilderFactories(Writer writer) {
        try {
            Field field = WriterImpl.class.getDeclaredField("factories");
            field.setAccessible(true);
            return (List<SemanticTagBuilderFactory>) new ArrayList<>(((Map) field.get(writer)).values());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class TagEncoderTestImpl
            implements TagEncoder {

        @Override
        public long process(Object value, long offset, EncoderContext encoderContext) {
            return 0;
        }

        @Override
        public boolean handles(Object value) {
            return false;
        }
    }

    private static class SemanticTagBuilderFactory1TestImpl
            implements SemanticTagBuilderFactory {

        @Override
        public Object newSemanticTagBuilder(EncoderContext encoderContext) {
            return null;
        }

        @Override
        public int tagId() {
            return 0;
        }

        @Override
        public Class semanticTagBuilderType() {
            return Byte.class;
        }
    }

    private static class SemanticTagBuilderFactory2TestImpl
            implements SemanticTagBuilderFactory {

        @Override
        public Object newSemanticTagBuilder(EncoderContext encoderContext) {
            return null;
        }

        @Override
        public int tagId() {
            return 0;
        }

        @Override
        public Class semanticTagBuilderType() {
            return Short.class;
        }
    }

    private static class SemanticTagBuilderFactory3TestImpl
            implements SemanticTagBuilderFactory {

        @Override
        public Object newSemanticTagBuilder(EncoderContext encoderContext) {
            return null;
        }

        @Override
        public int tagId() {
            return 0;
        }

        @Override
        public Class semanticTagBuilderType() {
            return Integer.class;
        }
    }

    private static class SemanticTagBuilderFactory4TestImpl
            implements SemanticTagBuilderFactory {

        @Override
        public Object newSemanticTagBuilder(EncoderContext encoderContext) {
            return null;
        }

        @Override
        public int tagId() {
            return 0;
        }

        @Override
        public Class semanticTagBuilderType() {
            return Long.class;
        }
    }

}
