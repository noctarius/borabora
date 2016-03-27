package com.noctarius.borabora;

import java.util.Map;
import java.util.function.Predicate;

public interface Dictionary {

    long size();

    boolean isEmpty();

    boolean containsKey(Predicate<Value> predicate);

    boolean containsValue(Predicate<Value> predicate);

    Value get(Predicate<Value> predicate);

    Iterable<Value> keys();

    Iterable<Value> values();

    Iterable<Map.Entry<Value, Value>> entries();

}
