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
 * The <tt>PipelineStageFactory</tt> implementation is responsible for creating and returning
 * {@link PipelineStage} instances based on the provided <tt>left</tt>, <tt>right</tt> and
 * <tt>stage</tt> parameters. A PipelineStage is always designed as a binary tree.
 * <p>The default PipelineStage implementation also assumes a LCRS tree (Left Child, Right
 * Sibling), however implementations are free to change this behavior as long as the visitor
 * methods {@link PipelineStage#visit(PipelineStage, QueryContext)}</p> and
 * {@link PipelineStage#visitChildren(QueryContext)} are correctly used.
 */
public interface PipelineStageFactory {

    /**
     * Returns a new <tt>PipelineStage</tt> instance based on the given parameters.
     *
     * @param left  the first child
     * @param right the first sibling
     * @param stage the stage
     * @return the new pipeline stage instance
     */
    PipelineStage newPipelineStage(PipelineStage left, PipelineStage right, QueryStage stage);

}
