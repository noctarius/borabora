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
package com.noctarius.borabora.spi.query.optimizer;

import com.noctarius.borabora.spi.query.pipeline.PipelineStage;
import com.noctarius.borabora.spi.query.pipeline.PipelineStageFactory;

/**
 * The <tt>QueryOptimizer</tt> interface defines a single optimizer strategy. An optimizer
 * is expected to apply zero or more optimizations to the provided {@link PipelineStage}.
 * Since the pipeline is immutable, optimizations must create a new pipeline using the
 * provided {@link PipelineStageFactory} instance.
 * <p>Before the {@link #optimize(PipelineStage, PipelineStageFactory)} method is called
 * the optimizer is asked if the actual pipeline type is actually optimizable given the
 * optimizer implementation. If an optimizer cannot handle a specific type, or can only
 * handle a specific type {@link #handles(PipelineStage)} should be overridden accordingly.</p>
 */
public interface QueryOptimizer {

    /**
     * Returns true if the optimizer can handle the given type of {@link PipelineStage},
     * otherwise it is expected to return false.
     *
     * @param rooPipelineStage the PipelineStage to optimize
     * @return true if the PipelineStage can be optimized, otherwise false
     */
    default boolean handles(PipelineStage rooPipelineStage) {
        return true;
    }

    /**
     * Optimizes the given {@link PipelineStage} instance (<tt>pipelineStage</tt>). To create
     * a new pipeline, the provided {@link PipelineStageFactory} is expected to be used.
     *
     * @param rootPipelineStage    the PipelineStage to optimize
     * @param pipelineStageFactory the factory to create new PipelineStage instances
     * @return the optimized PipelineStage
     */
    PipelineStage optimize(PipelineStage rootPipelineStage, PipelineStageFactory pipelineStageFactory);

}
