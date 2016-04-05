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
        return false;
    }

    @Override
    public Iterator<Value> iterator() {
        return null;
    }

    @Override
    public Value[] toArray() {
        return new Value[0];
    }

    @Override
    public Value get(long sequenceIndex) {
        int baseIndex = (int) (sequenceIndex / Integer.MAX_VALUE);
        int elementIndex = (int) (sequenceIndex % Integer.MAX_VALUE);
        long position = elementIndexes[baseIndex][elementIndex];
        short head = Decoder.transientUint8(input, position);
        MajorType majorType = MajorType.findMajorType(head);
        ValueType valueType = ValueTypes.valueType(input, position);
        long length = majorType.byteSize(input, position);
        return new StreamValue(majorType, valueType, input, position, length, processors);
    }

}
