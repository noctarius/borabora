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

import com.noctarius.borabora.spi.pipeline.PipelineStage;
import com.noctarius.borabora.spi.pipeline.QueryStage;
import com.noctarius.borabora.spi.pipeline.VisitResult;
import com.noctarius.borabora.spi.query.QueryContext;
import com.noctarius.borabora.spi.query.ProjectionStrategy;

public class AsDictionarySelectorQueryStage
        implements QueryStage {

    public static final QueryStage INSTANCE = new AsDictionarySelectorQueryStage();

    protected AsDictionarySelectorQueryStage() {
    }

    @Override
    public VisitResult evaluate(PipelineStage previousPipelineStage, PipelineStage pipelineStage, QueryContext queryContext) {
        ProjectionStrategy projectionStrategy = queryContext.projectionStrategy();
        projectionStrategy.beginDictionary(queryContext);

        VisitResult visitResult = pipelineStage.visitChildren(queryContext);

        // If exit is set, we ignore all further executions
        if (VisitResult.Exit == visitResult) {
            return visitResult;
        }

        projectionStrategy.endDictionary(queryContext);
        return visitResult;
    }

    @Override
    public String toString() {
        return "AS_DIC";
    }

}
