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

public interface PipelineStage {

    PipelineStage NIL = new PipelineStage() {
        @Override
        public VisitResult visit(PipelineStage previousPipelineStage, QueryContext pipelineContext) {
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

    VisitResult visit(PipelineStage previousPipelineStage, QueryContext pipelineContext);

    default VisitResult visitChildren(QueryContext pipelineContext) {
        return VisitResult.Continue;
    }

    QueryStage stage();

    PipelineStage left();

    PipelineStage right();

}
