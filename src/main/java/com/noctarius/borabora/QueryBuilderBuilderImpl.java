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

import com.noctarius.borabora.builder.QueryBuilder;
import com.noctarius.borabora.builder.QueryBuilderBuilder;
import com.noctarius.borabora.spi.QueryOptimizer;
import com.noctarius.borabora.spi.SelectStatementStrategy;

import java.util.ArrayList;
import java.util.List;

class QueryBuilderBuilderImpl
        implements QueryBuilderBuilder {

    private final List<QueryOptimizer> queryOptimizers = new ArrayList<>();

    private SelectStatementStrategy selectStatementStrategy = BinarySelectStatementStrategy.INSTANCE;

    @Override
    public QueryBuilderBuilder withSelectStatementStrategy(SelectStatementStrategy selectStatementStrategy) {
        this.selectStatementStrategy = selectStatementStrategy;
        return this;
    }

    @Override
    public QueryBuilderBuilder addQueryOptimizer(QueryOptimizer queryOptimizer) {
        if (!this.queryOptimizers.contains(queryOptimizer)) {
            this.queryOptimizers.add(queryOptimizer);
        }
        return this;
    }

    @Override
    public QueryBuilder newBuilder() {
        return new QueryBuilderImpl(selectStatementStrategy, queryOptimizers);
    }

}
