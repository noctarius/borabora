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

import com.noctarius.borabora.Parser;
import com.noctarius.borabora.builder.ParserBuilder;
import com.noctarius.borabora.impl.query.BTreeFactories;
import com.noctarius.borabora.spi.codec.TagStrategies;
import com.noctarius.borabora.spi.codec.TagStrategy;
import com.noctarius.borabora.spi.pipeline.PipelineStageFactory;
import com.noctarius.borabora.spi.pipeline.QueryOptimizer;
import com.noctarius.borabora.spi.pipeline.QueryOptimizerStrategyFactory;
import com.noctarius.borabora.spi.pipeline.QueryPipelineFactory;
import com.noctarius.borabora.spi.query.BinaryProjectionStrategy;
import com.noctarius.borabora.spi.query.ObjectProjectionStrategy;
import com.noctarius.borabora.spi.query.QueryContextFactory;
import com.noctarius.borabora.spi.query.ProjectionStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class ParserBuilderImpl
        implements ParserBuilder {

    private final List<TagStrategy> tagStrategies = new ArrayList<>(Arrays.asList(TagStrategies.values()));
    private final List<QueryOptimizer> queryOptimizers = new ArrayList<>();

    private QueryContextFactory queryContextFactory = DefaultQueryContextFactory.INSTANCE;
    private ProjectionStrategy projectionStrategy = BinaryProjectionStrategy.INSTANCE;
    private PipelineStageFactory pipelineStageFactory = BTreeFactories.newPipelineStageFactory();
    private QueryPipelineFactory queryPipelineFactory = BTreeFactories.newQueryPipelineFactory();
    private QueryOptimizerStrategyFactory queryOptimizerStrategyFactory = BTreeFactories.newQueryOptimizerStrategyFactory();

    @Override
    public <S, V> ParserBuilder addTagStrategy(TagStrategy<S, V> tagStrategy) {
        Objects.requireNonNull(tagStrategy, "tagStrategy must not be null");
        if (!tagStrategies.contains(tagStrategy)) {
            tagStrategies.add(tagStrategy);
        }
        return this;
    }

    @Override
    public ParserBuilder addTagStrategies(TagStrategy tagStrategy1, TagStrategy tagStrategy2) {
        addTagStrategy(tagStrategy1);
        addTagStrategy(tagStrategy2);
        return this;
    }

    @Override
    public ParserBuilder addTagStrategies(TagStrategy tagStrategy1, TagStrategy tagStrategy2, TagStrategy... tagStrategies) {
        addTagStrategy(tagStrategy1);
        addTagStrategy(tagStrategy2);
        for (TagStrategy tagStrategy : tagStrategies) {
            addTagStrategy(tagStrategy);
        }
        return this;
    }

    @Override
    public ParserBuilder addTagStrategies(Iterable<TagStrategy> tagStrategies) {
        for (TagStrategy tagStrategy : tagStrategies) {
            addTagStrategy(tagStrategy);
        }
        return this;
    }

    @Override
    public ParserBuilder withProjectionStrategy(ProjectionStrategy projectionStrategy) {
        Objects.requireNonNull(projectionStrategy, "projectionStrategy must not be null");
        this.projectionStrategy = projectionStrategy;
        return this;
    }

    @Override
    public ParserBuilder withQueryContextFactory(QueryContextFactory queryContextFactory) {
        Objects.requireNonNull(queryContextFactory, "queryContextFactory must not be null");
        this.queryContextFactory = queryContextFactory;
        return this;
    }

    @Override
    public ParserBuilder withQueryPipelineFactory(QueryPipelineFactory queryPipelineFactory) {
        Objects.requireNonNull(queryPipelineFactory, "queryPipelineFactory must not be null");
        this.queryPipelineFactory = queryPipelineFactory;
        return this;
    }

    @Override
    public ParserBuilder withPipelineStageFactory(PipelineStageFactory pipelineStageFactory) {
        Objects.requireNonNull(pipelineStageFactory, "pipelineStageFactory must not be null");
        this.pipelineStageFactory = pipelineStageFactory;
        return this;
    }

    @Override
    public ParserBuilder withQueryOptimizerStrategyFactory(QueryOptimizerStrategyFactory queryOptimizerStrategyFactory) {
        Objects.requireNonNull(queryOptimizerStrategyFactory, "queryOptimizerStrategyFactory must not be null");
        this.queryOptimizerStrategyFactory = queryOptimizerStrategyFactory;
        return this;
    }

    @Override
    public ParserBuilder addQueryOptimizer(QueryOptimizer queryOptimizer) {
        Objects.requireNonNull(queryOptimizer, "queryOptimizer must not be null");
        if (!this.queryOptimizers.contains(queryOptimizer)) {
            this.queryOptimizers.add(queryOptimizer);
        }
        return this;
    }

    @Override
    public ParserBuilder addQueryOptimizers(QueryOptimizer queryOptimizer1, QueryOptimizer queryOptimizer2) {
        addQueryOptimizer(queryOptimizer1);
        addQueryOptimizer(queryOptimizer2);
        return this;
    }

    @Override
    public ParserBuilder addQueryOptimizers(QueryOptimizer queryOptimizer1, QueryOptimizer queryOptimizer2,
                                            QueryOptimizer... queryOptimizers) {

        addQueryOptimizer(queryOptimizer1);
        addQueryOptimizer(queryOptimizer2);
        for (QueryOptimizer queryOptimizer : queryOptimizers) {
            addQueryOptimizer(queryOptimizer);
        }
        return this;
    }

    @Override
    public ParserBuilder addQueryOptimizers(Iterable<QueryOptimizer> queryOptimizers) {
        for (QueryOptimizer queryOptimizer : queryOptimizers) {
            addQueryOptimizer(queryOptimizer);
        }
        return this;
    }

    @Override
    public ParserBuilder asBinaryProjectionStrategy() {
        projectionStrategy = BinaryProjectionStrategy.INSTANCE;
        return this;
    }

    @Override
    public ParserBuilder asObjectProjectionStrategy() {
        projectionStrategy = ObjectProjectionStrategy.INSTANCE;
        return this;
    }

    @Override
    public Parser build() {
        return new ParserImpl(tagStrategies, projectionStrategy, queryContextFactory, queryPipelineFactory,
                pipelineStageFactory, queryOptimizerStrategyFactory, Collections.unmodifiableList(queryOptimizers));
    }

}
