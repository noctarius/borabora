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
import com.noctarius.borabora.spi.query.QueryContext;
import com.noctarius.borabora.spi.codec.Decoder;
import com.noctarius.borabora.spi.pipeline.PipelineStage;
import com.noctarius.borabora.spi.pipeline.VisitResult;

public class SingleStreamElementQueryStage
        implements QueryStage {

    private final long streamElementIndex;

    public SingleStreamElementQueryStage(long streamElementIndex) {
        this.streamElementIndex = streamElementIndex;
    }

    @Override
    public VisitResult evaluate(PipelineStage<QueryContext, QueryStage> previousPipelineStage, //
                                PipelineStage<QueryContext, QueryStage> pipelineStage, //
                                QueryContext pipelineContext) {

        Input input = pipelineContext.input();
        long offset = pipelineContext.offset();

        // Skip unnecessary objects
        if (streamElementIndex > 0) {
            for (int i = 0; i < streamElementIndex; i++) {
                offset = Decoder.skip(input, offset);
            }
        }

        // Set up new offset
        pipelineContext.offset(offset);

        // Visit children
        return pipelineStage.visitChildren(pipelineContext);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SingleStreamElementQueryStage)) {
            return false;
        }

        SingleStreamElementQueryStage that = (SingleStreamElementQueryStage) o;

        return streamElementIndex == that.streamElementIndex;
    }

    @Override
    public int hashCode() {
        return (int) (streamElementIndex ^ (streamElementIndex >>> 32));
    }

    @Override
    public String toString() {
        return "STREAM_INDEX[ " + streamElementIndex + " ]";
    }

}
