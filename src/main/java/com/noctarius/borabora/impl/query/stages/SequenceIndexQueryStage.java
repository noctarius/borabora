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
import com.noctarius.borabora.WrongTypeException;
import com.noctarius.borabora.spi.codec.ByteSizes;
import com.noctarius.borabora.spi.codec.Decoder;
import com.noctarius.borabora.spi.codec.ElementCounts;
import com.noctarius.borabora.spi.pipeline.PipelineStage;
import com.noctarius.borabora.spi.pipeline.QueryStage;
import com.noctarius.borabora.spi.pipeline.VisitResult;
import com.noctarius.borabora.spi.query.QueryContext;

import static com.noctarius.borabora.spi.Constants.OFFSET_CODE_NULL;

public class SequenceIndexQueryStage
        implements QueryStage {

    private final long sequenceIndex;

    public SequenceIndexQueryStage(long sequenceIndex) {
        if (sequenceIndex < 0) {
            throw new IllegalArgumentException("sequenceIndex must be equal or larger than 0");
        }
        this.sequenceIndex = sequenceIndex;
    }

    @Override
    public VisitResult evaluate(PipelineStage previousPipelineStage, PipelineStage pipelineStage, QueryContext queryContext) {
        Input input = queryContext.input();
        long offset = queryContext.offset();

        short head = Decoder.readUInt8(input, offset);
        MajorType majorType = MajorType.findMajorType(head);

        if (majorType != MajorType.Sequence) {
            throw new WrongTypeException(offset, "Encountered " + majorType + " when a sequence was expected");
        }

        // Sequences need head skipped
        long elementCount = ElementCounts.elementCountByMajorType(majorType, input, offset);
        if (elementCount <= sequenceIndex) {
            queryContext.offset(OFFSET_CODE_NULL);
            return VisitResult.Break;
        }

        // Element access
        long headByteSize = ByteSizes.headByteSize(input, offset);
        offset += headByteSize;

        // Skip items until sequenceIndex
        offset = skip(input, offset);
        queryContext.offset(offset);

        return pipelineStage.visitChildren(queryContext);
    }

    private long skip(Input input, long offset) {
        // Skip unnecessary objects
        for (int i = 0; i < sequenceIndex; i++) {
            offset = Decoder.skip(input, offset);
        }
        return offset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SequenceIndexQueryStage)) {
            return false;
        }

        SequenceIndexQueryStage that = (SequenceIndexQueryStage) o;

        return sequenceIndex == that.sequenceIndex;
    }

    @Override
    public int hashCode() {
        return (int) (sequenceIndex ^ (sequenceIndex >>> 32));
    }

    @Override
    public String toString() {
        return "SEQ_INDEX[ " + sequenceIndex + " ]";
    }

}
