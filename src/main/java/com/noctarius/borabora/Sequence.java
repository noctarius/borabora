package com.noctarius.borabora;

import java.util.Iterator;
import java.util.function.Predicate;

public interface Sequence
        extends Iterable<Value> {

    long size();

    boolean isEmpty();

    boolean contains(Predicate<Value> predicate);

    Iterator<Value> iterator();

    Value[] toArray();

    Value get(long sequenceIndex);

}
