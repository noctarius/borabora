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

import com.noctarius.borabora.builder.StreamQueryBuilder;
import com.noctarius.borabora.spi.QueryContext;
import com.noctarius.borabora.spi.SelectStatementStrategy;

public interface Query {

    default boolean isStreamQueryCapable() {
        return true;
    }

    long access(long offset, QueryContext queryContext);

    static StreamQueryBuilder newBuilder() {
        return newBuilder(true);
    }

    static StreamQueryBuilder newBuilder(boolean binarySelectStatement) {
        return newBuilder(binarySelectStatement ? //
                BinarySelectStatementStrategy.INSTANCE : ObjectSelectStatementStrategy.INSTANCE);
    }

    static StreamQueryBuilder newBuilder(SelectStatementStrategy selectStatementStrategy) {
        return new QueryBuilderImpl(selectStatementStrategy);
    }

    static boolean equals(Object first, Object second) {
        String name = first.getClass().getName();
        String otherName = second.getClass().getName();

        if (name.contains("$$Lambda$") && !otherName.contains("$$Lambda$") //
                || !name.contains("$$Lambda$") && otherName.contains("$$Lambda$")) {

            return false;
        }

        if (!name.contains("$$Lambda$") && !otherName.contains("$$Lambda$")) {
            return first.equals(second);
        }

        int nameIndex = name.indexOf("$$Lambda$");
        int otherNameIndex = otherName.indexOf("$$Lambda$");

        int nameEndIndex = name.indexOf('/', nameIndex);
        int otherNameEndIndex = name.indexOf('/', otherNameIndex);

        return name.substring(nameIndex, nameEndIndex).equals(otherName.substring(otherNameIndex, otherNameEndIndex));
    }

}
