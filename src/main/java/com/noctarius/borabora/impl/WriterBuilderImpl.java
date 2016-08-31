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
package com.noctarius.borabora.impl;

import com.noctarius.borabora.Writer;
import com.noctarius.borabora.builder.WriterBuilder;
import com.noctarius.borabora.spi.codec.TagStrategies;
import com.noctarius.borabora.spi.codec.TagStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class WriterBuilderImpl
        implements WriterBuilder {

    private final List<TagStrategy> tagStrategies = new ArrayList<>(Arrays.asList(TagStrategies.values()));

    @Override
    public WriterBuilder addTagStrategy(TagStrategy tagStrategy) {
        Objects.requireNonNull(tagStrategy, "tagStrategy must not be null");
        if (!tagStrategies.contains(tagStrategy)) {
            tagStrategies.add(tagStrategy);
        }
        return this;
    }

    @Override
    public WriterBuilder addTagStrategies(TagStrategy tagStrategy1, TagStrategy tagStrategy2) {
        addTagStrategy(tagStrategy1);
        addTagStrategy(tagStrategy2);
        return this;
    }

    @Override
    public WriterBuilder addTagStrategies(TagStrategy tagStrategy1, TagStrategy tagStrategy2, TagStrategy... tagStrategies) {
        Objects.requireNonNull(tagStrategies, "tagStrategies must not be null");
        addTagStrategy(tagStrategy1);
        addTagStrategy(tagStrategy2);
        for (TagStrategy tagStrategy : tagStrategies) {
            addTagStrategy(tagStrategy);
        }
        return this;
    }

    @Override
    public WriterBuilder addTagStrategies(Iterable<TagStrategy> semanticTagBuilderFactories) {
        Objects.requireNonNull(tagStrategies, "tagStrategies must not be null");
        for (TagStrategy semanticTagStrategy : semanticTagBuilderFactories) {
            addTagStrategy(semanticTagStrategy);
        }
        return this;
    }

    @Override
    public Writer build() {
        Map<Class<?>, TagStrategy> factoryMap = new HashMap<>();
        for (TagStrategy tagStrategy : tagStrategies) {
            factoryMap.put(tagStrategy.tagBuilderType(), tagStrategy);
        }
        return new WriterImpl(factoryMap);
    }

}
