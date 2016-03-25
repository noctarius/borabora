package com.noctarius.borabora;

import java.util.Iterator;

final class SequenceImpl
        implements Sequence {

    private final Decoder stream;
    private final long index;
    private final long size;

    SequenceImpl(Decoder stream, long index, long size) {
        this.stream = stream;
        this.index = index;
        this.size = size;
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
    public Iterator<Value> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public Value get(long index) {
        return null;
    }
}
