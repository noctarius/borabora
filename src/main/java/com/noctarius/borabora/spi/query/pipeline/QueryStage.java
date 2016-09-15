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

import com.noctarius.borabora.impl.query.stages.BaseQueryStage;
import com.noctarius.borabora.spi.query.QueryContext;

/**
 * The <tt>QueryStage</tt> interface defines the actual execution part of a {@link PipelineStage}s. A
 * <tt>QueryStage</tt> can be either a shared instance if non properties are bound or bound instances
 * can be reused. All instances, however, are considered to be fully thread-safe for concurrent access
 * from multiple threads since the whole query pipeline is designed to be that way. Building a non
 * multi-threading stage implementation will break the overall assumption given to users and will result
 * in unexpected behavior to users.
 */
public interface QueryStage {

    /**
     * The common base for all queries. It handles the special magic semantic tag id 55799 which marks
     * the following stream of a CBOR encoded data stream and automatically unwraps it. It does not
     * handle the semantic tag 24 (which is CBOR encoded data item).
     */
    QueryStage QUERY_BASE = BaseQueryStage.INSTANCE;

    /**
     * Evaluates the current stage with the given <tt>previousPipelineStage</tt> which can either be
     * the previous sibling or the parent stage that was previously executed.
     *
     * @param previousPipelineStage the previously executed sibling or parent pipeline stage
     * @param pipelineStage         the current pipeline stage
     * @param queryContext          the current query context
     * @return the VisitResult defines how the current pipeline will behave for the current or further pipeline stages
     */
    VisitResult evaluate(PipelineStage previousPipelineStage, PipelineStage pipelineStage, QueryContext queryContext);

}
