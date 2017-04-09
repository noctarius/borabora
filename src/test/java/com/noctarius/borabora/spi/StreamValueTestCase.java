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
package com.noctarius.borabora.spi;

import com.noctarius.borabora.AbstractTestCase;
import com.noctarius.borabora.Input;
import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.Output;
import com.noctarius.borabora.ValueTypes;
import com.noctarius.borabora.Writer;
import com.noctarius.borabora.spi.codec.TagStrategies;
import com.noctarius.borabora.spi.codec.TagStrategy;
import com.noctarius.borabora.spi.query.BinaryProjectionStrategy;
import com.noctarius.borabora.spi.query.ProjectionStrategy;
import com.noctarius.borabora.spi.query.QueryContext;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class StreamValueTestCase
        extends AbstractTestCase {

    @Test(expected = IllegalArgumentException.class)
    public void test_illegal_stream_position_null_offset() {
        QueryContext queryContext = newQueryContext();
        new StreamValue(MajorType.Dictionary, ValueTypes.Dictionary, -1, queryContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_illegal_stream_position_illegal_offset() {
        QueryContext queryContext = newQueryContext();
        new StreamValue(MajorType.Dictionary, ValueTypes.Dictionary, -2, queryContext);
    }

    @Test
    public void test_relocatable_tostring() {
        Writer writer = Writer.newWriter();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.newGraphBuilder(Output.toOutputStream(baos)).putBigInteger(BigInteger.ONE).finishStream();
        RelocatableStreamValue value = new RelocatableStreamValue();

        List<TagStrategy> tagStrategies = Arrays.asList(TagStrategies.values());
        ProjectionStrategy projectionStrategy = BinaryProjectionStrategy.INSTANCE;
        Input input = Input.fromByteArray(baos.toByteArray());
        QueryContext queryContext = newQueryContext(input, tagStrategies, projectionStrategy);

        value.relocate(queryContext, MajorType.SemanticTag, ValueTypes.UBigNum, 0);
        assertEquals("RelocatableStreamValue{valueType=UBigNum, offset=0, value=1}", value.toString());
    }

    @Test
    public void test_relocatable_extract_tag() {
        Writer writer = Writer.newWriter();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.newGraphBuilder(Output.toOutputStream(baos)).putBigInteger(BigInteger.ONE).finishStream();
        RelocatableStreamValue value = new RelocatableStreamValue();

        List<TagStrategy> tagStrategies = Arrays.asList(TagStrategies.values());
        ProjectionStrategy projectionStrategy = BinaryProjectionStrategy.INSTANCE;
        Input input = Input.fromByteArray(baos.toByteArray());
        QueryContext queryContext = newQueryContext(input, tagStrategies, projectionStrategy);

        value.relocate(queryContext, MajorType.SemanticTag, ValueTypes.UBigNum, 0);
        assertEquals(BigInteger.ONE, value.tag());
    }

}
