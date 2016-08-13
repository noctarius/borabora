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
import java.util.Arrays;
import java.util.List;

public final class WriterBuilderImpl
        implements WriterBuilder {

    private final List<TagEncoder> tagEncoders = new ArrayList<>();

    @Override
    public <V> WriterBuilder withTagEncoder(TagEncoder<V> tagEncoder) {
        tagEncoders.add(tagEncoder);
        return this;
    }

    @Override
    public <V> WriterBuilder withTagEncoder(TagEncoder<V> tagEncoder1, TagEncoder<V> tagEncoder2) {
        tagEncoders.add(tagEncoder1);
        tagEncoders.add(tagEncoder2);
        return this;
    }

    @Override
    public <V> WriterBuilder withTagEncoder(TagEncoder<V> tagEncoder1, TagEncoder<V> tagEncoder2, TagEncoder<V>... tagEncoders) {
        this.tagEncoders.add(tagEncoder1);
        this.tagEncoders.add(tagEncoder2);
        this.tagEncoders.addAll(Arrays.asList(tagEncoders));
        return this;
    }

    @Override
    public Writer build() {
        return new WriterImpl();
    }

}
