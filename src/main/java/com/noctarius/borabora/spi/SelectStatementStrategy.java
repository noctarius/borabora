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
package com.noctarius.borabora.spi;

import com.noctarius.borabora.GraphQuery;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.builder.DictionaryGraphQueryBuilder;
import com.noctarius.borabora.builder.GraphQueryBuilder;
import com.noctarius.borabora.builder.SequenceGraphQueryBuilder;
import com.noctarius.borabora.builder.StreamEntryGraphQueryBuilder;

import java.util.List;

public interface SelectStatementStrategy {

    Value finalizeSelect(QueryContext queryContext);

    DictionaryGraphQueryBuilder<GraphQueryBuilder> asDictionary(GraphQueryBuilder graphQueryBuilder,
                                                                List<GraphQuery> graphQueries);

    SequenceGraphQueryBuilder<GraphQueryBuilder> asSequence(GraphQueryBuilder graphQueryBuilder, List<GraphQuery> graphQueries);

    <T, D extends DictionaryGraphQueryBuilder<T>> StreamEntryGraphQueryBuilder<D> putDictionaryEntry(String key, D queryBuilder,
                                                                                                     List<GraphQuery> graphQueries);

    <T, D extends DictionaryGraphQueryBuilder<T>> StreamEntryGraphQueryBuilder<D> putDictionaryEntry(long key, D queryBuilder,
                                                                                                     List<GraphQuery> graphQueries);

    <T, D extends DictionaryGraphQueryBuilder<T>> StreamEntryGraphQueryBuilder<D> putDictionaryEntry(double key, D queryBuilder,
                                                                                                     List<GraphQuery> graphQueries);

    <T> T endDictionary(T queryBuilder, List<GraphQuery> graphQueries);

    <T, S extends SequenceGraphQueryBuilder<T>> StreamEntryGraphQueryBuilder<S> putSequenceEntry(S queryBuilder,
                                                                                                 List<GraphQuery> graphQueries);

    <T> T endSequence(T queryBuilder, List<GraphQuery> graphQueries);

}
