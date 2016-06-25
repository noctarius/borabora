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

import com.noctarius.borabora.builder.SequenceGraphQueryBuilder;
import com.noctarius.borabora.builder.StreamEntryGraphQueryBuilder;

import java.util.List;

class SequenceGraphQueryBuilderImpl<T>
        implements SequenceGraphQueryBuilder<T> {

    private final T queryBuilder;
    private final List<GraphQuery> graphQueries;

    SequenceGraphQueryBuilderImpl(T queryBuilder, List<GraphQuery> graphQueries) {
        this.queryBuilder = queryBuilder;
        this.graphQueries = graphQueries;
    }

    @Override
    public StreamEntryGraphQueryBuilder<SequenceGraphQueryBuilder<T>> putEntry() {
        graphQueries.add(ResetOffsetGraphQuery.INSTANCE);
        return new StreamEntryGraphQueryBuilderImpl<>(this, graphQueries, EndSequenceEntryGraphQuery.INSTANCE);
    }

    @Override
    public T endSequence() {
        graphQueries.add(EndSequenceGraphQuery.INSTANCE);
        return queryBuilder;
    }

}
