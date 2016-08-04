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
package com.noctarius.borabora;

import com.noctarius.borabora.builder.DictionaryQueryBuilder;
import com.noctarius.borabora.builder.StreamEntryQueryBuilder;
import com.noctarius.borabora.impl.query.stages.AsDictionarySelectorEntryQueryStage;
import com.noctarius.borabora.spi.QueryBuilderTreeNode;
import com.noctarius.borabora.spi.SelectStatementStrategy;

import static com.noctarius.borabora.impl.query.stages.ConsumeDictionaryEntryValueQueryStage.INSTANCE;

class DictionaryQueryBuilderImpl<T>
        implements DictionaryQueryBuilder<T> {

    private final T queryBuilder;
    private final QueryBuilderTreeNode parentTreeNode;
    private SelectStatementStrategy selectStatementStrategy;

    DictionaryQueryBuilderImpl(T queryBuilder, QueryBuilderTreeNode parentTreeNode,
                               SelectStatementStrategy selectStatementStrategy) {

        this.queryBuilder = queryBuilder;
        this.parentTreeNode = parentTreeNode;
        this.selectStatementStrategy = selectStatementStrategy;
    }

    @Override
    public StreamEntryQueryBuilder<DictionaryQueryBuilder<T>> putEntry(String key) {
        Tracer.traceCall("DictionaryQueryBuilderImpl#putEntry-string", this);
        QueryBuilderTreeNode entry = new QueryBuilderTreeNode(AsDictionarySelectorEntryQueryStage.withStringKey(key));
        parentTreeNode.children().add(entry);
        return new StreamEntryQueryBuilderImpl<>(this, entry, INSTANCE, selectStatementStrategy);
    }

    @Override
    public StreamEntryQueryBuilder<DictionaryQueryBuilder<T>> putEntry(double key) {
        Tracer.traceCall("DictionaryQueryBuilderImpl#putEntry-double", this);
        QueryBuilderTreeNode entry = new QueryBuilderTreeNode(AsDictionarySelectorEntryQueryStage.withFloatKey(key));
        parentTreeNode.children().add(entry);
        return new StreamEntryQueryBuilderImpl<>(this, entry, INSTANCE, selectStatementStrategy);
    }

    @Override
    public StreamEntryQueryBuilder<DictionaryQueryBuilder<T>> putEntry(long key) {
        Tracer.traceCall("DictionaryQueryBuilderImpl#putEntry-long", this);
        QueryBuilderTreeNode entry = new QueryBuilderTreeNode(AsDictionarySelectorEntryQueryStage.withIntKey(key));
        parentTreeNode.children().add(entry);
        return new StreamEntryQueryBuilderImpl<>(this, entry, INSTANCE, selectStatementStrategy);
    }

    @Override
    public T endDictionary() {
        Tracer.traceReturn("DictionaryQueryBuilderImpl#endDictionary", this);
        return queryBuilder;

    }

}
