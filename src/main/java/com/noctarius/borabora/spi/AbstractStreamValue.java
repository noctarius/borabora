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
package com.noctarius.borabora.spi;

import com.noctarius.borabora.Dictionary;
import com.noctarius.borabora.Input;
import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.Sequence;
import com.noctarius.borabora.ValueTypes;
import com.noctarius.borabora.spi.codec.Decoder;
import com.noctarius.borabora.spi.query.QueryContext;
import com.noctarius.borabora.spi.query.QueryContextAware;

import java.util.function.Supplier;

public abstract class AbstractStreamValue
        extends AbstractValue
        implements QueryContextAware {

    private final QueryContext queryContext;

    protected AbstractStreamValue(QueryContext queryContext) {
        this.queryContext = queryContext;
    }

    @Override
    public <V> V tag() {
        return extract(() -> matchMajorType(majorType(), MajorType.SemanticTag), () -> extractTag());
    }

    @Override
    public Number number() {
        return extract(() -> matchValueType(valueType(), ValueTypes.Int, ValueTypes.Float),
                () -> Decoder.readNumber(input(), valueType(), offset()));
    }

    @Override
    public Sequence sequence() {
        return extract(() -> matchValueType(valueType(), ValueTypes.Sequence), //
                () -> Decoder.readSequence(offset(), queryContext()));
    }

    @Override
    public Dictionary dictionary() {
        return extract(() -> matchValueType(valueType(), ValueTypes.Dictionary), //
                () -> Decoder.readDictionary(offset(), queryContext()));
    }

    @Override
    public String string() {
        return extract(() -> matchStringValueType(valueType()), () -> Decoder.readString(input(), offset()));
    }

    @Override
    public Boolean bool() {
        return extract(() -> matchValueType(valueType(), ValueTypes.Bool), () -> Decoder.getBooleanValue(input(), offset()));
    }

    @Override
    public byte[] raw() {
        return extract(() -> Decoder.readRaw(input(), majorType(), offset()));
    }

    @Override
    public <V> V byValueType() {
        return valueType().value(this);
    }

    @Override
    public QueryContext queryContext() {
        return queryContext;
    }

    protected Input input() {
        return queryContext().input();
    }

    protected <T> T extract(Validator validator, Supplier<T> supplier) {
        short head = Decoder.readUInt8(input(), offset());
        // Null is legal for all types
        if (Decoder.isNull(head)) {
            return null;
        }
        // Not null? Validate value type
        if (validator != null) {
            validator.validate();
        }
        // Semantic tag? Extract real value
        if (MajorType.SemanticTag == majorType()) {
            return extractTag();
        }
        return supplier.get();
    }

    protected abstract <T> T extractTag();

}
