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
package com.noctarius.borabora.spi.builder;

/**
 * The <tt>TagBuilderConsumer</tt> is used to collect and execute semantic tag builder
 * calls against the actual writer. This is normally not considered to be implemented
 * by an SPI user, but there may be situations where it is easier to work with the
 * encoder context directly.
 *
 * @param <B> the builder type
 */
public interface TagBuilderConsumer<B> {

    /**
     * Encodes the previously configured semantic tag ({@link TagSupport#semanticTag(Class)})
     * into the given {@link EncoderContext}.
     *
     * @param encoderContext the encoder context
     * @param builder        the current parent builder
     * @return the builder
     * @see TagSupport
     */
    B execute(EncoderContext encoderContext, B builder);

}
