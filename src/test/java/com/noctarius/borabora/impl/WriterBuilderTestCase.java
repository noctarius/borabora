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
import com.noctarius.borabora.spi.codec.CommonTagCodec;
import com.noctarius.borabora.spi.codec.EncoderContext;
import com.noctarius.borabora.spi.codec.TagBuilderFactory;
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

    private static final TagBuilderFactory STBF_1 = new SemanticTagBuilderFactory1TestImpl();
    private static final TagBuilderFactory STBF_2 = new SemanticTagBuilderFactory2TestImpl();
    private static final TagBuilderFactory STBF_3 = new SemanticTagBuilderFactory3TestImpl();
    private static final TagBuilderFactory STBF_4 = new SemanticTagBuilderFactory4TestImpl();

    @Test
    public void test_addsemantictagbuilderfactory_single() {
        Writer writer = Writer.newBuilder().addSemanticTagBuilderFactory(STBF_1).build();
        List<TagBuilderFactory> semanticTagBuilderFactories = extractSemanticTagBuilderFactories(writer);
        assertEquals(1, semanticTagBuilderFactories.size());
        assertSame(STBF_1, semanticTagBuilderFactories.iterator().next());

        List<TagEncoder> tagEncoders = extractTagEncoders(writer);
        assertEquals(2, tagEncoders.size());
        Iterator<TagEncoder> iterator = tagEncoders.iterator();
        assertSame(CommonTagCodec.INSTANCE, iterator.next());
        assertSame(TE_1, iterator.next());
    }

    @Test
    public void test_addsemantictagbuilderfactory_double() {
        Writer writer = Writer.newBuilder().addSemanticTagBuilderFactories(STBF_1, STBF_2).build();
        List<TagBuilderFactory> semanticTagBuilderFactories = extractSemanticTagBuilderFactories(writer);
        assertEquals(2, semanticTagBuilderFactories.size());
        assertTrue(semanticTagBuilderFactories.contains(STBF_1));
        assertTrue(semanticTagBuilderFactories.contains(STBF_2));

        List<TagEncoder> tagEncoders = extractTagEncoders(writer);
        assertEquals(3, tagEncoders.size());
        Iterator<TagEncoder> iterator = tagEncoders.iterator();
        assertSame(CommonTagCodec.INSTANCE, iterator.next());
        assertSame(TE_1, iterator.next());
        assertSame(TE_2, iterator.next());
    }

    @Test
    public void test_addsemantictagbuilderfactory_array() {
        Writer writer = Writer.newBuilder().addSemanticTagBuilderFactories(STBF_1, STBF_2, STBF_3, STBF_4).build();
        List<TagBuilderFactory> semanticTagBuilderFactories = extractSemanticTagBuilderFactories(writer);
        assertEquals(4, semanticTagBuilderFactories.size());
        assertTrue(semanticTagBuilderFactories.contains(STBF_1));
        assertTrue(semanticTagBuilderFactories.contains(STBF_2));
        assertTrue(semanticTagBuilderFactories.contains(STBF_3));
        assertTrue(semanticTagBuilderFactories.contains(STBF_4));

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
    public void test_addsemantictagbuilderfactory_iterable() {
        List<TagBuilderFactory> factories = Stream.of(STBF_1, STBF_2, STBF_3, STBF_4).collect(Collectors.toList());
        Writer writer = Writer.newBuilder().addSemanticTagBuilderFactories(factories).build();
        List<TagBuilderFactory> semanticTagBuilderFactories = extractSemanticTagBuilderFactories(writer);
        assertEquals(4, semanticTagBuilderFactories.size());
        assertTrue(semanticTagBuilderFactories.contains(STBF_1));
        assertTrue(semanticTagBuilderFactories.contains(STBF_2));
        assertTrue(semanticTagBuilderFactories.contains(STBF_3));
        assertTrue(semanticTagBuilderFactories.contains(STBF_4));

        List<TagEncoder> tagEncoders = extractTagEncoders(writer);
        assertEquals(5, tagEncoders.size());
        Iterator<TagEncoder> iterator = tagEncoders.iterator();
        assertSame(CommonTagCodec.INSTANCE, iterator.next());
        assertSame(TE_1, iterator.next());
        assertSame(TE_2, iterator.next());
        assertSame(TE_3, iterator.next());
        assertSame(TE_4, iterator.next());
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

    private List<TagBuilderFactory> extractSemanticTagBuilderFactories(Writer writer) {
        try {
            Field field = WriterImpl.class.getDeclaredField("factories");
            field.setAccessible(true);
            return (List<TagBuilderFactory>) new ArrayList<>(((Map) field.get(writer)).values());
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
            implements TagBuilderFactory {

        @Override
        public Object newTagBuilder(EncoderContext encoderContext) {
            return null;
        }

        @Override
        public int tagId() {
            return 0;
        }

        @Override
        public Class tagBuilderType() {
            return Byte.class;
        }

        @Override
        public TagEncoder tagEncoder() {
            return TE_1;
        }
    }

    private static class SemanticTagBuilderFactory2TestImpl
            implements TagBuilderFactory {

        @Override
        public Object newTagBuilder(EncoderContext encoderContext) {
            return null;
        }

        @Override
        public int tagId() {
            return 0;
        }

        @Override
        public Class tagBuilderType() {
            return Short.class;
        }

        @Override
        public TagEncoder tagEncoder() {
            return TE_2;
        }
    }

    private static class SemanticTagBuilderFactory3TestImpl
            implements TagBuilderFactory {

        @Override
        public Object newTagBuilder(EncoderContext encoderContext) {
            return null;
        }

        @Override
        public int tagId() {
            return 0;
        }

        @Override
        public Class tagBuilderType() {
            return Integer.class;
        }

        @Override
        public TagEncoder tagEncoder() {
            return TE_3;
        }
    }

    private static class SemanticTagBuilderFactory4TestImpl
            implements TagBuilderFactory {

        @Override
        public Object newTagBuilder(EncoderContext encoderContext) {
            return null;
        }

        @Override
        public int tagId() {
            return 0;
        }

        @Override
        public Class tagBuilderType() {
            return Long.class;
        }

        @Override
        public TagEncoder tagEncoder() {
            return TE_4;
        }
    }

}
