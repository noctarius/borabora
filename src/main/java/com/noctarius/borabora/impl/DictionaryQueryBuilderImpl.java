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

import com.noctarius.borabora.builder.DictionaryQueryBuilder;
import com.noctarius.borabora.builder.StreamEntryQueryBuilder;
import com.noctarius.borabora.impl.query.stages.AsDictionarySelectorEntryQueryStage;
import com.noctarius.borabora.impl.query.stages.ConsumeDictionaryEntryValueQueryStage;
import com.noctarius.borabora.spi.pipeline.QueryBuilderNode;
import com.noctarius.borabora.spi.query.SelectStatementStrategy;

class DictionaryQueryBuilderImpl<T>
        implements DictionaryQueryBuilder<T> {

    private final T queryBuilder;
    private final QueryBuilderNode parentTreeNode;
    private SelectStatementStrategy selectStatementStrategy;

    DictionaryQueryBuilderImpl(T queryBuilder, QueryBuilderNode parentTreeNode, SelectStatementStrategy selectStatementStrategy) {

        this.queryBuilder = queryBuilder;
        this.parentTreeNode = parentTreeNode;
        this.selectStatementStrategy = selectStatementStrategy;
    }

    @Override
    public StreamEntryQueryBuilder<DictionaryQueryBuilder<T>> putEntry(String key) {
        Tracer.traceCall("DictionaryQueryBuilderImpl#putEntry-string", this);
        QueryBuilderNode entry = new QueryBuilderNode(AsDictionarySelectorEntryQueryStage.withStringKey(key));
        parentTreeNode.children().add(entry);
        return new StreamEntryQueryBuilderImpl<>(this, entry, ConsumeDictionaryEntryValueQueryStage.INSTANCE,
                selectStatementStrategy);
    }

    @Override
    public StreamEntryQueryBuilder<DictionaryQueryBuilder<T>> putEntry(double key) {
        Tracer.traceCall("DictionaryQueryBuilderImpl#putEntry-double", this);
        QueryBuilderNode entry = new QueryBuilderNode(AsDictionarySelectorEntryQueryStage.withFloatKey(key));
        parentTreeNode.children().add(entry);
        return new StreamEntryQueryBuilderImpl<>(this, entry, ConsumeDictionaryEntryValueQueryStage.INSTANCE,
                selectStatementStrategy);
    }

    @Override
    public StreamEntryQueryBuilder<DictionaryQueryBuilder<T>> putEntry(long key) {
        Tracer.traceCall("DictionaryQueryBuilderImpl#putEntry-long", this);
        QueryBuilderNode entry = new QueryBuilderNode(AsDictionarySelectorEntryQueryStage.withIntKey(key));
        parentTreeNode.children().add(entry);
        return new StreamEntryQueryBuilderImpl<>(this, entry, ConsumeDictionaryEntryValueQueryStage.INSTANCE,
                selectStatementStrategy);
    }

    @Override
    public T endDictionary() {
        Tracer.traceReturn("DictionaryQueryBuilderImpl#endDictionary", this);
        return queryBuilder;

    }

}
