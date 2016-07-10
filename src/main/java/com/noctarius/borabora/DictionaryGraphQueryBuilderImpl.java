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

import com.noctarius.borabora.builder.DictionaryGraphQueryBuilder;
import com.noctarius.borabora.builder.StreamEntryGraphQueryBuilder;
import com.noctarius.borabora.spi.SelectStatementStrategy;

import java.util.List;

class DictionaryGraphQueryBuilderImpl<T>
        implements DictionaryGraphQueryBuilder<T> {

    private final T queryBuilder;
    private final List<Query> graphQueries;
    private SelectStatementStrategy selectStatementStrategy;

    DictionaryGraphQueryBuilderImpl(T queryBuilder, List<Query> graphQueries,
                                    SelectStatementStrategy selectStatementStrategy) {

        this.queryBuilder = queryBuilder;
        this.graphQueries = graphQueries;
        this.selectStatementStrategy = selectStatementStrategy;
    }

    @Override
    public StreamEntryGraphQueryBuilder<DictionaryGraphQueryBuilder<T>> putEntry(String key) {
        return selectStatementStrategy.putDictionaryEntry(key, this, graphQueries);
    }

    @Override
    public StreamEntryGraphQueryBuilder<DictionaryGraphQueryBuilder<T>> putEntry(double key) {
        return selectStatementStrategy.putDictionaryEntry(key, this, graphQueries);
    }

    @Override
    public StreamEntryGraphQueryBuilder<DictionaryGraphQueryBuilder<T>> putEntry(long key) {
        return selectStatementStrategy.putDictionaryEntry(key, this, graphQueries);
    }

    @Override
    public T endDictionary() {
        return selectStatementStrategy.endDictionary(queryBuilder, graphQueries);
    }

}
