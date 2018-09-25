/*
 * Copyright (c) 2016-2018, Christoph Engelbert (aka noctarius) and
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
package com.noctarius.borabora.builder.query;

import com.noctarius.borabora.Query;

/**
 * The <tt>QueryBuilder</tt> interface is used to define a query to search,
 * read and extract a data item inside the CBOR encoded data stream or to
 * execute a projection (runtime sequence or dictionary creation) to generate
 * a new data structure during a query execution.
 */
public interface QueryBuilder
        extends QueryTokenBuilder<QueryBuilder> {

    /**
     * Finalizes the query definition and returns the final {@link Query} instance.
     * This instance is fully thread-safe and can be stored and shared by multiple threads.
     *
     * @return the Query instance representing the actual query defined by the fluent builder
     */
    Query build();

}
