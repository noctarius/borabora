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

import com.noctarius.borabora.Query;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.builder.query.DictionaryQueryBuilder;
import com.noctarius.borabora.builder.query.QueryBuilder;
import com.noctarius.borabora.builder.query.SequenceQueryBuilder;
import com.noctarius.borabora.builder.query.StreamQueryBuilder;
import com.noctarius.borabora.impl.query.QueryImpl;
import com.noctarius.borabora.impl.query.stages.AsDictionarySelectorQueryStage;
import com.noctarius.borabora.impl.query.stages.AsSequenceSelectorQueryStage;
import com.noctarius.borabora.impl.query.stages.ConsumeSelectedQueryStage;
import com.noctarius.borabora.impl.query.stages.ConsumerQueryStage;
import com.noctarius.borabora.impl.query.stages.MultiStreamElementQueryStage;
import com.noctarius.borabora.impl.query.stages.PrepareSelectionQueryStage;
import com.noctarius.borabora.impl.query.stages.SingleStreamElementQueryStage;
import com.noctarius.borabora.spi.query.TypeSpec;
import com.noctarius.borabora.spi.query.optimizer.QueryOptimizerStrategy;
import com.noctarius.borabora.spi.query.pipeline.PipelineStageFactory;
import com.noctarius.borabora.spi.query.pipeline.QueryBuilderNode;
import com.noctarius.borabora.spi.query.pipeline.QueryPipeline;
import com.noctarius.borabora.spi.query.pipeline.QueryPipelineFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static com.noctarius.borabora.spi.query.pipeline.QueryStage.QUERY_BASE;

final class QueryBuilderImpl
        extends AbstractQueryBuilder
        implements StreamQueryBuilder {

    private final PipelineStageFactory pipelineStageFactory;
    private final QueryPipelineFactory queryPipelineFactory;
    private final QueryOptimizerStrategy queryOptimizerStrategy;

    QueryBuilderImpl(QueryOptimizerStrategy queryOptimizerStrategy, PipelineStageFactory pipelineStageFactory,
                     QueryPipelineFactory queryPipelineFactory) {

        super(new QueryBuilderNode(QUERY_BASE));
        Objects.requireNonNull(queryOptimizerStrategy, "queryOptimizerStrategy must not be null");
        Objects.requireNonNull(pipelineStageFactory, "pipelineStageFactory must not be null");
        Objects.requireNonNull(queryPipelineFactory, "queryPipelineFactory must not be null");
        this.queryOptimizerStrategy = queryOptimizerStrategy;
        this.pipelineStageFactory = pipelineStageFactory;
        this.queryPipelineFactory = queryPipelineFactory;
    }

    @Override
    public QueryBuilder stream(long streamIndex) {
        Tracer.traceInfo("QueryBuilderImpl#stream", this);
        if (streamIndex < 0) {
            throw new IllegalArgumentException("streamIndex must not be negative");
        }
        currentTreeNode = currentTreeNode.pushChild(new SingleStreamElementQueryStage(streamIndex));
        return this;
    }

    @Override
    public QueryBuilder multiStream() {
        Tracer.traceInfo("QueryBuilderImpl#multiStream", this);
        currentTreeNode = currentTreeNode.pushChild(MultiStreamElementQueryStage.INSTANCE);
        return this;
    }

    @Override
    public DictionaryQueryBuilder<QueryBuilder> asDictionary() {
        Tracer.traceCall("QueryBuilderImpl#asDictionary", this);
        currentTreeNode = currentTreeNode.pushChild(PrepareSelectionQueryStage.INSTANCE);
        QueryBuilderNode newNode = currentTreeNode.pushChild(AsDictionarySelectorQueryStage.INSTANCE);
        currentTreeNode.pushChild(ConsumeSelectedQueryStage.INSTANCE);
        return new DictionaryQueryBuilderImpl<>(this, newNode);
    }

    @Override
    public SequenceQueryBuilder<QueryBuilder> asSequence() {
        Tracer.traceCall("QueryBuilderImpl#asSequence", this);
        currentTreeNode = currentTreeNode.pushChild(PrepareSelectionQueryStage.INSTANCE);
        QueryBuilderNode newNode = currentTreeNode.pushChild(AsSequenceSelectorQueryStage.INSTANCE);
        currentTreeNode.pushChild(ConsumeSelectedQueryStage.INSTANCE);
        return new SequenceQueryBuilderImpl<>(this, newNode);
    }

    @Override
    public Query build() {
        // Fix basic queries without any special access pattern
        fixQueryStartup(parentTreeNode);

        // Add consumers at the end of any left edge
        fixConsumers(parentTreeNode);

        // Fix selector multiple consumers
        fixSelectorConsumers(parentTreeNode);

        // Build query pipeline with optimized query execution plan
        QueryPipeline queryPipeline = queryPipelineFactory
                .newQueryPipeline(parentTreeNode, pipelineStageFactory, queryOptimizerStrategy);

        return new QueryImpl(queryPipeline);
    }

    @Override
    public QueryBuilder sequence(long index) {
        Tracer.traceInfo("QueryBuilderImpl#sequence", this);
        sequence0(index);
        return this;
    }

    @Override
    public QueryBuilder sequenceMatch(Predicate<Value> predicate) {
        Tracer.traceInfo("QueryBuilderImpl#sequenceMatch", this);
        Objects.requireNonNull(predicate, "predicate must not be null");
        sequenceMatch0(predicate);
        return this;
    }

    @Override
    public QueryBuilder dictionary(Predicate<Value> predicate) {
        Tracer.traceInfo("QueryBuilderImpl#dictionary", this);
        Objects.requireNonNull(predicate, "predicate must not be null");
        dictionary0(predicate);
        return this;
    }

    @Override
    public QueryBuilder dictionary(String key) {
        Tracer.traceInfo("QueryBuilderImpl#dictionary", this);
        Objects.requireNonNull(key, "key must not be null");
        dictionary0(key);
        return this;
    }

    @Override
    public QueryBuilder dictionary(double key) {
        Tracer.traceInfo("QueryBuilderImpl#dictionary", this);
        dictionary0(key);
        return this;
    }

    @Override
    public QueryBuilder dictionary(long key) {
        Tracer.traceInfo("QueryBuilderImpl#dictionary", this);
        dictionary0(key);
        return this;
    }

    @Override
    public QueryBuilder nullOrType(TypeSpec typeSpec) {
        Tracer.traceInfo("QueryBuilderImpl#nullOrType", this);
        Objects.requireNonNull(typeSpec, "typeSpec must not be null");
        nullOrType0(typeSpec);
        return this;
    }

    @Override
    public QueryBuilder requireType(TypeSpec typeSpec) {
        Tracer.traceInfo("QueryBuilderImpl#requireType", this);
        Objects.requireNonNull(typeSpec, "typeSpec must not be null");
        requireType0(typeSpec);
        return this;
    }

    private void fixSelectorConsumers(QueryBuilderNode node) {
        if (node.childrenCount() > 0) {
            node.forEachChild(this::fixSelectorConsumers);
        }

        List<QueryBuilderNode> newChildren = new ArrayList<>();
        Iterator<QueryBuilderNode> iterator = node.childIterator();
        while (iterator.hasNext()) {
            QueryBuilderNode child = iterator.next();
            if (child.stage() instanceof ConsumerQueryStage && child.childrenCount() > 0) {
                iterator.remove();
                child.forEachChild(newChildren::add);
            }
        }
        node.pushChildNodes(newChildren);
    }

    private void fixConsumers(QueryBuilderNode node) {
        if (node.childrenCount() > 0) {
            node.forEachChild(this::fixConsumers);
        } else {
            if (!(node.stage() instanceof ConsumerQueryStage)) {
                node.pushChild(ConsumerQueryStage.INSTANCE);
            }
        }
    }

    private void fixQueryStartup(QueryBuilderNode parentTreeNode) {
        if (parentTreeNode.childrenCount() == 0) {
            parentTreeNode.pushChild(new SingleStreamElementQueryStage(0));
        } else {
            // Copy old children to inject a new hierarchy level
            List<QueryBuilderNode> children = new ArrayList<>();
            parentTreeNode.forEachChild(children::add);

            QueryBuilderNode node = children.get(0);
            if (!(node.stage() instanceof SingleStreamElementQueryStage) && //
                    !(node.stage() instanceof MultiStreamElementQueryStage)) {

                parentTreeNode.clearChildren();
                QueryBuilderNode newNode = parentTreeNode.pushChild(new SingleStreamElementQueryStage(0));
                newNode.pushChildNodes(children);
            }
        }
    }

}
