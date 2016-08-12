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
package com.noctarius.borabora;

import com.noctarius.borabora.builder.DictionaryQueryBuilder;
import com.noctarius.borabora.builder.QueryBuilder;
import com.noctarius.borabora.builder.SequenceQueryBuilder;
import com.noctarius.borabora.builder.StreamQueryBuilder;
import com.noctarius.borabora.impl.query.QueryImpl;
import com.noctarius.borabora.impl.query.QueryPipelineImpl;
import com.noctarius.borabora.impl.query.stages.AsDictionarySelectorQueryStage;
import com.noctarius.borabora.impl.query.stages.AsSequenceSelectorQueryStage;
import com.noctarius.borabora.impl.query.stages.ConsumeSelectedQueryStage;
import com.noctarius.borabora.impl.query.stages.ConsumerQueryStage;
import com.noctarius.borabora.impl.query.stages.MultiStreamElementQueryStage;
import com.noctarius.borabora.impl.query.stages.PrepareSelectionQueryStage;
import com.noctarius.borabora.impl.query.stages.QueryStage;
import com.noctarius.borabora.impl.query.stages.SingleStreamElementQueryStage;
import com.noctarius.borabora.spi.QueryBuilderTreeNode;
import com.noctarius.borabora.spi.QueryContext;
import com.noctarius.borabora.spi.pipeline.QueryOptimizer;
import com.noctarius.borabora.spi.SelectStatementStrategy;
import com.noctarius.borabora.spi.TypeSpec;
import com.noctarius.borabora.spi.pipeline.PipelineStage;
import com.noctarius.borabora.spi.pipeline.QueryPipeline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import static com.noctarius.borabora.spi.QueryBuilderTreeNode.QUERY_BASE;

final class QueryBuilderImpl
        extends AbstractQueryBuilder
        implements StreamQueryBuilder {

    private final List<QueryOptimizer> queryOptimizers;

    QueryBuilderImpl(SelectStatementStrategy selectStatementStrategy, List<QueryOptimizer> queryOptimizers) {
        super(new QueryBuilderTreeNode(QUERY_BASE), selectStatementStrategy);
        this.queryOptimizers = queryOptimizers;
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
        QueryBuilderTreeNode newNode = currentTreeNode.pushChild(AsDictionarySelectorQueryStage.INSTANCE);
        currentTreeNode.pushChild(ConsumeSelectedQueryStage.INSTANCE);
        return new DictionaryQueryBuilderImpl<>(this, newNode, selectStatementStrategy);
    }

    @Override
    public SequenceQueryBuilder<QueryBuilder> asSequence() {
        Tracer.traceCall("QueryBuilderImpl#asSequence", this);
        currentTreeNode = currentTreeNode.pushChild(PrepareSelectionQueryStage.INSTANCE);
        QueryBuilderTreeNode newNode = currentTreeNode.pushChild(AsSequenceSelectorQueryStage.INSTANCE);
        currentTreeNode.pushChild(ConsumeSelectedQueryStage.INSTANCE);
        return new SequenceQueryBuilderImpl<>(this, newNode, selectStatementStrategy);
    }

    @Override
    public Query build() {
        // Fix basic queries without any special access pattern
        fixQueryStartup(parentTreeNode);

        // Add consumers at the end of any left edge
        fixConsumers(parentTreeNode);

        // Fix selector multiple consumers
        fixSelectorConsumers(parentTreeNode);

        // Build LCRS query plan
        PipelineStage<QueryContext, QueryStage> rootStage = QueryBuilderTreeNode.build(parentTreeNode);

        // Apply query optimizers
        PipelineStage<QueryContext, QueryStage> optimizedRootStage = rootStage;
        for (QueryOptimizer queryOptimizer : queryOptimizers) {
            if (queryOptimizer.handles(optimizedRootStage)) {
                optimizedRootStage = queryOptimizer.optimize(optimizedRootStage);
            }
        }

        // Build query pipeline
        QueryPipeline<QueryContext> queryPipeline = new QueryPipelineImpl(optimizedRootStage);

        return new QueryImpl(queryPipeline, selectStatementStrategy);
    }

    private void fixSelectorConsumers(QueryBuilderTreeNode node) {
        if (node.childrenCount() > 0) {
            node.children().forEach(this::fixSelectorConsumers);
        }

        List<QueryBuilderTreeNode> newChildren = new ArrayList<>();
        Iterator<QueryBuilderTreeNode> iterator = node.children().iterator();
        while (iterator.hasNext()) {
            QueryBuilderTreeNode child = iterator.next();
            if (child.stage() instanceof ConsumerQueryStage && child.childrenCount() > 0) {
                iterator.remove();
                newChildren.addAll(child.children());
            }
        }
        node.children().addAll(newChildren);
    }

    private void fixConsumers(QueryBuilderTreeNode node) {
        if (node.childrenCount() > 0) {
            node.children().forEach(this::fixConsumers);
        } else {
            if (!(node.stage() instanceof ConsumerQueryStage)) {
                node.children().add(new QueryBuilderTreeNode(ConsumerQueryStage.INSTANCE));
            }
        }
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
        sequenceMatch0(predicate);
        return this;
    }

    @Override
    public QueryBuilder dictionary(Predicate<Value> predicate) {
        Tracer.traceInfo("QueryBuilderImpl#dictionary", this);
        dictionary0(predicate);
        return this;
    }

    @Override
    public QueryBuilder dictionary(String key) {
        Tracer.traceInfo("QueryBuilderImpl#dictionary", this);
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
        nullOrType0(typeSpec);
        return this;
    }

    @Override
    public QueryBuilder requireType(TypeSpec typeSpec) {
        Tracer.traceInfo("QueryBuilderImpl#requireType", this);
        requireType0(typeSpec);
        return this;
    }

    private void fixQueryStartup(QueryBuilderTreeNode parentTreeNode) {
        if (parentTreeNode.childrenCount() == 0) {
            parentTreeNode.pushChild(new SingleStreamElementQueryStage(0));
        } else {
            List<QueryBuilderTreeNode> children = parentTreeNode.children();
            QueryBuilderTreeNode node = children.get(0);
            if (!(node.stage() instanceof SingleStreamElementQueryStage) && //
                    !(node.stage() instanceof MultiStreamElementQueryStage)) {

                QueryBuilderTreeNode newNode = new QueryBuilderTreeNode(new SingleStreamElementQueryStage(0));
                newNode.children().addAll(children);
                children.clear();
                children.add(newNode);
            }
        }
    }

}
