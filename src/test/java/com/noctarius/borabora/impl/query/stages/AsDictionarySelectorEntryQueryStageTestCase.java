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
import org.mockito.Mockito;

import static com.noctarius.borabora.spi.pipeline.PipelineStage.NIL;
import static org.junit.Assert.assertEquals;

public class AsDictionarySelectorEntryQueryStageTestCase
        extends AbstractQueryStageTestCase {

    @Test
    public void test_toString() {
        assertEquals("DIC_ENTRY_BEGIN[ foo ]", AsDictionarySelectorEntryQueryStage.withStringKey("foo").toString());
        assertEquals("DIC_ENTRY_BEGIN[ -1000 ]", AsDictionarySelectorEntryQueryStage.withIntKey(-1000).toString());
        assertEquals("DIC_ENTRY_BEGIN[ -12.0 ]", AsDictionarySelectorEntryQueryStage.withFloatKey(-12.d).toString());
    }

    @Test
    public void test_evaluate_string_key() {
        Input input = Input.fromByteArray(new byte[0]);
        QueryStage queryStage = AsDictionarySelectorEntryQueryStage.withStringKey("foo");

        ProjectionStrategy spy = Mockito.spy(ProjectionStrategy.class);

        EvaluationResult evaluationResult = evaluate(input, queryStage, null, null, null, null, spy);
        Mockito.verify(spy, Mockito.times(1)).putDictionaryKey(Mockito.eq("foo"), Mockito.any(QueryContext.class));
        Mockito.verify(spy, Mockito.times(0)).putDictionaryValue(Mockito.eq(NIL), Mockito.any(QueryContext.class));
        Mockito.verify(spy, Mockito.times(0)).putDictionaryNullValue(Mockito.any(QueryContext.class));
        assertEquals(VisitResult.Continue, evaluationResult.visitResult);
    }

    @Test
    public void test_evaluate_int_key() {
        Input input = Input.fromByteArray(new byte[0]);
        QueryStage queryStage = AsDictionarySelectorEntryQueryStage.withIntKey(-1000);

        ProjectionStrategy spy = Mockito.spy(ProjectionStrategy.class);

        EvaluationResult evaluationResult = evaluate(input, queryStage, null, null, null, null, spy);
        Mockito.verify(spy, Mockito.times(1)).putDictionaryKey(Mockito.eq(-1000L), Mockito.any(QueryContext.class));
        Mockito.verify(spy, Mockito.times(0)).putDictionaryValue(Mockito.eq(NIL), Mockito.any(QueryContext.class));
        Mockito.verify(spy, Mockito.times(0)).putDictionaryNullValue(Mockito.any(QueryContext.class));
        assertEquals(VisitResult.Continue, evaluationResult.visitResult);
    }

    @Test
    public void test_evaluate_float_key() {
        Input input = Input.fromByteArray(new byte[0]);
        QueryStage queryStage = AsDictionarySelectorEntryQueryStage.withFloatKey(-12.d);

        ProjectionStrategy spy = Mockito.spy(ProjectionStrategy.class);

        EvaluationResult evaluationResult = evaluate(input, queryStage, null, null, null, null, spy);
        Mockito.verify(spy, Mockito.times(1)).putDictionaryKey(Mockito.eq(-12.d), Mockito.any(QueryContext.class));
        Mockito.verify(spy, Mockito.times(0)).putDictionaryValue(Mockito.eq(NIL), Mockito.any(QueryContext.class));
        Mockito.verify(spy, Mockito.times(0)).putDictionaryNullValue(Mockito.any(QueryContext.class));
        assertEquals(VisitResult.Continue, evaluationResult.visitResult);
    }

    @Test
    public void test_evaluate_exit() {
        Input input = Input.fromByteArray(new byte[0]);
        QueryStage queryStage = AsDictionarySelectorEntryQueryStage.withIntKey(-1000);
        QueryStage exitStage = (previousPipelineStage, pipelineStage, queryContext) -> VisitResult.Exit;

        ProjectionStrategy spy = Mockito.spy(ProjectionStrategy.class);

        evaluate(input, queryStage, null, exitStage, null, null, spy);
        Mockito.verify(spy, Mockito.times(0)).putDictionaryValue(Mockito.eq(NIL), Mockito.any(QueryContext.class));
        Mockito.verify(spy, Mockito.times(0)).putDictionaryNullValue(Mockito.any(QueryContext.class));
    }

    @Test
    public void test_evaluate_break() {
        Input input = Input.fromByteArray(new byte[0]);
        QueryStage queryStage = AsDictionarySelectorEntryQueryStage.withIntKey(-1000);
        QueryStage breakStage = (previousPipelineStage, pipelineStage, queryContext) -> {
            queryContext.offset(-1);
            return VisitResult.Break;
        };

        ProjectionStrategy spy = Mockito.spy(ProjectionStrategy.class);

        EvaluationResult evaluationResult = evaluate(input, queryStage, null, breakStage, null, null, spy);
        Mockito.verify(spy, Mockito.times(0)).putDictionaryValue(Mockito.eq(NIL), Mockito.any(QueryContext.class));
        Mockito.verify(spy, Mockito.times(1)).putDictionaryNullValue(Mockito.any(QueryContext.class));
        assertEquals(VisitResult.Continue, evaluationResult.visitResult);
    }

}