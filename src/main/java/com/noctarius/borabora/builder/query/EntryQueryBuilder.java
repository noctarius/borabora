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
package com.noctarius.borabora.builder.query;

import com.noctarius.borabora.spi.builder.BuilderStackPop;

/**
 * The <tt>EntryQueryBuilder</tt> interface is used to create entries in a runtime
 * defined new entry instance using the to be defined sub-query executed before the
 * entry is finished.
 *
 * @param <T> the parent's builder type
 */
public interface EntryQueryBuilder<T>
        extends QueryTokenBuilder<EntryQueryBuilder<T>> {

    /**
     * Finalizes the runtime defined entry and returns to the parent builder
     *
     * @return the parent builder instance
     */
    @BuilderStackPop
    T endEntry();

}
