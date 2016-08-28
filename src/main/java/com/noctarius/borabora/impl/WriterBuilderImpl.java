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
import com.noctarius.borabora.spi.SemanticTagBuilderFactory;
import com.noctarius.borabora.spi.codec.CommonTagCodec;
import com.noctarius.borabora.spi.codec.TagEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class WriterBuilderImpl
        implements WriterBuilder {

    private final Map<Class<?>, SemanticTagBuilderFactory> factories = new HashMap<>();
    private final List<TagEncoder> tagEncoders = new ArrayList<>();

    public WriterBuilderImpl() {
        addTagEncoder(CommonTagCodec.INSTANCE);
    }

    @Override
    public WriterBuilder addSemanticTagBuilderFactory(SemanticTagBuilderFactory semanticTagBuilderFactory) {
        factories.put(semanticTagBuilderFactory.semanticTagBuilderType(), semanticTagBuilderFactory);
        return this;
    }

    @Override
    public <V> WriterBuilder addTagEncoder(TagEncoder<V> tagEncoder) {
        Objects.requireNonNull(tagEncoder, "tagEncoder must not be null");
        if (!tagEncoders.contains(tagEncoder)) {
            tagEncoders.add(tagEncoder);
        }
        return this;
    }

    @Override
    public <V> WriterBuilder addTagEncoders(TagEncoder<V> tagEncoder1, TagEncoder<V> tagEncoder2) {
        addTagEncoder(tagEncoder1);
        addTagEncoder(tagEncoder2);
        return this;
    }

    @Override
    public <V> WriterBuilder addTagEncoders(TagEncoder<V> tagEncoder1, TagEncoder<V> tagEncoder2, TagEncoder<V>... tagEncoders) {
        addTagEncoder(tagEncoder1);
        addTagEncoder(tagEncoder2);
        for (TagEncoder tagEncoder : tagEncoders) {
            addTagEncoder(tagEncoder);
        }
        return this;
    }

    @Override
    public WriterBuilder addTagEncoders(Iterable<TagEncoder> tagEncoders) {
        for (TagEncoder tagEncoder : tagEncoders) {
            addTagEncoder(tagEncoder);
        }
        return this;
    }

    @Override
    public Writer build() {
        return new WriterImpl(factories, tagEncoders);
    }

}
