package com.noctarius.borabora;

import java.util.Collection;
import java.util.function.Supplier;

final class ObjectValue
        extends AbstractValue {

    private final Collection<SemanticTagProcessor> processors;
    private final Object value;

    ObjectValue(MajorType majorType, ValueType valueType, Object value, Collection<SemanticTagProcessor> processors) {
        super(majorType, valueType);
        this.value = value;
        this.processors = processors;
    }

    @Override
    public <V> V tag() {
        return null;
    }

    @Override
    public Number number() {
        return null;
    }

    @Override
    public Sequence sequence() {
        return null;
    }

    @Override
    public Dictionary dictionary() {
        return null;
    }

    @Override
    public String string() {
        return null;
    }

    @Override
    public Boolean bool() {
        return null;
    }

    @Override
    public byte[] raw() {
        throw new UnsupportedOperationException("raw is not supported on ObjectValue");
    }

    @Override
    protected <T> T extract(Validator validator, Supplier<T> supplier) {
        return null;
    }
}
