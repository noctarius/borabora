/*
 * Copyright (c) 2016-2018, Christoph Engelbert (aka noctarius) and
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
package com.noctarius.borabora.spi;

import com.noctarius.borabora.Dictionary;
import com.noctarius.borabora.Input;
import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.Sequence;
import com.noctarius.borabora.ValueType;
import com.noctarius.borabora.ValueTypes;
import com.noctarius.borabora.WrongTypeException;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * The <tt>ObjectValue</tt> class implements a {@link com.noctarius.borabora.Value} type which
 * is backed by a value object. In contrast to a {@link StreamValue} based implementation,
 * instances of this type cannot be used for stream based extractions, therefore {@link #raw()}
 * throws a {@link WrongTypeException}.
 */
public class ObjectValue
        extends AbstractValue {

    private final MajorType majorType;
    private final ValueType valueType;
    private final Supplier<?> supplier;

    /**
     * Creates a new <tt>ObjectValue</tt> instance based on the given <tt>majorType</tt>,
     * <tt>valueType</tt> and <tt>value</tt>.
     *
     * @param majorType the MajorType of the value
     * @param valueType the ValueType of the value
     * @param value     the object value
     */
    public ObjectValue(MajorType majorType, ValueType valueType, Object value) {
        Objects.requireNonNull(majorType, "majorType must not be null");
        Objects.requireNonNull(valueType, "valueType must not be null");
        this.majorType = majorType;
        this.valueType = valueType;
        this.supplier = () -> value;
    }

    @Override
    protected <T> T extract(Validator validator, Supplier<T> supplier) {
        validator.validate();
        return supplier.get();
    }

    @Override
    public MajorType majorType() {
        return majorType;
    }

    @Override
    public ValueType valueType() {
        return valueType;
    }

    @Override
    public <V> V tag() {
        return extract(() -> matchMajorType(majorType, MajorType.SemanticTag), (Supplier<V>) supplier);
    }

    @Override
    public Number number() {
        return extract(() -> matchValueType(valueType, ValueTypes.Number), (Supplier<Number>) supplier);
    }

    @Override
    public Sequence sequence() {
        return extract(() -> matchValueType(valueType, ValueTypes.Sequence), (Supplier<Sequence>) supplier);
    }

    @Override
    public Dictionary dictionary() {
        return extract(() -> matchValueType(valueType, ValueTypes.Dictionary), (Supplier<Dictionary>) supplier);
    }

    @Override
    public String string() {
        return extract(() -> matchValueType(valueType, ValueTypes.String), (Supplier<String>) supplier);
    }

    @Override
    public Boolean bool() {
        return extract(() -> matchValueType(valueType, ValueTypes.Bool), (Supplier<Boolean>) supplier);
    }

    @Override
    public byte[] bytes() {
        return extract(() -> matchMajorType(majorType, MajorType.ByteString), (Supplier<byte[]>) supplier);
    }

    @Override
    public byte[] raw() {
        throw new WrongTypeException(offset(), "Current valueType is not a legal raw value");
    }

    @Override
    public <V> V byValueType() {
        return (V) supplier.get();
    }

    @Override
    public long offset() {
        return -1;
    }

    @Override
    public Input input() {
        return null;
    }

    @Override
    public String toString() {
        return "ObjectValue{" + "valueType=" + valueType + ", value=" + byValueType() + '}';
    }

}
