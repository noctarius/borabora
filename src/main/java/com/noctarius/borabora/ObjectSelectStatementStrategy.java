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
import com.noctarius.borabora.builder.SequenceQueryBuilder;
import com.noctarius.borabora.builder.StreamEntryQueryBuilder;
import com.noctarius.borabora.spi.QueryContext;
import com.noctarius.borabora.spi.SelectStatementStrategy;

import java.util.List;

public class ObjectSelectStatementStrategy
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
    public <T> DictionaryQueryBuilder<T> asDictionary(T graphQueryBuilder, List<Query> graphQueries) {
        graphQueries.add(AsDictionaryQuery.INSTANCE);
        return new DictionaryQueryBuilderImpl<>(graphQueryBuilder, graphQueries, this);
    }

    @Override
    public <T> SequenceQueryBuilder<T> asSequence(T graphQueryBuilder, List<Query> graphQueries) {
        graphQueries.add(AsSequenceQuery.INSTANCE);
        return new SequenceQueryBuilderImpl<>(graphQueryBuilder, graphQueries, this);
    }

    @Override
    public <T, D extends DictionaryQueryBuilder<T>> StreamEntryQueryBuilder<D> putDictionaryEntry(String key,
                                                                                                            D queryBuilder,
                                                                                                            List<Query> graphQueries) {

        return putDictionaryEntry(new PutEntryQuery(key), queryBuilder, graphQueries);
    }

    @Override
    public <T, D extends DictionaryQueryBuilder<T>> StreamEntryQueryBuilder<D> putDictionaryEntry(long key,
                                                                                                            D queryBuilder,
                                                                                                            List<Query> graphQueries) {

        return putDictionaryEntry(new PutEntryQuery(key), queryBuilder, graphQueries);
    }

    @Override
    public <T, D extends DictionaryQueryBuilder<T>> StreamEntryQueryBuilder<D> putDictionaryEntry(double key,
                                                                                                            D queryBuilder,
                                                                                                            List<Query> graphQueries) {

        return putDictionaryEntry(new PutEntryQuery(key), queryBuilder, graphQueries);
    }

    @Override
    public <T> T endDictionary(T queryBuilder, List<Query> graphQueries) {
        graphQueries.add(EndDictionaryQuery.INSTANCE);
        return queryBuilder;
    }

    @Override
    public <T, S extends SequenceQueryBuilder<T>> StreamEntryQueryBuilder<S> putSequenceEntry(S queryBuilder,
                                                                                                        List<Query> graphQueries) {

        graphQueries.add(ResetOffsetQuery.INSTANCE);
        return new StreamEntryQueryBuilderImpl<>(queryBuilder, graphQueries, EndSequenceEntryQuery.INSTANCE, this);
    }

    @Override
    public <T> T endSequence(T queryBuilder, List<Query> graphQueries) {
        graphQueries.add(EndSequenceQuery.INSTANCE);
        return queryBuilder;
    }

    private <T, D extends DictionaryQueryBuilder<T>> StreamEntryQueryBuilder<D> putDictionaryEntry(
            Query putKeyQuery, D queryBuilder, List<Query> graphQueries) {

        graphQueries.add(putKeyQuery);
        graphQueries.add(ResetOffsetQuery.INSTANCE);
        return new StreamEntryQueryBuilderImpl<>(queryBuilder, graphQueries, EndDictionaryEntryQuery.INSTANCE, this);
    }

}
