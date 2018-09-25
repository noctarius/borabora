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
package com.noctarius.borabora;

import com.noctarius.borabora.spi.StreamableIterable;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;

/**
 * <p>A <tt>Sequence</tt> is a list of value data items. Sequences are also known
 * as arrays, list or tuples.</p>
 * <p>Sequences implementations are lazy evaluating and just pre-evaluate the
 * basic information like element stream positions for direct access to a certain
 * element.</p>
 * <p>In contrast to a Java Lists some operations use {@link Predicate} classes
 * to match elements since objects are not eagerly deserialized / read. Additionally
 * the sequence supports 64bit indexes sizes.</p>
 */
public interface Sequence
        extends StreamableIterable<Value> {

    /**
     * Returns the size of the sequence in number of value elements. The parser is free
     * to decide to return <tt>-1</tt> in case of large <b>indefinite</b> sequences. In
     * this case the stream will be walked element by element instead of based on the size
     * of the sequences when matching elements.
     *
     * @return the number of elements in the sequence or may return <tt>-1</tt> for large
     * indefinite sequences
     */
    long size();

    /**
     * Returns the empty state of the sequence. The empty state is defined by size to be
     * unequal to 0. This is true for indefinite and known-length sequences. Also size equals
     * to -1 means non-empty.
     *
     * @return <tt>true</tt> if the sequence is empty, otherwise <tt>false</tt>
     */
    boolean isEmpty();

    /**
     * <p>Matches values of entries against the provided {@link Predicate} matcher implementation and returns
     * <tt>true</tt> if a match is found, otherwise <tt>false</tt>.</p>
     * <p>This process is lazy and stops whenever the first value matches.</p>
     *
     * @param predicate the {@link Predicate} to match the value
     * @return <tt>true</tt> if the value if found, otherwise <tt>false</tt>
     */
    boolean contains(Predicate<Value> predicate);

    /**
     * <p>Returns a lazy evaluating {@link Iterator} implementation to traverse all values in the sequence
     * represented by this instance. That means, whenever the traversal ends, no more elements will be evaluated.</p>
     *
     * @return a a fully lazy, values traversing {@link Iterator} instance
     */
    Iterator<Value> iterator();

    /**
     * <p>Returns an array of all {@link Value}s in this sequence. This is not supported for 64bit sized sequences as
     * Java does not support long-based arrays.</p>
     * <p>All values in the returned array are pre-evaluated based on the {@link MajorType} and {@link ValueType},
     * as well as offset. Anyhow the actual value's content itself is still lazily evaluated whenever requested.</p>
     *
     * @return an array of all {@link Value}s in this sequence
     */
    Value[] toArray();

    /**
     * Returns the {@link Value} at the given sequence index or null if the sequence index is outside the available
     * range of elements.
     *
     * @param sequenceIndex the sequence index to retrieve a {@link Value}
     * @return the {@link Value} at the given sequence index, otherwise null if outside of range
     */
    Value get(long sequenceIndex);

    /**
     * Returns a string-based representation of the internal CBOR bytestream as a human-readable version.
     *
     * @return human-readable representation of the CBOR stream
     */
    String asString();

    /**
     * Returns <tt>true</tt> if the underlying CBOR stream is lazily evaluated and this dictionary is an indefinite
     * marked data item, otherwise it will return <tt>false</tt>.
     *
     * @return true if the dictionary is indefinite and lazily evaluated, otherwise false
     */
    boolean isIndefinite();

    /**
     * Creates a {@link Spliterator} over the elements in this list.
     *
     * @return a {@code Spliterator} over the elements in this list
     */
    @Override
    default Spliterator<Value> spliterator() {
        return Spliterators.spliterator(this.iterator(), this.size(), Spliterator.ORDERED);
    }

}
