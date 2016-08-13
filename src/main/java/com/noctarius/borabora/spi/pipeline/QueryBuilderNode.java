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
package com.noctarius.borabora.spi.pipeline;

import com.noctarius.borabora.impl.query.stages.QueryStage;
import com.noctarius.borabora.impl.query.BTreePipelineStage;
import com.noctarius.borabora.spi.query.QueryContext;

import java.util.ArrayList;
import java.util.List;

import static com.noctarius.borabora.impl.query.BTreePipelineStage.NIL;

public final class QueryBuilderNode {

    public static final Stage<QueryContext, QueryStage> QUERY_BASE = new QueryStage() {
        @Override
        public VisitResult evaluate(PipelineStage<QueryContext, QueryStage> previousPipelineStage,//
                                    PipelineStage<QueryContext, QueryStage> pipelineStage, //
                                    QueryContext pipelineContext) {

            return pipelineStage.visitChildren(pipelineContext);
        }

        @Override
        public String toString() {
            return "QUERY_BASE";
        }
    };

    private final List<QueryBuilderNode> children = new ArrayList<>();
    private final Stage<QueryContext, QueryStage> stage;

    public QueryBuilderNode(Stage<QueryContext, QueryStage> stage) {
        this.stage = stage;
    }

    public QueryBuilderNode pushChild(Stage<QueryContext, QueryStage> stage) {
        QueryBuilderNode child = new QueryBuilderNode(stage);
        children.add(child);
        return child;
    }

    public int childrenCount() {
        return children.size();
    }

    public List<QueryBuilderNode> children() {
        return children;
    }

    public Stage<QueryContext, QueryStage> stage() {
        return stage;
    }

    @Override
    public String toString() {
        return "QueryBuilderTreeNode{stage=" + stage + ", children=" + children + '}';
    }

    @SuppressWarnings("unchecked")
    public static <PC, T extends Stage<PC, T>> BTreePipelineStage<PC, T> build(QueryBuilderNode tree) {
        BTreePipelineStage<PC, T> left = NIL;
        BTreePipelineStage<PC, T> right = NIL;

        List<QueryBuilderNode> children = tree.children;
        if (children.size() > 0) {
            left = transform(children, 0);
        }

        return new BTreePipelineStage(left, right, tree.stage);
    }

    @SuppressWarnings("unchecked")
    private static <PC, T extends Stage<PC, T>> BTreePipelineStage<PC, T> transform(List<QueryBuilderNode> children,
                                                                                    int index) {
        BTreePipelineStage<PC, T> left = NIL;
        BTreePipelineStage<PC, T> right = NIL;

        Stage<QueryContext, QueryStage> stage = null;
        if (index < children.size()) {
            right = transform(children, index + 1);

            QueryBuilderNode treeNode = children.get(index);
            stage = treeNode.stage;
            if (treeNode.children.size() > 0) {
                left = transform(treeNode.children, 0);
            }
        }

        if (left == NIL && right == NIL && stage == null) {
            return NIL;
        }

        return new BTreePipelineStage(left, right, stage);
    }
}
