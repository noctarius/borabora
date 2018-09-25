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

import com.noctarius.borabora.Input;
import com.noctarius.borabora.spi.query.ProjectionStrategy;
import com.noctarius.borabora.spi.query.QueryContext;
import com.noctarius.borabora.spi.query.pipeline.QueryStage;
import com.noctarius.borabora.spi.query.pipeline.VisitResult;
import org.junit.Test;

import static com.noctarius.borabora.spi.query.pipeline.PipelineStage.NIL;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AsDictionaryProjectionEntryQueryStageTestCase
        extends AbstractQueryStageTestCase {

    @Test
    public void test_toString() {
        assertEquals("DIC_ENTRY_BEGIN[ foo ]", AsDictionaryProjectionEntryQueryStage.withStringKey("foo").toString());
        assertEquals("DIC_ENTRY_BEGIN[ -1000 ]", AsDictionaryProjectionEntryQueryStage.withIntKey(-1000).toString());
        assertEquals("DIC_ENTRY_BEGIN[ -12.0 ]", AsDictionaryProjectionEntryQueryStage.withFloatKey(-12.d).toString());
    }

    @Test
    public void test_evaluate_string_key() {
        Input input = Input.fromByteArray(new byte[0]);
        QueryStage queryStage = AsDictionaryProjectionEntryQueryStage.withStringKey("foo");

        ProjectionStrategy spy = spy(ProjectionStrategy.class);

        EvaluationResult evaluationResult = evaluate(input, queryStage, null, null, null, true, null, spy);
        verify(spy, times(1)).putDictionaryKey(eq("foo"), any(QueryContext.class));
        verify(spy, times(0)).putDictionaryValue(eq(NIL), any(QueryContext.class));
        verify(spy, times(0)).putDictionaryNullValue(any(QueryContext.class));
        assertEquals(VisitResult.Continue, evaluationResult.visitResult);
    }

    @Test
    public void test_evaluate_int_key() {
        Input input = Input.fromByteArray(new byte[0]);
        QueryStage queryStage = AsDictionaryProjectionEntryQueryStage.withIntKey(-1000);

        ProjectionStrategy spy = spy(ProjectionStrategy.class);

        EvaluationResult evaluationResult = evaluate(input, queryStage, null, null, null, true, null, spy);
        verify(spy, times(1)).putDictionaryKey(eq(-1000L), any(QueryContext.class));
        verify(spy, times(0)).putDictionaryValue(eq(NIL), any(QueryContext.class));
        verify(spy, times(0)).putDictionaryNullValue(any(QueryContext.class));
        assertEquals(VisitResult.Continue, evaluationResult.visitResult);
    }

    @Test
    public void test_evaluate_float_key() {
        Input input = Input.fromByteArray(new byte[0]);
        QueryStage queryStage = AsDictionaryProjectionEntryQueryStage.withFloatKey(-12.d);

        ProjectionStrategy spy = spy(ProjectionStrategy.class);

        EvaluationResult evaluationResult = evaluate(input, queryStage, null, null, null, true, null, spy);
        verify(spy, times(1)).putDictionaryKey(eq(-12.d), any(QueryContext.class));
        verify(spy, times(0)).putDictionaryValue(eq(NIL), any(QueryContext.class));
        verify(spy, times(0)).putDictionaryNullValue(any(QueryContext.class));
        assertEquals(VisitResult.Continue, evaluationResult.visitResult);
    }

    @Test
    public void test_evaluate_exit() {
        Input input = Input.fromByteArray(new byte[0]);
        QueryStage queryStage = AsDictionaryProjectionEntryQueryStage.withIntKey(-1000);
        QueryStage exitStage = (previousPipelineStage, pipelineStage, queryContext) -> VisitResult.Exit;

        ProjectionStrategy spy = spy(ProjectionStrategy.class);

        evaluate(input, queryStage, null, exitStage, null, true, null, spy);
        verify(spy, times(0)).putDictionaryValue(eq(NIL), any(QueryContext.class));
        verify(spy, times(0)).putDictionaryNullValue(any(QueryContext.class));
    }

    @Test
    public void test_evaluate_break() {
        Input input = Input.fromByteArray(new byte[0]);
        QueryStage queryStage = AsDictionaryProjectionEntryQueryStage.withIntKey(-1000);
        QueryStage breakStage = (previousPipelineStage, pipelineStage, queryContext) -> {
            queryContext.offset(-1);
            return VisitResult.Break;
        };

        ProjectionStrategy spy = spy(ProjectionStrategy.class);

        EvaluationResult evaluationResult = evaluate(input, queryStage, null, breakStage, null, true, null, spy);
        verify(spy, times(0)).putDictionaryValue(eq(NIL), any(QueryContext.class));
        verify(spy, times(1)).putDictionaryNullValue(any(QueryContext.class));
        assertEquals(VisitResult.Continue, evaluationResult.visitResult);
    }

    @Test
    public void test_equals_string_key() {
        QueryStage qs1 = AsDictionaryProjectionEntryQueryStage.withStringKey("foo");
        QueryStage qs2 = AsDictionaryProjectionEntryQueryStage.withStringKey("bar");
        QueryStage qs3 = AsDictionaryProjectionEntryQueryStage.withStringKey("foo");

        assertTrue(qs1.equals(qs1));
        assertFalse(qs1.equals(new Object()));
        assertFalse(qs1.equals(qs2));
        assertTrue(qs1.equals(qs3));
    }

    @Test
    public void test_hashcode_string_key() {
        QueryStage qs1 = AsDictionaryProjectionEntryQueryStage.withStringKey("foo");
        QueryStage qs2 = AsDictionaryProjectionEntryQueryStage.withStringKey("bar");
        QueryStage qs3 = AsDictionaryProjectionEntryQueryStage.withStringKey("foo");

        assertEquals(qs1.hashCode(), qs1.hashCode());
        assertNotEquals(qs1.hashCode(), qs2.hashCode());
        assertEquals(qs1.hashCode(), qs3.hashCode());
    }

    @Test
    public void test_equals_int_key() {
        QueryStage qs1 = AsDictionaryProjectionEntryQueryStage.withIntKey(1000);
        QueryStage qs2 = AsDictionaryProjectionEntryQueryStage.withIntKey(-1000);
        QueryStage qs3 = AsDictionaryProjectionEntryQueryStage.withIntKey(1000);

        assertTrue(qs1.equals(qs1));
        assertFalse(qs1.equals(new Object()));
        assertFalse(qs1.equals(qs2));
        assertTrue(qs1.equals(qs3));
    }

    @Test
    public void test_hashcode_int_key() {
        QueryStage qs1 = AsDictionaryProjectionEntryQueryStage.withIntKey(1000);
        QueryStage qs2 = AsDictionaryProjectionEntryQueryStage.withIntKey(-1000);
        QueryStage qs3 = AsDictionaryProjectionEntryQueryStage.withIntKey(1000);

        assertEquals(qs1.hashCode(), qs1.hashCode());
        assertNotEquals(qs1.hashCode(), qs2.hashCode());
        assertEquals(qs1.hashCode(), qs3.hashCode());
    }

    @Test
    public void test_equals_float_key() {
        QueryStage qs1 = AsDictionaryProjectionEntryQueryStage.withFloatKey(12.d);
        QueryStage qs2 = AsDictionaryProjectionEntryQueryStage.withFloatKey(-12.d);
        QueryStage qs3 = AsDictionaryProjectionEntryQueryStage.withFloatKey(12.d);

        assertTrue(qs1.equals(qs1));
        assertFalse(qs1.equals(new Object()));
        assertFalse(qs1.equals(qs2));
        assertTrue(qs1.equals(qs3));
    }

    @Test
    public void test_hashcode_float_key() {
        QueryStage qs1 = AsDictionaryProjectionEntryQueryStage.withFloatKey(12.d);
        QueryStage qs2 = AsDictionaryProjectionEntryQueryStage.withFloatKey(-12.d);
        QueryStage qs3 = AsDictionaryProjectionEntryQueryStage.withFloatKey(12.d);

        assertEquals(qs1.hashCode(), qs1.hashCode());
        assertNotEquals(qs1.hashCode(), qs2.hashCode());
        assertEquals(qs1.hashCode(), qs3.hashCode());
    }

}
