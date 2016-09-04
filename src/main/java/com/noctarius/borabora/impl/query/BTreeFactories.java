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
package com.noctarius.borabora.impl.query;

import com.noctarius.borabora.spi.query.optimizer.QueryOptimizer;
import com.noctarius.borabora.spi.query.optimizer.QueryOptimizerStrategy;
import com.noctarius.borabora.spi.query.optimizer.QueryOptimizerStrategyFactory;
import com.noctarius.borabora.spi.query.pipeline.PipelineStage;
import com.noctarius.borabora.spi.query.pipeline.PipelineStageFactory;
import com.noctarius.borabora.spi.query.pipeline.QueryBuilderNode;
import com.noctarius.borabora.spi.query.pipeline.QueryPipelineFactory;

import java.util.List;

public final class BTreeFactories {

    private BTreeFactories() {
    }

    public static QueryPipelineFactory newQueryPipelineFactory() {
        return (queryRootNode, pipelineStageFactory, queryOptimizerStrategy) -> {
            // Build LCRS query plan
            PipelineStage rootPipelineStage = QueryBuilderNode.build(queryRootNode, pipelineStageFactory);

            // Apply query optimizers
            rootPipelineStage = queryOptimizerStrategy.optimizeQuery(rootPipelineStage, pipelineStageFactory);

            // Build query pipeline
            return new QueryPipelineImpl(rootPipelineStage);
        };
    }

    public static PipelineStageFactory newPipelineStageFactory() {
        return BTreePipelineStage::new;
    }

    public static QueryOptimizerStrategyFactory newQueryOptimizerStrategyFactory() {
        return BTreeFactories::newQueryOptimizerStrategy;
    }

    private static QueryOptimizerStrategy newQueryOptimizerStrategy(List<QueryOptimizer> queryOptimizers) {
        return (rootPipelineStage, pipelineStageFactory) -> {
            PipelineStage optimizedRootStage = rootPipelineStage;
            for (QueryOptimizer queryOptimizer : queryOptimizers) {
                if (queryOptimizer.handles(optimizedRootStage)) {
                    optimizedRootStage = queryOptimizer.optimize(optimizedRootStage, pipelineStageFactory);
                }
            }
            return optimizedRootStage;
        };
    }

}
