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

import com.noctarius.borabora.Input;
import com.noctarius.borabora.NoSuchByteException;
import com.noctarius.borabora.spi.io.Decoder;
import com.noctarius.borabora.spi.query.QueryContext;
import com.noctarius.borabora.spi.query.pipeline.PipelineStage;
import com.noctarius.borabora.spi.query.pipeline.QueryStage;
import com.noctarius.borabora.spi.query.pipeline.VisitResult;

public class SingleStreamElementQueryStage
        implements QueryStage {

    private final long streamElementIndex;

    public SingleStreamElementQueryStage(long streamElementIndex) {
        if (streamElementIndex < 0) {
            throw new IllegalArgumentException("streamElementIndex must be equal or larger than 0");
        }
        this.streamElementIndex = streamElementIndex;
    }

    @Override
    public VisitResult evaluate(PipelineStage previousPipelineStage, PipelineStage pipelineStage, QueryContext queryContext) {
        Input input = queryContext.input();
        long offset = queryContext.offset();

        // Skip unnecessary objects
        if (streamElementIndex > 0) {
            for (int i = 0; i < streamElementIndex; i++) {
                offset = Decoder.skip(input, offset);
            }
        }

        // Outside of valid range?
        if (!input.offsetValid(offset)) {
            throw new NoSuchByteException(offset, "Offset " + offset + " outside of available data");
        }

        // Set up new offset
        queryContext.offset(offset);

        // Visit children
        return pipelineStage.visitChildren(queryContext);
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
