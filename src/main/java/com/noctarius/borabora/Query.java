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

import com.noctarius.borabora.builder.QueryBuilderBuilder;
import com.noctarius.borabora.builder.StreamQueryBuilder;
import com.noctarius.borabora.impl.QueryBuilderBuilderImpl;
import com.noctarius.borabora.spi.pipeline.QueryPipeline;
import com.noctarius.borabora.spi.query.BinaryProjectionStrategy;
import com.noctarius.borabora.spi.query.ProjectionStrategy;

public interface Query {

    QueryPipeline newQueryPipeline();

    void printQueryGraph();

    static QueryBuilderBuilder configureBuilder() {
        return new QueryBuilderBuilderImpl();
    }

    static StreamQueryBuilder newBuilder() {
        return newBuilder(BinaryProjectionStrategy.INSTANCE);
    }

    static StreamQueryBuilder newBuilder(ProjectionStrategy projectionStrategy) {
        return configureBuilder().withProjectionStrategy(projectionStrategy).newBuilder();
    }

}
