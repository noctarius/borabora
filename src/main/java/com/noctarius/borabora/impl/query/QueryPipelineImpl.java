/*
 * Copyright (c) 2016-2018, Christoph Engelbert (aka noctarius) and
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
package com.noctarius.borabora.impl.query;

import com.noctarius.borabora.spi.query.QueryContext;
import com.noctarius.borabora.spi.query.pipeline.PipelineStage;
import com.noctarius.borabora.spi.query.pipeline.QueryPipeline;

import java.util.Objects;

import static com.noctarius.borabora.spi.query.pipeline.PipelineStage.NIL;

class QueryPipelineImpl
        implements QueryPipeline {

    private final PipelineStage rootPipelineStage;

    QueryPipelineImpl(PipelineStage rootPipelineStage) {
        Objects.requireNonNull(rootPipelineStage, "rootPipelineStage must not be null");
        this.rootPipelineStage = rootPipelineStage;
    }

    @Override
    public void evaluate(QueryContext queryContext) {
        rootPipelineStage.visit(NIL, queryContext);
    }

    @Override
    public String printQueryGraph() {
        return PipelineStagePrinter.printTree(rootPipelineStage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QueryPipelineImpl)) {
            return false;
        }

        QueryPipelineImpl that = (QueryPipelineImpl) o;

        return rootPipelineStage.equals(that.rootPipelineStage);
    }

    @Override
    public int hashCode() {
        return rootPipelineStage.hashCode();
    }

    @Override
    public String toString() {
        return "QueryPipelineImpl{" + "rootPipelineStage=" + rootPipelineStage + '}';
    }

}
