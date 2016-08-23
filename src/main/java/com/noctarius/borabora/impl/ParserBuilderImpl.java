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
import com.noctarius.borabora.spi.codec.CommonTagCodec;
import com.noctarius.borabora.spi.codec.TagDecoder;
import com.noctarius.borabora.spi.pipeline.PipelineStageFactory;
import com.noctarius.borabora.spi.pipeline.QueryOptimizer;
import com.noctarius.borabora.spi.pipeline.QueryOptimizerStrategyFactory;
import com.noctarius.borabora.spi.pipeline.QueryPipelineFactory;
import com.noctarius.borabora.spi.query.BinarySelectStatementStrategy;
import com.noctarius.borabora.spi.query.ObjectSelectStatementStrategy;
import com.noctarius.borabora.spi.query.QueryContextFactory;
import com.noctarius.borabora.spi.query.SelectStatementStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class ParserBuilderImpl
        implements ParserBuilder {

    private final List<TagDecoder> tagDecoders = new ArrayList<>();
    private final List<QueryOptimizer> queryOptimizers = new ArrayList<>();

    private QueryContextFactory queryContextFactory = DefaultQueryContextFactory.INSTANCE;
    private SelectStatementStrategy selectStatementStrategy = BinarySelectStatementStrategy.INSTANCE;
    private PipelineStageFactory pipelineStageFactory = BTreeFactories.newPipelineStageFactory();
    private QueryPipelineFactory queryPipelineFactory = BTreeFactories.newQueryPipelineFactory();
    private QueryOptimizerStrategyFactory queryOptimizerStrategyFactory = BTreeFactories.newQueryOptimizerStrategyFactory();

    public ParserBuilderImpl() {
        addTagDecoder(CommonTagCodec.INSTANCE);
    }

    @Override
    public <V> ParserBuilder addTagDecoder(TagDecoder<V> tagDecoder) {
        Objects.requireNonNull(tagDecoder, "tagDecoder must not be null");
        if (!tagDecoders.contains(tagDecoder)) {
            tagDecoders.add(tagDecoder);
        }
        return this;
    }

    @Override
    public <V> ParserBuilder addTagDecoders(TagDecoder<V> tagDecoder1, TagDecoder<V> tagDecoder2) {
        addTagDecoder(tagDecoder1);
        addTagDecoder(tagDecoder2);
        return this;
    }

    @Override
    public <V> ParserBuilder addTagDecoders(TagDecoder<V> tagDecoder1, TagDecoder<V> tagDecoder2, TagDecoder... tagDecoders) {
        addTagDecoder(tagDecoder1);
        addTagDecoder(tagDecoder2);
        for (TagDecoder tagDecoder : tagDecoders) {
            addTagDecoder(tagDecoder);
        }
        return this;
    }

    @Override
    public ParserBuilder addTagDecoders(Iterable<TagDecoder> tagDecoders) {
        for (TagDecoder tagDecoder : tagDecoders) {
            addTagDecoder(tagDecoder);
        }
        return this;
    }

    @Override
    public ParserBuilder withSelectStatementStrategy(SelectStatementStrategy selectStatementStrategy) {
        Objects.requireNonNull(selectStatementStrategy, "selectStatementStrategy must not be null");
        this.selectStatementStrategy = selectStatementStrategy;
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
    public ParserBuilder asBinarySelectStatementStrategy() {
        selectStatementStrategy = BinarySelectStatementStrategy.INSTANCE;
        return this;
    }

    @Override
    public ParserBuilder asObjectSelectStatementStrategy() {
        selectStatementStrategy = ObjectSelectStatementStrategy.INSTANCE;
        return this;
    }

    @Override
    public Parser build() {
        return new ParserImpl(tagDecoders, selectStatementStrategy, queryContextFactory, queryPipelineFactory,
                pipelineStageFactory, queryOptimizerStrategyFactory, Collections.unmodifiableList(queryOptimizers));
    }

}
