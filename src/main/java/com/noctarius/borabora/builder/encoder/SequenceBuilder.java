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
package com.noctarius.borabora.builder.encoder;

import com.noctarius.borabora.spi.builder.BuilderStackPop;

/**
 * The <tt>SequenceBuilder</tt> interface is used to create sequence (array) elements
 * in the generated CBOR stream. A SequenceBuilder instance is retrieved by calling
 * {@link ValueBuilder#putSequence()} ()} or {@link ValueBuilder#putSequence(long)} for
 * either an indefinite or fixed size sequence.
 *
 * @param <B> the parent builder's type
 */
public interface SequenceBuilder<B>
        extends ValueBuilder<SequenceBuilder<B>> {

    /**
     * Ends the current sequence. In case of an indefinite sequence, it will also write
     * the closing tag to the generated CBOR stream. In case of a fixed size sequence, the
     * number of created elements is tested and if the number is less than the configured
     * number of elements expected, an {@link IllegalStateException} is thrown.
     *
     * @return the parent builder
     */
    @BuilderStackPop
    B endSequence();

}
