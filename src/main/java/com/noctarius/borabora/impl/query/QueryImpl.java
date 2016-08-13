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
package com.noctarius.borabora.impl.query;

import com.noctarius.borabora.Query;
import com.noctarius.borabora.spi.pipeline.QueryPipeline;
import com.noctarius.borabora.spi.query.SelectStatementStrategy;
import com.noctarius.borabora.spi.query.SelectStatementStrategyAware;

public final class QueryImpl
        implements Query, SelectStatementStrategyAware {

    private final boolean streamingCapable;
    private final QueryPipeline queryPipeline;
    private SelectStatementStrategy selectStatementStrategy;

    public QueryImpl(QueryPipeline queryPipeline, SelectStatementStrategy selectStatementStrategy) {
        this.queryPipeline = queryPipeline;
        this.streamingCapable = queryPipeline.isStreamQueryCapable();
        this.selectStatementStrategy = selectStatementStrategy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QueryImpl)) {
            return false;
        }

        QueryImpl query = (QueryImpl) o;

        if (streamingCapable != query.streamingCapable) {
            return false;
        }
        if (queryPipeline != null ? !queryPipeline.equals(query.queryPipeline) : query.queryPipeline != null) {
            return false;
        }
        return selectStatementStrategy != null ? selectStatementStrategy.equals(query.selectStatementStrategy) :
                query.selectStatementStrategy == null;

    }

    @Override
    public int hashCode() {
        int result = (streamingCapable ? 1 : 0);
        result = 31 * result + (queryPipeline != null ? queryPipeline.hashCode() : 0);
        result = 31 * result + (selectStatementStrategy != null ? selectStatementStrategy.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Query{" + "queryPipeline=" + queryPipeline + '}';
    }

    @Override
    public QueryPipeline newQueryPipeline() {
        return queryPipeline;
    }

    @Override
    public SelectStatementStrategy selectStatementStrategy() {
        return selectStatementStrategy;
    }

    @Override
    public void printQueryGraph() {
        System.out.println(queryPipeline.printQueryGraph());
    }

}
