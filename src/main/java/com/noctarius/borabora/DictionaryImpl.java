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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

final class DictionaryImpl
        implements Dictionary {

    private final Decoder stream;
    private final long size;
    private final long[][] elementIndexes;
    private final Collection<SemanticTagProcessor> processors;

    public DictionaryImpl(Decoder stream, long size, long[][] elementIndexes, Collection<SemanticTagProcessor> processors) {
        this.stream = stream;
        this.size = size;
        this.elementIndexes = elementIndexes;
        this.processors = processors;
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Predicate<Value> predicate) {
        return findValueByPredicate(predicate, false) != null;
    }

    @Override
    public boolean containsValue(Predicate<Value> predicate) {
        return findValueByPredicate(predicate, true) != null;
    }

    @Override
    public Value get(Predicate<Value> predicate) {
        for (long i = 0; i < size; i++) {
            long index = calculateArrayIndex(i * 2);
            Value value = stream.readValue(index, processors);
            if (predicate.test(value)) {
                long position = stream.skip(index);
                return stream.readValue(position, processors);
            }
        }
        return null;
    }

    @Override
    public Iterable<Value> keys() {
        return new DictionaryIterable(0);
    }

    @Override
    public Iterable<Value> values() {
        return new DictionaryIterable(1);
    }

    @Override
    public Iterable<Map.Entry<Value, Value>> entries() {
        return new EntriesIterable();
    }

    private Value findValueByPredicate(Predicate<Value> predicate, boolean findValue) {
        for (long i = findValue ? 1 : 0; i < size; i = i + 2) {
            long index = calculateArrayIndex(i);
            Value value = stream.readValue(index, processors);
            if (predicate.test(value)) {
                return value;
            }
        }
        return null;
    }

    private long calculateArrayIndex(long index) {
        int baseIndex = (int) (index / Integer.MAX_VALUE);
        int elementIndex = (int) (index % Integer.MAX_VALUE);
        return elementIndexes[baseIndex][elementIndex];
    }

    private class DictionaryIterable
            implements Iterable<Value> {

        private final long initialArrayIndex;

        private DictionaryIterable(long initialArrayIndex) {
            this.initialArrayIndex = initialArrayIndex;
        }

        @Override
        public Iterator<Value> iterator() {
            return new DictionaryIterator(initialArrayIndex);
        }
    }

    private class DictionaryIterator
            implements Iterator<Value> {

        private long arrayIndex;

        private DictionaryIterator(long initialArrayIndex) {
            this.arrayIndex = initialArrayIndex;
        }

        @Override
        public boolean hasNext() {
            return arrayIndex + 2 >= elementIndexes.length;
        }

        @Override
        public Value next() {
            try {
                if (arrayIndex >= elementIndexes.length) {
                    throw new NoSuchElementException("No further element available");
                }
                long index = calculateArrayIndex(arrayIndex);
                return stream.readValue(index, processors);

            } finally {
                arrayIndex += 2;
            }
        }
    }

    private class EntriesIterable
            implements Iterable<Map.Entry<Value, Value>> {

        @Override
        public Iterator<Map.Entry<Value, Value>> iterator() {
            return new EntriesIterator();
        }
    }

    private class EntriesIterator
            implements Iterator<Map.Entry<Value, Value>> {

        private long arrayIndex = 0;

        @Override
        public boolean hasNext() {
            return arrayIndex + 2 >= elementIndexes.length;
        }

        @Override
        public Map.Entry<Value, Value> next() {
            try {
                if (arrayIndex >= elementIndexes.length) {
                    throw new NoSuchElementException("No further element available");
                }
                long keyIndex = calculateArrayIndex(arrayIndex);
                long valueIndex = calculateArrayIndex(arrayIndex + 1);
                return new SimpleEntry(keyIndex, valueIndex);

            } finally {
                arrayIndex += 2;
            }
        }
    }

    private class SimpleEntry
            implements Map.Entry<Value, Value> {

        private final long keyIndex;
        private final long valueIndex;

        private SimpleEntry(long keyIndex, long valueIndex) {
            this.keyIndex = keyIndex;
            this.valueIndex = valueIndex;
        }

        @Override
        public Value getKey() {
            return stream.readValue(keyIndex, processors);
        }

        @Override
        public Value getValue() {
            return stream.readValue(valueIndex, processors);
        }

        @Override
        public Value setValue(Value value) {
            throw new UnsupportedOperationException("setValue not supported");
        }
    }

}
