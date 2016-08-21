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
import com.noctarius.borabora.spi.codec.TagEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class WriterBuilderImpl
        implements WriterBuilder {

    private final List<TagEncoder> tagEncoders = new ArrayList<>();

    @Override
    public <V> WriterBuilder withTagEncoder(TagEncoder<V> tagEncoder) {
        Objects.requireNonNull(tagEncoder, "tagEncoder must not be null");
        if (!tagEncoders.contains(tagEncoder)) {
            tagEncoders.add(tagEncoder);
        }
        return this;
    }

    @Override
    public <V> WriterBuilder withTagEncoder(TagEncoder<V> tagEncoder1, TagEncoder<V> tagEncoder2) {
        withTagEncoder(tagEncoder1);
        withTagEncoder(tagEncoder2);
        return this;
    }

    @Override
    public <V> WriterBuilder withTagEncoder(TagEncoder<V> tagEncoder1, TagEncoder<V> tagEncoder2, TagEncoder<V>... tagEncoders) {
        withTagEncoder(tagEncoder1);
        withTagEncoder(tagEncoder2);
        for (TagEncoder tagEncoder : tagEncoders) {
            withTagEncoder(tagEncoder);
        }
        return this;
    }

    @Override
    public WriterBuilder withTagEncoder(Iterable<TagEncoder> tagEncoders) {
        for (TagEncoder tagEncoder : tagEncoders) {
            withTagEncoder(tagEncoder);
        }
        return this;
    }

    @Override
    public Writer build() {
        return new WriterImpl(tagEncoders);
    }

}
