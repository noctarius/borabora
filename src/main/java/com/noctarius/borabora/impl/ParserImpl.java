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
import com.noctarius.borabora.Query;
import com.noctarius.borabora.QueryParserException;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.builder.QueryBuilder;
import com.noctarius.borabora.spi.Constants;
import com.noctarius.borabora.spi.codec.Decoder;
import com.noctarius.borabora.spi.codec.TagStrategy;
import com.noctarius.borabora.spi.pipeline.PipelineStageFactory;
import com.noctarius.borabora.spi.pipeline.QueryOptimizer;
import com.noctarius.borabora.spi.pipeline.QueryOptimizerStrategyFactory;
import com.noctarius.borabora.spi.pipeline.QueryPipeline;
import com.noctarius.borabora.spi.pipeline.QueryPipelineFactory;
import com.noctarius.borabora.spi.query.ProjectionStrategy;
import com.noctarius.borabora.spi.query.ProjectionStrategyAware;
import com.noctarius.borabora.spi.query.QueryConsumer;
import com.noctarius.borabora.spi.query.QueryContext;
import com.noctarius.borabora.spi.query.QueryContextFactory;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

final class ParserImpl
        implements Parser {

    private final List<TagStrategy> tagStrategies;
    private final QueryContextFactory queryContextFactory;
    private final ProjectionStrategy projectionStrategy;
    private final QueryPipelineFactory queryPipelineFactory;
    private final PipelineStageFactory pipelineStageFactory;
    private final QueryOptimizerStrategyFactory queryOptimizerStrategyFactory;
    private final List<QueryOptimizer> queryOptimizers;

    ParserImpl(List<TagStrategy> tagStrategies, ProjectionStrategy projectionStrategy, QueryContextFactory queryContextFactory,
               QueryPipelineFactory queryPipelineFactory, PipelineStageFactory pipelineStageFactory,
               QueryOptimizerStrategyFactory queryOptimizerStrategyFactory, List<QueryOptimizer> queryOptimizers) {

        Objects.requireNonNull(tagStrategies, "tagStrategies must not be null");
        Objects.requireNonNull(queryContextFactory, "queryContextFactory must not be null");
        Objects.requireNonNull(projectionStrategy, "projectionStrategy must not be null");
        Objects.requireNonNull(queryPipelineFactory, "queryPipelineFactory must not be null");
        Objects.requireNonNull(pipelineStageFactory, "pipelineStageFactory must not be null");
        Objects.requireNonNull(queryOptimizerStrategyFactory, "queryOptimizerStrategyFactory must not be null");
        Objects.requireNonNull(queryOptimizers, "queryOptimizers must not be null");
        this.tagStrategies = tagStrategies;
        this.queryContextFactory = queryContextFactory;
        this.projectionStrategy = projectionStrategy;
        this.queryPipelineFactory = queryPipelineFactory;
        this.pipelineStageFactory = pipelineStageFactory;
        this.queryOptimizerStrategyFactory = queryOptimizerStrategyFactory;
        this.queryOptimizers = queryOptimizers;
    }

    @Override
    public Value read(Input input, Query query) {
        SingleConsumer consumer = new SingleConsumer();
        read(input, query, consumer, false);
        return consumer.value == null ? Value.NULL_VALUE : consumer.value;
    }

    @Override
    public Value read(Input input, String query) {
        Objects.requireNonNull(input, "input must not be null");
        Objects.requireNonNull(query, "query must not be null");
        // #{'b'}(1)->?number
        // \#([0-9]+)? <- stream identifier and optional index, if no index defined then index=-1
        // \{(\'[^\}]+\')\} <- dictionary identifier and key spec
        // \(([0-9]+)\) <- sequence identifier and sequence index
        // ->(\?)?(.+){1} <- expected result type, if ? is defined and type does not match result=null, otherwise exception

        return read(input, prepareQuery(query));
    }

    @Override
    public Value read(Input input, long offset) {
        Objects.requireNonNull(input, "input must not be null");
        QueryContext queryContext = newQueryContext(input, Constants.EMPTY_QUERY_CONSUMER, projectionStrategy);
        return Decoder.readValue(offset, queryContext);
    }

    @Override
    public void read(Input input, Query query, Consumer<Value> consumer) {
        read(input, query, consumer, true);
    }

    @Override
    public void read(Input input, String query, Consumer<Value> consumer) {
        Objects.requireNonNull(input, "input must not be null");
        Objects.requireNonNull(query, "query must not be null");
        Objects.requireNonNull(consumer, "consumer must not be null");
        read(input, prepareQuery(query), consumer, true);
    }

    @Override
    public byte[] extract(Input input, Query query) {
        Objects.requireNonNull(input, "input must not be null");
        Objects.requireNonNull(query, "query must not be null");
        Value value = read(input, query);
        return value == null ? Constants.EMPTY_BYTE_ARRAY : value.raw();
    }

    @Override
    public byte[] extract(Input input, String query) {
        Objects.requireNonNull(input, "input must not be null");
        Objects.requireNonNull(query, "query must not be null");
        return extract(input, prepareQuery(query));
    }

    @Override
    public byte[] extract(Input input, long offset) {
        Objects.requireNonNull(input, "input must not be null");
        return read(input, offset).raw();
    }

    @Override
    public Query prepareQuery(String query) {
        Objects.requireNonNull(query, "query must not be null");
        try {
            QueryBuilder queryBuilder = Query.configureBuilder().withProjectionStrategy(projectionStrategy)
                                             .withPipelineStageFactory(pipelineStageFactory)
                                             .withQueryPipelineFactory(queryPipelineFactory)
                                             .withQueryOptimizerStrategyFactory(queryOptimizerStrategyFactory)
                                             .addQueryOptimizers(queryOptimizers).newBuilder();
            QueryParser.parse(query, queryBuilder, tagStrategies);
            return queryBuilder.build();

        } catch (Exception | TokenMgrError e) {
            throw new QueryParserException(e);
        }
    }

    private void read(Input input, Query query, Consumer<Value> consumer, boolean multiConsumer) {
        Objects.requireNonNull(input, "input must not be null");
        Objects.requireNonNull(query, "query must not be null");
        Objects.requireNonNull(consumer, "consumer must not be null");
        ProjectionStrategy projectionStrategy = this.projectionStrategy;
        if (query instanceof ProjectionStrategyAware) {
            projectionStrategy = ((ProjectionStrategyAware) query).projectionStrategy();
        }

        QueryConsumer queryConsumer = bridgeConsumer(consumer, multiConsumer);
        evaluate(query, input, queryConsumer, projectionStrategy);
    }

    private QueryContext newQueryContext(Input input, QueryConsumer queryConsumer, ProjectionStrategy projectionStrategy) {
        return queryContextFactory.newQueryContext(input, queryConsumer, tagStrategies, projectionStrategy);
    }

    private void evaluate(Query query, Input input, QueryConsumer queryConsumer, ProjectionStrategy projectionStrategy) {
        QueryPipeline queryPipeline = query.newQueryPipeline();
        QueryContext queryContext = newQueryContext(input, queryConsumer, projectionStrategy);

        queryPipeline.evaluate(queryContext);
    }

    private QueryConsumer bridgeConsumer(Consumer<Value> consumer, boolean multiConsumer) {
        return (value) -> {
            consumer.accept(value);
            return multiConsumer;
        };
    }

    private static class SingleConsumer
            implements Consumer<Value> {

        private Value value;

        @Override
        public void accept(Value value) {
            if (this.value != null) {
                throw new IllegalStateException("value already set");
            }
            this.value = value;
        }
    }

}
