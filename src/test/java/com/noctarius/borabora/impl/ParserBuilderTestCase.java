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
import com.noctarius.borabora.Parser;
import com.noctarius.borabora.builder.ParserBuilder;
import com.noctarius.borabora.spi.codec.TagStrategy;
import com.noctarius.borabora.spi.query.BinaryProjectionStrategy;
import com.noctarius.borabora.spi.query.ObjectProjectionStrategy;
import com.noctarius.borabora.spi.query.ProjectionStrategy;
import com.noctarius.borabora.spi.query.QueryConsumer;
import com.noctarius.borabora.spi.query.QueryContext;
import com.noctarius.borabora.spi.query.QueryContextFactory;
import com.noctarius.borabora.spi.query.pipeline.PipelineStage;
import com.noctarius.borabora.spi.query.pipeline.PipelineStageFactory;
import com.noctarius.borabora.spi.query.pipeline.QueryBuilderNode;
import com.noctarius.borabora.spi.query.pipeline.QueryOptimizer;
import com.noctarius.borabora.spi.query.pipeline.QueryOptimizerStrategy;
import com.noctarius.borabora.spi.query.pipeline.QueryOptimizerStrategyFactory;
import com.noctarius.borabora.spi.query.pipeline.QueryPipeline;
import com.noctarius.borabora.spi.query.pipeline.QueryPipelineFactory;
import com.noctarius.borabora.spi.query.pipeline.QueryStage;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.noctarius.borabora.impl.WriterBuilderTestCase.TBF_1;
import static com.noctarius.borabora.impl.WriterBuilderTestCase.TBF_2;
import static com.noctarius.borabora.impl.WriterBuilderTestCase.TBF_3;
import static com.noctarius.borabora.impl.WriterBuilderTestCase.TBF_4;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParserBuilderTestCase {

    private static final QueryOptimizer QO_1 = new QueryOptimizerTestImpl();
    private static final QueryOptimizer QO_2 = new QueryOptimizerTestImpl();
    private static final QueryOptimizer QO_3 = new QueryOptimizerTestImpl();
    private static final QueryOptimizer QO_4 = new QueryOptimizerTestImpl();

    private static final PipelineStageFactory PIPELINE_STAGE_FACTORY = new PipelineStageFactoryTestImpl();
    private static final QueryContextFactory QUERY_CONTEXT_FACTORY = new QueryContextFactoryTestImpl();
    private static final QueryPipelineFactory QUERY_PIPELINE_FACTORY = new QueryPipelineFactoryTestImpl();
    private static final QueryOptimizerStrategyFactory QUERY_OPTIMIZER_STRATEGY_FACTORY = //
            new QueryOptimizerStrategyFactoryTestImpl();

    @Test
    public void test_withpipelinestagefactory() {
        ParserBuilder parserBuilder = new ParserBuilderImpl();
        parserBuilder.withPipelineStageFactory(PIPELINE_STAGE_FACTORY);
        Parser parser = parserBuilder.build();
        assertEquals(PIPELINE_STAGE_FACTORY, extractPipelineStageFactory(parser));
    }

    @Test
    public void test_withquerycontextfactory() {
        ParserBuilder parserBuilder = new ParserBuilderImpl();
        parserBuilder.withQueryContextFactory(QUERY_CONTEXT_FACTORY);
        Parser parser = parserBuilder.build();
        assertEquals(QUERY_CONTEXT_FACTORY, extractQueryContextFactory(parser));
    }

    @Test
    public void test_withqueryoptimizerstrategyfactory() {
        ParserBuilder parserBuilder = new ParserBuilderImpl();
        parserBuilder.withQueryOptimizerStrategyFactory(QUERY_OPTIMIZER_STRATEGY_FACTORY);
        Parser parser = parserBuilder.build();
        assertEquals(QUERY_OPTIMIZER_STRATEGY_FACTORY, extractQueryOptimizerStrategyFactory(parser));
    }

    @Test
    public void test_withquerypipelinefactory() {
        ParserBuilder parserBuilder = new ParserBuilderImpl();
        parserBuilder.withQueryPipelineFactory(QUERY_PIPELINE_FACTORY);
        Parser parser = parserBuilder.build();
        assertEquals(QUERY_PIPELINE_FACTORY, extractQueryPipelineFactory(parser));
    }

    @Test
    public void test_addqueryoptimizer_single() {
        ParserBuilder parserBuilder = new ParserBuilderImpl();
        parserBuilder.addQueryOptimizer(QO_1);
        Parser parser = parserBuilder.build();
        List<QueryOptimizer> queryOptimizers = extractQueryOptimizers(parser);
        assertEquals(1, queryOptimizers.size());
        assertEquals(QO_1, queryOptimizers.iterator().next());
    }

    @Test
    public void test_addqueryoptimizer_prevent_double_registration() {
        ParserBuilder parserBuilder = new ParserBuilderImpl();
        parserBuilder.addQueryOptimizer(QO_1);
        parserBuilder.addQueryOptimizer(QO_1);
        Parser parser = parserBuilder.build();
        List<QueryOptimizer> queryOptimizers = extractQueryOptimizers(parser);
        assertEquals(1, queryOptimizers.size());
        assertEquals(QO_1, queryOptimizers.iterator().next());
    }

    @Test
    public void test_addqueryoptimizer_double() {
        ParserBuilder parserBuilder = new ParserBuilderImpl();
        parserBuilder.addQueryOptimizers(QO_1, QO_2);
        Parser parser = parserBuilder.build();
        List<QueryOptimizer> queryOptimizers = extractQueryOptimizers(parser);
        assertEquals(2, queryOptimizers.size());
        Iterator<QueryOptimizer> iterator = queryOptimizers.iterator();
        assertEquals(QO_1, iterator.next());
        assertEquals(QO_2, iterator.next());
    }

    @Test
    public void test_addqueryoptimizer_array() {
        ParserBuilder parserBuilder = new ParserBuilderImpl();
        parserBuilder.addQueryOptimizers(QO_1, QO_2, QO_3, QO_4);
        Parser parser = parserBuilder.build();
        List<QueryOptimizer> queryOptimizers = extractQueryOptimizers(parser);
        assertEquals(4, queryOptimizers.size());
        Iterator<QueryOptimizer> iterator = queryOptimizers.iterator();
        assertEquals(QO_1, iterator.next());
        assertEquals(QO_2, iterator.next());
        assertEquals(QO_3, iterator.next());
        assertEquals(QO_4, iterator.next());
    }

    @Test
    public void test_addqueryoptimizer_iterable() {
        ParserBuilder parserBuilder = new ParserBuilderImpl();
        parserBuilder.addQueryOptimizers(Stream.of(QO_1, QO_2, QO_3, QO_4).collect(Collectors.toList()));
        Parser parser = parserBuilder.build();
        List<QueryOptimizer> queryOptimizers = extractQueryOptimizers(parser);
        assertEquals(4, queryOptimizers.size());
        Iterator<QueryOptimizer> iterator = queryOptimizers.iterator();
        assertEquals(QO_1, iterator.next());
        assertEquals(QO_2, iterator.next());
        assertEquals(QO_3, iterator.next());
        assertEquals(QO_4, iterator.next());
    }

    @Test
    public void test_addtagstrategy_single() {
        ParserBuilder parserBuilder = new ParserBuilderImpl();
        parserBuilder.addTagStrategy(TBF_1);
        Parser parser = parserBuilder.build();
        List<TagStrategy> tagStrategies = extractTagStrategies(parser);
        assertEquals(8, tagStrategies.size());
        assertTrue(tagStrategies.contains(TBF_1));
    }

    @Test
    public void test_addtagstrategy_prevent_double_registration() {
        ParserBuilder parserBuilder = new ParserBuilderImpl();
        parserBuilder.addTagStrategy(TBF_1);
        parserBuilder.addTagStrategy(TBF_1);
        Parser parser = parserBuilder.build();
        List<TagStrategy> tagStrategies = extractTagStrategies(parser);
        assertEquals(8, tagStrategies.size());
    }

    @Test
    public void test_addtagstrategies_double() {
        ParserBuilder parserBuilder = new ParserBuilderImpl();
        parserBuilder.addTagStrategies(TBF_1, TBF_2);
        Parser parser = parserBuilder.build();
        List<TagStrategy> tagStrategies = extractTagStrategies(parser);
        assertEquals(9, tagStrategies.size());
        assertTrue(tagStrategies.contains(TBF_1));
        assertTrue(tagStrategies.contains(TBF_2));
    }

    @Test
    public void test_addtagstrategies_array() {
        ParserBuilder parserBuilder = new ParserBuilderImpl();
        parserBuilder.addTagStrategies(TBF_1, TBF_2, TBF_3, TBF_4);
        Parser parser = parserBuilder.build();
        List<TagStrategy> tagStrategies = extractTagStrategies(parser);
        assertEquals(11, tagStrategies.size());
        assertTrue(tagStrategies.contains(TBF_1));
        assertTrue(tagStrategies.contains(TBF_2));
        assertTrue(tagStrategies.contains(TBF_3));
        assertTrue(tagStrategies.contains(TBF_4));
    }

    @Test
    public void test_addtagstrategies_iterable() {
        ParserBuilder parserBuilder = new ParserBuilderImpl();
        parserBuilder.addTagStrategies(Stream.of(TBF_1, TBF_2, TBF_3, TBF_4).collect(Collectors.toList()));
        Parser parser = parserBuilder.build();
        List<TagStrategy> tagStrategies = extractTagStrategies(parser);
        assertEquals(11, tagStrategies.size());
        assertTrue(tagStrategies.contains(TBF_1));
        assertTrue(tagStrategies.contains(TBF_2));
        assertTrue(tagStrategies.contains(TBF_3));
        assertTrue(tagStrategies.contains(TBF_4));
    }

    @Test
    public void test_asobjectselector() {
        ParserBuilder parserBuilder = new ParserBuilderImpl();
        parserBuilder.asObjectProjectionStrategy();
        Parser parser = parserBuilder.build();
        assertEquals(ObjectProjectionStrategy.INSTANCE, extractProjectionStrategy(parser));
    }

    @Test
    public void test_asbinaryselector() {
        ParserBuilder parserBuilder = new ParserBuilderImpl();
        parserBuilder.asObjectProjectionStrategy().asBinaryProjectionStrategy();
        Parser parser = parserBuilder.build();
        assertEquals(BinaryProjectionStrategy.INSTANCE, extractProjectionStrategy(parser));
    }

    @Test
    public void test_with_selectstrategy() {
        ParserBuilder parserBuilder = new ParserBuilderImpl();
        parserBuilder.withProjectionStrategy(ObjectProjectionStrategy.INSTANCE);
        Parser parser = parserBuilder.build();
        assertEquals(ObjectProjectionStrategy.INSTANCE, extractProjectionStrategy(parser));
    }

    private PipelineStageFactory extractPipelineStageFactory(Parser parser) {
        try {
            Field field = ParserImpl.class.getDeclaredField("pipelineStageFactory");
            field.setAccessible(true);
            return (PipelineStageFactory) field.get(parser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private QueryOptimizerStrategyFactory extractQueryOptimizerStrategyFactory(Parser parser) {
        try {
            Field field = ParserImpl.class.getDeclaredField("queryOptimizerStrategyFactory");
            field.setAccessible(true);
            return (QueryOptimizerStrategyFactory) field.get(parser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private QueryPipelineFactory extractQueryPipelineFactory(Parser parser) {
        try {
            Field field = ParserImpl.class.getDeclaredField("queryPipelineFactory");
            field.setAccessible(true);
            return (QueryPipelineFactory) field.get(parser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private QueryContextFactory extractQueryContextFactory(Parser parser) {
        try {
            Field field = ParserImpl.class.getDeclaredField("queryContextFactory");
            field.setAccessible(true);
            return (QueryContextFactory) field.get(parser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ProjectionStrategy extractProjectionStrategy(Parser parser) {
        try {
            Field field = ParserImpl.class.getDeclaredField("projectionStrategy");
            field.setAccessible(true);
            return (ProjectionStrategy) field.get(parser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<QueryOptimizer> extractQueryOptimizers(Parser parser) {
        try {
            Field field = ParserImpl.class.getDeclaredField("queryOptimizers");
            field.setAccessible(true);
            return (List<QueryOptimizer>) field.get(parser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<TagStrategy> extractTagStrategies(Parser parser) {
        try {
            Field field = ParserImpl.class.getDeclaredField("tagStrategies");
            field.setAccessible(true);
            return (List<TagStrategy>) field.get(parser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class QueryOptimizerTestImpl
            implements QueryOptimizer {

        @Override
        public boolean handles(PipelineStage rooPipelineStage) {
            return false;
        }

        @Override
        public PipelineStage optimize(PipelineStage rootPipelineStage, PipelineStageFactory pipelineStageFactory) {
            return null;
        }
    }

    private static class PipelineStageFactoryTestImpl
            implements PipelineStageFactory {

        @Override
        public PipelineStage newPipelineStage(PipelineStage left, PipelineStage right, QueryStage stage) {
            return null;
        }
    }

    private static class QueryContextFactoryTestImpl
            implements QueryContextFactory {

        @Override
        public QueryContext newQueryContext(Input input, QueryConsumer queryConsumer, List<TagStrategy> tagStrategies,
                                            ProjectionStrategy projectionStrategy) {
            return null;
        }
    }

    private static class QueryOptimizerStrategyFactoryTestImpl
            implements QueryOptimizerStrategyFactory {

        @Override
        public QueryOptimizerStrategy newQueryOptimizerStrategy(List<QueryOptimizer> queryOptimizers) {
            return null;
        }
    }

    private static class QueryPipelineFactoryTestImpl
            implements QueryPipelineFactory {

        @Override
        public QueryPipeline newQueryPipeline(QueryBuilderNode queryRootNode, PipelineStageFactory pipelineStageFactory,
                                              QueryOptimizerStrategy queryOptimizerStrategy) {
            return null;
        }
    }

}
