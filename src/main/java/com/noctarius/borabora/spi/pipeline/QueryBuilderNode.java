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

import com.noctarius.borabora.spi.query.QueryContext;

import java.util.ArrayList;
import java.util.List;

import static com.noctarius.borabora.spi.pipeline.PipelineStage.NIL;

public final class QueryBuilderNode {

    public static final QueryStage QUERY_BASE = new QueryStage() {
        @Override
        public VisitResult evaluate(PipelineStage previousPipelineStage,//
                                    PipelineStage pipelineStage, //
                                    QueryContext pipelineContext) {

            return pipelineStage.visitChildren(pipelineContext);
        }

        @Override
        public String toString() {
            return "QUERY_BASE";
        }
    };

    private final List<QueryBuilderNode> children = new ArrayList<>();
    private final QueryStage stage;

    public QueryBuilderNode(QueryStage stage) {
        this.stage = stage;
    }

    public QueryBuilderNode pushChild(QueryStage stage) {
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

    public QueryStage stage() {
        return stage;
    }

    @Override
    public String toString() {
        return "QueryBuilderTreeNode{stage=" + stage + ", children=" + children + '}';
    }

    public static PipelineStage build(QueryBuilderNode tree, PipelineStageFactory pipelineStageFactory) {

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
