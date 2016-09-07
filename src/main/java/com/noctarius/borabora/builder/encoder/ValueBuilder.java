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

import com.noctarius.borabora.spi.builder.BuilderStackPush;
import com.noctarius.borabora.spi.builder.TagBuilderConsumer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.time.Instant;
import java.util.Date;

/**
 * The <tt>ValueBuilder</tt> interface defines most of the basic write methods used
 * to insert values into a CBOR encoded stream using the fluent builder API.
 *
 * @param <B> the current builder's type
 */
public interface ValueBuilder<B> {

    /**
     * Encodes a <tt>byte</tt> at the current position into the CBOR stream.
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putNumber(byte value);

    /**
     * Encodes a {@link Byte} at the current position into the CBOR stream. If <tt>value</tt>
     * is <tt>null</tt>, a null-type will be written.
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putNumber(Byte value);

    /**
     * Encodes a <tt>short</tt> at the current position into the CBOR stream.
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putNumber(short value);

    /**
     * Encodes a {@link Short} at the current position into the CBOR stream. If <tt>value</tt>
     * is <tt>null</tt>, a null-type will be written.
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putNumber(Short value);

    /**
     * Encodes an <tt>int</tt> at the current position into the CBOR stream.
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putNumber(int value);

    /**
     * Encodes an {@link Integer} at the current position into the CBOR stream. If <tt>value</tt>
     * is <tt>null</tt>, a null-type will be written.
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putNumber(Integer value);

    /**
     * Encodes a <tt>long</tt> at the current position into the CBOR stream.
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putNumber(long value);

    /**
     * Encodes a {@link Long} at the current position into the CBOR stream. If <tt>value</tt>
     * is <tt>null</tt>, a null-type will be written.
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putNumber(Long value);

    /**
     * Encodes a {@link Number} at the current position into the CBOR stream. If <tt>value</tt>
     * is <tt>null</tt>, a null-type will be written.
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putNumber(Number value);

    /**
     * Encodes a <tt>float</tt> at the current position into the CBOR stream.
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putNumber(float value);

    /**
     * Encodes a {@link Float} at the current position into the CBOR stream. If <tt>value</tt>
     * is <tt>null</tt>, a null-type will be written.
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putNumber(Float value);

    /**
     * Encodes a <tt>double</tt> at the current position into the CBOR stream.
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putNumber(double value);

    /**
     * Encodes a {@link Double} at the current position into the CBOR stream. If <tt>value</tt>
     * is <tt>null</tt>, a null-type will be written.
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putNumber(Double value);

    /**
     * Encodes a half precision <tt>float</tt> at the current position into the CBOR stream.
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putHalfPrecision(float value);

    /**
     * Encodes a half precision {@link Float} at the current position into the CBOR stream. If
     * <tt>value</tt> is <tt>null</tt>, a null-type will be written.
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putHalfPrecision(Float value);

    /**
     * Encodes a {@link BigInteger} at the current position into the CBOR stream. If <tt>value</tt>
     * is <tt>null</tt>, a null-type will be written.
     * <p>The <tt>BigInteger</tt> will be encoded using a semantic tag.</p>
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putBigInteger(BigInteger value);

    /**
     * Encodes a {@link String} at the current position into the CBOR stream. If <tt>value</tt>
     * is <tt>null</tt>, a null-type will be written.
     * <p>The given string will be tested to only contain <tt>ASCII</tt> characters and will be, if
     * possible, encoded using a <tt>ByteString</tt>, otherwise as a <tt>TextString</tt>.</p>
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putString(String value);

    /**
     * Encodes a {@link String} at the current position into the CBOR stream. If <tt>value</tt>
     * is <tt>null</tt>, a null-type will be written.
     * <p>The given string is tested to only contain <tt>ASCII</tt> characters and will throw
     * an {@link java.io.UnsupportedEncodingException} if non-encodeable characters are found.</p>
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putByteString(String value);

    /**
     * Encodes a {@link String} at the current position into the CBOR stream. If <tt>value</tt>
     * is <tt>null</tt>, a null-type will be written.
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putTextString(String value);

    /**
     * Encodes an {@link java.net.URL} at the current position into the CBOR stream. If <tt>value</tt>
     * is <tt>null</tt>, a null-type will be written.
     * <p>The <tt>URI</tt> will be encoded using a semantic tag.</p>
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putURI(URI value);

    /**
     * Encodes an {@link Instant} at the current position into the CBOR stream. If <tt>value</tt>
     * is <tt>null</tt>, a null-type will be written.
     * <p>The <tt>Instant</tt> will be encoded using a DateTime semantic tag.</p>
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putDateTime(Instant value);

    /**
     * Encodes a {@link Date} at the current position into the CBOR stream. If <tt>value</tt>
     * is <tt>null</tt>, a null-type will be written.
     * <p>The <tt>Date</tt> will be encoded using a semantic tag.</p>
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putDateTime(Date value);

    /**
     * Encodes an <tt>long</tt> at the current position into the CBOR stream. If <tt>value</tt>
     * is <tt>null</tt>, a null-type will be written.
     * <p>The <tt>long</tt> will be encoded using a Timestamp semantic tag.</p>
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putTimestamp(long value);

    /**
     * Encodes an {@link Instant} at the current position into the CBOR stream. If <tt>value</tt>
     * is <tt>null</tt>, a null-type will be written.
     * <p>The <tt>URI</tt> will be encoded using a Timestamp semantic tag.</p>
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putTimestamp(Instant value);

    /**
     * Encodes an {@link BigDecimal} at the current position into the CBOR stream. If <tt>value</tt>
     * is <tt>null</tt>, a null-type will be written.
     * <p>The <tt>BigDecimal</tt> will be encoded using a Timestamp semantic tag.</p>
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putFraction(BigDecimal value);

    /**
     * Encodes an indefinite length <tt>ASCII</tt>-string at the current position into the CBOR stream. The
     * returned builder can be used to write an arbitrary number of {@link String} elements to the stream.
     * Those, while parsing, will be concatenated into a single string. This is most practical when the string
     * is not immediately available but will be generated in chunks.
     *
     * @return the current builder
     */
    @BuilderStackPush
    IndefiniteStringBuilder<B> putIndefiniteByteString();

    /**
     * Encodes an indefinite length <tt>UTF-8</tt>-string at the current position into the CBOR stream. The
     * returned builder can be used to write an arbitrary number of {@link String} elements to the stream.
     * Those, while parsing, will be concatenated into a single string. This is most practical when the string
     * is not immediately available but will be generated in chunks.
     *
     * @return the current builder
     */
    @BuilderStackPush
    IndefiniteStringBuilder<B> putIndefiniteTextString();

    /**
     * Encodes a <tt>boolean</tt> at the current position into the CBOR stream.
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putBoolean(boolean value);

    /**
     * Encodes a {@link Boolean} at the current position into the CBOR stream. If <tt>value</tt>
     * is <tt>null</tt>, a null-type will be written.
     *
     * @param value the value to encode
     * @return the current builder
     */
    B putBoolean(Boolean value);

    /**
     * Encodes an {@link Object} at the current position into the CBOR stream. The object will be written
     * as one of the known types (this includes any registered semantic tag encoder, see
     * {@link com.noctarius.borabora.spi.codec.TagStrategy}) and will throw a
     * {@link com.noctarius.borabora.WrongTypeException} if the value cannot be encoded. If <tt>value</tt>
     * is <tt>null</tt>, a null-type will be written.
     *
     * @param value the value to encode
     * @return the current builder
     * @throws com.noctarius.borabora.WrongTypeException if the value cannot be encoded
     */
    B putValue(Object value);

    /**
     * Encodes an {@link Object} at the current position into the CBOR stream. The object will be written
     * by utilizing the registered semantic tag encoder (see {@link com.noctarius.borabora.spi.codec.TagStrategy})
     * and will throw a {@link com.noctarius.borabora.WrongTypeException} if the value cannot be encoded. If
     * <tt>value</tt> is <tt>null</tt>, a null-type will be written.
     *
     * @param value the value to encode
     * @return the current builder
     * @throws com.noctarius.borabora.WrongTypeException if the value cannot be encoded
     */
    B putTag(Object value);

    /**
     * Encodes a semantic tag at the current position into the CBOR stream using the given {@link TagBuilderConsumer}.
     *
     * @param consumer the TagBuilderConsumer to write the tag to the stream
     * @return the current builder
     * @throws com.noctarius.borabora.WrongTypeException if the value cannot be encoded
     */
    B putTag(TagBuilderConsumer<B> consumer);

    /**
     * Encodes an indefinite sized sequence (array) at the current position into the CBOR stream. The returned
     * {@link SequenceBuilder} can be used to put an arbitrary number of elements into the sequence.
     * <p>Since indefinite sized sequences are slower to parse, it is recommended to use {@link #putSequence(long)}
     * with the known number of elements whenever possible.</p>
     *
     * @return the current builder
     */
    @BuilderStackPush
    SequenceBuilder<B> putSequence();

    /**
     * Encodes a fixed sized sequence (array) at the current position into the CBOR stream. The returned
     * {@link SequenceBuilder} can be used to put the <b>exact</b> given <tt>elements</tt> number of elements
     * into the sequence. This method throws an {@link IllegalStateException} when more or less elements
     * are provided than defined.
     *
     * @param elements the number of elements to put into the sequence
     * @return the current builder
     * @throws IllegalStateException if more or less elements are provided than defined
     */
    @BuilderStackPush
    SequenceBuilder<B> putSequence(long elements);

    /**
     * Encodes an indefinite sized dictionary (map) at the current position into the CBOR stream. The returned
     * {@link DictionaryBuilder} can be used to put an arbitrary number of key-value pairs into the dictionary.
     * <p>Since indefinite sized dictionaries are slower to parse, it is recommended to use
     * {@link #putDictionary(long)} with the known number of pairs whenever possible.</p>
     *
     * @return the current builder
     */
    @BuilderStackPush
    DictionaryBuilder<B> putDictionary();

    /**
     * Encodes a fixed sized dictionary (map) at the current position into the CBOR stream. The returned
     * {@link SequenceBuilder} can be used to put the <b>exact</b> given <tt>elements</tt> number of key-value
     * pairs into the dictionary. This method throws an {@link IllegalStateException} when more or less pairs
     * are provided than defined.
     *
     * @param elements the number of elements to put into the sequence
     * @return the current builder
     * @throws IllegalStateException if more or less key-value pairs are provided than defined
     */
    @BuilderStackPush
    DictionaryBuilder<B> putDictionary(long elements);

}
