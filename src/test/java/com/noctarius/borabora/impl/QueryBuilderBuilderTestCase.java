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

import com.noctarius.borabora.builder.query.QueryBuilder;
import com.noctarius.borabora.builder.QueryBuilderBuilder;
import com.noctarius.borabora.builder.query.StreamQueryBuilder;
import com.noctarius.borabora.spi.query.pipeline.PipelineStage;
import com.noctarius.borabora.spi.query.pipeline.PipelineStageFactory;
import com.noctarius.borabora.spi.query.pipeline.QueryBuilderNode;
import com.noctarius.borabora.spi.query.optimizer.QueryOptimizer;
import com.noctarius.borabora.spi.query.optimizer.QueryOptimizerStrategy;
import com.noctarius.borabora.spi.query.optimizer.QueryOptimizerStrategyFactory;
import com.noctarius.borabora.spi.query.pipeline.QueryPipeline;
import com.noctarius.borabora.spi.query.pipeline.QueryPipelineFactory;
import com.noctarius.borabora.spi.query.pipeline.QueryStage;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class QueryBuilderBuilderTestCase {

    private static final QueryOptimizer QO_1 = new QueryOptimizerTestImpl();
    private static final QueryOptimizer QO_2 = new QueryOptimizerTestImpl();
    private static final QueryOptimizer QO_3 = new QueryOptimizerTestImpl();
    private static final QueryOptimizer QO_4 = new QueryOptimizerTestImpl();

    private static final QueryOptimizerStrategy QUERY_OPTIMIZER_STRATEGY = new QueryOptimizerStrategyTestImpl();
    private static final PipelineStageFactory PIPELINE_STAGE_FACTORY = new PipelineStageFactoryTestImpl();
    private static final QueryPipelineFactory QUERY_PIPELINE_FACTORY = new QueryPipelineFactoryTestImpl();
    private static final QueryOptimizerStrategyFactory QUERY_OPTIMIZER_STRATEGY_FACTORY = //
            new QueryOptimizerStrategyFactoryTestImpl();

    @Test
    public void test_withpipelinestagefactory() {
        QueryBuilderBuilder queryBuilderBuilder = new QueryBuilderBuilderImpl();
        queryBuilderBuilder.withPipelineStageFactory(PIPELINE_STAGE_FACTORY);
        StreamQueryBuilder queryBuilder = queryBuilderBuilder.newBuilder();
        assertEquals(PIPELINE_STAGE_FACTORY, extractPipelineStageFactory(queryBuilder));
    }

    @Test
    public void test_withquerypipelinefactory() {
        QueryBuilderBuilder queryBuilderBuilder = new QueryBuilderBuilderImpl();
        queryBuilderBuilder.withQueryPipelineFactory(QUERY_PIPELINE_FACTORY);
        StreamQueryBuilder queryBuilder = queryBuilderBuilder.newBuilder();
        assertEquals(QUERY_PIPELINE_FACTORY, extractQueryPipelineFactory(queryBuilder));
    }

    @Test
    public void test_withqueryoptimizerstrategyfactory() {
        QueryBuilderBuilder queryBuilderBuilder = new QueryBuilderBuilderImpl();
        queryBuilderBuilder.withQueryOptimizerStrategyFactory(QUERY_OPTIMIZER_STRATEGY_FACTORY);
        StreamQueryBuilder queryBuilder = queryBuilderBuilder.newBuilder();
        assertEquals(QUERY_OPTIMIZER_STRATEGY, extractQueryOptimizerStrategy(queryBuilder));
    }

    @Test
    public void test_addqueryoptimizer_single() {
        QueryBuilderBuilder queryBuilderBuilder = new QueryBuilderBuilderImpl();
        queryBuilderBuilder.addQueryOptimizer(QO_1);
        StreamQueryBuilder queryBuilder = queryBuilderBuilder.newBuilder();
        List<QueryOptimizer> queryOptimizers = extractQueryOptimizers(queryBuilder);
        assertEquals(1, queryOptimizers.size());
        assertEquals(QO_1, queryOptimizers.iterator().next());
    }

    @Test
    public void test_addqueryoptimizer_prevent_double_registration() {
        QueryBuilderBuilder queryBuilderBuilder = new QueryBuilderBuilderImpl();
        queryBuilderBuilder.addQueryOptimizer(QO_1);
        queryBuilderBuilder.addQueryOptimizer(QO_1);
        StreamQueryBuilder queryBuilder = queryBuilderBuilder.newBuilder();
        List<QueryOptimizer> queryOptimizers = extractQueryOptimizers(queryBuilder);
        assertEquals(1, queryOptimizers.size());
        assertEquals(QO_1, queryOptimizers.iterator().next());
    }

    @Test
    public void test_addqueryoptimizer_double() {
        QueryBuilderBuilder queryBuilderBuilder = new QueryBuilderBuilderImpl();
        queryBuilderBuilder.addQueryOptimizers(QO_1, QO_2);
        StreamQueryBuilder queryBuilder = queryBuilderBuilder.newBuilder();
        List<QueryOptimizer> queryOptimizers = extractQueryOptimizers(queryBuilder);
        assertEquals(2, queryOptimizers.size());
        Iterator<QueryOptimizer> iterator = queryOptimizers.iterator();
        assertEquals(QO_1, iterator.next());
        assertEquals(QO_2, iterator.next());
    }

    @Test
    public void test_addqueryoptimizer_array() {
        QueryBuilderBuilder queryBuilderBuilder = new QueryBuilderBuilderImpl();
        queryBuilderBuilder.addQueryOptimizers(QO_1, QO_2, QO_3, QO_4);
        StreamQueryBuilder queryBuilder = queryBuilderBuilder.newBuilder();
        List<QueryOptimizer> queryOptimizers = extractQueryOptimizers(queryBuilder);
        assertEquals(4, queryOptimizers.size());
        Iterator<QueryOptimizer> iterator = queryOptimizers.iterator();
        assertEquals(QO_1, iterator.next());
        assertEquals(QO_2, iterator.next());
        assertEquals(QO_3, iterator.next());
        assertEquals(QO_4, iterator.next());
    }

    @Test
    public void test_addqueryoptimizer_iterable() {
        QueryBuilderBuilder queryBuilderBuilder = new QueryBuilderBuilderImpl();
        queryBuilderBuilder.addQueryOptimizers(Stream.of(QO_1, QO_2, QO_3, QO_4).collect(Collectors.toList()));
        StreamQueryBuilder queryBuilder = queryBuilderBuilder.newBuilder();
        List<QueryOptimizer> queryOptimizers = extractQueryOptimizers(queryBuilder);
        assertEquals(4, queryOptimizers.size());
        Iterator<QueryOptimizer> iterator = queryOptimizers.iterator();
        assertEquals(QO_1, iterator.next());
        assertEquals(QO_2, iterator.next());
        assertEquals(QO_3, iterator.next());
        assertEquals(QO_4, iterator.next());
    }

    private List<QueryOptimizer> extractQueryOptimizers(QueryBuilder queryBuilder) {
        try {
            Field field = QueryBuilderImpl.class.getDeclaredField("queryOptimizerStrategy");
            field.setAccessible(true);
            QueryOptimizerStrategy queryOptimizerStrategy = (QueryOptimizerStrategy) field.get(queryBuilder);
            Field field2 = queryOptimizerStrategy.getClass().getDeclaredField("arg$1");
            field2.setAccessible(true);
            return (List<QueryOptimizer>) field2.get(queryOptimizerStrategy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private PipelineStageFactory extractPipelineStageFactory(QueryBuilder queryBuilder) {
        try {
            Field field = QueryBuilderImpl.class.getDeclaredField("pipelineStageFactory");
            field.setAccessible(true);
            return (PipelineStageFactory) field.get(queryBuilder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private QueryPipelineFactory extractQueryPipelineFactory(StreamQueryBuilder queryBuilder) {
        try {
            Field field = QueryBuilderImpl.class.getDeclaredField("queryPipelineFactory");
            field.setAccessible(true);
            return (QueryPipelineFactory) field.get(queryBuilder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private QueryOptimizerStrategy extractQueryOptimizerStrategy(StreamQueryBuilder queryBuilder) {
        try {
            Field field = QueryBuilderImpl.class.getDeclaredField("queryOptimizerStrategy");
            field.setAccessible(true);
            return (QueryOptimizerStrategy) field.get(queryBuilder);
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

    private static class QueryOptimizerStrategyTestImpl
            implements QueryOptimizerStrategy {

        @Override
        public PipelineStage optimizeQuery(PipelineStage rootPipelineStage, PipelineStageFactory pipelineStageFactory) {
            return null;
        }
    }

    private static class QueryOptimizerStrategyFactoryTestImpl
            implements QueryOptimizerStrategyFactory {

        @Override
        public QueryOptimizerStrategy newQueryOptimizerStrategy(List<QueryOptimizer> queryOptimizers) {
            return QUERY_OPTIMIZER_STRATEGY;
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
