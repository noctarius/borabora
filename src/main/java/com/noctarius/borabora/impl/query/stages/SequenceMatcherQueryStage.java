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
import com.noctarius.borabora.Value;
import com.noctarius.borabora.ValueType;
import com.noctarius.borabora.WrongTypeException;
import com.noctarius.borabora.spi.EqualsSupport;
import com.noctarius.borabora.spi.RelocatableStreamValue;
import com.noctarius.borabora.spi.io.ByteSizes;
import com.noctarius.borabora.spi.io.Decoder;
import com.noctarius.borabora.spi.io.ElementCounts;
import com.noctarius.borabora.spi.query.QueryContext;
import com.noctarius.borabora.spi.query.pipeline.PipelineStage;
import com.noctarius.borabora.spi.query.pipeline.QueryStage;
import com.noctarius.borabora.spi.query.pipeline.VisitResult;

import java.util.Objects;
import java.util.function.Predicate;

public class SequenceMatcherQueryStage
        implements QueryStage {

    private final Predicate<Value> predicate;

    public SequenceMatcherQueryStage(Predicate<Value> predicate) {
        Objects.requireNonNull(predicate, "predicate must not be null");
        this.predicate = predicate;
    }

    @Override
    public VisitResult evaluate(PipelineStage previousPipelineStage, PipelineStage pipelineStage, QueryContext queryContext) {
        Input input = queryContext.input();
        long offset = queryContext.offset();

        short head = Decoder.readUInt8(input, offset);
        MajorType majorType = MajorType.findMajorType(head);

        if (majorType != MajorType.Sequence) {
            // If not a sequence we're done here
            throw new WrongTypeException(offset, "Encountered " + majorType + " when a sequence was expected");
        }

        long elementCount = ElementCounts.sequenceElementCount(input, offset);

        // Skip sequence header and make element 1 accessible
        offset += ByteSizes.headByteSize(input, offset);
        queryContext.offset(offset);

        RelocatableStreamValue streamValue = new RelocatableStreamValue();
        for (int i = 0; i < elementCount; i++) {
            short itemHead = Decoder.readUInt8(input, offset);
            MajorType itemMajorType = MajorType.findMajorType(itemHead);
            ValueType itemValueType = queryContext.valueType(offset);
            streamValue.relocate(queryContext, itemMajorType, itemValueType, offset);

            if (predicate.test(streamValue)) {
                queryContext.offset(offset);
                VisitResult visitResult = pipelineStage.visitChildren(queryContext);
                // TODO break is exit?
                if (visitResult == VisitResult.Break || visitResult == VisitResult.Exit) {
                    return visitResult;
                }
            }
            offset += Decoder.length(input, itemMajorType, offset);
        }

        return VisitResult.Continue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SequenceMatcherQueryStage)) {
            return false;
        }

        SequenceMatcherQueryStage that = (SequenceMatcherQueryStage) o;

        return EqualsSupport.equals(predicate, that.predicate);
    }

    @Override
    public int hashCode() {
        return predicate.hashCode();
    }

    @Override
    public String toString() {
        return "SEQ_MATCH[ " + predicate + " ]";
    }

}
