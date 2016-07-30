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

import com.noctarius.borabora.spi.QueryContext;
import com.noctarius.borabora.spi.SelectStatementStrategy;
import com.noctarius.borabora.spi.SelectStatementStrategyAware;

import java.util.Collections;
import java.util.List;

import static com.noctarius.borabora.spi.Constants.QUERY_RETURN_CODE_NULL;

final class ChainQuery
        implements Query, SelectStatementStrategyAware {

    private final List<Query> graphQueries;
    private SelectStatementStrategy selectStatementStrategy;

    ChainQuery(List<Query> graphQueries, SelectStatementStrategy selectStatementStrategy) {
        this.graphQueries = graphQueries;
        this.selectStatementStrategy = selectStatementStrategy;
    }

    @Override
    public long access(long offset, QueryContext queryContext) {
        for (Query query : graphQueries) {
            offset = query.access(offset, queryContext);
            if (offset == QUERY_RETURN_CODE_NULL) {
                return QUERY_RETURN_CODE_NULL;
            }
        }
        return offset;
    }

    @Override
    public String toString() {
        return "ChainGraphQuery{" + "graphQueries=" + graphQueries + '}';
    }

    @Override
    public SelectStatementStrategy selectStatementStrategy() {
        return selectStatementStrategy;
    }

    List<Query> nodes() {
        return Collections.unmodifiableList(graphQueries);
    }

}
