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
package com.noctarius.borabora;

/**
 * A <tt>Value</tt> represents a known or unknown data item in borabora. While CBOR is type-safe in terms
 * of data items, it is schema-less overall, or better said the schema is defined by the elements inside
 * the stream itself. To prevent deserialization, borabora does not tries to validate any of the elements
 * before it actually is request to do so explicitly.
 * <p>Value for this sake serves as a container storing the offset and found {@link MajorType} and
 * {@link ValueType}. It is up to the user to extract the actual data item's value itself.</p>
 * <p>If the value does not match or is not transformable into the requested type of value, borabora
 * will throw a {@link WrongTypeException}.</p>
 * <p>A common usage example is shown in the following snippet:</p>
 * <pre>
 *     Value value = parser.read( input, query );
 *     stringBuilder.append( value.string() );
 * </pre>
 * <p>This extracts the actual data item's value represented by the <tt>value</tt> instance as a string
 * and appends it to the given string builder.</p>
 */
public interface Value {

    /**
     * This represents a value that does not exist in the stream. If for example a query could not find
     * a specific key during a key-lookup or a sequence does not have a certain index, a <tt>NULL</tt>
     * value is returned. borabora does never emit actual Java <tt>null</tt> values, except for
     * requests to {@link Dictionary} or {@link Sequence} methods, as those are expected to return the
     * actual represented data item's value itself.
     */
    Value NULL_VALUE = new NullValue();

    /**
     * Returns the {@link MajorType} represented by this value.
     *
     * @return the MajorType of this value
     */
    MajorType majorType();

    /**
     * Returns the {@link ValueType} represented by this value.
     *
     * @return the ValueType of this value
     */
    ValueType valueType();

    /**
     * Extracts the given Value as a {@link MajorType#SemanticTag}. To extract the actual value, the known
     * {@link ValueType} is used to find the corresponding {@link com.noctarius.borabora.spi.codec.TagDecoder}.
     * If the represented value is not of type {@link MajorType#SemanticTag} a {@link WrongTypeException} is
     * thrown.
     *
     * @param <V> the expected type of the extracted value
     * @return the extracted value
     */
    <V> V tag();

    /**
     * Extracts the given Value as a {@link Number} instance. If the represented value is not possible to be
     * represented as or boxed to a number, a {@link WrongTypeException} is thrown.
     *
     * @return the extracted value
     */
    Number number();

    /**
     * Extracts the given Value as a {@link Sequence} instance. If the represented value is not possible to be
     * represented as sequence, a {@link WrongTypeException} is thrown.
     *
     * @return the extracted value
     */
    Sequence sequence();

    /**
     * Extracts the given Value as a {@link Dictionary} instance. If the represented value is not possible to be
     * represented as dictionary, a {@link WrongTypeException} is thrown.
     *
     * @return the extracted value
     */
    Dictionary dictionary();

    /**
     * Extracts the given Value as a {@link String} instance. If the represented value is not possible to be
     * represented as string, a {@link WrongTypeException} is thrown.
     *
     * @return the extracted value
     */
    String string();

    /**
     * Extracts the given Value as a <tt>boolean</tt>. If the represented value is not possible to be
     * represented as a boolean, a {@link WrongTypeException} is thrown.
     *
     * @return the extracted value
     */
    Boolean bool();

    /**
     * Extracts the given Value as a CBOR encoded byte-array. This doesn't transform the value in any way
     * but returns the actual value stream itself.
     *
     * @return the extracted value as a CBOR encoded byte-array
     */
    byte[] raw();

    /**
     * Extracts the given Value based on the known {@link ValueType} stored inside this instance.
     *
     * @param <V> the expected type of the extracted value
     * @return the extracted value
     */
    <V> V byValueType();

    /**
     * Returns the offset inside the stream for this value.
     *
     * @return the offset of this value
     */
    long offset();

    /**
     * Returns the internal stream representation if available, otherwise it will return <tt>null</tt>.
     *
     * @return the input stream if available, otherwise null
     */
    Input input();

    /**
     * Returns a string-based representation of the internal CBOR bytestream as a human-readable version.
     * <p>Each known value type in CBOR can be represented that way. The structure follows one of the
     * following three types:</p>
     * <ul>
     * <li><b>Sequences:</b> [item1, item2, item3, ...]</li>
     * <li><b>Dictionary:</b> [key1=value1, key2=value2, key3=value3, ...]</li>
     * <li><b>Data Item:</b> ValueTypeName{ actualValue }</li>
     * </ul>
     * <p>To clarify in an example, a dictionary has a single key-value pair, where the key is a String
     * and the value is a sequence of UInts.</p>
     * <pre>
     *     Dictionary{ [ByteString{ myKey }=Sequence{ [UInt{ 1 }, UInt{ 2 }, UInt{ 3 }] }] }
     * </pre>
     *
     * @return human-readable representation of the CBOR stream
     */
    String asString();

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    String toString();

}
