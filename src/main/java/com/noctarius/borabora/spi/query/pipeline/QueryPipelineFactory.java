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
package com.noctarius.borabora.spi.query.pipeline;

import com.noctarius.borabora.spi.query.optimizer.QueryOptimizerStrategy;

/**
 * The <tt>QueryPipelineFactory</tt> implementation is responsible to build a new {@link QueryPipeline} after
 * the optimization is executed. The implementation is also free to change the pipeline depending on its own
 * implementation needs, too.
 */
public interface QueryPipelineFactory {

    /**
     * Creates a new {@link QueryPipeline} based on the provided <tt>rootPipelineStage</tt> and is free to
     * execute the actual query optimization. The factory might decide to skip optimization for debugging
     * purpose or implementation specific reasons, however it is strongly recommended to execute optimization
     * for normal operation reasons.
     *
     * @param rootPipelineStage      the unoptimized root pipeline stage element
     * @param pipelineStageFactory   the configured PipelineStageFactory to create new PipelineStage elements
     * @param queryOptimizerStrategy the configured QueryOptimizerStrategy to optimize the pipeline
     * @return a new QueryPipeline instance with either optimized or unoptimized pipeline stages
     */
    QueryPipeline newQueryPipeline(PipelineStage rootPipelineStage, PipelineStageFactory pipelineStageFactory,
                                   QueryOptimizerStrategy queryOptimizerStrategy);

}
