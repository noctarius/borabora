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
 * The <tt>TagWriter</tt> interface defines an encoder for a certain {@link ValueType}.
 *
 * @param <V> the type of the value
 */
public interface TagWriter<V> {

    /**
     * Reads the current data item using this <tt>TagReader</tt> instance. If the data
     * item cannot be read because the data type does not match or for any other reason
     * an implementation specific exception might be thrown.
     *
     * @param value          the value to encode
     * @param offset         the current stream offset
     * @param encoderContext the current encoder context
     * @return the new offset after writing the data item
     */
    long process(V value, long offset, EncoderContext encoderContext);

}
