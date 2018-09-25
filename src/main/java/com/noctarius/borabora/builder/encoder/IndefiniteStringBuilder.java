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
import com.noctarius.borabora.spi.builder.BuilderStackPush;

/**
 * The <tt>IndefiniteStringBuilder</tt> interface is used to create a string that consists of
 * an indefinite number of independent strings. This is useful to generate a stream where the
 * string generation is chunked. While reading the string is deserialized as a single, continuous
 * element.
 * <p>A IndefiniteStringBuilder instance is retrieved by calling
 * {@link ValueBuilder#putIndefiniteByteString()} or {@link ValueBuilder#putIndefiniteTextString()}.</p>
 * <p>Depending on the previously chosen method, the stream either all has to consist of <tt>ASCII</tt>
 * ByteStrings or <tt>UTF-8</tt> TextStrings. Trying to put a string, that contains UTF-8 characters,
 * into a IndefiniteStringBuilder that was created with {@link ValueBuilder#putIndefiniteByteString()}
 * will throw an {@link IllegalArgumentException}.</p>
 *
 * @param <B> the parent builder's type
 */
public interface IndefiniteStringBuilder<B> {

    /**
     * Encodes a new {@link String} chunk at the current position into the CBOR stream. If <tt>value</tt>
     * is <tt>null</tt>, a {@link NullPointerException} is thrown.
     * <p>If this IndefiniteStringBuilder instance was created by calling
     * {@link ValueBuilder#putIndefiniteByteString()} but the <tt>value</tt> contains characters that
     * are not encodeable using <tt>ASCII</tt> encoding an {@link IllegalArgumentException} is thrown.</p>
     *
     * @param value the value to encode
     * @return the current builder
     * @throws IllegalArgumentException if the builder is configured for ASCII characters but the value
     *                                  contains at least one non-encodeable character
     */
    @BuilderStackPush
    IndefiniteStringBuilder<B> putString(String value);

    /**
     * Ends the current indefinite string and write the closing tag to the generated CBOR
     * stream.
     *
     * @return the parent builder
     */
    @BuilderStackPop
    B endIndefiniteString();

}
