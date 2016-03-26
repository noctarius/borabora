package com.noctarius.borabora;

public interface Value {

    MajorType majorType();

    ValueType valueType();

    <V> V tag();

    Number number();

    Sequence sequence();

    String string();

    Boolean bool();

}
