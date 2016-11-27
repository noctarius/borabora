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
 * The <tt>PipelineStage</tt> interface defines a single point of execution inside a query execution plan.
 * <p>PipelineStages are grouped into a {@link QueryPipeline} and designed as a binary tree. The default
 * implementation utilizes a LCRS (Left Child Right Sibling) tree which is easy to traverse (preOrder traversal).</p>
 * <p>PipelineStage implementations are considered to be immutable and all accessing code is meant to
 * work in the assumption that this holds true. Furthermore {@link #left()} or {@link #right()} are not
 * allowed to return <tt>null</tt> but an end stage has to be signalled by returning {@link #NIL} from
 * either or both of the above methods.</p>
 * <p>{@link #stage()} on the other side is allowed to return <tt>null</tt>, however this is considered
 * to be a corner case and non of the default operations takes this possibility.</p>
 * <p>Overall the PipelineStage and QueryPipeline employ the Visitor pattern to implement the execution
 * plan and the overall query execution. {@link VisitResult} is used to control the query flow and to
 * {@link VisitResult#Break} from the current query path, {@link VisitResult#Continue} it,
 * {@link VisitResult#Exit} from the query altogether, or {@link VisitResult#Loop} on the current
 * pipeline stage.</p>
 */
public interface PipelineStage {

    /**
     * An empty, so called <tt>Null-PipelineStage</tt>. This <tt>PipelineStage</tt> does not execute
     * anything but signals the end of the query execution graph at the occurring position.
     */
    PipelineStage NIL = new PipelineStage() {
        @Override
        public VisitResult visit(PipelineStage previousPipelineStage, QueryContext queryContext) {
            return VisitResult.Continue;
        }

        @Override
        public QueryStage stage() {
            return null;
        }

        @Override
        public PipelineStage left() {
            return NIL;
        }

        @Override
        public PipelineStage right() {
            return NIL;
        }

        @Override
        public String toString() {
            return "NIL";
        }
    };

    /**
     * Visits the current PipelineState and executed the {@link QueryStage} if available or walks
     * down the execution plan onwards children first, siblings secondly.
     *
     * @param previousPipelineStage the previously executed PipelineStage
     * @param queryContext          the current query context
     * @return the VisitResult to control the further execution
     */
    VisitResult visit(PipelineStage previousPipelineStage, QueryContext queryContext);

    /**
     * Callback from the {@link #visit(PipelineStage, QueryContext)} method to visit available
     * children. If no child is available, the method immediately returns with
     * {@link VisitResult#Continue} and the execution will continue with the next available
     * sibling in the pipeline.
     *
     * @param queryContext the current query context
     * @return the VisitResult to control the further execution
     */
    default VisitResult visitChildren(QueryContext queryContext) {
        return VisitResult.Continue;
    }

    /**
     * Returns the current {@link QueryStage}.
     *
     * @return the current stage
     */
    QueryStage stage();

    /**
     * Returns the first child.
     *
     * @return the first child if available, otherwise {@link #NIL}
     */
    PipelineStage left();

    /**
     * Returns the first sibling.
     *
     * @return the first sibling if available, otherwise {@link #NIL}
     */
    PipelineStage right();

}
