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

final class ParserImpl implements Parser {

    private final Input input;
    private final Collection<SemanticTagProcessor> processors;

    ParserImpl(Input input, Collection<SemanticTagProcessor> processors) {
        this.input = input;
        this.processors = processors;
    }

    @Override
    public Value read(Graph graph) {
        Decoder source = new Decoder(input, 0);
        Decoder stream = graph.access(source);
        short head = stream.transientUint8();
        MajorType mt = MajorType.findMajorType(head);
        ValueType vt = ValueTypes.valueType(stream, 0);
        long index = stream.position();
        long length = stream.length(mt, 0);
        return new Value(mt, vt, stream, index, length, processors);
    }

    @Override
    public Value read(String query) {
        //TODO Parse query into Graph nodes
        return null;
    }
}
