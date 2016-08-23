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

import java.math.BigInteger;
import java.net.URI;
import java.time.Instant;
import java.util.Date;

public interface ValueBuilder<B> {

    B putNumber(byte value);

    B putNumber(Byte value);

    B putNumber(short value);

    B putNumber(Short value);

    B putNumber(int value);

    B putNumber(Integer value);

    B putNumber(long value);

    B putNumber(Long value);

    B putNumber(Number value);

    B putNumber(float value);

    B putNumber(Float value);

    B putNumber(double value);

    B putNumber(Double value);

    B putHalfPrecision(float value);

    B putHalfPrecision(Float value);

    B putBigInteger(BigInteger value);

    B putString(String value);

    B putByteString(String value);

    B putTextString(String value);

    B putURI(URI uri);

    B putDateTime(Instant instant);

    B putDateTime(Date date);

    B putTimestamp(long timestamp);

    B putTimestamp(Instant instant);

    IndefiniteStringBuilder<B> putIndefiniteByteString();

    IndefiniteStringBuilder<B> putIndefiniteTextString();

    B putBoolean(boolean value);

    B putBoolean(Boolean value);

    B putValue(Object value);

    B putTag(Object value);

    SequenceBuilder<B> putSequence();

    SequenceBuilder<B> putSequence(long elements);

    DictionaryBuilder<B> putDictionary();

    DictionaryBuilder<B> putDictionary(long elements);

}
