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

import com.noctarius.borabora.builder.query.QueryBuilder;
import com.noctarius.borabora.spi.query.pipeline.QueryPipeline;

/**
 * The <tt>Query</tt> interface represents any kind of prepared statement like query to be
 * executed against any of the {@link Parser} data querying or extraction methods. Query
 * instances can be created by either of the following ways:
 * <ul>
 * <li>{@link Parser#prepareQuery(String)}</li>
 * <li>{@link Parser#newQueryBuilder()} and {@link QueryBuilder#build()}</li>
 * </ul>
 * <p>The resulting Query instances of any of the above ways are fully stateless and
 * thread-safe and can be used concurrently by multiple threads and stored into static
 * final fields without any further issues.</p>
 * <p>A common example how to query an element from the CBOR stream is shown in the following snippet:</p>
 * <pre>
 *     Parser parser = Parser.newBuilder().build();
 *     Input input = Input.fromByteArray( getByteArray() );
 *     Query query = parser.newQueryBuilder().stream( 0 ).sequence( 10 ).build();
 *     Value value = parser.read( input, query );
 * </pre>
 *
 * @see Parser
 * @see QueryBuilder
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

}
