package com.noctarius.borabora;

final class NullValue
        implements Value {

    NullValue() {
    }

    @Override
    public MajorType majorType() {
        return MajorType.Unknown;
    }

    @Override
    public ValueType valueType() {
        return ValueTypes.Null;
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
        return new byte[0];
    }

}
