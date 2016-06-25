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
import com.noctarius.borabora.builder.EntryGraphQueryBuilder;
import com.noctarius.borabora.builder.SequenceGraphQueryBuilder;
import com.noctarius.borabora.builder.StreamEntryGraphQueryBuilder;
import com.noctarius.borabora.spi.TypeSpec;

import java.util.List;
import java.util.function.Predicate;

class StreamEntryGraphQueryBuilderImpl<T>
        extends AbstractGraphQueryBuilder
        implements StreamEntryGraphQueryBuilder<T> {

    private final T queryBuilder;
    private final List<GraphQuery> graphQueries;
    private final GraphQuery endEntryGraphQuery;

    StreamEntryGraphQueryBuilderImpl(T queryBuilder, List<GraphQuery> graphQueries, GraphQuery endEntryGraphQuery) {
        super(graphQueries);
        this.queryBuilder = queryBuilder;
        this.graphQueries = graphQueries;
        this.endEntryGraphQuery = endEntryGraphQuery;
    }

    @Override
    public EntryGraphQueryBuilder<T> stream(long offset) {
        graphQueries.add(new StreamGraphQuery(offset));
        return this;
    }

    @Override
    public DictionaryGraphQueryBuilder<EntryGraphQueryBuilder<T>> asDictionary() {
        return new DictionaryGraphQueryBuilderImpl<>(this, graphQueries);
    }

    @Override
    public SequenceGraphQueryBuilder<EntryGraphQueryBuilder<T>> asSequence() {
        return new SequenceGraphQueryBuilderImpl<>(this, graphQueries);
    }

    @Override
    public T endEntry() {
        graphQueries.add(endEntryGraphQuery);
        return queryBuilder;
    }

    @Override
    public EntryGraphQueryBuilder<T> sequence(long index) {
        sequence0(index);
        return this;
    }

    @Override
    public EntryGraphQueryBuilder<T> dictionary(Predicate<Value> predicate) {
        dictionary0(predicate);
        return this;
    }

    @Override
    public EntryGraphQueryBuilder<T> dictionary(String key) {
        dictionary0(key);
        return this;
    }

    @Override
    public EntryGraphQueryBuilder<T> dictionary(double key) {
        dictionary0(key);
        return this;
    }

    @Override
    public EntryGraphQueryBuilder<T> dictionary(long key) {
        dictionary0(key);
        return this;
    }

    @Override
    public EntryGraphQueryBuilder<T> nullOrType(TypeSpec typeSpec) {
        nullOrType0(typeSpec);
        return this;
    }

    @Override
    public EntryGraphQueryBuilder<T> requireType(TypeSpec typeSpec) {
        requireType0(typeSpec);
        return this;
    }

}
