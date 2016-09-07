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
package com.noctarius.borabora.spi.builder;

import com.noctarius.borabora.Output;
import com.noctarius.borabora.WrongTypeException;
import com.noctarius.borabora.spi.codec.TagStrategy;
import com.noctarius.borabora.spi.io.Encoder;

import java.util.Objects;

/**
 * The <tt>EncoderContext</tt> supports encoding values into a CBOR stream. It keeps
 * track of the offsets and {@link Output} instance. The encoders main purpose is to
 * keep the actual implementations of {@link com.noctarius.borabora.Writer}s stateless
 * and thread-safe but to handle the non-stateless parts inside the EncoderContext.
 * <p>Furthermore the EncoderContext knows about the semantic tag encoders that are
 * registered with the Writer instance and can apply them accordingly.</p>
 * <p>It also offers additional convenience methods like {@link #encodeNull()},
 * {@link #encodeNullOrType(Object, EncoderFunction)} or a generalized form for more
 * customized encoding {@link #encode(EncoderFunction)}.</p>
 */
public interface EncoderContext {

    /**
     * Returns the bound {@link Output} instance the generated CBOR stream is written to.
     *
     * @return the bound output
     */
    Output output();

    /**
     * Returns the current offset of the generated CBOR stream.
     *
     * @return the current offset
     */
    long offset();

    /**
     * Sets the new offset of the generated CBOR stream.
     *
     * @param offset the new offset
     */
    void offset(long offset);

    /**
     * Tries to apply one of the registered {@link com.noctarius.borabora.spi.codec.TagEncoder}
     * instances. Only the first matching encoder is used. If an ambiguous configuration exists,
     * results might vary and the outcome is undefined. However if an item can be encoded into
     * two different ways, there might not be a problem according to the CBOR specification.
     * <p>If not matching <tt>TagEncoder</tt> can be found a {@link WrongTypeException} is thrown.</p>
     *
     * @param value  the value to encode
     * @param offset the current offset
     * @return the new offset after writing the tag
     * @throws WrongTypeException if no matching TagEncoder was found
     */
    long applyEncoder(Object value, long offset);

    /**
     * Returns the {@link TagStrategy} for the given semantic tag <tt>type</tt> or <tt>null</tt>
     * if no TagStrategy is found for that very type.
     *
     * @param type the semantic tag object class
     * @param <S>  the type of the semantic tag class
     * @return the TagStrategy for the given type if available, otherwise null
     * @throws NullPointerException when type is null
     */
    <S> TagStrategy findTagStrategy(Class<S> type);

    /**
     * Encodes the given <tt>value</tt> as either a null-type (if the value if <tt>null</tt>) or
     * with the given {@link EncoderFunction}. If the given <tt>EncoderFunction</tt> is <tt>null</tt>
     * a {@link NullPointerException} is thrown.
     *
     * @param value           the value to encode
     * @param encoderFunction the EncoderFunction to be used for encoding the value
     * @throws NullPointerException if encoderFunction is null
     */
    default void encodeNullOrType(Object value, EncoderFunction encoderFunction) {
        Objects.requireNonNull(encoderFunction, "encoderFunction must not be null");
        if (value == null) {
            encodeNull();
        } else {
            encode(encoderFunction);
        }
    }

    /**
     * Encodes a null-type at the current position in the CBOR stream.
     */
    default void encodeNull() {
        encode(Encoder::putNull);
    }

    /**
     * Encodes a value bound in the given {@link EncoderFunction} to the current position
     * in the CBOR stream.  If the given <tt>EncoderFunction</tt> is <tt>null</tt>
     * a {@link NullPointerException} is thrown.
     *
     * @param encoderFunction the encoder function to write the actual value
     * @throws NullPointerException if encoderFunction is null
     */
    default void encode(EncoderFunction encoderFunction) {
        Objects.requireNonNull(encoderFunction, "encoderFunction must not be null");
        long offset = offset();
        offset = encoderFunction.encode(offset, output());
        offset(offset);
    }

    /**
     * The <tt>EncoderFunction</tt> interface defines an encoder to bind and write a value
     * to a certain location inside the generated CBOR stream.
     */
    interface EncoderFunction {

        /**
         * @param offset the current offset
         * @param output the output instance to encode to
         * @return the new offset
         */
        long encode(long offset, Output output);
    }
}
