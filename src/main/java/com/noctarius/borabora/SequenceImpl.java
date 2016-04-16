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
import java.util.NoSuchElementException;
import java.util.function.Predicate;

final class SequenceImpl
        implements Sequence {

    private final Input input;
    private final long size;
    private final long[][] elementIndexes;
    private final Collection<SemanticTagProcessor> processors;

    SequenceImpl(Input input, long size, long[][] elementIndexes, Collection<SemanticTagProcessor> processors) {
        this.input = input;
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
    public boolean contains(Predicate<Value> predicate) {
        RelocatableStreamValue streamValue = new RelocatableStreamValue(input, processors);
        for (long i = 0; i < size; i++) {
            long offset = calculateArrayIndex(i);
            short head = Decoder.transientUint8(input, offset);
            MajorType majorType = MajorType.findMajorType(head);
            ValueType valueType = ValueTypes.valueType(input, offset);
            streamValue.relocate(majorType, valueType, offset);
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
        return Decoder.readValue(input, offset, processors);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (long i = 0; i < size; i++) {
            sb.append(get(i)).append(", ");
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
                return Decoder.readValue(input, offset, processors);

            } finally {
                arrayIndex++;
            }
        }
    }

}
