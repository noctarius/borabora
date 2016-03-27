package com.noctarius.borabora;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

final class SequenceImpl
        implements Sequence {

    private final Decoder stream;
    private final long size;
    private final long[][] elementIndexes;
    private final Collection<SemanticTagProcessor> processors;

    SequenceImpl(Decoder stream, long size, long[][] elementIndexes, Collection<SemanticTagProcessor> processors) {
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
        short head = stream.transientUint8(position);
        MajorType majorType = MajorType.findMajorType(head);
        ValueType valueType = ValueTypes.valueType(stream, position);
        long length = majorType.byteSize(stream, position);
        return new StreamValue(majorType, valueType, stream, position, length, processors);
    }

}
