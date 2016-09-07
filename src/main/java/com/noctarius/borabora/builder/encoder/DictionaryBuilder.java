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
package com.noctarius.borabora.builder.encoder;

import com.noctarius.borabora.spi.builder.BuilderStackPop;
import com.noctarius.borabora.spi.builder.BuilderStackPush;

/**
 * The <tt>DictionaryBuilder</tt> interface is used to create dictionary entries (key-value
 * pairs) in the generated CBOR stream. A DictionaryBuilder instance is retrieved by calling
 * {@link ValueBuilder#putDictionary()} or {@link ValueBuilder#putDictionary(long)} for
 * either an indefinite or fixed size dictionary.
 *
 * @param <B> the parent builder's type
 */
public interface DictionaryBuilder<B> {

    /**
     * Starts a new key-value pair in the dictionary and returns the corresponding
     * {@link DictionaryEntryBuilder}. If the dictionary is configured to be of a
     * fixed size and the new entry exceeds the number of legal entries, an
     * {@link IllegalStateException} is thrown.
     *
     * @return the new DictionaryEntryBuilder
     * @throws IllegalStateException if the new entry exceeds the number of allowed elements
     */
    @BuilderStackPush
    DictionaryEntryBuilder<B> putEntry();

    /**
     * Ends the current dictionary. In case of an indefinite dictionary, it will also write
     * the closing tag to the generated CBOR stream. In case of a fixed size dictionary, the
     * number of created entries is tested and if the number is less than the configured
     * number of elements expected, an {@link IllegalStateException} is thrown.
     *
     * @return the parent builder
     */
    @BuilderStackPop
    B endDictionary();

}
