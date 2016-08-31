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
import com.noctarius.borabora.spi.pipeline.QueryStage;
import com.noctarius.borabora.spi.pipeline.VisitResult;
import com.noctarius.borabora.spi.query.ProjectionStrategy;
import com.noctarius.borabora.spi.query.QueryContext;
import org.junit.Test;

import static com.noctarius.borabora.spi.pipeline.PipelineStage.NIL;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AsSequenceSelectorEntryQueryStageTestCase
        extends AbstractQueryStageTestCase {

    @Test
    public void test_toString() {
        assertEquals("SEQ_ENTRY_BEGIN", AsSequenceSelectorEntryQueryStage.INSTANCE.toString());
    }

    @Test
    public void test_evaluate() {
        Input input = Input.fromByteArray(new byte[0]);
        QueryStage queryStage = AsSequenceSelectorEntryQueryStage.INSTANCE;

        ProjectionStrategy spy = spy(ProjectionStrategy.class);

        EvaluationResult evaluationResult = evaluate(input, queryStage, null, null, null, null, spy);
        verify(spy, times(0)).putSequenceValue(eq(NIL), any(QueryContext.class));
        verify(spy, times(0)).putSequenceNullValue(any(QueryContext.class));
        assertEquals(VisitResult.Continue, evaluationResult.visitResult);
    }

    @Test
    public void test_evaluate_exit() {
        Input input = Input.fromByteArray(new byte[0]);
        QueryStage queryStage = AsSequenceSelectorEntryQueryStage.INSTANCE;
        QueryStage exitStage = (previousPipelineStage, pipelineStage, queryContext) -> VisitResult.Exit;

        ProjectionStrategy spy = spy(ProjectionStrategy.class);

        evaluate(input, queryStage, null, exitStage, null, null, spy);
        verify(spy, times(0)).putSequenceValue(eq(NIL), any(QueryContext.class));
        verify(spy, times(0)).putSequenceNullValue(any(QueryContext.class));
    }

    @Test
    public void test_evaluate_break() {
        Input input = Input.fromByteArray(new byte[0]);
        QueryStage queryStage = AsSequenceSelectorEntryQueryStage.INSTANCE;
        QueryStage breakStage = (previousPipelineStage, pipelineStage, queryContext) -> {
            queryContext.offset(-1);
            return VisitResult.Break;
        };

        ProjectionStrategy spy = spy(ProjectionStrategy.class);

        EvaluationResult evaluationResult = evaluate(input, queryStage, null, breakStage, null, null, spy);
        verify(spy, times(0)).putSequenceValue(eq(NIL), any(QueryContext.class));
        verify(spy, times(1)).putSequenceNullValue(any(QueryContext.class));
        assertEquals(VisitResult.Continue, evaluationResult.visitResult);
    }

}
