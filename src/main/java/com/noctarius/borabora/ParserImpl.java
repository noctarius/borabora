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

import com.noctarius.borabora.builder.GraphQueryBuilder;
import com.noctarius.borabora.spi.QueryContext;
import com.noctarius.borabora.spi.SelectStatementStrategy;
import com.noctarius.borabora.spi.SemanticTagProcessor;

import java.util.List;

import static com.noctarius.borabora.Bytes.readUInt8;
import static com.noctarius.borabora.Constants.EMPTY_BYTE_ARRAY;
import static com.noctarius.borabora.Value.NULL_VALUE;

final class ParserImpl
        implements Parser {

    private final List<SemanticTagProcessor> semanticTagProcessors;
    private final SelectStatementStrategy selectStatementStrategy;

    ParserImpl(List<SemanticTagProcessor> semanticTagProcessors, SelectStatementStrategy selectStatementStrategy) {
        this.semanticTagProcessors = semanticTagProcessors;
        this.selectStatementStrategy = selectStatementStrategy;
    }

    @Override
    public Value read(Input input, GraphQuery graphQuery) {
        QueryContext queryContext = newQueryContext(input);
        long offset = graphQuery.access(0, queryContext);
        if (offset == -1) {
            return NULL_VALUE;
        } else if (offset == -2) {
            return selectStatementStrategy.finalizeSelect(queryContext);
        }
        short head = readUInt8(input, offset);
        MajorType mt = MajorType.findMajorType(head);
        ValueType vt = ValueTypes.valueType(input, offset);
        return new StreamValue(mt, vt, offset, queryContext);
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
        short head = readUInt8(input, offset);
        MajorType mt = MajorType.findMajorType(head);
        ValueType vt = ValueTypes.valueType(input, offset);
        return new StreamValue(mt, vt, offset, newQueryContext(input));
    }

    @Override
    public byte[] extract(Input input, GraphQuery graphQuery) {
        Value value = read(input, graphQuery);
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
    public GraphQuery prepareQuery(String query) {
        try {

            GraphQueryBuilder queryBuilder = GraphQuery.newBuilder(selectStatementStrategy);
            QueryParser.parse(query, queryBuilder, semanticTagProcessors);
            return queryBuilder.build();

        } catch (Exception | TokenMgrError e) {
            throw new QueryParserException(e);
        }
    }

    private QueryContext newQueryContext(Input input) {
        return new QueryContextImpl(input, semanticTagProcessors, selectStatementStrategy);
    }

}
