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
        return valueType;
    }

    public <V> V tag() {
        return extract(() -> {
            matchMajorType(MajorType.SemanticTag);
            SemanticTagProcessor<V> processor = findProcessor();
            if (processor == null) {
                return null;
            }
            return processor.process(stream, index, length);
        });
    }

    public Number uint() {
        return extract(() -> {
            matchMajorType(MajorType.UnsignedInteger);
            return stream.readUint(index);
        });
    }

    public Number sint() {
        return extract(() -> {
            matchMajorType(MajorType.NegativeInteger);
            return stream.readSInt(index);
        });
    }

    public String string() {
        return extract(() -> {
            matchStringMajorType();
            return stream.readString(index);
        });
    }

    public Boolean bool() {
        return extract(() -> stream.getBooleanValue(index));
    }

    private <T> T extract(Supplier<T> supplier) {
        short head = stream.transientUint8();
        // Null is legal for all types
        if (stream.isNull(head)) {
            return null;
        }
        return supplier.get();
    }

    private void matchMajorType(MajorType expected) {
        if (expected != majorType) {
            String msg = String.format("Requested value type does not match the read value: %s != %s", expected, majorType);
            throw new IllegalStateException(msg);
        }
    }

    private void matchStringMajorType() {
        if (MajorType.ByteString != majorType
                && MajorType.TextString != majorType) {

            String msg = String.format("Requested value type does not match the read value: {%s|%s} != %s",
                    MajorType.ByteString, MajorType.TextString, majorType);

            throw new IllegalStateException(msg);
        }
    }

    private <V> SemanticTagProcessor<V> findProcessor() {
        Optional<SemanticTagProcessor> optional = processors.stream().filter(p -> p.handles(stream, index)).findFirst();
        return optional.isPresent() ? optional.get() : null;
    }

}
