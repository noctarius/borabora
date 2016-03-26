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

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

public final class Value {

    public static final String VALUE_TYPE_DOES_NOT_MATCH = "Requested value type does not match the read value: %s != %s";
    public static final String MAJOR_TYPE_DOES_NOT_MATCH = "Requested major type does not match the read value: %s != %s";
    public static final String VALUE_TYPE_NOT_A_DOUBLE = "Requested value type does not match the read value: {%s|%s} != %s";

    private final Collection<SemanticTagProcessor> processors;
    private final MajorType majorType;
    private final ValueType valueType;
    private final Decoder stream;
    private final long index;
    private final long length;

    protected Value(MajorType majorType, ValueType valueType, Decoder stream, long index, long length,
                    Collection<SemanticTagProcessor> processors) {

        if (index == -1) {
            throw new IllegalArgumentException("No index available for CBOR type");
        }
        if (length == -1) {
            throw new IllegalArgumentException(String.format("Length calculation for CBOR type %s is not available", majorType));
        }
        this.processors = processors;
        this.majorType = majorType;
        this.valueType = valueType;
        this.stream = stream;
        this.index = index;
        this.length = length;
    }

    public MajorType majorType() {
        return majorType;
    }

    public ValueType valueType() {
        return valueType.identity();
    }

    public <V> V tag() {
        return extract(() -> matchMajorType(MajorType.SemanticTag), () -> tag0(index, length));
    }

    public Number number() {
        return extract(() -> matchValueType(ValueTypes.Uint, ValueTypes.NInt), () -> stream.readInt(index));
    }

    public SequenceImpl sequence() {
        return extract(() -> matchValueType(ValueTypes.Sequence), () -> stream.readSequence(index, processors));
    }

    public String string() {
        return extract(this::matchStringValueType, () -> stream.readString(index));
    }

    public Boolean bool() {
        return extract(() -> stream.getBooleanValue(index));
    }

    private <T> T extract(Supplier<T> supplier) {
        return extract(null, supplier);
    }

    private <T> T extract(Validator validator, Supplier<T> supplier) {
        if (validator != null) {
            validator.validate();
        }
        short head = stream.transientUint8(index);
        // Null is legal for all types
        if (stream.isNull(head)) {
            return null;
        }
        if (MajorType.SemanticTag == majorType) {
            return tag0(index, length);
        }
        return supplier.get();
    }

    private void matchMajorType(MajorType expected) {
        if (expected != majorType) {
            String msg = String.format(MAJOR_TYPE_DOES_NOT_MATCH, expected, majorType);
            throw new IllegalStateException(msg);
        }
    }

    private void matchValueType(ValueType expected) {
        ValueType identity = valueType.identity();
        if (expected != identity) {
            String msg = String.format(VALUE_TYPE_DOES_NOT_MATCH, expected, identity);
            throw new IllegalStateException(msg);
        }
    }

    private void matchValueType(ValueType expected1, ValueType expected2) {
        ValueType identity = valueType.identity();
        if (expected1 != identity && expected2 != identity) {
            String msg = String.format(VALUE_TYPE_NOT_A_DOUBLE, expected1, expected2, identity);
            throw new IllegalStateException(msg);
        }
    }

    private void matchStringValueType() {
        ValueType identity = valueType.identity();
        if (ValueTypes.ByteString != identity && ValueTypes.TextString != identity) {

            String msg = String.format(VALUE_TYPE_NOT_A_DOUBLE, ValueTypes.ByteString, ValueTypes.TextString, identity);
            throw new IllegalStateException(msg);
        }
    }

    private <V> SemanticTagProcessor<V> findProcessor(long index) {
        Optional<SemanticTagProcessor> optional = processors.stream().filter(p -> p.handles(stream, index)).findFirst();
        return optional.isPresent() ? optional.get() : null;
    }

    private <V> V tag0(long index, long length) {
        SemanticTagProcessor<V> processor = findProcessor(index);
        if (processor == null) {
            return null;
        }
        return processor.process(stream, index, length);
    }

    private interface Validator {
        void validate();
    }

}
