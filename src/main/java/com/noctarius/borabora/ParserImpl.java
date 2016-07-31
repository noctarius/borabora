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

import com.noctarius.borabora.builder.QueryBuilder;
import com.noctarius.borabora.spi.Decoder;
import com.noctarius.borabora.spi.QueryContext;
import com.noctarius.borabora.spi.SelectStatementStrategy;
import com.noctarius.borabora.spi.SelectStatementStrategyAware;
import com.noctarius.borabora.spi.StreamValue;
import com.noctarius.borabora.spi.TagDecoder;

import java.util.List;
import java.util.function.Consumer;

import static com.noctarius.borabora.Value.NULL_VALUE;
import static com.noctarius.borabora.spi.Constants.EMPTY_BYTE_ARRAY;
import static com.noctarius.borabora.spi.Constants.QUERY_RETURN_CODE_FINALIZE_SELECT;
import static com.noctarius.borabora.spi.Constants.QUERY_RETURN_CODE_NULL;

final class ParserImpl
        implements Parser {

    private final List<TagDecoder> tagDecoders;
    private final SelectStatementStrategy selectStatementStrategy;

    ParserImpl(List<TagDecoder> tagDecoders, SelectStatementStrategy selectStatementStrategy) {
        this.tagDecoders = tagDecoders;
        this.selectStatementStrategy = selectStatementStrategy;
    }

    @Override
    public Value read(Input input, Query query) {
        SelectStatementStrategy selectStatementStrategy = this.selectStatementStrategy;
        if (query instanceof SelectStatementStrategyAware) {
            selectStatementStrategy = ((SelectStatementStrategyAware) query).selectStatementStrategy();
        }

        return evaluate(query, input, 0, selectStatementStrategy);
    }

    @Override
    public Value read(Input input, String query) {
        // #{'b'}(1)->?number
        // \#([0-9]+)? <- stream identifier and optional index, if no index defined then index=-1
        // \{(\'[^\}]+\')\} <- dictionary identifier and key spec
        // \(([0-9]+)\) <- sequence identifier and sequence index
        // ->(\?)?(.+){1} <- expected result type, if ? is defined and type does not match result=null, otherwise exception

        return read(input, prepareQuery(query));
    }

    @Override
    public Value read(Input input, long offset) {
        short head = Decoder.readUInt8(input, offset);
        MajorType mt = MajorType.findMajorType(head);
        ValueType vt = ValueTypes.valueType(input, offset);
        return new StreamValue(mt, vt, offset, newQueryContext(input, selectStatementStrategy));
    }

    @Override
    public void read(Input input, Query query, Consumer<Value> consumer) {
        SelectStatementStrategy selectStatementStrategy = this.selectStatementStrategy;
        if (query instanceof SelectStatementStrategyAware) {
            selectStatementStrategy = ((SelectStatementStrategyAware) query).selectStatementStrategy();
        }

        long startOffset = 0;
        do {
            Value value = evaluate(query, input, startOffset, selectStatementStrategy);
            consumer.accept(value);

            MajorType majorType = value.majorType();
            startOffset += Decoder.length(input, majorType, startOffset);
        } while (query.isStreamQueryCapable() && input.offsetValid(startOffset));
    }

    @Override
    public void read(Input input, String query, Consumer<Value> consumer) {
        read(input, prepareQuery(query), consumer);
    }

    @Override
    public void read(Input input, long offset, Consumer<Value> consumer) {
        consumer.accept(read(input, offset));
    }

    @Override
    public byte[] extract(Input input, Query query) {
        Value value = read(input, query);
        return value == null ? EMPTY_BYTE_ARRAY : value.raw();
    }

    @Override
    public byte[] extract(Input input, String query) {
        return extract(input, prepareQuery(query));
    }

    @Override
    public byte[] extract(Input input, long offset) {
        return read(input, offset).raw();
    }

    @Override
    public Query prepareQuery(String query) {
        try {

            QueryBuilder queryBuilder = Query.newBuilder(selectStatementStrategy);
            QueryParser.parse(query, queryBuilder, tagDecoders);
            return queryBuilder.build();

        } catch (Exception | TokenMgrError e) {
            throw new QueryParserException(e);
        }
    }

    private QueryContext newQueryContext(Input input, SelectStatementStrategy selectStatementStrategy) {
        return new QueryContextImpl(input, tagDecoders, selectStatementStrategy);
    }

    private Value evaluate(Query query, Input input, long startOffset, SelectStatementStrategy selectStatementStrategy) {
        QueryContext queryContext = newQueryContext(input, selectStatementStrategy);
        selectStatementStrategy.beginSelect(queryContext);

        long offset = query.access(startOffset, queryContext);
        if (offset == QUERY_RETURN_CODE_NULL) {
            return NULL_VALUE;
        } else if (offset == QUERY_RETURN_CODE_FINALIZE_SELECT) {
            return selectStatementStrategy.finalizeSelect(queryContext);
        }
        short head = Decoder.readUInt8(input, offset);
        MajorType mt = MajorType.findMajorType(head);
        ValueType vt = ValueTypes.valueType(input, offset);
        return new StreamValue(mt, vt, offset, queryContext);
    }

}
