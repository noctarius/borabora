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
package com.noctarius.borabora.impl.query.stages;

import com.noctarius.borabora.Input;
import com.noctarius.borabora.spi.query.ProjectionStrategy;
import com.noctarius.borabora.spi.query.QueryContext;
import com.noctarius.borabora.spi.query.pipeline.QueryStage;
import com.noctarius.borabora.spi.query.pipeline.VisitResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AsSequenceProjectionQueryStageTestCase
        extends AbstractQueryStageTestCase {

    @Test
    public void test_toString() {
        assertEquals("AS_SEQ", AsSequenceProjectionQueryStage.INSTANCE.toString());
    }

    @Test
    public void test_evaluate() {
        Input input = Input.fromByteArray(new byte[0]);
        QueryStage queryStage = AsSequenceProjectionQueryStage.INSTANCE;

        ProjectionStrategy spy = spy(ProjectionStrategy.class);

        evaluate(input, queryStage, null, null, null, true, null, spy);
        verify(spy, times(1)).beginSequence(any(QueryContext.class));
        verify(spy, times(1)).endSequence(any(QueryContext.class));
    }

    @Test
    public void test_evaluate_exit() {
        Input input = Input.fromByteArray(new byte[0]);
        QueryStage queryStage = AsSequenceProjectionQueryStage.INSTANCE;
        QueryStage exitStage = (previousPipelineStage, pipelineStage, queryContext) -> VisitResult.Exit;

        ProjectionStrategy spy = spy(ProjectionStrategy.class);

        evaluate(input, queryStage, null, exitStage, null, true, null, spy);
        verify(spy, times(1)).beginSequence(any(QueryContext.class));
        verify(spy, times(0)).endSequence(any(QueryContext.class));
    }

    @Test
    public void test_evaluate_break() {
        Input input = Input.fromByteArray(new byte[0]);
        QueryStage queryStage = AsSequenceProjectionQueryStage.INSTANCE;
        QueryStage breakStage = (previousPipelineStage, pipelineStage, queryContext) -> VisitResult.Break;

        ProjectionStrategy spy = spy(ProjectionStrategy.class);

        EvaluationResult evaluationResult = evaluate(input, queryStage, null, breakStage, null, true, null, spy);
        verify(spy, times(1)).beginSequence(any(QueryContext.class));
        verify(spy, times(1)).endSequence(any(QueryContext.class));
        assertEquals(VisitResult.Continue, evaluationResult.visitResult);
    }

}
