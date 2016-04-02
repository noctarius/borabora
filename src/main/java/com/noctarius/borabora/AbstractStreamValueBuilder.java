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
package com.noctarius.borabora;

import com.noctarius.borabora.builder.DictionaryBuilder;
import com.noctarius.borabora.builder.SequenceBuilder;
import com.noctarius.borabora.builder.ValueBuilder;

abstract class AbstractStreamValueBuilder<B>
        implements ValueBuilder<B> {

    private final Output output;
    private final B builder;

    private long offset = 0;

    AbstractStreamValueBuilder(Output output) {
        this.output = output;
        this.builder = (B) this;
    }

    @Override
    public B putNumber(byte value) {
        return builder;
    }

    @Override
    public B putNumber(short value) {
        return builder;
    }

    @Override
    public B putNumber(int value) {
        return builder;
    }

    @Override
    public B putNumber(long value) {
        return builder;
    }

    @Override
    public B putNumber(Number value) {
        if (value == null) {
            offset = Encoder.putNull(offset, output);
        } else {
            // TODO
        }
        return builder;
    }

    @Override
    public B putNumber(float value) {
        return builder;
    }

    @Override
    public B putNumber(double value) {
        return builder;
    }

    @Override
    public B putString(String value) {
        offset = Encoder.putString(value, offset, output);
        return builder;
    }

    @Override
    public B putBoolean(boolean value) {
        offset = Encoder.putBoolean(value, offset, output);
        return builder;
    }

    @Override
    public B putBoolean(Object value) {
        if (value == null) {
            offset = Encoder.putNull(offset, output);
        } else {
            putBoolean((boolean) value);
        }
        return builder;
    }

    @Override
    public SequenceBuilder<B> putSequence() {
        return null;
    }

    @Override
    public SequenceBuilder<B> putSequence(int elements) {
        return null;
    }

    @Override
    public DictionaryBuilder<B> putDictionary() {
        return null;
    }

    @Override
    public DictionaryBuilder<B> putDictionary(int elements) {
        return null;
    }

}
