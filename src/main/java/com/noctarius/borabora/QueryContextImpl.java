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

import com.noctarius.borabora.spi.Decoder;
import com.noctarius.borabora.spi.QueryConsumer;
import com.noctarius.borabora.spi.QueryContext;
import com.noctarius.borabora.spi.SelectStatementStrategy;
import com.noctarius.borabora.spi.TagDecoder;
import com.noctarius.borabora.spi.pipeline.QueryPipeline;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

final class QueryContextImpl
        implements QueryContext {

    // Queries are inherently thread-safe!
    private final Deque<Object> stack = new LinkedList<>();
    private final List<TagDecoder> tagDecoders;
    private final QueryConsumer queryConsumer;
    private final SelectStatementStrategy selectStatementStrategy;
    private final Input input;

    private long offset;

    QueryContextImpl(Input input, QueryPipeline queryPipeline, QueryConsumer queryConsumer, //
                     List<TagDecoder> tagDecoders, SelectStatementStrategy selectStatementStrategy) {

        this.input = input;
        this.queryConsumer = queryConsumer;
        this.tagDecoders = tagDecoders;
        this.selectStatementStrategy = selectStatementStrategy;
    }

    QueryContextImpl(Input input, QueryPipeline queryPipeline,//
                     QueryConsumer queryConsumer, QueryContextImpl queryContext) {

        this.input = input;
        this.queryConsumer = queryConsumer;
        this.tagDecoders = queryContext.tagDecoders;
        this.selectStatementStrategy = queryContext.selectStatementStrategy;
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
    public SelectStatementStrategy selectStatementStrategy() {
        return selectStatementStrategy;
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
    public <T> T applyDecoder(long offset, MajorType majorType) {
        TagDecoder<T> processor = findProcessor(offset);
        if (processor == null) {
            return null;
        }
        long length = Decoder.length(input, majorType, offset);
        return processor.process(offset, length, this);
    }

    @Override
    public <T> void queryStackPush(T element) {
        stack.addFirst(element);
    }

    @Override
    public <T> T queryStackPop() {
        return (T) stack.removeFirst();
    }

    @Override
    public <T> T queryStackPeek() {
        return (T) stack.peekFirst();
    }

    private <V> TagDecoder<V> findProcessor(long offset) {
        for (int i = 0; i < tagDecoders.size(); i++) {
            TagDecoder<V> tagDecoder = tagDecoders.get(i);
            if (tagDecoder.handles(input, offset)) {
                return tagDecoder;
            }
        }
        return null;
    }

}
