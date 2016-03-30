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
import java.util.function.Supplier;

final class ObjectValue
        extends AbstractValue {

    private final Collection<SemanticTagProcessor> processors;
    private final Object value;

    ObjectValue(MajorType majorType, ValueType valueType, Object value, Collection<SemanticTagProcessor> processors) {
        super(majorType, valueType);
        this.value = value;
        this.processors = processors;
    }

    @Override
    public <V> V tag() {
        return null;
    }

    @Override
    public Number number() {
        return null;
    }

    @Override
    public Sequence sequence() {
        return null;
    }

    @Override
    public Dictionary dictionary() {
        return null;
    }

    @Override
    public String string() {
        return null;
    }

    @Override
    public Boolean bool() {
        return null;
    }

    @Override
    public byte[] raw() {
        throw new UnsupportedOperationException("raw is not supported on ObjectValue");
    }

    @Override
    public <V> V byValueType() {
        return (V) value;
    }

    @Override
    protected <T> T extract(Validator validator, Supplier<T> supplier) {
        return null;
    }
}
