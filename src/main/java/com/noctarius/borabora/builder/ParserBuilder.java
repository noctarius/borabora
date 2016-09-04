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
import com.noctarius.borabora.spi.query.optimizer.QueryOptimizer;
import com.noctarius.borabora.spi.query.optimizer.QueryOptimizerStrategyFactory;
import com.noctarius.borabora.spi.query.pipeline.PipelineStageFactory;
import com.noctarius.borabora.spi.query.pipeline.QueryPipelineFactory;

/**
 * The <tt>ParserBuilder</tt> class is used to configure a new {@link Parser} instance. Configuration
 * options include {@link TagStrategy}s, {@link QueryOptimizer}s, available {@link ProjectionStrategy}s
 * and other factories to create the query pipeline.
 */
public interface ParserBuilder {

    /**
     * Adds the given {@link TagStrategy} instance to the new {@link Parser} configuration.
     *
     * @param tagStrategy the TagStrategy to add to the configuration
     * @return this builder instance
     * @throws NullPointerException if tagStrategy is null
     */
    ParserBuilder addTagStrategy(TagStrategy tagStrategy);

    /**
     * Adds the given {@link TagStrategy} instances to the new {@link Parser} configuration.
     *
     * @param tagStrategy1 the first TagStrategy to add to the configuration
     * @param tagStrategy2 the second TagStrategy to add to the configuration
     * @return this builder instance
     * @throws NullPointerException if either tagStrategy instance is null
     */
    ParserBuilder addTagStrategies(TagStrategy tagStrategy1, TagStrategy tagStrategy2);

    /**
     * Adds the given {@link TagStrategy} instances to the new {@link Parser} configuration.
     *
     * @param tagStrategy1  the first TagStrategy to add to the configuration
     * @param tagStrategy2  the second TagStrategy to add to the configuration
     * @param tagStrategies the TagStrategy array to add to the configuration
     * @return this builder instance
     * @throws NullPointerException if any tagStrategies instance is null
     */
    ParserBuilder addTagStrategies(TagStrategy tagStrategy1, TagStrategy tagStrategy2, TagStrategy... tagStrategies);

    /**
     * Adds the given {@link TagStrategy} instances to the new {@link Parser} configuration.
     *
     * @param tagStrategies the TagStrategy array to add to the configuration
     * @return this builder instance
     * @throws NullPointerException if any tagStrategies instance is null
     */
    ParserBuilder addTagStrategies(Iterable<TagStrategy> tagStrategies);

    /**
     * Adds the given {@link QueryOptimizer} instance to the new {@link Parser} configuration.
     *
     * @param queryOptimizer the QueryOptimizer to add to the configuration
     * @return this builder instance
     * @throws NullPointerException if queryOptimizer is null
     */
    ParserBuilder addQueryOptimizer(QueryOptimizer queryOptimizer);

    /**
     * Adds the given {@link QueryOptimizer} instances to the new {@link Parser} configuration.
     *
     * @param queryOptimizer1 the first QueryOptimizer to add to the configuration
     * @param queryOptimizer2 the second QueryOptimizer to add to the configuration
     * @return this builder instance
     * @throws NullPointerException if either queryOptimizer instance is null
     */
    ParserBuilder addQueryOptimizers(QueryOptimizer queryOptimizer1, QueryOptimizer queryOptimizer2);

    /**
     * Adds the given {@link QueryOptimizer} instances to the new {@link Parser} configuration.
     *
     * @param queryOptimizer1 the first QueryOptimizer to add to the configuration
     * @param queryOptimizer2 the second QueryOptimizer to add to the configuration
     * @param queryOptimizers the QueryOptimizer array to add to the configuration
     * @return this builder instance
     * @throws NullPointerException if any queryOptimizer instance is null
     */
    ParserBuilder addQueryOptimizers(QueryOptimizer queryOptimizer1, QueryOptimizer queryOptimizer2,
                                     QueryOptimizer... queryOptimizers);

    /**
     * Adds the given {@link QueryOptimizer} instances to the new {@link Parser} configuration.
     *
     * @param queryOptimizers the QueryOptimizer array to add to the configuration
     * @return this builder instance
     * @throws NullPointerException if any queryOptimizer instance is null
     */
    ParserBuilder addQueryOptimizers(Iterable<QueryOptimizer> queryOptimizers);

    /**
     * Configures the {@link Parser} configuration to use a binary {@link ProjectionStrategy}.
     *
     * @return this builder instance
     * @see com.noctarius.borabora.spi.query.BinaryProjectionStrategy
     */
    ParserBuilder asBinaryProjectionStrategy();

    /**
     * Configures the {@link Parser} configuration to use a object {@link ProjectionStrategy}.
     *
     * @return this builder instance
     * @see com.noctarius.borabora.spi.query.ObjectProjectionStrategy
     */
    ParserBuilder asObjectProjectionStrategy();

    /**
     * Configures the {@link Parser} configuration to use the given {@link ProjectionStrategy}
     * to use a non-standard strategy.
     *
     * @return this builder instance
     */
    ParserBuilder withProjectionStrategy(ProjectionStrategy projectionStrategy);

    /**
     * Configures the {@link Parser} configuration to use the given {@link QueryContextFactory}
     * to create a non-standard {@link com.noctarius.borabora.spi.query.QueryContext}.
     *
     * @param queryContextFactory the factory to use to create QueryContext instances
     * @return this builder instance
     */
    ParserBuilder withQueryContextFactory(QueryContextFactory queryContextFactory);

    /**
     * Configures the {@link Parser} configuration to use the given {@link QueryPipelineFactory}
     * to create a non-standard {@link com.noctarius.borabora.spi.query.pipeline.QueryPipeline}.
     *
     * @param queryPipelineFactory the factory to use to create QueryPipeline instances
     * @return this builder instance
     */
    ParserBuilder withQueryPipelineFactory(QueryPipelineFactory queryPipelineFactory);

    /**
     * Configures the {@link Parser} configuration to use the given {@link PipelineStageFactory}
     * to create non-standard {@link com.noctarius.borabora.spi.query.pipeline.PipelineStage}s.
     *
     * @param pipelineStageFactory the factory to use to create PipelineStage instances
     * @return this builder instance
     */
    ParserBuilder withPipelineStageFactory(PipelineStageFactory pipelineStageFactory);

    /**
     * Configures the {@link Parser} configuration to use the given {@link QueryOptimizerStrategyFactory}
     * to create a non-standard {@link com.noctarius.borabora.spi.query.optimizer.QueryOptimizerStrategy}
     *
     * @param queryOptimizerStrategyFactory the factory to use to create QueryOptimizerStrategy instances
     * @return this builder instance
     */
    ParserBuilder withQueryOptimizerStrategyFactory(QueryOptimizerStrategyFactory queryOptimizerStrategyFactory);

    /**
     * Returns a new {@link Parser} instance based on the internal configuration. The returned parser
     * is fully thread-safe and stateless and can be stored and shared by multiple threads.
     *
     * @return the new Parser instance
     */
    Parser build();

}
