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

import com.noctarius.borabora.spi.query.pipeline.PipelineStage;
import com.noctarius.borabora.spi.query.pipeline.PipelineStageFactory;
import com.noctarius.borabora.spi.query.pipeline.QueryStage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.noctarius.borabora.spi.query.pipeline.PipelineStage.NIL;

final class QueryBuilderNode {

    private final List<QueryBuilderNode> children = new ArrayList<>();
    private final QueryStage stage;

    QueryBuilderNode(QueryStage stage) {
        this.stage = stage;
    }

    QueryBuilderNode pushChild(QueryStage stage) {
        QueryBuilderNode child = new QueryBuilderNode(stage);
        children.add(child);
        return child;
    }

    void pushChildNodes(Collection<QueryBuilderNode> childNodes) {
        children.addAll(childNodes);
    }

    int childrenCount() {
        return children.size();
    }

    void forEachChild(Consumer<? super QueryBuilderNode> consumer) {
        Objects.requireNonNull(consumer, "consumer must not be null");
        children.forEach(consumer);
    }

    void clearChildren() {
        children.clear();
    }

    QueryStage stage() {
        return stage;
    }

    @Override
    public String toString() {
        return "QueryBuilderNode{stage=" + stage + ", children=" + children + '}';
    }

    static PipelineStage build(QueryBuilderNode tree, PipelineStageFactory pipelineStageFactory) {
        Objects.requireNonNull(tree, "tree must not be null");
        Objects.requireNonNull(pipelineStageFactory, "pipelineStageFactory must not be null");
        PipelineStage left = NIL;
        PipelineStage right = NIL;

        List<QueryBuilderNode> children = tree.children;
        if (children.size() > 0) {
            left = transform(children, 0, pipelineStageFactory);
        }

        return pipelineStageFactory.newPipelineStage(left, right, tree.stage);
    }

    private static PipelineStage transform(List<QueryBuilderNode> children, int index,
                                           PipelineStageFactory pipelineStageFactory) {

        PipelineStage left = NIL;
        PipelineStage right = NIL;

        QueryStage stage = null;
        if (index < children.size()) {
            right = transform(children, index + 1, pipelineStageFactory);

            QueryBuilderNode treeNode = children.get(index);
            stage = treeNode.stage;
            if (treeNode.children.size() > 0) {
                left = transform(treeNode.children, 0, pipelineStageFactory);
            }
        }

        if (left == NIL && right == NIL && stage == null) {
            return NIL;
        }

        return pipelineStageFactory.newPipelineStage(left, right, stage);
    }
}
