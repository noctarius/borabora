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
import com.noctarius.borabora.spi.codec.CommonTagCodec;
import com.noctarius.borabora.spi.codec.TagBuilderFactory;
import com.noctarius.borabora.spi.codec.TagEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class WriterBuilderImpl
        implements WriterBuilder {

    private final List<TagBuilderFactory> factories = new ArrayList<>();

    @Override
    public WriterBuilder addSemanticTagBuilderFactory(TagBuilderFactory semanticTagBuilderFactory) {
        Objects.requireNonNull(semanticTagBuilderFactory, "semanticTagBuilderFactory must not be null");
        if (!factories.contains(semanticTagBuilderFactory)) {
            factories.add(semanticTagBuilderFactory);
        }
        return this;
    }

    @Override
    public WriterBuilder addSemanticTagBuilderFactories(TagBuilderFactory semanticTagBuilderFactory1,
                                                        TagBuilderFactory semanticTagBuilderFactory2) {

        addSemanticTagBuilderFactory(semanticTagBuilderFactory1);
        addSemanticTagBuilderFactory(semanticTagBuilderFactory2);
        return this;
    }

    @Override
    public WriterBuilder addSemanticTagBuilderFactories(TagBuilderFactory semanticTagBuilderFactory1,
                                                        TagBuilderFactory semanticTagBuilderFactory2,
                                                        TagBuilderFactory... semanticTagBuilderFactories) {

        addSemanticTagBuilderFactory(semanticTagBuilderFactory1);
        addSemanticTagBuilderFactory(semanticTagBuilderFactory2);
        for (TagBuilderFactory semanticTagBuilderFactory : semanticTagBuilderFactories) {
            addSemanticTagBuilderFactory(semanticTagBuilderFactory);
        }
        return this;
    }

    @Override
    public WriterBuilder addSemanticTagBuilderFactories(Iterable<TagBuilderFactory> semanticTagBuilderFactories) {
        for (TagBuilderFactory semanticTagBuilderFactory : semanticTagBuilderFactories) {
            addSemanticTagBuilderFactory(semanticTagBuilderFactory);
        }
        return this;
    }

    @Override
    public Writer build() {
        Map<Class<?>, TagBuilderFactory> factoryMap = new HashMap<>();
        List<TagEncoder> tagEncoders = new ArrayList<>();
        tagEncoders.add(CommonTagCodec.INSTANCE);

        for (TagBuilderFactory factory : factories) {
            factoryMap.put(factory.tagBuilderType(), factory);

            TagEncoder tagEncoder = factory.tagEncoder();
            if (tagEncoder != null && !tagEncoders.contains(tagEncoder)) {
                tagEncoders.add(tagEncoder);
            }
        }
        return new WriterImpl(factoryMap, tagEncoders);
    }

}
