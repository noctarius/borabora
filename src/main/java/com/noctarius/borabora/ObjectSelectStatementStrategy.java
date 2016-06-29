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
import com.noctarius.borabora.builder.SequenceGraphQueryBuilder;
import com.noctarius.borabora.builder.StreamEntryGraphQueryBuilder;
import com.noctarius.borabora.spi.QueryContext;
import com.noctarius.borabora.spi.SelectStatementStrategy;

import java.util.List;

class ObjectSelectStatementStrategy
        implements SelectStatementStrategy {

    public static final SelectStatementStrategy INSTANCE = new ObjectSelectStatementStrategy();

    private ObjectSelectStatementStrategy() {
    }

    @Override
    public void beginSelect(QueryContext queryContext) {
    }

    @Override
    public Value finalizeSelect(QueryContext queryContext) {
        return queryContext.queryStackPop();
    }

    @Override
    public <T> DictionaryGraphQueryBuilder<T> asDictionary(T graphQueryBuilder, List<GraphQuery> graphQueries) {
        graphQueries.add(new AsDictionaryGraphQuery());
        return new DictionaryGraphQueryBuilderImpl<>(graphQueryBuilder, graphQueries, this);
    }

    @Override
    public <T> SequenceGraphQueryBuilder<T> asSequence(T graphQueryBuilder, List<GraphQuery> graphQueries) {
        graphQueries.add(new AsSequenceGraphQuery());
        return new SequenceGraphQueryBuilderImpl<>(graphQueryBuilder, graphQueries, this);
    }

    @Override
    public <T, D extends DictionaryGraphQueryBuilder<T>> StreamEntryGraphQueryBuilder<D> putDictionaryEntry(String key,
                                                                                                            D queryBuilder,
                                                                                                            List<GraphQuery> graphQueries) {

        return putDictionaryEntry(new PutEntryGraphQuery(key), queryBuilder, graphQueries);
    }

    @Override
    public <T, D extends DictionaryGraphQueryBuilder<T>> StreamEntryGraphQueryBuilder<D> putDictionaryEntry(long key,
                                                                                                            D queryBuilder,
                                                                                                            List<GraphQuery> graphQueries) {

        return putDictionaryEntry(new PutEntryGraphQuery(key), queryBuilder, graphQueries);
    }

    @Override
    public <T, D extends DictionaryGraphQueryBuilder<T>> StreamEntryGraphQueryBuilder<D> putDictionaryEntry(double key,
                                                                                                            D queryBuilder,
                                                                                                            List<GraphQuery> graphQueries) {

        return putDictionaryEntry(new PutEntryGraphQuery(key), queryBuilder, graphQueries);
    }

    @Override
    public <T> T endDictionary(T queryBuilder, List<GraphQuery> graphQueries) {
        graphQueries.add(EndDictionaryGraphQuery.INSTANCE);
        return queryBuilder;
    }

    @Override
    public <T, S extends SequenceGraphQueryBuilder<T>> StreamEntryGraphQueryBuilder<S> putSequenceEntry(S queryBuilder,
                                                                                                        List<GraphQuery> graphQueries) {

        graphQueries.add(ResetOffsetGraphQuery.INSTANCE);
        return new StreamEntryGraphQueryBuilderImpl<>(queryBuilder, graphQueries, EndSequenceEntryGraphQuery.INSTANCE, this);
    }

    @Override
    public <T> T endSequence(T queryBuilder, List<GraphQuery> graphQueries) {
        graphQueries.add(EndSequenceGraphQuery.INSTANCE);
        return queryBuilder;
    }

    private <T, D extends DictionaryGraphQueryBuilder<T>> StreamEntryGraphQueryBuilder<D> putDictionaryEntry(
            GraphQuery putKeyGraphQuery, D queryBuilder, List<GraphQuery> graphQueries) {

        graphQueries.add(putKeyGraphQuery);
        graphQueries.add(ResetOffsetGraphQuery.INSTANCE);
        return new StreamEntryGraphQueryBuilderImpl<>(queryBuilder, graphQueries, EndDictionaryEntryGraphQuery.INSTANCE, this);
    }

}
