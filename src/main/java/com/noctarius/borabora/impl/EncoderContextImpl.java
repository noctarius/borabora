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

import com.noctarius.borabora.Output;
import com.noctarius.borabora.WrongTypeException;
import com.noctarius.borabora.spi.builder.EncoderContext;
import com.noctarius.borabora.spi.codec.TagStrategy;

import java.util.Map;
import java.util.Objects;

public class EncoderContextImpl
        implements EncoderContext {

    private final Map<Class<?>, TagStrategy> tagStrategies;
    private final Output output;

    private long offset;

    public EncoderContextImpl(Output output, Map<Class<?>, TagStrategy> tagStrategies) {
        Objects.requireNonNull(output, "output must not be null");
        Objects.requireNonNull(tagStrategies, "tagStrategies must not be null");
        this.output = output;
        this.tagStrategies = tagStrategies;
    }

    @Override
    public Output output() {
        return output;
    }

    @Override
    public long offset() {
        return offset;
    }

    @Override
    public void offset(long offset) {
        this.offset = offset;
    }

    @Override
    public long applyEncoder(Object value, long offset) {
        Objects.requireNonNull(value, "value must not be null");
        for (TagStrategy tagStrategy : tagStrategies.values()) {
            if (tagStrategy.handles(value)) {
                return tagStrategy.process(value, offset, this);
            }
        }
        throw new WrongTypeException(offset, "Found non-encodeable type: " + value.getClass().getName());
    }

    @Override
    public <S> TagStrategy findTagStrategy(Class<S> type) {
        Objects.requireNonNull(type, "type must not be null");
        return tagStrategies.get(type);
    }

}
