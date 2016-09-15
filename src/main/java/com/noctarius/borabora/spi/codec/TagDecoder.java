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

import com.noctarius.borabora.Input;
import com.noctarius.borabora.ValueType;
import com.noctarius.borabora.spi.query.TypeSpec;

/**
 * The <tt>TagDecoder</tt> is a sub-interface of {@link TagReader} to add functionality
 * for automatic recognition of data types and {@link ValueType}s.
 *
 * @param <V> the type of the value
 */
public interface TagDecoder<V>
        extends TagReader<V> {

    /**
     * Reads the data item at the current <tt>offset</tt> and returns <tt>true</tt> if this item can
     * be handled by the current <tt>TagDecoder</tt> instance, otherwise <tt>false</tt>.
     *
     * @param input  the current input instance
     * @param offset the current data item's offset
     * @return true when the TagDecoder can handle the current stream's data item, otherwise false
     */
    boolean handles(Input input, long offset);

    /**
     * Returns the assigned {@link TypeSpec} of this <tt>TagDecoder</tt>'s data item if the given
     * <tt>tagId</tt> matches the current one, otherwise <tt>null</tt>.
     *
     * @param tagId the tagId to test
     * @return the current TypeSpec if the tagId matches, otherwise null.
     */
    TypeSpec handles(long tagId);

    /**
     * Reads the data item at the current <tt>offset</tt> and returns the corresponding
     * {@link ValueType} if this item can be handled by the current <tt>TagDecoder</tt> instance,
     * otherwise <tt>null</tt>.
     *
     * @param input  the current input instance
     * @param offset the current data item's offset
     * @return the corresponding ValueType when the TagDecoder can handle the current stream's data item, otherwise null
     */
    ValueType valueType(Input input, long offset);

}
