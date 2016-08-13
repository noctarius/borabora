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
import com.noctarius.borabora.Predicates;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.WrongTypeException;
import com.noctarius.borabora.spi.Constants;
import com.noctarius.borabora.spi.query.QueryContext;
import com.noctarius.borabora.spi.codec.Decoder;
import com.noctarius.borabora.spi.codec.ByteSizes;
import com.noctarius.borabora.spi.pipeline.PipelineStage;
import com.noctarius.borabora.spi.pipeline.VisitResult;

import java.util.Objects;
import java.util.function.Predicate;

public class DictionaryLookupQueryStage
        implements QueryStage {

    protected final Predicate<Value> predicate;

    private DictionaryLookupQueryStage(Predicate<Value> predicate) {
        Objects.requireNonNull(predicate, "predicate must be set");
        this.predicate = predicate;
    }

    @Override
    public final VisitResult evaluate(PipelineStage<QueryContext, QueryStage> previousPipelineStage, //
                                      PipelineStage<QueryContext, QueryStage> pipelineStage, //
                                      QueryContext pipelineContext) {

        Input input = pipelineContext.input();
        long offset = pipelineContext.offset();

        short head = Decoder.readUInt8(input, offset);
        MajorType majorType = MajorType.findMajorType(head);
        if (majorType != MajorType.Dictionary) {
            throw new WrongTypeException("Not a dictionary");
        }

        // Skip head
        long headByteSize = ByteSizes.headByteSize(input, offset);
        offset += headByteSize;

        // Execute the key lookup
        offset = Decoder.findByDictionaryKey(predicate, offset, pipelineContext);
        if (offset == Constants.OFFSET_CODE_NULL) {
            pipelineContext.consume(offset);
            return VisitResult.Break;
        }

        pipelineContext.offset(offset);

        return pipelineStage.visitChildren(pipelineContext);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DictionaryLookupQueryStage)) {
            return false;
        }

        DictionaryLookupQueryStage that = (DictionaryLookupQueryStage) o;

        return predicate != null ? Predicates.predicateEquals(predicate, that.predicate) : that.predicate == null;
    }

    @Override
    public int hashCode() {
        return predicate != null ? predicate.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DIC_LOOKUP[ " + predicate + " ]";
    }

    public static DictionaryLookupQueryStage stringMatcher(String key) {
        return new DictionaryLookupQueryStage(Predicates.matchString(key));
    }

    public static DictionaryLookupQueryStage intMatcher(long key) {
        return new DictionaryLookupQueryStage(Predicates.matchInt(key));
    }

    public static DictionaryLookupQueryStage floatMatcher(double key) {
        return new DictionaryLookupQueryStage(Predicates.matchFloat(key));
    }

    public static DictionaryLookupQueryStage predicateMatcher(Predicate<Value> predicate) {
        return new DictionaryLookupQueryStage(predicate);
    }

}
