package com.noctarius.borabora;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public interface Dictionary {

    int size();

    boolean isEmpty();

    boolean containsKey(Predicate<Value> predicate);

    boolean containsValue(Predicate<Value> predicate);

    Value get(Predicate<Value> predicate);

    Set<Value> keySet();

    Collection<Value> values();

    Set<Map.Entry<Value, Value>> entrySet();

}
