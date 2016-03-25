package com.noctarius.borabora;

import java.util.Iterator;

public interface Sequence
        extends Iterable<Value> {

    long size();

    boolean isEmpty();

    Iterator<Value> iterator();

    Object[] toArray();

    <T> T[] toArray(T[] a);

    Value get(long index);

}
