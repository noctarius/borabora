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

import com.noctarius.borabora.spi.Dictionary;
import com.noctarius.borabora.spi.Sequence;

import java.util.function.Supplier;

class ObjectValue
        extends AbstractValue {

    private final MajorType majorType;
    private final ValueType valueType;
    private final Supplier<?> supplier;

    ObjectValue(MajorType majorType, ValueType valueType, Object value) {
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
    public byte[] raw() {
        throw new WrongTypeException("Current valueType is not a legal raw value");
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
    public String toString() {
        return "ObjectValue{" +
                "valueType=" + valueType +
                ", value=" + byValueType() +
                '}';
    }

}
