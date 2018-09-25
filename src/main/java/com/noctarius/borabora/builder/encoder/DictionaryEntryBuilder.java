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
 * The <tt>DictionaryEntryBuilder</tt> interface is used to create a dictionary entry (a single
 * key-value pair) in the generated CBOR stream. A DictionaryEntryBuilder instance is retrieved
 * by calling {@link DictionaryBuilder#putEntry()}.
 *
 * @param <B> the parent builder's type
 */
public interface DictionaryEntryBuilder<B>
        extends ValueBuilder<DictionaryEntryBuilder<B>> {

    /**
     * Ends the current dictionary entry. An entry has to have two elements written to
     * the stream. If the number of elements written with this builder is less than two
     * an {@link IllegalStateException} is thrown.
     *
     * @return the parent builder
     */
    @BuilderStackPop
    DictionaryBuilder<B> endEntry();

}
