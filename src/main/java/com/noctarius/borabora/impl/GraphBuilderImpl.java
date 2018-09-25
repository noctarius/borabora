/*
 * Copyright (c) 2016-2018, Christoph Engelbert (aka noctarius) and
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

import com.noctarius.borabora.builder.encoder.GraphBuilder;
import com.noctarius.borabora.spi.builder.AbstractStreamValueBuilder;
import com.noctarius.borabora.spi.builder.EncoderContext;

final class GraphBuilderImpl
        extends AbstractStreamValueBuilder<GraphBuilder>
        implements GraphBuilder {

    GraphBuilderImpl(EncoderContext encoderContext) {
        super(encoderContext);
    }

    @Override
    public void finishStream() {
    }

}
