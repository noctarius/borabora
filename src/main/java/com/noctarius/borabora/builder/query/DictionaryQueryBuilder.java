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
import com.noctarius.borabora.spi.builder.BuilderStackPush;

/**
 * The <tt>DictionaryQueryBuilder</tt> interface is used to create entries in a runtime
 * defined new dictionary instance projection. Each entry is defined by the given key and
 * the sub-query executed before the entry is finished.
 *
 * @param <T> the parent's builder type
 */
public interface DictionaryQueryBuilder<T> {

    /**
     * Defines a new entry using the given <tt>string</tt> typed <tt>key</tt>. The
     * returned {@link StreamEntryQueryBuilder} is used to define the entry's value based
     * on the defined sub-query.
     *
     * @param key the key of the dictionary entry
     * @return a builder to define the entry's value by a sub-query
     */
    @BuilderStackPush
    StreamEntryQueryBuilder<DictionaryQueryBuilder<T>> putEntry(String key);

    /**
     * Defines a new entry using the given <tt>double</tt> typed <tt>key</tt>. The
     * returned {@link StreamEntryQueryBuilder} is used to define the entry's value based
     * on the defined sub-query.
     *
     * @param key the key of the dictionary entry
     * @return a builder to define the entry's value by a sub-query
     */
    @BuilderStackPush
    StreamEntryQueryBuilder<DictionaryQueryBuilder<T>> putEntry(double key);

    /**
     * Defines a new entry using the given <tt>long</tt> typed <tt>key</tt>. The
     * returned {@link StreamEntryQueryBuilder} is used to define the entry's value based
     * on the defined sub-query.
     *
     * @param key the key of the dictionary entry
     * @return a builder to define the entry's value by a sub-query
     */
    @BuilderStackPush
    StreamEntryQueryBuilder<DictionaryQueryBuilder<T>> putEntry(long key);

    /**
     * Finalizes the runtime defined dictionary and returns to the parent builder
     *
     * @return the parent builder instance
     */
    @BuilderStackPop
    T endDictionary();

}
