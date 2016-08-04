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
package com.noctarius.borabora.impl.query.stages;

import com.noctarius.borabora.spi.QueryContext;
import com.noctarius.borabora.spi.pipeline.PipelineStage;
import com.noctarius.borabora.spi.pipeline.VisitResult;

public class ConsumeSequenceEntryValueQueryStage
        extends ConsumerQueryStage {

    public static final QueryStage INSTANCE = new ConsumeSequenceEntryValueQueryStage();

    protected ConsumeSequenceEntryValueQueryStage() {
    }

    @Override
    public VisitResult evaluate(PipelineStage<QueryContext, QueryStage> previousPipelineStage, //
                                PipelineStage<QueryContext, QueryStage> pipelineStage, //
                                QueryContext pipelineContext) {

        // Consume that offset to transform it into an Value
        pipelineContext.selectStatementStrategy().putSequenceValue(previousPipelineStage, pipelineContext);

        // Continue querying
        return VisitResult.Continue;
    }

    @Override
    public String toString() {
        return "SEQ_ENTRY_END";
    }

}
