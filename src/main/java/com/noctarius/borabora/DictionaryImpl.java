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

import com.noctarius.borabora.spi.Dictionary;
import com.noctarius.borabora.spi.QueryContext;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import static com.noctarius.borabora.Bytes.readUInt8;

final class DictionaryImpl
        implements Dictionary {

    private final long size;
    private final Input input;
    private final long[][] elementIndexes;
    private final QueryContext queryContext;

    public DictionaryImpl(long size, long[][] elementIndexes, QueryContext queryContext) {
        this.size = size;
        this.elementIndexes = elementIndexes;
        this.queryContext = queryContext;
        this.input = queryContext.input();
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
        return findValueByPredicate(predicate, false) != -1;
    }

    @Override
    public boolean containsKey(StreamPredicate predicate) {
        return findValueByPredicate(predicate, false) != -1;
    }

    @Override
    public boolean containsValue(Predicate<Value> predicate) {
        return findValueByPredicate(predicate, true) != -1;
    }

    @Override
    public boolean containsValue(StreamPredicate predicate) {
        return findValueByPredicate(predicate, true) != -1;
    }

    @Override
    public Value get(Predicate<Value> predicate) {
        long keyOffset = findValueByPredicate(predicate, false);
        return get(keyOffset);
    }

    @Override
    public Value get(StreamPredicate predicate) {
        long keyOffset = findValueByPredicate(predicate, false);
        return get(keyOffset);
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
    public Iterator<Map.Entry<Value, Value>> iterator() {
        return new EntriesIterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (long i = 0; i < size; i += 2) {
            Value key = Decoder.readValue(calculateArrayIndex(i), queryContext);
            Value value = Decoder.readValue(calculateArrayIndex(i + 1), queryContext);
            sb.append(key).append('=').append(value).append(", ");
        }
        return sb.deleteCharAt(sb.length() - 1).deleteCharAt(sb.length() - 1).append(']').toString();
    }

    private Value get(long keyOffset) {
        if (keyOffset == -1) {
            return null;
        }
        long valueOffset = Decoder.skip(input, keyOffset);
        if (valueOffset == -1) {
            return null;
        }
        return Decoder.readValue(valueOffset, queryContext);
    }

    private long findValueByPredicate(Predicate<Value> predicate, boolean findValue) {
        RelocatableStreamValue streamValue = new RelocatableStreamValue(queryContext);
        for (long i = findValue ? 1 : 0; i < size * 2; i = i + 2) {
            long offset = calculateArrayIndex(i);
            short head = readUInt8(input, offset);
            MajorType majorType = MajorType.findMajorType(head);
            ValueType valueType = ValueTypes.valueType(input, offset);

            streamValue.relocate(majorType, valueType, offset);
            if (predicate.test(streamValue)) {
                return offset;
            }
        }
        return -1;
    }

    private long findValueByPredicate(StreamPredicate predicate, boolean findValue) {
        for (long i = findValue ? 1 : 0; i < size * 2; i = i + 2) {
            long offset = calculateArrayIndex(i);
            short head = readUInt8(input, offset);
            MajorType majorType = MajorType.findMajorType(head);
            ValueType valueType = ValueTypes.valueType(input, offset);

            if (predicate.test(majorType, valueType, offset, queryContext)) {
                return offset;
            }
        }
        return -1;
    }

    private long calculateArrayIndex(long offset) {
        int baseIndex = (int) (offset / Integer.MAX_VALUE);
        int elementIndex = (int) (offset % Integer.MAX_VALUE);
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
            return arrayIndex < size * 2;
        }

        @Override
        public Value next() {
            try {
                if (arrayIndex >= size * 2) {
                    throw new NoSuchElementException("No further element available");
                }
                long offset = calculateArrayIndex(arrayIndex);
                return Decoder.readValue(offset, queryContext);

            } finally {
                arrayIndex += 2;
            }
        }
    }

    private class EntriesIterator
            implements Iterator<Map.Entry<Value, Value>> {

        private long arrayIndex = 0;

        @Override
        public boolean hasNext() {
            return arrayIndex < size * 2;
        }

        @Override
        public Map.Entry<Value, Value> next() {
            try {
                if (arrayIndex >= size * 2) {
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
            return Decoder.readValue(keyIndex, queryContext);
        }

        @Override
        public Value getValue() {
            return Decoder.readValue(valueIndex, queryContext);
        }

        @Override
        public Value setValue(Value value) {
            throw new UnsupportedOperationException("setValue not supported");
        }
    }

}
