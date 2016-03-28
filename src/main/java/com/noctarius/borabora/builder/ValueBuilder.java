/*
 * Copyright (c) 2016, Christoph Engelbert (aka noctarius) and
 * contributors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.noctarius.borabora.builder;

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
