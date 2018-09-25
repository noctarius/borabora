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
package com.noctarius.borabora.spi.query.optimizer;

import com.noctarius.borabora.spi.query.pipeline.PipelineStage;
import com.noctarius.borabora.spi.query.pipeline.PipelineStageFactory;

/**
 * The <tt>QueryOptimizerStrategy</tt> interface describes a strategy on how to apply different
 * {@link QueryOptimizer}s to optimize the actual query. A <tt>QueryOptimizerStrategy</tt> is
 * created using a {@link QueryOptimizerStrategyFactory} and the provided set of optimizers given
 * to the factory instance.
 */
public interface QueryOptimizerStrategy {

    /**
     * This method executes the bound set of {@link QueryOptimizer}s. Depending on the strategy
     * implementation certain optimizers might get a higher priority than others. All rules and
     * priorities applied are completely independent specific. The default implementation just
     * applied optimizers in the order they were registered.
     *
     * @param rootPipelineStage    the PipelineStage to optimize
     * @param pipelineStageFactory the factory to create new PipelineStage instances
     * @return the optimized PipelineStage
     */
    PipelineStage optimizeQuery(PipelineStage rootPipelineStage, PipelineStageFactory pipelineStageFactory);

}
