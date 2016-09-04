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
package com.noctarius.borabora.impl;

import com.noctarius.borabora.Value;
import com.noctarius.borabora.builder.query.DictionaryQueryBuilder;
import com.noctarius.borabora.builder.query.EntryQueryBuilder;
import com.noctarius.borabora.builder.query.SequenceQueryBuilder;
import com.noctarius.borabora.builder.query.StreamEntryQueryBuilder;
import com.noctarius.borabora.impl.query.stages.AsDictionarySelectorQueryStage;
import com.noctarius.borabora.impl.query.stages.AsSequenceSelectorQueryStage;
import com.noctarius.borabora.impl.query.stages.SingleStreamElementQueryStage;
import com.noctarius.borabora.spi.query.TypeSpec;
import com.noctarius.borabora.spi.query.ProjectionStrategy;
import com.noctarius.borabora.spi.query.pipeline.QueryBuilderNode;
import com.noctarius.borabora.spi.query.pipeline.QueryStage;

import java.util.Objects;
import java.util.function.Predicate;

class StreamEntryQueryBuilderImpl<T>
        extends AbstractQueryBuilder
        implements StreamEntryQueryBuilder<T>, EntryQueryBuilder<T> {

    private final T queryBuilder;
    private final QueryStage endQueryStage;

    StreamEntryQueryBuilderImpl(T queryBuilder, QueryBuilderNode parentTreeNode, QueryStage endQueryStage,
                                ProjectionStrategy projectionStrategy) {

        super(parentTreeNode, projectionStrategy);
        Objects.requireNonNull(queryBuilder, "queryBuilder must not be null");
        Objects.requireNonNull(endQueryStage, "endQueryStage must not be null");
        this.queryBuilder = queryBuilder;
        this.endQueryStage = endQueryStage;
    }

    @Override
    public EntryQueryBuilder<T> stream(long streamIndex) {
        Tracer.traceInfo("StreamEntryQueryBuilderImpl#stream", this);
        currentTreeNode = currentTreeNode.pushChild(new SingleStreamElementQueryStage(streamIndex));
        return this;
    }

    @Override
    public DictionaryQueryBuilder<EntryQueryBuilder<T>> asDictionary() {
        Tracer.traceCall("StreamEntryQueryBuilderImpl#asDictionary", this);
        QueryBuilderNode newNode = currentTreeNode.pushChild(AsDictionarySelectorQueryStage.INSTANCE);
        return new DictionaryQueryBuilderImpl<>(this, newNode, projectionStrategy);
    }

    @Override
    public SequenceQueryBuilder<EntryQueryBuilder<T>> asSequence() {
        Tracer.traceCall("StreamEntryQueryBuilderImpl#asSequence", this);
        QueryBuilderNode newNode = currentTreeNode.pushChild(AsSequenceSelectorQueryStage.INSTANCE);
        return new SequenceQueryBuilderImpl<>(this, newNode, projectionStrategy);
    }

    @Override
    public T endEntry() {
        Tracer.traceReturn("StreamEntryQueryBuilderImpl#endEntry", this);
        currentTreeNode = currentTreeNode.pushChild(endQueryStage);
        return queryBuilder;
    }

    @Override
    public EntryQueryBuilder<T> sequenceMatch(Predicate<Value> predicate) {
        Tracer.traceInfo("StreamEntryQueryBuilderImpl#sequenceMatch", this);
        Objects.requireNonNull(predicate, "predicate must not be null");
        sequenceMatch0(predicate);
        return this;
    }

    @Override
    public EntryQueryBuilder<T> sequence(long index) {
        Tracer.traceInfo("StreamEntryQueryBuilderImpl#sequence", this);
        sequence0(index);
        return this;
    }

    @Override
    public EntryQueryBuilder<T> dictionary(Predicate<Value> predicate) {
        Tracer.traceInfo("StreamEntryQueryBuilderImpl#dictionary", this);
        Objects.requireNonNull(predicate, "predicate must not be null");
        dictionary0(predicate);
        return this;
    }

    @Override
    public EntryQueryBuilder<T> dictionary(String key) {
        Tracer.traceInfo("StreamEntryQueryBuilderImpl#dictionary", this);
        Objects.requireNonNull(key, "key must not be null");
        dictionary0(key);
        return this;
    }

    @Override
    public EntryQueryBuilder<T> dictionary(double key) {
        Tracer.traceInfo("StreamEntryQueryBuilderImpl#dictionary", this);
        dictionary0(key);
        return this;
    }

    @Override
    public EntryQueryBuilder<T> dictionary(long key) {
        Tracer.traceInfo("StreamEntryQueryBuilderImpl#dictionary", this);
        dictionary0(key);
        return this;
    }

    @Override
    public EntryQueryBuilder<T> nullOrType(TypeSpec typeSpec) {
        Tracer.traceInfo("StreamEntryQueryBuilderImpl#nullOrType", this);
        Objects.requireNonNull(typeSpec, "typeSpec must not be null");
        nullOrType0(typeSpec);
        return this;
    }

    @Override
    public EntryQueryBuilder<T> requireType(TypeSpec typeSpec) {
        Tracer.traceInfo("StreamEntryQueryBuilderImpl#requireType", this);
        Objects.requireNonNull(typeSpec, "typeSpec must not be null");
        requireType0(typeSpec);
        return this;
    }

}
