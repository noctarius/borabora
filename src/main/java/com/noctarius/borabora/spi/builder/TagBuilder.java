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
 * The <tt>TagBuilder</tt> interface is the final point of every builder chain
 * for semantic tag builders. The {@link #endSemanticTag()} call has to be the
 * last call to signal that the semantic tag has finished and encoding can now
 * write it in a single go to the underlying stream.
 * <pre>
 * graphBuilder.putTag(
 *     semanticTag( MyTagBuilder.class ).putMyTag( getMyTag() ).endSemanticTag() )
 * ).finishStream();
 * </pre>
 */
public interface TagBuilder {

    /**
     * Signals the end of semantic tag preparation and that encoding the semantic
     * tag is supposed to happen now. The returned {@link TagBuilderConsumer} is
     * meant to be passed to {@link com.noctarius.borabora.builder.encoder.ValueBuilder#putTag(TagBuilderConsumer)}.
     *
     * @param <B> the type of the builder
     * @return the consumer implementation for encoding
     */
    <B> TagBuilderConsumer<B> endSemanticTag();

}
