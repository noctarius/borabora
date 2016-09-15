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
package com.noctarius.borabora.spi.query.pipeline;

import com.noctarius.borabora.spi.query.QueryContext;

/**
 * The <tt>QueryPipeline</tt> interface defines the actual query execution plan designed as an binary
 * tree pipeline.
 */
public interface QueryPipeline {

    /**
     * Evaluates the bound {@link PipelineStage}s. Evaluation is controlled by the different
     * {@link VisitResult} return values coming back from
     * {@link PipelineStage#visit(PipelineStage, QueryContext)} and
     * {@link PipelineStage#visitChildren(QueryContext)}.
     *
     * @param queryContext the current query context
     */
    void evaluate(QueryContext queryContext);

    /**
     * Returns the current execution plan as a binary tree diagram based on the internally bound
     * pipeline stages.
     *
     * @return the execution plan as a binary graph
     */
    String printQueryGraph();

}
