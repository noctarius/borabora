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
package com.noctarius.borabora.builder;

import com.noctarius.borabora.Parser;
import com.noctarius.borabora.spi.SelectStatementStrategy;
import com.noctarius.borabora.spi.TagDecoder;
import com.noctarius.borabora.spi.pipeline.QueryOptimizer;

public interface ParserBuilder {

    <V> ParserBuilder addTagDecoder(TagDecoder<V> tagDecoder);

    <V> ParserBuilder addTagDecoder(TagDecoder<V> tagDecoder1, TagDecoder<V> tagDecoder2);

    <V> ParserBuilder addTagDecoder(TagDecoder<V> tagDecoder1, TagDecoder<V> tagDecoder2, TagDecoder<V>... tagDecoders);

    ParserBuilder addQueryOptimizer(QueryOptimizer queryOptimizer);

    ParserBuilder addQueryOptimizer(QueryOptimizer queryOptimizer1, QueryOptimizer queryOptimizer2);

    ParserBuilder addQueryOptimizer(QueryOptimizer queryOptimizer1, QueryOptimizer queryOptimizer2,
                                    QueryOptimizer... queryOptimizers);

    ParserBuilder asBinarySelectStatementStrategy();

    ParserBuilder asObjectSelectStatementStrategy();

    ParserBuilder withSelectStatementStrategy(SelectStatementStrategy selectStatementStrategy);

    Parser build();

}
