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

import com.noctarius.borabora.Output;
import com.noctarius.borabora.Writer;
import com.noctarius.borabora.builder.GraphBuilder;
import com.noctarius.borabora.spi.codec.TagBuilderFactory;
import com.noctarius.borabora.spi.codec.EncoderContext;
import com.noctarius.borabora.spi.codec.TagEncoder;

import java.util.List;
import java.util.Map;

final class WriterImpl
        implements Writer {

    private final List<TagEncoder> tagEncoders;
    private final Map<Class<?>, TagBuilderFactory> factories;

    WriterImpl(Map<Class<?>, TagBuilderFactory> factories, List<TagEncoder> tagEncoders) {
        this.factories = factories;
        this.tagEncoders = tagEncoders;
    }

    @Override
    public GraphBuilder newGraphBuilder(Output output) {
        EncoderContext encoderContext = new EncoderContextImpl(output, tagEncoders, factories);
        return new GraphBuilderImpl(encoderContext);
    }

}
