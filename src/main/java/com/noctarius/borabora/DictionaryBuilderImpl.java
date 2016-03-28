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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class DictionaryBuilderImpl<B>
        implements DictionaryBuilder<B> {

    private final Map<Object, Object> dictionary;
    private final List<Object> outerValues;
    private final int maxElements;
    private final B builder;

    DictionaryBuilderImpl(B builder, List<Object> outerValues) {
        this(-1, builder, outerValues);
    }

    DictionaryBuilderImpl(int maxElements, B builder, List<Object> outerValues) {
        this.dictionary = maxElements > -1 ? new HashMap<>(maxElements) : new HashMap<>();
        this.outerValues = outerValues;
        this.maxElements = maxElements;
        this.builder = builder;
    }

    @Override
    public DictionaryEntryBuilder<B> putEntry() {
        validate();
        return new DictionaryEntryBuilderImpl<>(this, dictionary);
    }

    @Override
    public B endDictionary() {
        outerValues.add(dictionary);
        return builder;
    }

    private void validate() {
        if (maxElements > -1 && dictionary.size() >= maxElements) {
            throw new IllegalStateException("Cannot add another element, maximum element count reached");
        }
    }

}
