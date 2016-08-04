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
import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.spi.ByteSizes;
import com.noctarius.borabora.spi.Decoder;
import com.noctarius.borabora.spi.ElementCounts;
import com.noctarius.borabora.spi.QueryContext;
import com.noctarius.borabora.spi.pipeline.PipelineStage;
import com.noctarius.borabora.spi.pipeline.VisitResult;

public class SequenceLoopQueryStage
        implements QueryStage {

    public static final QueryStage INSTANCE = new SequenceLoopQueryStage();

    protected SequenceLoopQueryStage() {
    }

    @Override
    public VisitResult evaluate(PipelineStage<QueryContext, QueryStage> previousPipelineStage, //
                                PipelineStage<QueryContext, QueryStage> pipelineStage, //
                                QueryContext pipelineContext) {

        Input input = pipelineContext.input();
        long offset = pipelineContext.offset();

        short head = Decoder.readUInt8(input, offset);
        MajorType majorType = MajorType.findMajorType(head);

        if (majorType != MajorType.Sequence) {
            // If not a sequence break out of the current subtree
            return VisitResult.Break;
        }

        long elementCount = ElementCounts.sequenceElementCount(input, offset);

        // Skip sequence header and make element 1 accessible
        offset += ByteSizes.headByteSize(input, offset);
        pipelineContext.offset(offset);

        for (int i = 0; i < elementCount; i++) {
            VisitResult visitResult = pipelineStage.visitChildren(pipelineContext);
            if (visitResult == VisitResult.Break || visitResult == VisitResult.Exit) {
                return visitResult;
            }
        }

        return VisitResult.Continue;
    }

    @Override
    public String toString() {
        return "SEQ_LOOP";
    }

}
