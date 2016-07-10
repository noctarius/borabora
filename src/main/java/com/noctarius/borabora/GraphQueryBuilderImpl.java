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
import com.noctarius.borabora.builder.GraphQueryBuilder;
import com.noctarius.borabora.builder.SequenceGraphQueryBuilder;
import com.noctarius.borabora.builder.StreamGraphQueryBuilder;
import com.noctarius.borabora.spi.SelectStatementStrategy;
import com.noctarius.borabora.spi.TypeSpec;

import java.util.ArrayList;
import java.util.function.Predicate;

final class GraphQueryBuilderImpl
        extends AbstractGraphQueryBuilder
        implements StreamGraphQueryBuilder {

    private static final Query STREAM_INDEX_ZERO_GRAPH_QUERY = new StreamQuery(0);

    GraphQueryBuilderImpl(SelectStatementStrategy selectStatementStrategy) {
        super(new ArrayList<>(), selectStatementStrategy);
    }

    @Override
    public GraphQueryBuilder stream(long offset) {
        graphQueries.add(new StreamQuery(offset));
        return this;
    }

    @Override
    public DictionaryGraphQueryBuilder<GraphQueryBuilder> asDictionary() {
        return selectStatementStrategy.asDictionary(this, graphQueries);
    }

    @Override
    public SequenceGraphQueryBuilder<GraphQueryBuilder> asSequence() {
        return selectStatementStrategy.asSequence(this, graphQueries);
    }

    @Override
    public Query build() {
        if (graphQueries.size() == 0) {
            graphQueries.add(STREAM_INDEX_ZERO_GRAPH_QUERY);
        } else if (!(graphQueries.get(0) instanceof StreamQuery)) {
            graphQueries.add(0, STREAM_INDEX_ZERO_GRAPH_QUERY);
        }
        return new ChainQuery(graphQueries, selectStatementStrategy);
    }

    @Override
    public GraphQueryBuilder sequence(long index) {
        sequence0(index);
        return this;
    }

    @Override
    public GraphQueryBuilder dictionary(Predicate<Value> predicate) {
        dictionary0(predicate);
        return this;
    }

    @Override
    public GraphQueryBuilder dictionary(String key) {
        dictionary0(key);
        return this;
    }

    @Override
    public GraphQueryBuilder dictionary(double key) {
        dictionary0(key);
        return this;
    }

    @Override
    public GraphQueryBuilder dictionary(long key) {
        dictionary0(key);
        return this;
    }

    @Override
    public GraphQueryBuilder nullOrType(TypeSpec typeSpec) {
        nullOrType0(typeSpec);
        return this;
    }

    @Override
    public GraphQueryBuilder requireType(TypeSpec typeSpec) {
        requireType0(typeSpec);
        return this;
    }
}
