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
package com.noctarius.borabora.impl.query.stages;

import com.noctarius.borabora.AbstractTestCase;
import com.noctarius.borabora.Input;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.impl.DefaultQueryContextFactory;
import com.noctarius.borabora.impl.query.BTreeFactories;
import com.noctarius.borabora.spi.codec.TagStrategies;
import com.noctarius.borabora.spi.codec.TagStrategy;
import com.noctarius.borabora.spi.query.BinaryProjectionStrategy;
import com.noctarius.borabora.spi.query.ProjectionStrategy;
import com.noctarius.borabora.spi.query.QueryConsumer;
import com.noctarius.borabora.spi.query.QueryContext;
import com.noctarius.borabora.spi.query.QueryContextFactory;
import com.noctarius.borabora.spi.query.pipeline.PipelineStage;
import com.noctarius.borabora.spi.query.pipeline.PipelineStageFactory;
import com.noctarius.borabora.spi.query.pipeline.QueryStage;
import com.noctarius.borabora.spi.query.pipeline.VisitResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static com.noctarius.borabora.spi.query.pipeline.PipelineStage.NIL;

public abstract class AbstractQueryStageTestCase
        extends AbstractTestCase {

    protected EvaluationResult evaluate(Input input, QueryStage currentQueryStage) {
        return evaluate(input, currentQueryStage, null, null, null, null);
    }

    protected EvaluationResult evaluate(Input input, QueryStage currentQueryStage, QueryStage leftQueryStage,
                                        QueryStage rightQueryStage) {

        return evaluate(input, currentQueryStage, null, leftQueryStage, rightQueryStage, null);
    }

    protected EvaluationResult evaluate(Input input, QueryStage currentQueryStage, QueryStage previousQueryState) {
        return evaluate(input, currentQueryStage, previousQueryState, null, null, null);
    }

    protected EvaluationResult evaluate(Input input, QueryStage currentQueryStage, QueryStage previousQueryState,
                                        QueryStage leftQueryStage, QueryStage rightQueryStage) {

        return evaluate(input, currentQueryStage, previousQueryState, leftQueryStage, rightQueryStage, null);
    }

    protected EvaluationResult evaluate(Input input, QueryStage currentQueryStage,
                                        Consumer<QueryContext> queryContextInitializer) {

        return evaluate(input, currentQueryStage, null, null, null, queryContextInitializer);
    }

    protected EvaluationResult evaluate(Input input, QueryStage currentQueryStage, QueryStage previousQueryState,
                                        QueryStage leftQueryStage, QueryStage rightQueryStage,
                                        Consumer<QueryContext> queryContextInitializer) {

        return evaluate(input, currentQueryStage, previousQueryState, leftQueryStage, rightQueryStage, //
                true, queryContextInitializer, BinaryProjectionStrategy.INSTANCE);
    }

    protected EvaluationResult evaluate(Input input, QueryStage currentQueryStage, QueryStage previousQueryState,
                                        QueryStage leftQueryStage, QueryStage rightQueryStage, boolean multiConsumer) {

        return evaluate(input, currentQueryStage, previousQueryState, leftQueryStage, rightQueryStage, //
                multiConsumer, null, BinaryProjectionStrategy.INSTANCE);
    }

    protected EvaluationResult evaluate(Input input, QueryStage currentQueryStage, QueryStage previousQueryState,
                                        QueryStage leftQueryStage, QueryStage rightQueryStage, boolean multiConsumer,
                                        Consumer<QueryContext> queryContextInitializer, ProjectionStrategy projectionStrategy) {

        List<TagStrategy> tagStrategies = new ArrayList<>(Arrays.asList(TagStrategies.values()));

        List<Value> collectedResults = new ArrayList<>();
        QueryConsumer queryConsumer = bridgeConsumer(collectedResults::add, multiConsumer);

        QueryContextFactory queryContextFactory = DefaultQueryContextFactory.INSTANCE;
        QueryContext queryContext = queryContextFactory.newQueryContext(input, queryConsumer, tagStrategies, projectionStrategy);

        if (queryContextInitializer != null) {
            queryContextInitializer.accept(queryContext);
        }

        PipelineStageFactory pipelineStageFactory = BTreeFactories.newPipelineStageFactory();
        PipelineStage left = leftQueryStage == null ? NIL : pipelineStageFactory.newPipelineStage(NIL, NIL, leftQueryStage);
        PipelineStage right = rightQueryStage == null ? NIL : pipelineStageFactory.newPipelineStage(NIL, NIL, rightQueryStage);

        PipelineStage currentPipelineStage = pipelineStageFactory.newPipelineStage(left, right, currentQueryStage);
        PipelineStage previousPipelineStage = NIL;
        if (previousQueryState != null) {
            previousPipelineStage = pipelineStageFactory.newPipelineStage(currentPipelineStage, NIL, previousQueryState);
        }

        VisitResult visitResult = currentPipelineStage.visit(previousPipelineStage, queryContext);
        return new EvaluationResult(visitResult, queryContext, collectedResults);
    }

    private QueryConsumer bridgeConsumer(Consumer<Value> consumer, boolean multiConsumer) {
        return (value) -> {
            consumer.accept(value);
            return multiConsumer;
        };
    }

    protected static class EvaluationResult {
        protected final VisitResult visitResult;
        protected final QueryContext queryContext;
        protected final List<Value> values;

        public EvaluationResult(VisitResult visitResult, QueryContext queryContext, List<Value> values) {
            this.visitResult = visitResult;
            this.queryContext = queryContext;
            this.values = values;
        }
    }

}
