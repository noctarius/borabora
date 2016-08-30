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

import com.noctarius.borabora.Input;
import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.ValueType;
import com.noctarius.borabora.ValueTypes;
import com.noctarius.borabora.spi.codec.Decoder;
import com.noctarius.borabora.spi.codec.TagDecoder;
import com.noctarius.borabora.spi.codec.TagStrategy;
import com.noctarius.borabora.spi.query.ProjectionStrategy;
import com.noctarius.borabora.spi.query.QueryConsumer;
import com.noctarius.borabora.spi.query.QueryContext;
import com.noctarius.borabora.spi.query.QueryContextFactory;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

final class QueryContextImpl
        implements QueryContext {

    private final List<TagStrategy> tagStrategies;
    private final QueryConsumer queryConsumer;
    private final QueryContextFactory queryContextFactory;
    private final ProjectionStrategy projectionStrategy;
    private final Input input;

    // Queries are inherently thread-safe!
    private Deque<Object> stack;
    private long offset;

    QueryContextImpl(Input input, QueryConsumer queryConsumer, List<TagStrategy> tagStrategies,
                     ProjectionStrategy projectionStrategy, QueryContextFactory queryContextFactory) {

        this.input = input;
        this.queryConsumer = queryConsumer;
        this.tagStrategies = tagStrategies;
        this.projectionStrategy = projectionStrategy;
        this.queryContextFactory = queryContextFactory;
    }

    @Override
    public Input input() {
        return input;
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
    public ValueType valueType(long offset) {
        short head = Decoder.readUInt8(input, offset);
        if (MajorType.SemanticTag == MajorType.findMajorType(head)) {
            for (TagStrategy tagStrategy : tagStrategies) {
                ValueType valueType = tagStrategy.valueType(input, offset);
                if (valueType != ValueTypes.Unknown) {
                    return valueType;
                }
            }
            return ValueTypes.Unknown;
        }
        return ValueTypes.valueType(input, offset);
    }

    @Override
    public List<TagStrategy> tagStrategies() {
        return tagStrategies;
    }

    @Override
    public ProjectionStrategy projectionStrategy() {
        return projectionStrategy;
    }

    @Override
    public QueryContextFactory queryContextFactory() {
        return queryContextFactory;
    }

    @Override
    public void consume(long offset) {
        queryConsumer.accept(offset, this);
    }

    @Override
    public void consume(Value value) {
        queryConsumer.consume(value);
    }

    @Override
    public <T> T applyDecoder(long offset, MajorType majorType, ValueType valueType) {
        TagDecoder<T> processor = findProcessor(offset);
        if (processor == null) {
            return null;
        }
        long length = Decoder.length(input, majorType, offset);
        return processor.process(valueType, offset, length, this);
    }

    @Override
    public <T> void queryStackPush(T element) {
        getStack().addFirst(element);
    }

    @Override
    public <T> T queryStackPop() {
        return (T) getStack().removeFirst();
    }

    @Override
    public <T> T queryStackPeek() {
        return (T) getStack().peekFirst();
    }

    private <S, V> TagStrategy<S, V> findProcessor(long offset) {
        for (int i = 0; i < tagStrategies.size(); i++) {
            TagStrategy tagStrategy = tagStrategies.get(i);
            if (tagStrategy.handles(input, offset)) {
                return (TagStrategy<S, V>) tagStrategy;
            }
        }
        return null;
    }

    // Queries are inherently thread-safe!
    private Deque<Object> getStack() {
        if (stack == null) {
            stack = new LinkedList<>();
        }
        return stack;
    }

}
