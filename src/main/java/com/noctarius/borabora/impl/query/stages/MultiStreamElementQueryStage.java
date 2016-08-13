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

import com.noctarius.borabora.Input;
import com.noctarius.borabora.spi.codec.Decoder;
import com.noctarius.borabora.spi.pipeline.PipelineStage;
import com.noctarius.borabora.spi.pipeline.QueryStage;
import com.noctarius.borabora.spi.pipeline.VisitResult;
import com.noctarius.borabora.spi.query.QueryContext;

public class MultiStreamElementQueryStage
        implements QueryStage {

    public static final QueryStage INSTANCE = new MultiStreamElementQueryStage();

    protected MultiStreamElementQueryStage() {
    }

    @Override
    public VisitResult evaluate(PipelineStage previousPipelineStage, PipelineStage pipelineStage, QueryContext pipelineContext) {
        Input input = pipelineContext.input();
        long offset = pipelineContext.offset();

        do {
            // Visit children
            VisitResult visitResult = pipelineStage.visitChildren(pipelineContext);
            if (visitResult == VisitResult.Break || visitResult == VisitResult.Exit) {
                return visitResult;
            }

            // Skip the whole item
            offset = Decoder.skip(input, offset);
            pipelineContext.offset(offset);

        } while (input.offsetValid(offset));

        return VisitResult.Continue;
    }

    @Override
    public String toString() {
        return "ANY_STREAM_INDEX";
    }

}
