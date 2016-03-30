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

import java.util.Collection;

import static com.noctarius.borabora.Value.NULL_VALUE;

final class ParserImpl
        implements Parser {

    private final Input input;
    private final Collection<SemanticTagProcessor> processors;

    ParserImpl(Input input, Collection<SemanticTagProcessor> processors) {
        this.input = input;
        this.processors = processors;
    }

    @Override
    public Value read(GraphQuery graphQuery) {
        Decoder source = new Decoder(input);
        long offset = graphQuery.access(source, 0, processors);
        if (offset == -1) {
            return NULL_VALUE;
        }
        short head = source.transientUint8(offset);
        MajorType mt = MajorType.findMajorType(head);
        ValueType vt = ValueTypes.valueType(source, offset);
        long length = source.length(mt, offset);
        return new StreamValue(mt, vt, source, offset, length, processors);
    }

    @Override
    public Value read(String query) {
        // #{'b'}(1)->?number
        // \#([0-9]+)? <- stream identifier and optional index, if no index defined then index=-1
        // \{(\'[^\}]+\')\} <- dictionary identifier and key spec
        // \(([0-9]+)\) <- sequence identifier and sequence index
        // ->(\?)?(.+){1} <- expected result type, if ? is defined and type does not match result=null, otherwise exception

        return read(prepareQuery(query));
    }

    @Override
    public GraphQuery prepareQuery(String query) {
        try {

            GraphQueryBuilder queryBuilder = GraphQuery.newBuilder();
            QueryParser.parse(query, queryBuilder, processors);
            return queryBuilder.build();

        } catch (Exception e) {
            throw new QueryParserException(e);
        }
    }

}
