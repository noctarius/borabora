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

import com.noctarius.borabora.builder.GraphQueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

final class GraphQueryBuilderImpl
        implements GraphQueryBuilder {

    private List<GraphQuery> graphQueries = new ArrayList<>();

    @Override
    public GraphQueryBuilder sequence(int index) {
        graphQueries.add(new SequenceGraphQuery(index));
        return this;
    }

    @Override
    public GraphQueryBuilder dictionary(Predicate<Value> predicate) {
        graphQueries.add(new DictionaryGraphQuery(predicate));
        return this;
    }

    @Override
    public GraphQuery build() {
        if (graphQueries.size() == 0) {
            return new SequenceGraphQuery(-1);
        }
        return new ChainGraphQuery(graphQueries);
    }

}
