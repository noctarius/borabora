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

import com.noctarius.borabora.spi.StreamableIterable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * <p>A <tt>Dictionary</tt> is a map of key-value data items. Dictionaries are also known
 * as tables, maps, hashes or objects (as in JSON).</p>
 * <p>Dictionary implementations are lazy evaluating and just pre-evaluate the
 * basic information like key-value pair stream positions for direct access
 * to certain elements.</p>
 * <p>In contrast to a Java Map keys are matched using {@link Predicate} classes
 * since objects are not eagerly deserialized / read. Additionally the dictionary
 * supports 64bit indexes sizes.</p>
 *
 * @see Value
 */
public interface Dictionary
        extends Iterable<Map.Entry<Value, Value>> {

    /**
     * Returns the size of the dictionary in number of key-value pairs (entries), not elements
     * in the stream. The parser is free to decide to return <tt>-1</tt> in case of
     * large <b>indefinite</b> dictionaries. In this case the stream will be walked
     * element by element instead of based on the size of the dictionary when matching
     * for keys or values.
     *
     * @return the number of entries in the dictionary or may return <tt>-1</tt> for large
     * indefinite dictionaries
     */
    long size();

    /**
     * Returns the empty state of the dictionary. The empty state is defined by size to be
     * unequal to 0. This is true for indefinite and known-length dictionaries. Also size equals
     * to -1 means non-empty.
     *
     * @return <tt>true</tt> if the dictionary is empty, otherwise <tt>false</tt>
     */
    boolean isEmpty();

    /**
     * <p>Matches keys of entries against the provided {@link Predicate} matcher implementation and returns
     * <tt>true</tt> if a match is found, otherwise <tt>false</tt>.</p>
     * <p>This process is lazy and stops whenever the first key matches. According to the CBOR
     * specification, dictionaries are allowed to support the same key multiple times, the
     * support on parser side, however, is not strictly required and skipped for this implementation.</p>
     * <p>The scanning process can be pretty expensive and if the value is needed afterwards, it is
     * recommended to use {@link #get(Predicate)} directly as the cost is similar but since no caching
     * takes place, using both methods after another, the cost is multiplied by the number of operations.</p>
     *
     * @param predicate the {@link Predicate} to match the key
     * @return <tt>true</tt> if the key is found, otherwise <tt>false</tt>
     */
    boolean containsKey(Predicate<Value> predicate);

    /**
     * <p>Matches values of entries against the provided {@link Predicate} matcher implementation and returns
     * <tt>true</tt> if a match is found, otherwise <tt>false</tt>.</p>
     * <p>This process is lazy and stops whenever the first value matches.</p>
     *
     * @param predicate the {@link Predicate} to match the value
     * @return <tt>true</tt> if the value if found, otherwise <tt>false</tt>
     */
    boolean containsValue(Predicate<Value> predicate);

    /**
     * <p>Matches keys of entries against the provided {@link Predicate} matcher implementation and returns
     * the value if a match is found, other <tt>null</tt>.</p>
     * <p>This process is lazy and stops whenever the first key matches. According to the CBOR
     * specification, dictionaries are allowed to support the same key multiple times, the
     * support on parser side, however, is not strictly required and skipped for this implementation.</p>
     *
     * @param predicate the {@link Predicate} to match the key
     * @return <tt>true</tt> if the key is found, otherwise <tt>false</tt>
     */
    Value get(Predicate<Value> predicate);

    /**
     * <p>Returns a lazy evaluating {@link Iterable} implementation to traverse all keys in the dictionary
     * represented by this instance. Also the {@link java.util.Iterator} returned from
     * {@link Iterable#iterator()} is a fully lazy implementation. Whenever the traversal ends, no more
     * elements will be evaluated.</p>
     *
     * @return a a fully lazy, keys traversing {@link Iterable} instance
     */
    StreamableIterable<Value> keys();

    /**
     * <p>Returns a lazy evaluating {@link Iterable} implementation to traverse all values in the dictionary
     * represented by this instance. Also the {@link java.util.Iterator} returned from
     * {@link Iterable#iterator()} is a fully lazy implementation. Whenever the traversal ends, no more
     * elements will be evaluated.</p>
     *
     * @return a a fully lazy, values traversing {@link Iterable} instance
     */
    StreamableIterable<Value> values();

    /**
     * <p>Returns a lazy evaluating {@link Iterable} implementation to traverse all entries (key-value pairs)
     * in the dictionary represented by this instance. Also the {@link java.util.Iterator} returned from
     * {@link Iterable#iterator()} is a fully lazy implementation. Whenever the traversal ends, no more
     * elements will be evaluated.</p>
     *
     * @return a a fully lazy, entries traversing {@link Iterable} instance
     */
    StreamableIterable<Map.Entry<Value, Value>> entries();

    /**
     * <p>Returns a lazy evaluating {@link Iterator} implementation to traverse all entries in the dictionary
     * represented by this instance. That means, whenever the traversal ends, no more elements will be evaluated.</p>
     *
     * @return a a fully lazy, entries traversing {@link Iterator} instance
     */
    Iterator<Map.Entry<Value, Value>> iterator();

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
     * Returns the current <tt>Dictionary</tt> as an enclosing {@link Value} instance.
     *
     * @return an enclosing {@link Value} instance
     */
    Value asValue();

    /**
     * Performs the given action for each entry in this map until all entries
     * have been processed or the action throws an exception.   Unless
     * otherwise specified by the implementing class, actions are performed in
     * the order of entry set iteration (if an iteration order is specified.)
     * Exceptions thrown by the action are relayed to the caller.
     *
     * @param action The action to be performed for each entry
     * @throws NullPointerException if the specified action is null
     */
    default void forEach(BiConsumer<Value, Value> action) {
        Objects.requireNonNull(action, "action must not be null");
        for (Map.Entry<Value, Value> entry : entries()) {
            Value k = entry.getKey();
            Value v = entry.getValue();
            action.accept(k, v);
        }
    }

}
