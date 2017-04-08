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
package com.noctarius.borabora.impl;

import com.noctarius.borabora.Value;
import com.noctarius.borabora.builder.encoder.DictionaryBuilder;
import com.noctarius.borabora.builder.encoder.DictionaryEntryBuilder;
import com.noctarius.borabora.builder.encoder.IndefiniteStringBuilder;
import com.noctarius.borabora.builder.encoder.SequenceBuilder;
import com.noctarius.borabora.builder.encoder.ValueBuilder;
import com.noctarius.borabora.spi.builder.TagBuilderConsumer;
import com.noctarius.borabora.spi.transformation.ChainingMutationBuilder;
import com.noctarius.borabora.spi.transformation.Mutator;
import com.noctarius.borabora.spi.transformation.Transformation;
import com.noctarius.borabora.spi.transformation.TransformationBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

abstract class TransformationBuilderImpl<T>
        implements ValueBuilder<T> {

    final List<Function<?, ?>> executions;
    final T builder;

    private TransformationBuilderImpl(List<Function<?, ?>> executions) {
        this.executions = executions;
        this.builder = (T) this;
    }

    @Override
    public T putNumber(byte value) {
        withValueBuilder(vb -> vb.putNumber(value));
        return builder;
    }

    @Override
    public T putNumber(Byte value) {
        withValueBuilder(vb -> vb.putNumber(value));
        return builder;
    }

    @Override
    public T putNumber(short value) {
        withValueBuilder(vb -> vb.putNumber(value));
        return builder;
    }

    @Override
    public T putNumber(Short value) {
        withValueBuilder(vb -> vb.putNumber(value));
        return builder;
    }

    @Override
    public T putNumber(int value) {
        withValueBuilder(vb -> vb.putNumber(value));
        return builder;
    }

    @Override
    public T putNumber(Integer value) {
        withValueBuilder(vb -> vb.putNumber(value));
        return builder;
    }

    @Override
    public T putNumber(long value) {
        withValueBuilder(vb -> vb.putNumber(value));
        return builder;
    }

    @Override
    public T putNumber(Long value) {
        withValueBuilder(vb -> vb.putNumber(value));
        return builder;
    }

    @Override
    public T putNumber(Number value) {
        withValueBuilder(vb -> vb.putNumber(value));
        return builder;
    }

    @Override
    public T putNumber(float value) {
        withValueBuilder(vb -> vb.putNumber(value));
        return builder;
    }

    @Override
    public T putNumber(Float value) {
        withValueBuilder(vb -> vb.putNumber(value));
        return builder;
    }

    @Override
    public T putNumber(double value) {
        withValueBuilder(vb -> vb.putNumber(value));
        return builder;
    }

    @Override
    public T putNumber(Double value) {
        withValueBuilder(vb -> vb.putNumber(value));
        return builder;
    }

    @Override
    public T putHalfPrecision(float value) {
        withValueBuilder(vb -> vb.putHalfPrecision(value));
        return builder;
    }

    @Override
    public T putHalfPrecision(Float value) {
        withValueBuilder(vb -> vb.putHalfPrecision(value));
        return builder;
    }

    @Override
    public T putBigInteger(BigInteger value) {
        withValueBuilder(vb -> vb.putBigInteger(value));
        return builder;
    }

    @Override
    public T putString(String value) {
        withValueBuilder(vb -> vb.putString(value));
        return builder;
    }

    @Override
    public T putByteString(String value) {
        withValueBuilder(vb -> vb.putByteString(value));
        return builder;
    }

    @Override
    public T putTextString(String value) {
        withValueBuilder(vb -> vb.putTextString(value));
        return builder;
    }

    @Override
    public T putURI(URI value) {
        withValueBuilder(vb -> vb.putURI(value));
        return builder;
    }

    @Override
    public T putDateTime(Instant value) {
        withValueBuilder(vb -> vb.putDateTime(value));
        return builder;
    }

    @Override
    public T putDateTime(Date value) {
        withValueBuilder(vb -> vb.putDateTime(value));
        return builder;
    }

    @Override
    public T putTimestamp(long value) {
        withValueBuilder(vb -> vb.putTimestamp(value));
        return builder;
    }

    @Override
    public T putTimestamp(Instant value) {
        withValueBuilder(vb -> vb.putTimestamp(value));
        return builder;
    }

    @Override
    public T putFraction(BigDecimal value) {
        withValueBuilder(vb -> vb.putFraction(value));
        return builder;
    }

    @Override
    public IndefiniteStringBuilder<T> putIndefiniteByteString() {
        withValueBuilder(vb -> vb.putIndefiniteByteString());
        return new IndefiniteStringBuilderImpl(executions, this);
    }

    @Override
    public IndefiniteStringBuilder<T> putIndefiniteTextString() {
        withValueBuilder(vb -> vb.putIndefiniteTextString());
        return new IndefiniteStringBuilderImpl(executions, this);
    }

    @Override
    public T putBoolean(boolean value) {
        withValueBuilder(vb -> vb.putBoolean(value));
        return builder;
    }

    @Override
    public T putBoolean(Boolean value) {
        withValueBuilder(vb -> vb.putBoolean(value));
        return builder;
    }

    @Override
    public T putValue(Object value) {
        withValueBuilder(vb -> vb.putValue(value));
        return builder;
    }

    @Override
    public T putTag(Object value) {
        withValueBuilder(vb -> vb.putTag(value));
        return builder;
    }

    @Override
    public T putTag(TagBuilderConsumer<T> consumer) {
        withValueBuilder(vb -> vb.putTag(consumer));
        return builder;
    }

    void withValueBuilder(Function<ValueBuilder, ?> consumer) {
        executions.add(consumer);
    }

    void withSequenceBuilder(Function<SequenceBuilder, ?> consumer) {
        executions.add(consumer);
    }

    void withDictionaryEntryBuilder(Function<DictionaryEntryBuilder, ?> consumer) {
        executions.add(consumer);
    }

    static TransformationBuilder newTransformationBuilder(Function<Transformation, Mutator> mutatorFactory,
                                                          ChainingMutationBuilder parentBuilder, List<Mutator> mutators) {

        return new TransformationBuilderImpl0(mutatorFactory, parentBuilder, mutators);
    }

    private static class TransformationBuilderImpl0
            extends com.noctarius.borabora.impl.TransformationBuilderImpl<TransformationBuilder>
            implements TransformationBuilder {

        private final Function<Transformation, Mutator> mutatorFactory;
        private final ChainingMutationBuilder parentBuilder;
        private final List<Mutator> mutators;

        private TransformationBuilderImpl0(Function<Transformation, Mutator> mutatorFactory,
                                           ChainingMutationBuilder parentBuilder, List<Mutator> mutators) {
            super(new ArrayList<>());
            this.mutatorFactory = mutatorFactory;
            this.parentBuilder = parentBuilder;
            this.mutators = mutators;
        }

        @Override
        public ChainingMutationBuilder endReplace() {
            mutators.add(mutatorFactory.apply(new TransformationImpl(executions)));
            return parentBuilder;
        }

        @Override
        public SequenceBuilder<TransformationBuilder> putSequence() {
            withValueBuilder(ValueBuilder::putSequence);
            return new SequenceBuilderImpl<>(executions, this);
        }

        @Override
        public SequenceBuilder<TransformationBuilder> putSequence(long elements) {
            withValueBuilder(vb -> vb.putSequence(elements));
            return new SequenceBuilderImpl<>(executions, this);
        }

        @Override
        public DictionaryBuilder<TransformationBuilder> putDictionary() {
            withValueBuilder(ValueBuilder::putDictionary);
            return new DictionaryBuilderImpl<>(executions, this);
        }

        @Override
        public DictionaryBuilder<TransformationBuilder> putDictionary(long elements) {
            withValueBuilder(vb -> vb.putDictionary(elements));
            return new DictionaryBuilderImpl<>(executions, this);
        }
    }

    private static class SequenceBuilderImpl<T extends ValueBuilder<T>>
            extends com.noctarius.borabora.impl.TransformationBuilderImpl<SequenceBuilder<T>>
            implements SequenceBuilder<T> {

        private final T parent;

        private SequenceBuilderImpl(List<Function<?, ?>> executions, T parent) {
            super(executions);
            this.parent = parent;
        }

        @Override
        public T endSequence() {
            withSequenceBuilder(SequenceBuilder::endSequence);
            return parent;
        }

        @Override
        public SequenceBuilder<SequenceBuilder<T>> putSequence() {
            withValueBuilder(ValueBuilder::putSequence);
            return (SequenceBuilder<SequenceBuilder<T>>) this;
        }

        @Override
        public SequenceBuilder<SequenceBuilder<T>> putSequence(long elements) {
            withValueBuilder(vb -> vb.putSequence(elements));
            return (SequenceBuilder<SequenceBuilder<T>>) this;
        }

        @Override
        public DictionaryBuilder<SequenceBuilder<T>> putDictionary() {
            withValueBuilder(ValueBuilder::putDictionary);
            return new DictionaryBuilderImpl<>(executions, this);
        }

        @Override
        public DictionaryBuilder<SequenceBuilder<T>> putDictionary(long elements) {
            withValueBuilder(vb -> vb.putDictionary(elements));
            return new DictionaryBuilderImpl<>(executions, this);
        }
    }

    private static class DictionaryBuilderImpl<T>
            implements DictionaryBuilder<T> {

        private final List<Function<?, ?>> executions;
        private final T parent;

        private DictionaryBuilderImpl(List<Function<?, ?>> executions, T parent) {
            this.executions = executions;
            this.parent = parent;
        }

        @Override
        public DictionaryEntryBuilder<T> putEntry() {
            withDictionaryBuilder(DictionaryBuilder::putEntry);
            return new DictionaryEntryBuilderImpl<>(executions, this);
        }

        @Override
        public T endDictionary() {
            withDictionaryBuilder(DictionaryBuilder::endDictionary);
            return parent;
        }

        private void withDictionaryBuilder(Function<DictionaryBuilder, ?> consumer) {
            executions.add(consumer);
        }
    }

    private static class DictionaryEntryBuilderImpl<T>
            extends TransformationBuilderImpl<DictionaryEntryBuilder<T>>
            implements DictionaryEntryBuilder<T> {

        private final DictionaryBuilder<T> parent;

        private DictionaryEntryBuilderImpl(List<Function<?, ?>> executions, DictionaryBuilder<T> parent) {
            super(executions);
            this.parent = parent;
        }

        @Override
        public DictionaryBuilder<T> endEntry() {
            withDictionaryEntryBuilder(DictionaryEntryBuilder::endEntry);
            return parent;
        }

        @Override
        public SequenceBuilder<DictionaryEntryBuilder<T>> putSequence() {
            withValueBuilder(ValueBuilder::putSequence);
            return new SequenceBuilderImpl<>(executions, this);
        }

        @Override
        public SequenceBuilder<DictionaryEntryBuilder<T>> putSequence(long elements) {
            withValueBuilder(vb -> vb.putSequence(elements));
            return new SequenceBuilderImpl<>(executions, this);
        }

        @Override
        public DictionaryBuilder<DictionaryEntryBuilder<T>> putDictionary() {
            withValueBuilder(ValueBuilder::putDictionary);
            return (DictionaryBuilder<DictionaryEntryBuilder<T>>) this;
        }

        @Override
        public DictionaryBuilder<DictionaryEntryBuilder<T>> putDictionary(long elements) {
            withValueBuilder(vb -> vb.putDictionary(elements));
            return (DictionaryBuilder<DictionaryEntryBuilder<T>>) this;
        }
    }

    private static class IndefiniteStringBuilderImpl<T>
            implements IndefiniteStringBuilder<T> {

        private final List<Function<?, ?>> executions;
        private final T parent;

        private IndefiniteStringBuilderImpl(List<Function<?, ?>> executions, T parent) {
            this.executions = executions;
            this.parent = parent;
        }

        @Override
        public IndefiniteStringBuilder putString(String value) {
            withIndefiniteStringBuilder(isb -> isb.putString(value));
            return this;
        }

        @Override
        public T endIndefiniteString() {
            withIndefiniteStringBuilder(IndefiniteStringBuilder::endIndefiniteString);
            return parent;
        }

        private void withIndefiniteStringBuilder(Function<IndefiniteStringBuilder, ?> consumer) {
            executions.add(consumer);
        }
    }

    private static class TransformationImpl
            implements Transformation {

        private final Function[] functions;

        private TransformationImpl(List<Function<?, ?>> functions) {
            this.functions = functions.toArray(new Function[0]);
        }

        @Override
        public <T> void transform(Value value, T builder) {
            Object currentBuilder = builder;
            for (Function function : functions) {
                currentBuilder = function.apply(currentBuilder);
            }
        }
    }
}
