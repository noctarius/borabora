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
import com.noctarius.borabora.builder.QueryBuilder;
import com.noctarius.borabora.builder.SequenceQueryBuilder;
import com.noctarius.borabora.builder.StreamQueryBuilder;
import com.noctarius.borabora.spi.SelectStatementStrategy;
import com.noctarius.borabora.spi.TypeSpec;

import java.util.ArrayList;
import java.util.function.Predicate;

final class QueryBuilderImpl
        extends AbstractQueryBuilder
        implements StreamQueryBuilder {

    private static final Query STREAM_INDEX_ZERO_GRAPH_QUERY = new StreamQuery(0);

    QueryBuilderImpl(SelectStatementStrategy selectStatementStrategy) {
        super(new ArrayList<>(), selectStatementStrategy);
    }

    @Override
    public QueryBuilder stream(long streamIndex) {
        if (streamIndex < 0) {
            throw new IllegalArgumentException("streamIndex must not be negative");
        }
        graphQueries.add(new StreamQuery(streamIndex));
        return this;
    }

    @Override
    public QueryBuilder multiStream() {
        graphQueries.add(MultiStreamQuery.INSTANCE);
        return this;
    }

    @Override
    public DictionaryQueryBuilder<QueryBuilder> asDictionary() {
        return selectStatementStrategy.asDictionary(this, graphQueries);
    }

    @Override
    public SequenceQueryBuilder<QueryBuilder> asSequence() {
        return selectStatementStrategy.asSequence(this, graphQueries);
    }

    @Override
    public Query build() {
        if (graphQueries.size() == 0) {
            graphQueries.add(STREAM_INDEX_ZERO_GRAPH_QUERY);
        } else if (!(graphQueries.get(0) instanceof StreamQuery)
                && !(graphQueries.get(0) instanceof MultiStreamQuery)) {
            graphQueries.add(0, STREAM_INDEX_ZERO_GRAPH_QUERY);
        }
        return new ChainQuery(graphQueries, selectStatementStrategy);
    }

    @Override
    public QueryBuilder sequence(long index) {
        sequence0(index);
        return this;
    }

    @Override
    public QueryBuilder dictionary(Predicate<Value> predicate) {
        dictionary0(predicate);
        return this;
    }

    @Override
    public QueryBuilder dictionary(String key) {
        dictionary0(key);
        return this;
    }

    @Override
    public QueryBuilder dictionary(double key) {
        dictionary0(key);
        return this;
    }

    @Override
    public QueryBuilder dictionary(long key) {
        dictionary0(key);
        return this;
    }

    @Override
    public QueryBuilder nullOrType(TypeSpec typeSpec) {
        nullOrType0(typeSpec);
        return this;
    }

    @Override
    public QueryBuilder requireType(TypeSpec typeSpec) {
        requireType0(typeSpec);
        return this;
    }
}
