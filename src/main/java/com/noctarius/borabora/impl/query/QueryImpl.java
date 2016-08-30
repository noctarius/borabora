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
import com.noctarius.borabora.spi.query.ProjectionStrategy;
import com.noctarius.borabora.spi.query.ProjectionStrategyAware;

public final class QueryImpl
        implements Query, ProjectionStrategyAware {

    private final QueryPipeline queryPipeline;
    private ProjectionStrategy projectionStrategy;

    public QueryImpl(QueryPipeline queryPipeline, ProjectionStrategy projectionStrategy) {
        this.queryPipeline = queryPipeline;
        this.projectionStrategy = projectionStrategy;
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

        if (queryPipeline != null ? !queryPipeline.equals(query.queryPipeline) : query.queryPipeline != null) {
            return false;
        }
        return projectionStrategy != null ? projectionStrategy.equals(query.projectionStrategy) :
                query.projectionStrategy == null;

    }

    @Override
    public int hashCode() {
        int result = queryPipeline != null ? queryPipeline.hashCode() : 0;
        result = 31 * result + (projectionStrategy != null ? projectionStrategy.hashCode() : 0);
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
    public ProjectionStrategy projectionStrategy() {
        return projectionStrategy;
    }

    @Override
    public void printQueryGraph() {
        System.out.println(queryPipeline.printQueryGraph());
    }

}
