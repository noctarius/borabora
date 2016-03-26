package com.noctarius.borabora;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

abstract class AbstractValueBuilder<B>
        implements ValueBuilder<B> {

    private final List<Object> values = new ArrayList<>();
    private final B builder;

    protected AbstractValueBuilder() {
        this.builder = (B) this;
    }

    @Override
    public B putNumber(byte value) {
        validate();
        put(value);
        return builder;
    }

    @Override
    public B putNumber(short value) {
        validate();
        put(value);
        return builder;
    }

    @Override
    public B putNumber(int value) {
        validate();
        put(value);
        return builder;
    }

    @Override
    public B putNumber(long value) {
        validate();
        put(value);
        return builder;
    }

    @Override
    public B putNumber(BigInteger value) {
        validate();
        put(value);
        return builder;
    }

    @Override
    public B putNumber(float value) {
        validate();
        put(value);
        return builder;
    }

    @Override
    public B putNumber(double value) {
        validate();
        put(value);
        return builder;
    }

    @Override
    public B putNumber(BigDecimal value) {
        validate();
        put(value);
        return builder;
    }

    @Override
    public B putString(String value) {
        validate();
        put(value);
        return builder;
    }

    @Override
    public B putBoolean(boolean value) {
        validate();
        put(value);
        return builder;
    }

    @Override
    public SequenceBuilder<B> putSequence() {
        validate();
        return new SequenceBuilderImpl<>(builder, values);
    }

    @Override
    public SequenceBuilder<B> putSequence(int elements) {
        validate();
        return new SequenceBuilderImpl<B>(elements, builder, values);
    }

    @Override
    public DictionaryBuilder<B> putDictionary() {
        validate();
        return new DictionaryBuilderImpl<B>(builder, values);
    }

    @Override
    public DictionaryBuilder<B> putDictionary(int elements) {
        validate();
        return new DictionaryBuilderImpl<B>(elements, builder, values);
    }

    protected List<Object> values() {
        return values;
    }

    protected void validate() {
    }

    protected void put(Object value) {
        values.add(value);
    }

}
