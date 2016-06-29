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

import com.noctarius.borabora.builder.StreamGraphQueryBuilder;
import com.noctarius.borabora.spi.QueryContext;
import com.noctarius.borabora.spi.SelectStatementStrategy;

public interface GraphQuery {

    long access(long offset, QueryContext queryContext);

    static StreamGraphQueryBuilder newBuilder() {
        return newBuilder(true);
    }

    static StreamGraphQueryBuilder newBuilder(boolean binarySelectStatement) {
        return newBuilder(binarySelectStatement ?
                BinarySelectStatementStrategy.INSTANCE : ObjectSelectStatementStrategy.INSTANCE);
    }

    static StreamGraphQueryBuilder newBuilder(SelectStatementStrategy selectStatementStrategy) {
        return new GraphQueryBuilderImpl(selectStatementStrategy);
    }

}
