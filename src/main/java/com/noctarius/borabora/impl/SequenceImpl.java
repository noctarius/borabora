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
package com.noctarius.borabora.impl;

import com.noctarius.borabora.Input;
import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.Sequence;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.ValueType;
import com.noctarius.borabora.spi.RelocatableStreamValue;
import com.noctarius.borabora.spi.io.ByteSizes;
import com.noctarius.borabora.spi.io.Decoder;
import com.noctarius.borabora.spi.io.ElementCounts;
import com.noctarius.borabora.spi.query.QueryContext;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

import static com.noctarius.borabora.spi.io.Bytes.readUInt8;

public final class SequenceImpl
        implements Sequence {

    private final Input input;
    private final long size;
    private final long[][] elementIndexes;
    private final QueryContext queryContext;

    SequenceImpl(long size, long[][] elementIndexes, QueryContext queryContext) {
        Objects.requireNonNull(elementIndexes, "elementIndexes must not be null");
        Objects.requireNonNull(queryContext, "queryContext must not be null");
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
    public boolean contains(Predicate<Value> predicate) {
        Objects.requireNonNull(predicate, "predicate must not be null");
        RelocatableStreamValue streamValue = new RelocatableStreamValue();
        for (long i = 0; i < size; i++) {
            long offset = calculateArrayIndex(i);
            short head = readUInt8(input, offset);
            MajorType majorType = MajorType.findMajorType(head);
            ValueType valueType = queryContext.valueType(offset);

            streamValue.relocate(queryContext, majorType, valueType, offset);
            if (predicate.test(streamValue)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<Value> iterator() {
        return new SequenceIterator();
    }

    @Override
    public Value[] toArray() {
        if (size > Integer.MAX_VALUE) {
            throw new IllegalStateException("Sequence size larger than Integer.MAX_VALUE, array cannot be created");
        }
        Value[] values = new Value[(int) size];
        for (int i = 0; i < size; i++) {
            values[i] = get(i);
        }
        return values;
    }

    @Override
    public Value get(long sequenceIndex) {
        if (sequenceIndex > size || sequenceIndex < 0) {
            return null;
        }
        long offset = calculateArrayIndex(sequenceIndex);
        return Decoder.readValue(offset, queryContext);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (long i = 0; i < size; i++) {
            sb.append(get(i)).append(", ");
        }
        return sb.deleteCharAt(sb.length() - 1).deleteCharAt(sb.length() - 1).append(']').toString();
    }

    @Override
    public String asString() {
        StringBuilder sb = new StringBuilder("[");
        for (long i = 0; i < size; i++) {
            sb.append(get(i).asString()).append(", ");
        }
        return sb.deleteCharAt(sb.length() - 1).deleteCharAt(sb.length() - 1).append(']').toString();
    }

    private long calculateArrayIndex(long sequenceIndex) {
        int baseIndex = (int) (sequenceIndex / Integer.MAX_VALUE);
        int elementIndex = (int) (sequenceIndex % Integer.MAX_VALUE);
        return elementIndexes[baseIndex][elementIndex];
    }

    private class SequenceIterator
            implements Iterator<Value> {

        private long arrayIndex = 0;

        @Override
        public boolean hasNext() {
            return arrayIndex < size;
        }

        @Override
        public Value next() {
            try {
                if (arrayIndex >= size) {
                    throw new NoSuchElementException("No further element available");
                }
                long offset = calculateArrayIndex(arrayIndex);
                return Decoder.readValue(offset, queryContext);

            } finally {
                arrayIndex++;
            }
        }
    }

    public static Sequence readSequence(long offset, QueryContext queryContext) {
        Objects.requireNonNull(queryContext, "queryContext must not be null");
        Input input = queryContext.input();
        long headByteSize = ByteSizes.headByteSize(input, offset);
        long size = ElementCounts.sequenceElementCount(input, offset);
        long[][] elementIndexes = Decoder.readElementIndexes(input, offset + headByteSize, size);
        return new SequenceImpl(size, elementIndexes, queryContext);
    }

}
