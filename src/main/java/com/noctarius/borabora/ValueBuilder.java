package com.noctarius.borabora;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface ValueBuilder<B> {

    B putNumber(byte value);

    B putNumber(short value);

    B putNumber(int value);

    B putNumber(long value);

    B putNumber(BigInteger value);

    B putNumber(float value);

    B putNumber(double value);

    B putNumber(BigDecimal value);

    B putString(String value);

    B putBoolean(boolean value);

    SequenceBuilder<B> putSequence();

    SequenceBuilder<B> putSequence(int elements);

    DictionaryBuilder<B> putDictionary();

    DictionaryBuilder<B> putDictionary(int elements);

}
