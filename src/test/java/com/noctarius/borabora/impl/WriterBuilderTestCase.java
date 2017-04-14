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

import com.noctarius.borabora.Input;
import com.noctarius.borabora.ValueType;
import com.noctarius.borabora.Writer;
import com.noctarius.borabora.spi.builder.EncoderContext;
import com.noctarius.borabora.spi.codec.TagEncoder;
import com.noctarius.borabora.spi.codec.TagStrategy;
import com.noctarius.borabora.spi.query.QueryContext;
import com.noctarius.borabora.spi.query.TypeSpec;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WriterBuilderTestCase {

    private static final TagEncoder TE_1 = new TagEncoderTestImpl();
    private static final TagEncoder TE_2 = new TagEncoderTestImpl();
    private static final TagEncoder TE_3 = new TagEncoderTestImpl();
    private static final TagEncoder TE_4 = new TagEncoderTestImpl();

    static final TagStrategy TBF_1 = new TagStrategy1TestImpl();
    static final TagStrategy TBF_2 = new TagStrategy2TestImpl();
    static final TagStrategy TBF_3 = new TagStrategy3TestImpl();
    static final TagStrategy TBF_4 = new TagStrategy4TestImpl();

    @Test
    public void test_addtagstrategy_single() {
        Writer writer = Writer.newBuilder().addTagStrategy(TBF_1).build();
        List<TagStrategy> tagStrategies = extractTagStrategies(writer);
        assertEquals(9, tagStrategies.size());
        assertTrue(tagStrategies.contains(TBF_1));
    }

    @Test
    public void test_addtagstrategy_prevent_double_registration() {
        Writer writer = Writer.newBuilder().addTagStrategy(TBF_1).addTagStrategy(TBF_1).build();
        List<TagStrategy> tagStrategies = extractTagStrategies(writer);
        assertEquals(9, tagStrategies.size());
    }

    @Test
    public void test_addtagstrategies_double() {
        Writer writer = Writer.newBuilder().addTagStrategies(TBF_1, TBF_2).build();
        List<TagStrategy> tagStrategies = extractTagStrategies(writer);
        assertEquals(10, tagStrategies.size());
        assertTrue(tagStrategies.contains(TBF_1));
        assertTrue(tagStrategies.contains(TBF_2));
    }

    @Test
    public void test_addtagstrategies_array() {
        Writer writer = Writer.newBuilder().addTagStrategies(TBF_1, TBF_2, TBF_3, TBF_4).build();
        List<TagStrategy> tagStrategies = extractTagStrategies(writer);
        assertEquals(12, tagStrategies.size());
        assertTrue(tagStrategies.contains(TBF_1));
        assertTrue(tagStrategies.contains(TBF_2));
        assertTrue(tagStrategies.contains(TBF_3));
        assertTrue(tagStrategies.contains(TBF_4));
    }

    @Test
    public void test_addtagstrategies_iterable() {
        List<TagStrategy> strategies = Stream.of(TBF_1, TBF_2, TBF_3, TBF_4).collect(Collectors.toList());
        Writer writer = Writer.newBuilder().addTagStrategies(strategies).build();
        List<TagStrategy> tagStrategies = extractTagStrategies(writer);
        assertEquals(12, tagStrategies.size());
        assertTrue(tagStrategies.contains(TBF_1));
        assertTrue(tagStrategies.contains(TBF_2));
        assertTrue(tagStrategies.contains(TBF_3));
        assertTrue(tagStrategies.contains(TBF_4));
    }

    private List<TagStrategy> extractTagStrategies(Writer writer) {
        try {
            Field field = WriterImpl.class.getDeclaredField("tagStrategies");
            field.setAccessible(true);
            return (List<TagStrategy>) new ArrayList<>(((Map) field.get(writer)).values());
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

    private static class TagStrategy1TestImpl
            implements TagStrategy {

        @Override
        public Object newTagBuilder(EncoderContext encoderContext) {
            return null;
        }

        @Override
        public int tagId() {
            return 0;
        }

        @Override
        public ValueType valueType() {
            return null;
        }

        @Override
        public Class tagBuilderType() {
            return Byte.class;
        }

        @Override
        public long process(Object value, long offset, EncoderContext encoderContext) {
            return 0;
        }

        @Override
        public boolean handles(Object value) {
            return false;
        }

        @Override
        public Object process(ValueType valueType, long offset, long length, QueryContext queryContext) {
            return null;
        }

        @Override
        public boolean handles(Input input, long offset) {
            return false;
        }

        @Override
        public TypeSpec handles(long tagId) {
            return null;
        }

        @Override
        public ValueType valueType(Input input, long offset) {
            return null;
        }
    }

    private static class TagStrategy2TestImpl
            implements TagStrategy {

        @Override
        public Object newTagBuilder(EncoderContext encoderContext) {
            return null;
        }

        @Override
        public int tagId() {
            return 0;
        }

        @Override
        public ValueType valueType() {
            return null;
        }

        @Override
        public Class tagBuilderType() {
            return Short.class;
        }

        @Override
        public long process(Object value, long offset, EncoderContext encoderContext) {
            return 0;
        }

        @Override
        public boolean handles(Object value) {
            return false;
        }

        @Override
        public Object process(ValueType valueType, long offset, long length, QueryContext queryContext) {
            return null;
        }

        @Override
        public boolean handles(Input input, long offset) {
            return false;
        }

        @Override
        public TypeSpec handles(long tagId) {
            return null;
        }

        @Override
        public ValueType valueType(Input input, long offset) {
            return null;
        }
    }

    private static class TagStrategy3TestImpl
            implements TagStrategy {

        @Override
        public Object newTagBuilder(EncoderContext encoderContext) {
            return null;
        }

        @Override
        public int tagId() {
            return 0;
        }

        @Override
        public ValueType valueType() {
            return null;
        }

        @Override
        public Class tagBuilderType() {
            return Integer.class;
        }

        @Override
        public long process(Object value, long offset, EncoderContext encoderContext) {
            return 0;
        }

        @Override
        public boolean handles(Object value) {
            return false;
        }

        @Override
        public Object process(ValueType valueType, long offset, long length, QueryContext queryContext) {
            return null;
        }

        @Override
        public boolean handles(Input input, long offset) {
            return false;
        }

        @Override
        public TypeSpec handles(long tagId) {
            return null;
        }

        @Override
        public ValueType valueType(Input input, long offset) {
            return null;
        }
    }

    private static class TagStrategy4TestImpl
            implements TagStrategy {

        @Override
        public Object newTagBuilder(EncoderContext encoderContext) {
            return null;
        }

        @Override
        public int tagId() {
            return 0;
        }

        @Override
        public ValueType valueType() {
            return null;
        }

        @Override
        public Class tagBuilderType() {
            return Long.class;
        }

        @Override
        public long process(Object value, long offset, EncoderContext encoderContext) {
            return 0;
        }

        @Override
        public boolean handles(Object value) {
            return false;
        }

        @Override
        public Object process(ValueType valueType, long offset, long length, QueryContext queryContext) {
            return null;
        }

        @Override
        public boolean handles(Input input, long offset) {
            return false;
        }

        @Override
        public TypeSpec handles(long tagId) {
            return null;
        }

        @Override
        public ValueType valueType(Input input, long offset) {
            return null;
        }
    }

}
