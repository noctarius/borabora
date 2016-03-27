package com.noctarius.borabora;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

final class DictionaryImpl
        implements Dictionary {

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Predicate<Value> predicate) {
        return false;
    }

    @Override
    public boolean containsValue(Predicate<Value> predicate) {
        return false;
    }

    @Override
    public Value get(Predicate<Value> predicate) {
        return null;
    }

    @Override
    public Set<Value> keySet() {
        return null;
    }

    @Override
    public Collection<Value> values() {
        return null;
    }

    @Override
    public Set<Map.Entry<Value, Value>> entrySet() {
        return null;
    }

}
