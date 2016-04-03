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
import com.noctarius.borabora.builder.DictionaryEntryBuilder;
import com.noctarius.borabora.builder.IndefiniteStringBuilder;
import com.noctarius.borabora.builder.SequenceBuilder;
import com.noctarius.borabora.builder.ValueBuilder;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import static com.noctarius.borabora.Constants.OPCODE_BREAK_MASK;

abstract class AbstractStreamValueBuilder<B>
        implements ValueBuilder<B> {

    private static final Charset ASCII = Charset.forName("ASCII");
    private static final CharsetEncoder ASCII_ENCODER = ASCII.newEncoder();

    private final Output output;
    private final B builder;

    private long offset;

    AbstractStreamValueBuilder(Output output) {
        this(0, output);
    }

    AbstractStreamValueBuilder(long offset, Output output) {
        this.offset = offset;
        this.output = output;
        this.builder = (B) this;
    }

    @Override
    public B putNumber(byte value) {
        validate();
        return builder;
    }

    @Override
    public B putNumber(short value) {
        validate();
        return builder;
    }

    @Override
    public B putNumber(int value) {
        validate();
        return builder;
    }

    @Override
    public B putNumber(long value) {
        validate();
        return builder;
    }

    @Override
    public B putNumber(Number value) {
        validate();
        if (value == null) {
            offset = Encoder.putNull(offset, output);
        } else {
            // TODO
        }
        return builder;
    }

    @Override
    public B putNumber(float value) {
        validate();
        return builder;
    }

    @Override
    public B putNumber(double value) {
        validate();
        return builder;
    }

    @Override
    public B putString(String value) {
        validate();
        offset = Encoder.putString(value, offset, output);
        return builder;
    }

    @Override
    public IndefiniteStringBuilder<B> putIndefiniteByteString() {
        validate();
        offset = Encoder.encodeLength(MajorType.ByteString, -1, offset, output);
        return new IndefiniteStringBuilderImpl<>(true, builder);
    }

    @Override
    public IndefiniteStringBuilder<B> putIndefiniteTextString() {
        validate();
        offset = Encoder.encodeLength(MajorType.TextString, -1, offset, output);
        return new IndefiniteStringBuilderImpl<>(false, builder);
    }

    @Override
    public B putBoolean(boolean value) {
        validate();
        offset = Encoder.putBoolean(value, offset, output);
        return builder;
    }

    @Override
    public B putBoolean(Object value) {
        validate();
        if (value == null) {
            offset = Encoder.putNull(offset, output);
        } else {
            putBoolean((boolean) value);
        }
        return builder;
    }

    @Override
    public SequenceBuilder<B> putSequence() {
        validate();
        return new SequenceBuilderImpl<>(-1, output, builder);
    }

    @Override
    public SequenceBuilder<B> putSequence(int elements) {
        validate();
        return new SequenceBuilderImpl<>(elements, output, builder);
    }

    @Override
    public DictionaryBuilder<B> putDictionary() {
        validate();
        return new DictionaryBuilderImpl<>(-1, output, builder);
    }

    @Override
    public DictionaryBuilder<B> putDictionary(int elements) {
        validate();
        return new DictionaryBuilderImpl<>(elements, output, builder);
    }

    protected long offset() {
        return offset;
    }

    protected void validate() {
    }

    private class IndefiniteStringBuilderImpl<B>
            implements IndefiniteStringBuilder<B> {

        private final boolean asciiOnly;
        private final B builder;

        IndefiniteStringBuilderImpl(boolean asciiOnly, B builder) {
            this.asciiOnly = asciiOnly;
            this.builder = builder;
        }

        @Override
        public IndefiniteStringBuilder<B> putString(String value) {
            if (value == null) {
                throw new NullPointerException("null is not a legal value of an indefinite string");
            }
            if (asciiOnly) {
                if (!ASCII_ENCODER.canEncode(value)) {
                    throw new IllegalArgumentException("UTF8 string cannot be added to a CBOR ByteString");
                }
                offset = Encoder.putByteString(value, offset, output);

            } else {
                offset = Encoder.putTextString(value, offset, output);
            }
            return this;
        }

        @Override
        public B endIndefiniteString() {
            output.write(offset++, (byte) OPCODE_BREAK_MASK);
            return builder;
        }
    }

    private class SequenceBuilderImpl<B>
            extends AbstractStreamValueBuilder<SequenceBuilder<B>>
            implements SequenceBuilder<B> {

        private final B builder;
        private final int maxElements;

        private int elements;

        SequenceBuilderImpl(int maxElements, Output output, B builder) {
            super(offset, output);
            this.maxElements = maxElements;
            this.builder = builder;
        }

        @Override
        public B endSequence() {
            offset = offset();
            return builder;
        }

        @Override
        protected void validate() {
            if (maxElements > -1 && elements >= maxElements) {
                throw new IllegalStateException("Cannot add another element, maximum element count reached");
            }
            elements++;
        }
    }

    private class DictionaryBuilderImpl<B>
            implements DictionaryBuilder<B> {

        private final Output output;
        private final B builder;
        private final int maxElements;

        private int elements;

        DictionaryBuilderImpl(int maxElements, Output output, B builder) {
            this.output = output;
            this.builder = builder;
            this.maxElements = maxElements;
        }

        @Override
        public DictionaryEntryBuilder<B> putEntry() {
            validate();
            return new DictionaryEntryBuilderImpl<>(offset, output, this);
        }

        @Override
        public B endDictionary() {
            return builder;
        }

        private void validate() {
            if (maxElements > -1 && elements >= maxElements) {
                throw new IllegalStateException("Cannot add another element, maximum element count reached");
            }
            elements++;
        }
    }

    private class DictionaryEntryBuilderImpl<B>
            extends AbstractStreamValueBuilder<DictionaryEntryBuilder<B>>
            implements DictionaryEntryBuilder<B> {

        private final DictionaryBuilder<B> builder;

        private boolean key = false;
        private boolean value = false;

        DictionaryEntryBuilderImpl(long offset, Output output, DictionaryBuilder<B> builder) {
            super(offset, output);
            this.builder = builder;
        }

        @Override
        public DictionaryBuilder<B> endEntry() {
            offset = offset();
            return builder;
        }

        @Override
        protected void validate() {
            if (key && value) {
                throw new IllegalStateException("Cannot add another element, key and value are already set");
            }
            if (key) {
                value = true;
            } else {
                key = true;
            }
        }
    }

}
