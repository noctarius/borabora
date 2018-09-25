/*
 * Copyright (c) 2016-2018, Christoph Engelbert (aka noctarius) and
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

import com.noctarius.borabora.AbstractTestCase;
import com.noctarius.borabora.spi.query.optimizer.QueryOptimizer;
import com.noctarius.borabora.spi.query.optimizer.QueryOptimizerStrategy;
import com.noctarius.borabora.spi.query.optimizer.QueryOptimizerStrategyFactory;
import com.noctarius.borabora.spi.query.pipeline.PipelineStage;
import com.noctarius.borabora.spi.query.pipeline.PipelineStageFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.noctarius.borabora.spi.query.pipeline.PipelineStage.NIL;
import static org.junit.Assert.assertSame;

public class BTreeFactoriesTestCase
        extends AbstractTestCase {

    @Test
    public void call_constructor() {
        callConstructor(BTreeFactories.class);
    }

    @Test
    public void test_query_optimizer_strategy_factory_apply_optimizers() {
        PipelineStage expected = new BTreePipelineStage(NIL, NIL, null);
        List<QueryOptimizer> queryOptimizers = new ArrayList<>();

        // First one doesn't "match"
        queryOptimizers.add(new QueryOptimizer() {
            @Override
            public boolean handles(PipelineStage rooPipelineStage) {
                return false;
            }

            @Override
            public PipelineStage optimize(PipelineStage rootPipelineStage, PipelineStageFactory pipelineStageFactory) {
                return null;
            }
        });

        // Second one matches
        queryOptimizers.add(new QueryOptimizer() {
            @Override
            public boolean handles(PipelineStage rooPipelineStage) {
                return true;
            }

            @Override
            public PipelineStage optimize(PipelineStage rootPipelineStage, PipelineStageFactory pipelineStageFactory) {
                return expected;
            }
        });

        QueryOptimizerStrategyFactory optimizerStrategyFactory = BTreeFactories.newQueryOptimizerStrategyFactory();
        QueryOptimizerStrategy queryOptimizerStrategy = optimizerStrategyFactory.newQueryOptimizerStrategy(queryOptimizers);

        PipelineStage pipelineStage = new BTreePipelineStage(NIL, NIL, null);
        PipelineStage actual = queryOptimizerStrategy.optimizeQuery(pipelineStage, BTreeFactories.newPipelineStageFactory());
        assertSame(expected, actual);
    }

}
