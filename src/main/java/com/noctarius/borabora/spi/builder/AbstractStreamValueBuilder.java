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
package com.noctarius.borabora.spi.builder;

import com.noctarius.borabora.HalfPrecisionFloat;
import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.builder.encoder.DictionaryBuilder;
import com.noctarius.borabora.builder.encoder.DictionaryEntryBuilder;
import com.noctarius.borabora.builder.encoder.IndefiniteByteStringBuilder;
import com.noctarius.borabora.builder.encoder.IndefiniteStringBuilder;
import com.noctarius.borabora.builder.encoder.SequenceBuilder;
import com.noctarius.borabora.builder.encoder.ValueBuilder;
import com.noctarius.borabora.spi.io.Encoder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;

import static com.noctarius.borabora.spi.io.Constants.OPCODE_BREAK_MASK;
import static com.noctarius.borabora.spi.io.Constants.TAG_ASCII_STRING;
import static com.noctarius.borabora.spi.io.Constants.UTC;

/**
 * The <tt>AbstractStreamValueBuilder</tt> class is an abstract convenience implementation of
 * {@link ValueBuilder} and provides all the common value encodings. When implementing a
 * <tt>ValueBuilder</tt> based interface, it is highly recommended to build on the base of this
 * abstract class to have support for future extension of the interface.
 *
 * @param <B> the current builder's type
 */
public abstract class AbstractStreamValueBuilder<B>
        implements ValueBuilder<B> {

    private static final Charset ASCII = Charset.forName("ASCII");
    private static final CharsetEncoder ASCII_ENCODER = ASCII.newEncoder();

    protected final EncoderContext encoderContext;
    private final B builder;

    protected AbstractStreamValueBuilder(EncoderContext encoderContext) {
        Objects.requireNonNull(encoderContext, "encoderContext must not be null");
        this.builder = (B) this;
        this.encoderContext = encoderContext;
    }

    @Override
    public B putTag(TagBuilderConsumer<B> consumer) {
        if (consumer == null) {
            return putTag((Object) null);
        }
        validate();
        consumer.execute(encoderContext, builder);
        return builder;
    }

    @Override
    public B putNumber(byte value) {
        validate();
        encoderContext.encode((offset, output) -> Encoder.putNumber(value, offset, output));
        return builder;
    }

    @Override
    public B putNumber(Byte value) {
        validate();
        encodeInt(value);
        return builder;
    }

    @Override
    public B putNumber(short value) {
        validate();
        encoderContext.encode((offset, output) -> Encoder.putNumber(value, offset, output));
        return builder;
    }

    @Override
    public B putNumber(Short value) {
        validate();
        encodeInt(value);
        return builder;
    }

    @Override
    public B putNumber(int value) {
        validate();
        encoderContext.encode((offset, output) -> Encoder.putNumber(value, offset, output));
        return builder;
    }

    @Override
    public B putNumber(Integer value) {
        validate();
        encodeInt(value);
        return builder;
    }

    @Override
    public B putFraction(BigDecimal value) {
        validate();
        encoderContext.encodeNullOrType(value, (offset, output) -> Encoder.putFraction(value, offset, output));
        return builder;
    }

    @Override
    public B putNumber(long value) {
        validate();
        encoderContext.encode((offset, output) -> Encoder.putNumber(value, offset, output));
        return builder;
    }

    @Override
    public B putNumber(Long value) {
        validate();
        encodeInt(value);
        return builder;
    }

    @Override
    public B putNumber(Number value) {
        if (value == null) {
            validate();
            encoderContext.encodeNull();
        } else if (value instanceof BigInteger) {
            validate();
            encoderContext.encode((offset, output) -> Encoder.putNumber((BigInteger) value, offset, output));

        } else if (value instanceof BigDecimal) {
            throw new IllegalArgumentException("BigDecimal is not supported");

        } else if (value instanceof Byte) {
            putNumber((byte) value);

        } else if (value instanceof Short) {
            putNumber((short) value);

        } else if (value instanceof Integer) {
            putNumber((int) value);

        } else if (value instanceof Long) {
            putNumber((long) value);

        } else if (value instanceof HalfPrecisionFloat) {
            putHalfPrecision(value.floatValue());

        } else if (value instanceof Float) {
            putNumber((float) value);

        } else if (value instanceof Double) {
            putNumber((double) value);

        } else {
            throw new IllegalArgumentException("Unknown Number type, cannot encode");
        }
        return builder;
    }

    @Override
    public B putNumber(float value) {
        validate();
        encoderContext.encode((offset, output) -> Encoder.putFloat(value, offset, output));
        return builder;
    }

    @Override
    public B putNumber(Float value) {
        if (value == null) {
            validate();
            encoderContext.encodeNull();
        } else {
            return putNumber(value.floatValue());
        }
        return builder;
    }

    @Override
    public B putNumber(double value) {
        validate();
        encoderContext.encode((offset, output) -> Encoder.putDouble(value, offset, output));
        return builder;
    }

    @Override
    public B putNumber(Double value) {
        if (value == null) {
            validate();
            encoderContext.encodeNull();
        } else {
            return putNumber(value.doubleValue());
        }
        return builder;
    }

    @Override
    public B putHalfPrecision(float value) {
        validate();
        encoderContext.encode((offset, output) -> Encoder.putHalfPrecision(value, offset, output));
        return builder;
    }

    @Override
    public B putHalfPrecision(Float value) {
        if (value == null) {
            validate();
            encoderContext.encodeNull();
        } else {
            return putHalfPrecision(value.floatValue());
        }
        return builder;
    }

    @Override
    public B putBigInteger(BigInteger value) {
        validate();
        if (value == null) {
            encoderContext.encodeNull();
        } else {
            encoderContext.encode((offset, output) -> Encoder.putBigInteger(value, offset, output));
        }
        return builder;
    }

    @Override
    public B putByteString(byte[] value) {
        validate();
        if (value == null) {
            encoderContext.encodeNull();
        } else {
            encoderContext.encode((offset, output) -> Encoder.putByteString(value, offset, output));
        }
        return builder;
    }

    @Override
    public B putString(String value) {
        validate();
        if (value == null) {
            encoderContext.encodeNull();
        } else {
            encoderContext.encode((offset, output) -> Encoder.putString(value, offset, output));
        }
        return builder;
    }

    @Override
    public B putAsciiString(String value) {
        validate();
        if (value == null) {
            encoderContext.encodeNull();
        } else {
            encoderContext.encode((offset, output) -> Encoder.putAsciiString(value, offset, output));
        }
        return builder;
    }

    @Override
    public B putTextString(String value) {
        validate();
        if (value == null) {
            encoderContext.encodeNull();
        } else {
            encoderContext.encode((offset, output) -> Encoder.putTextString(value, offset, output));
        }
        return builder;
    }

    @Override
    public B putURI(URI uri) {
        validate();
        if (uri == null) {
            encoderContext.encodeNull();

        } else {
            encoderContext.encode((offset, output) -> Encoder.putUri(uri, offset, output));
        }
        return builder;
    }

    @Override
    public B putDateTime(Instant instant) {
        validate();
        if (instant == null) {
            encoderContext.encodeNull();

        } else {
            ZonedDateTime atUTC = instant.atZone(UTC);
            encoderContext.encode((offset, output) -> Encoder.putDateTime(atUTC, offset, output));
        }
        return builder;
    }

    @Override
    public B putDateTime(Date date) {
        if (date == null) {
            validate();
            encoderContext.encodeNull();
            return builder;
        }
        return putDateTime(date.toInstant());
    }

    @Override
    public B putTimestamp(long timestamp) {
        validate();
        encoderContext.encode((offset, output) -> Encoder.putTimestamp(timestamp, offset, output));
        return builder;
    }

    @Override
    public B putTimestamp(Instant instant) {
        if (instant == null) {
            validate();
            encoderContext.encodeNull();
            return builder;
        }
        return putTimestamp(instant.getEpochSecond());
    }

    @Override
    public IndefiniteByteStringBuilder<B> putIndefiniteByteString() {
        validate();
        encoderContext.encode((offset, output) -> Encoder.encodeLengthAndValue(MajorType.ByteString, -1, offset, output));
        return new IndefiniteByteStringBuilderImpl<>(encoderContext, builder);
    }

    @Override
    public IndefiniteStringBuilder<B> putIndefiniteAsciiString() {
        validate();
        encoderContext.encode((offset, output) -> Encoder.putSemanticTag(TAG_ASCII_STRING, offset, output));
        encoderContext.encode((offset, output) -> Encoder.encodeLengthAndValue(MajorType.Sequence, -1, offset, output));
        return new IndefiniteStringBuilderImpl<>(encoderContext, true, builder);
    }

    @Override
    public IndefiniteStringBuilder<B> putIndefiniteTextString() {
        validate();
        encoderContext.encode((offset, output) -> Encoder.encodeLengthAndValue(MajorType.TextString, -1, offset, output));
        return new IndefiniteStringBuilderImpl<>(encoderContext, false, builder);
    }

    @Override
    public B putBoolean(boolean value) {
        validate();
        encoderContext.encode((offset, output) -> Encoder.putBoolean(value, offset, output));
        return builder;
    }

    @Override
    public B putBoolean(Boolean value) {
        validate();
        if (value == null) {
            encoderContext.encodeNull();
        } else {
            putBoolean((boolean) value);
        }
        return builder;
    }

    @Override
    public B putValue(Object value) {
        // TODO Capture Dictionary and Sequence!

        validate();
        if (value == null) {
            encoderContext.encodeNull();

        } else if (value instanceof Number //
                && !(value instanceof BigInteger) && !(value instanceof BigDecimal)) {

            return putNumber((Number) value);

        } else if (value instanceof Boolean) {
            return putBoolean((Boolean) value);

        } else if (value instanceof String) {
            return putString((String) value);

        } else {
            return putTag(value);
        }

        return builder;
    }

    @Override
    public B putTag(Object value) {
        validate();
        if (value == null) {
            encoderContext.encodeNull();

        } else {
            // Try to write as semantic tag
            encoderContext.encode((offset, output) -> encoderContext.applyEncoder(value, offset));
        }
        return builder;
    }

    @Override
    public SequenceBuilder<B> putSequence() {
        validate();
        encoderContext.encode((offset, output) -> Encoder.encodeLengthAndValue(MajorType.Sequence, -1, offset, output));
        return new SequenceBuilderImpl<>(encoderContext, -1, builder);
    }

    @Override
    public SequenceBuilder<B> putSequence(long elements) {
        validate();
        encoderContext.encode((offset, output) -> Encoder.encodeLengthAndValue(MajorType.Sequence, elements, offset, output));
        return new SequenceBuilderImpl<>(encoderContext, elements, builder);
    }

    @Override
    public DictionaryBuilder<B> putDictionary() {
        validate();
        encoderContext.encode((offset, output) -> Encoder.encodeLengthAndValue(MajorType.Dictionary, -1, offset, output));
        return new DictionaryBuilderImpl<>(encoderContext, -1, builder);
    }

    @Override
    public DictionaryBuilder<B> putDictionary(long elements) {
        validate();
        encoderContext.encode((offset, output) -> Encoder.encodeLengthAndValue(MajorType.Dictionary, elements, offset, output));
        return new DictionaryBuilderImpl<>(encoderContext, elements, builder);
    }

    protected void validate() {
    }

    private void encodeInt(Number value) {
        encoderContext.encodeNullOrType(value, (offset, output) -> Encoder.putNumber(value.longValue(), offset, output));
    }

    private class IndefiniteByteStringBuilderImpl<B>
            implements IndefiniteByteStringBuilder<B> {

        private final EncoderContext encoderContext;
        private final B builder;

        IndefiniteByteStringBuilderImpl(EncoderContext encoderContext, B builder) {
            Objects.requireNonNull(encoderContext, "encoderContext must not be null");
            Objects.requireNonNull(builder, "builder must not be null");
            this.encoderContext = encoderContext;
            this.builder = builder;
        }

        @Override
        public IndefiniteByteStringBuilder<B> putByteString(byte[] value) {
            Objects.requireNonNull(value, "null is not a legal value of an indefinite bytestring");
            encoderContext.encode((offset, output) -> Encoder.putByteString(value, offset, output));
            return this;
        }

        @Override
        public B endIndefiniteByteString() {
            encoderContext.encode((offset, output) -> output.write(offset++, (byte) OPCODE_BREAK_MASK));
            return builder;
        }
    }

    private static class IndefiniteStringBuilderImpl<B>
            implements IndefiniteStringBuilder<B> {

        private final EncoderContext encoderContext;
        private final boolean asciiOnly;
        private final B builder;

        IndefiniteStringBuilderImpl(EncoderContext encoderContext, boolean asciiOnly, B builder) {
            Objects.requireNonNull(encoderContext, "encoderContext must not be null");
            Objects.requireNonNull(builder, "builder must not be null");
            this.encoderContext = encoderContext;
            this.asciiOnly = asciiOnly;
            this.builder = builder;
        }

        @Override
        public IndefiniteStringBuilder<B> putString(String value) {
            Objects.requireNonNull(value, "null is not a legal value of an indefinite string");
            if (asciiOnly) {
                if (!ASCII_ENCODER.canEncode(value)) {
                    throw new IllegalArgumentException("UTF8 string cannot be added to a CBOR ByteString");
                }
                encoderContext.encode((offset, output) -> Encoder.putAsciiString(value, offset, output));

            } else {
                encoderContext.encode((offset, output) -> Encoder.putTextString(value, offset, output));
            }
            return this;
        }

        @Override
        public B endIndefiniteString() {
            encoderContext.encode((offset, output) -> output.write(offset++, (byte) OPCODE_BREAK_MASK));
            return builder;
        }
    }

    private static class SequenceBuilderImpl<B>
            extends AbstractStreamValueBuilder<SequenceBuilder<B>>
            implements SequenceBuilder<B> {

        private final B builder;
        private final long maxElements;

        private long elements;

        SequenceBuilderImpl(EncoderContext encoderContext, long maxElements, B builder) {
            super(encoderContext);
            Objects.requireNonNull(builder, "builder must not be null");
            this.maxElements = maxElements;
            this.builder = builder;
        }

        @Override
        public SequenceBuilder<B> putTag(TagBuilderConsumer<SequenceBuilder<B>> consumer) {
            validate();
            consumer.execute(encoderContext, this);
            return this;
        }

        @Override
        public B endSequence() {
            if (maxElements != -1 && maxElements != elements) {
                String msg = String.format("Expected %s element but only %s elements written", maxElements, elements);
                throw new IllegalStateException(msg);
            }

            if (maxElements == -1) {
                encoderContext.encode((offset, output) -> output.write(offset++, (byte) OPCODE_BREAK_MASK));
            }
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

    private static class DictionaryBuilderImpl<B>
            implements DictionaryBuilder<B> {

        private final B builder;
        private final long maxElements;
        private final EncoderContext encoderContext;

        private long elements;

        DictionaryBuilderImpl(EncoderContext encoderContext, long maxElements, B builder) {
            Objects.requireNonNull(encoderContext, "encoderContext must not be null");
            Objects.requireNonNull(builder, "builder must not be null");
            this.builder = builder;
            this.maxElements = maxElements;
            this.encoderContext = encoderContext;
        }

        @Override
        public DictionaryEntryBuilder<B> putEntry() {
            validate();
            return new DictionaryEntryBuilderImpl<>(encoderContext, this);
        }

        @Override
        public B endDictionary() {
            if (maxElements != -1 && maxElements != elements) {
                String msg = String.format("Expected %s element but only %s elements written", maxElements, elements);
                throw new IllegalStateException(msg);
            }
            if (maxElements == -1) {
                encoderContext.encode((offset, output) -> output.write(offset++, (byte) OPCODE_BREAK_MASK));
            }
            return builder;
        }

        private void validate() {
            if (maxElements > -1 && elements >= maxElements) {
                throw new IllegalStateException("Cannot add another element, maximum element count reached");
            }
            elements++;
        }
    }

    private static class DictionaryEntryBuilderImpl<B>
            extends AbstractStreamValueBuilder<DictionaryEntryBuilder<B>>
            implements DictionaryEntryBuilder<B> {

        private final DictionaryBuilder<B> builder;

        private boolean key = false;
        private boolean value = false;

        DictionaryEntryBuilderImpl(EncoderContext encoderContext, DictionaryBuilder<B> builder) {
            super(encoderContext);
            Objects.requireNonNull(builder, "builder must not be null");
            this.builder = builder;
        }

        @Override
        public DictionaryEntryBuilder<B> putTag(TagBuilderConsumer<DictionaryEntryBuilder<B>> consumer) {
            validate();
            Objects.requireNonNull(consumer, "consumer must not be null");
            consumer.execute(encoderContext, this);
            return this;
        }

        @Override
        public DictionaryBuilder<B> endEntry() {
            if (!key) {
                throw new IllegalStateException("Entry key not set");
            }
            if (!value) {
                throw new IllegalStateException("Entry value not set");
            }
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
