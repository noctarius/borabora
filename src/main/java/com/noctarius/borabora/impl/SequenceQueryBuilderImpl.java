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

import com.noctarius.borabora.builder.query.SequenceQueryBuilder;
import com.noctarius.borabora.builder.query.StreamEntryQueryBuilder;
import com.noctarius.borabora.impl.query.stages.AsSequenceProjectionEntryQueryStage;
import com.noctarius.borabora.impl.query.stages.ConsumeSequenceEntryValueQueryStage;

import java.util.Objects;

class SequenceQueryBuilderImpl<T>
        implements SequenceQueryBuilder<T> {

    private final T queryBuilder;
    private final QueryBuilderNode parentTreeNode;

    SequenceQueryBuilderImpl(T queryBuilder, QueryBuilderNode parentTreeNode) {
        Objects.requireNonNull(queryBuilder, "queryBuilder must not be null");
        Objects.requireNonNull(parentTreeNode, "parentTreeNode must not be null");
        this.queryBuilder = queryBuilder;
        this.parentTreeNode = parentTreeNode;
    }

    @Override
    public StreamEntryQueryBuilder<SequenceQueryBuilder<T>> putElement() {
        Tracer.traceCall("SequenceQueryBuilderImpl#putElement", this);
        QueryBuilderNode entry = parentTreeNode.pushChild(AsSequenceProjectionEntryQueryStage.INSTANCE);
        return new StreamEntryQueryBuilderImpl<>(this, entry, ConsumeSequenceEntryValueQueryStage.INSTANCE);
    }

    @Override
    public T endSequence() {
        Tracer.traceReturn("SequenceQueryBuilderImpl#endSequence", this);
        return queryBuilder;
    }

}
