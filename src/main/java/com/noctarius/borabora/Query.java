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
import com.noctarius.borabora.builder.query.QueryBuilder;
import com.noctarius.borabora.builder.query.StreamQueryBuilder;
import com.noctarius.borabora.impl.QueryBuilderBuilderImpl;
import com.noctarius.borabora.spi.query.BinaryProjectionStrategy;
import com.noctarius.borabora.spi.query.pipeline.QueryPipeline;

/**
 * The <tt>Query</tt> interface represents any kind of prepared statement like query to be
 * executed against any of the {@link Parser} data querying or extraction methods. Query
 * instances can be created by either of the following ways:
 * <ul>
 * <li>{@link Parser#prepareQuery(String)}</li>
 * <li>{@link Query#newBuilder()} and {@link QueryBuilder#build()}</li>
 * <li>{@link Query#configureBuilder()} and {@link QueryBuilder#build()}</li>
 * </ul>
 * <p>The resulting Query instances of any of the above ways are fully stateless and
 * thread-safe and can be used concurrently by multiple threads and stored into static
 * final fields without any further issues.</p>
 *
 * @see Parser
 * @see QueryBuilder
 * @see QueryBuilderBuilder
 * @see Input
 */
public interface Query {

    /**
     * Returns a new {@link QueryPipeline} instance representing the actual query's
     * optimized execution plan. Query implementations are free to cache the
     * QueryPipeline instance in case it is known to be stateless, however this is
     * not required and completely depends on the implementation details. Anyhow
     * expecting new instances is also not recommended and not guaranteed to work.
     *
     * @return a new or cache QueryPipeline instance representing this query
     */
    QueryPipeline newQueryPipeline();

    /**
     * Prints the query graph (execution plan) of this query instance.
     */
    void printQueryGraph();

    /**
     * Returns a new {@link QueryBuilderBuilder} instance to pre-configure the actual
     * {@link QueryBuilder} with certain special abilities or configurations.
     *
     * @return a new configuration builder
     */
    static QueryBuilderBuilder configureBuilder() {
        return new QueryBuilderBuilderImpl();
    }

    /**
     * Returns a new {@link QueryBuilder} instance pre-configured with the
     * {@link BinaryProjectionStrategy} in case of projections being used. Using this method
     * is equivalent to:
     * <pre>
     *     Query.configureBuilder().withProjectionStrategy(BinaryProjectionStrategy.INSTANCE).newBuilder();
     * </pre>
     *
     * @return a binary projection strategy pre-configured QueryBuilder instance
     */
    static StreamQueryBuilder newBuilder() {
        return configureBuilder().newBuilder();
    }

}
