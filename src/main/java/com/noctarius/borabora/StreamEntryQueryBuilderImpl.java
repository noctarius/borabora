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
import com.noctarius.borabora.builder.EntryQueryBuilder;
import com.noctarius.borabora.builder.SequenceQueryBuilder;
import com.noctarius.borabora.builder.StreamEntryQueryBuilder;
import com.noctarius.borabora.spi.SelectStatementStrategy;
import com.noctarius.borabora.spi.TypeSpec;

import java.util.List;
import java.util.function.Predicate;

class StreamEntryQueryBuilderImpl<T>
        extends AbstractQueryBuilder
        implements StreamEntryQueryBuilder<T> {

    private final T queryBuilder;
    private final List<Query> graphQueries;
    private final Query endEntryQuery;

    StreamEntryQueryBuilderImpl(T queryBuilder, List<Query> graphQueries, Query endEntryQuery,
                                     SelectStatementStrategy selectStatementStrategy) {

        super(graphQueries, selectStatementStrategy);
        this.queryBuilder = queryBuilder;
        this.graphQueries = graphQueries;
        this.endEntryQuery = endEntryQuery;
    }

    @Override
    public EntryQueryBuilder<T> stream(long offset) {
        graphQueries.add(new StreamQuery(offset));
        return this;
    }

    @Override
    public DictionaryQueryBuilder<EntryQueryBuilder<T>> asDictionary() {
        return selectStatementStrategy.asDictionary(this, graphQueries);
    }

    @Override
    public SequenceQueryBuilder<EntryQueryBuilder<T>> asSequence() {
        return selectStatementStrategy.asSequence(this, graphQueries);
    }

    @Override
    public T endEntry() {
        graphQueries.add(endEntryQuery);
        return queryBuilder;
    }

    @Override
    public EntryQueryBuilder<T> sequence(long index) {
        sequence0(index);
        return this;
    }

    @Override
    public EntryQueryBuilder<T> dictionary(Predicate<Value> predicate) {
        dictionary0(predicate);
        return this;
    }

    @Override
    public EntryQueryBuilder<T> dictionary(String key) {
        dictionary0(key);
        return this;
    }

    @Override
    public EntryQueryBuilder<T> dictionary(double key) {
        dictionary0(key);
        return this;
    }

    @Override
    public EntryQueryBuilder<T> dictionary(long key) {
        dictionary0(key);
        return this;
    }

    @Override
    public EntryQueryBuilder<T> nullOrType(TypeSpec typeSpec) {
        nullOrType0(typeSpec);
        return this;
    }

    @Override
    public EntryQueryBuilder<T> requireType(TypeSpec typeSpec) {
        requireType0(typeSpec);
        return this;
    }

}
