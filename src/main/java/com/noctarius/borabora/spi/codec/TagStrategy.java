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
package com.noctarius.borabora.spi.codec;

import com.noctarius.borabora.ValueType;
import com.noctarius.borabora.spi.builder.EncoderContext;

/**
 * The <tt>TagStrategy</tt> is a sub-interface of both {@link TagDecoder} and {@link TagEncoder}, but
 * adds functionality for the {@link com.noctarius.borabora.spi.builder.TagBuilder} SPI.
 *
 * @param <S> the type of the semantic tag builder
 * @param <V> the type of the value
 */
public interface TagStrategy<S, V>
        extends TagDecoder<V>, TagEncoder<V> {

    S newTagBuilder(EncoderContext encoderContext);

    /**
     * Returns the actual semantic tag id for the data type represented by this <tt>TagStrategy</tt>.
     *
     * @return the represented data type's semantic tag id
     */
    int tagId();

    /**
     * Returns the actual {@link ValueType} instance for the data type represented by this <tt>TagStrategy</tt>.
     *
     * @return the represented data type's ValueType
     */
    ValueType valueType();

    /**
     * Returns the builder's class which is the entry point into this value's semantic tag building process.
     *
     * @return the semantic tag builder's class of this data type
     */
    Class<S> tagBuilderType();

}
