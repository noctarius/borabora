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
package com.noctarius.borabora.impl.query.stages;

import com.noctarius.borabora.spi.query.QueryContext;
import com.noctarius.borabora.spi.query.pipeline.PipelineStage;
import com.noctarius.borabora.spi.query.pipeline.QueryStage;
import com.noctarius.borabora.spi.query.pipeline.VisitResult;

import static com.noctarius.borabora.spi.io.Constants.OFFSET_CODE_NULL;

public class AsSequenceProjectionEntryQueryStage
        implements QueryStage {

    public static final QueryStage INSTANCE = new AsSequenceProjectionEntryQueryStage();

    protected AsSequenceProjectionEntryQueryStage() {
    }

    @Override
    public VisitResult evaluate(PipelineStage previousPipelineStage, PipelineStage pipelineStage, QueryContext queryContext) {
        long offset = queryContext.offset();
        queryContext.offset(0);

        // Try execution of the child query subtree
        VisitResult visitResult = pipelineStage.visitChildren(queryContext);
        if (visitResult == VisitResult.Break || visitResult == VisitResult.Exit) {
            if (queryContext.offset() == OFFSET_CODE_NULL) {
                queryContext.projectionStrategy().putSequenceNullValue(queryContext);
            }
            // If break, move on with the next sibling, for exit: stop here
            return visitResult == VisitResult.Break ? VisitResult.Continue : visitResult;
        }

        queryContext.offset(offset);
        return VisitResult.Continue;
    }

    @Override
    public String toString() {
        return "SEQ_ENTRY_BEGIN";
    }

}
