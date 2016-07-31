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

import com.noctarius.borabora.Query;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.builder.DictionaryQueryBuilder;
import com.noctarius.borabora.builder.SequenceQueryBuilder;
import com.noctarius.borabora.builder.StreamEntryQueryBuilder;

import java.util.List;

public interface SelectStatementStrategy {

    void beginSelect(QueryContext queryContext);

    Value finalizeSelect(QueryContext queryContext);

    <T> DictionaryQueryBuilder<T> asDictionary(T graphQueryBuilder, List<Query> graphQueries);

    <T> SequenceQueryBuilder<T> asSequence(T graphQueryBuilder, List<Query> graphQueries);

    <T, D extends DictionaryQueryBuilder<T>> StreamEntryQueryBuilder<D> putDictionaryEntry(String key, D queryBuilder,
                                                                                                     List<Query> graphQueries);

    <T, D extends DictionaryQueryBuilder<T>> StreamEntryQueryBuilder<D> putDictionaryEntry(long key, D queryBuilder,
                                                                                                     List<Query> graphQueries);

    <T, D extends DictionaryQueryBuilder<T>> StreamEntryQueryBuilder<D> putDictionaryEntry(double key, D queryBuilder,
                                                                                                     List<Query> graphQueries);

    <T> T endDictionary(T queryBuilder, List<Query> graphQueries);

    <T, S extends SequenceQueryBuilder<T>> StreamEntryQueryBuilder<S> putSequenceEntry(S queryBuilder,
                                                                                                 List<Query> graphQueries);

    <T> T endSequence(T queryBuilder, List<Query> graphQueries);

}
