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

import com.noctarius.borabora.builder.DictionaryBuilder;
import com.noctarius.borabora.builder.DictionaryEntryBuilder;

import java.util.Map;

final class DictionaryEntryBuilderImpl<B>
        extends AbstractValueBuilder<DictionaryEntryBuilder<B>>
        implements DictionaryEntryBuilder<B> {

    private final Object NULL_OBJECT = new Object();

    private final Map<Object, Object> outerValues;
    private final DictionaryBuilder<B> builder;

    private Object key;
    private Object value;

    DictionaryEntryBuilderImpl(DictionaryBuilder<B> builder, Map<Object, Object> outerValues) {
        this.outerValues = outerValues;
        this.builder = builder;
    }

    @Override
    public DictionaryBuilder<B> endEntry() {
        outerValues.put(key, value);
        return builder;
    }

    @Override
    protected void validate() {
        if (key != null && (value != null || value == NULL_OBJECT)) {
            throw new IllegalStateException("Dictionary key and value already set");
        }
    }

    @Override
    protected void put(Object value) {
        if (key != null) {
            if (value == null) {
                throw new NullPointerException("Dictionary key cannot be null");
            }
            key = value;
        } else if (this.value == null) {
            this.value = value == null ? NULL_OBJECT : value;
        }
    }

}
