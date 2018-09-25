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

public class ConsumeSequenceEntryValueQueryStage
        extends ConsumerQueryStage {

    public static final QueryStage INSTANCE = new ConsumeSequenceEntryValueQueryStage();

    protected ConsumeSequenceEntryValueQueryStage() {
    }

    @Override
    public VisitResult evaluate(PipelineStage previousPipelineStage, PipelineStage pipelineStage, QueryContext queryContext) {
        // Consume that offset to transform it into an Value
        queryContext.projectionStrategy().putSequenceValue(previousPipelineStage, queryContext);

        // Continue querying
        return VisitResult.Continue;
    }

    @Override
    public String toString() {
        return "SEQ_ENTRY_END";
    }

}
