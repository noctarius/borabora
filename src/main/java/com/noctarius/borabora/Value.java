package com.noctarius.borabora;

public interface Value {

    MajorType majorType();

    ValueType valueType();

    <V> V tag();

    Number number();

    Sequence sequence();

    Dictionary dictionary();

    String string();

    Boolean bool();

    byte[] raw();

}
