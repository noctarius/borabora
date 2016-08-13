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

import com.noctarius.borabora.impl.query.stages.AsSequenceSelectorEntryQueryStage;
import com.noctarius.borabora.impl.query.stages.ConsumeSequenceEntryValueQueryStage;
import com.noctarius.borabora.spi.query.SelectStatementStrategy;
import com.noctarius.borabora.spi.pipeline.QueryBuilderNode;
import com.noctarius.borabora.builder.SequenceQueryBuilder;
import com.noctarius.borabora.builder.StreamEntryQueryBuilder;

class SequenceQueryBuilderImpl<T>
        implements SequenceQueryBuilder<T> {

    private final T queryBuilder;
    private final QueryBuilderNode parentTreeNode;
    private SelectStatementStrategy selectStatementStrategy;

    SequenceQueryBuilderImpl(T queryBuilder, QueryBuilderNode parentTreeNode,
                             SelectStatementStrategy selectStatementStrategy) {

        this.queryBuilder = queryBuilder;
        this.parentTreeNode = parentTreeNode;
        this.selectStatementStrategy = selectStatementStrategy;
    }

    @Override
    public StreamEntryQueryBuilder<SequenceQueryBuilder<T>> putEntry() {
        Tracer.traceCall("SequenceQueryBuilderImpl#putEntry", this);
        QueryBuilderNode entry = new QueryBuilderNode(AsSequenceSelectorEntryQueryStage.INSTANCE);
        parentTreeNode.children().add(entry);
        return new StreamEntryQueryBuilderImpl<>(this, entry, ConsumeSequenceEntryValueQueryStage.INSTANCE, selectStatementStrategy);
    }

    @Override
    public T endSequence() {
        Tracer.traceReturn("SequenceQueryBuilderImpl#endSequence", this);
        return queryBuilder;
    }

}
