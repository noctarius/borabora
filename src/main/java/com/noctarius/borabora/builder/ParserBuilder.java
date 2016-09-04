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
import com.noctarius.borabora.spi.codec.TagStrategy;
import com.noctarius.borabora.spi.query.ProjectionStrategy;
import com.noctarius.borabora.spi.query.QueryContextFactory;
import com.noctarius.borabora.spi.query.pipeline.PipelineStageFactory;
import com.noctarius.borabora.spi.query.optimizer.QueryOptimizer;
import com.noctarius.borabora.spi.query.optimizer.QueryOptimizerStrategyFactory;
import com.noctarius.borabora.spi.query.pipeline.QueryPipelineFactory;

public interface ParserBuilder {

    <S, V> ParserBuilder addTagStrategy(TagStrategy<S, V> tagStrategy);

    ParserBuilder addTagStrategies(TagStrategy tagStrategy1, TagStrategy tagStrategy2);

    ParserBuilder addTagStrategies(TagStrategy tagStrategy1, TagStrategy tagStrategy2, TagStrategy... tagStrategies);

    ParserBuilder addTagStrategies(Iterable<TagStrategy> tagStrategies);

    ParserBuilder addQueryOptimizer(QueryOptimizer queryOptimizer);

    ParserBuilder addQueryOptimizers(QueryOptimizer queryOptimizer1, QueryOptimizer queryOptimizer2);

    ParserBuilder addQueryOptimizers(QueryOptimizer queryOptimizer1, QueryOptimizer queryOptimizer2,
                                     QueryOptimizer... queryOptimizers);

    ParserBuilder addQueryOptimizers(Iterable<QueryOptimizer> queryOptimizers);

    ParserBuilder asBinaryProjectionStrategy();

    ParserBuilder asObjectProjectionStrategy();

    ParserBuilder withProjectionStrategy(ProjectionStrategy projectionStrategy);

    ParserBuilder withQueryContextFactory(QueryContextFactory queryContextFactory);

    ParserBuilder withQueryPipelineFactory(QueryPipelineFactory queryPipelineFactory);

    ParserBuilder withPipelineStageFactory(PipelineStageFactory pipelineStageFactory);

    ParserBuilder withQueryOptimizerStrategyFactory(QueryOptimizerStrategyFactory queryOptimizerStrategyFactory);

    Parser build();

}
