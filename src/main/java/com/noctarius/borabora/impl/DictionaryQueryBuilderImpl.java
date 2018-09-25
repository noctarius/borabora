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
package com.noctarius.borabora.impl;

import com.noctarius.borabora.builder.query.DictionaryQueryBuilder;
import com.noctarius.borabora.builder.query.StreamEntryQueryBuilder;
import com.noctarius.borabora.impl.query.stages.AsDictionaryProjectionEntryQueryStage;
import com.noctarius.borabora.impl.query.stages.ConsumeDictionaryEntryValueQueryStage;

import java.util.Objects;

class DictionaryQueryBuilderImpl<T>
        implements DictionaryQueryBuilder<T> {

    private final T queryBuilder;
    private final QueryBuilderNode parentTreeNode;

    DictionaryQueryBuilderImpl(T queryBuilder, QueryBuilderNode parentTreeNode) {
        Objects.requireNonNull(queryBuilder, "queryBuilder must not be null");
        Objects.requireNonNull(parentTreeNode, "parentTreeNode must not be null");
        this.queryBuilder = queryBuilder;
        this.parentTreeNode = parentTreeNode;
    }

    @Override
    public StreamEntryQueryBuilder<DictionaryQueryBuilder<T>> putEntry(String key) {
        Tracer.traceCall("DictionaryQueryBuilderImpl#putElement-string", this);
        Objects.requireNonNull(key, "key must not be null");
        QueryBuilderNode entry = parentTreeNode.pushChild(AsDictionaryProjectionEntryQueryStage.withStringKey(key));
        return new StreamEntryQueryBuilderImpl<>(this, entry, ConsumeDictionaryEntryValueQueryStage.INSTANCE);
    }

    @Override
    public StreamEntryQueryBuilder<DictionaryQueryBuilder<T>> putEntry(double key) {
        Tracer.traceCall("DictionaryQueryBuilderImpl#putElement-double", this);
        QueryBuilderNode entry = parentTreeNode.pushChild(AsDictionaryProjectionEntryQueryStage.withFloatKey(key));
        return new StreamEntryQueryBuilderImpl<>(this, entry, ConsumeDictionaryEntryValueQueryStage.INSTANCE);
    }

    @Override
    public StreamEntryQueryBuilder<DictionaryQueryBuilder<T>> putEntry(long key) {
        Tracer.traceCall("DictionaryQueryBuilderImpl#putElement-long", this);
        QueryBuilderNode entry = parentTreeNode.pushChild(AsDictionaryProjectionEntryQueryStage.withIntKey(key));
        return new StreamEntryQueryBuilderImpl<>(this, entry, ConsumeDictionaryEntryValueQueryStage.INSTANCE);
    }

    @Override
    public T endDictionary() {
        Tracer.traceReturn("DictionaryQueryBuilderImpl#endDictionary", this);
        return queryBuilder;

    }

}
