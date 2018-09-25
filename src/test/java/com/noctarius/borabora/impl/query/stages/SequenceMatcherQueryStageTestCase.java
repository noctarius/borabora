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
import com.noctarius.borabora.Value;
import com.noctarius.borabora.WrongTypeException;
import com.noctarius.borabora.spi.query.pipeline.QueryStage;
import com.noctarius.borabora.spi.query.pipeline.VisitResult;
import org.junit.Test;

import java.util.function.Predicate;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class SequenceMatcherQueryStageTestCase
        extends AbstractQueryStageTestCase {

    @Test
    public void test_tostring() {
        QueryStage queryStage = new SequenceMatcherQueryStage(VALUE_PREDICATE);
        assertEquals("SEQ_MATCH[ VALUE_PREDICATE ]", queryStage.toString());
    }

    @Test(expected = NullPointerException.class)
    public void fail_create_sequencematcherquerystage() {
        new SequenceMatcherQueryStage(null);
    }

    @Test(expected = WrongTypeException.class)
    public void fail_evaluate_no_sequence() {
        Input input = Input.fromByteArray(new byte[]{(byte) 0x1});
        evaluate(input, new SequenceMatcherQueryStage(VALUE_PREDICATE));
    }

    @Test
    public void test_find_first_element() {
        Input input = Input.fromByteArray(hexToBytes("0x820102"));
        QueryStage queryStage = new SequenceMatcherQueryStage(v -> v.number().intValue() == 1);
        EvaluationResult evaluationResult = evaluate(input, queryStage, ConsumerQueryStage.INSTANCE, null);
        assertEquals(VisitResult.Continue, evaluationResult.visitResult);
        assertEquals(1, evaluationResult.values.size());
        assertEqualsNumber(1, evaluationResult.values.get(0).number());
    }

    @Test
    public void test_find_second_element() {
        Input input = Input.fromByteArray(hexToBytes("0x820102"));
        QueryStage queryStage = new SequenceMatcherQueryStage(v -> v.number().intValue() == 2);
        EvaluationResult evaluationResult = evaluate(input, queryStage, ConsumerQueryStage.INSTANCE, null);
        assertEquals(VisitResult.Continue, evaluationResult.visitResult);
        assertEquals(1, evaluationResult.values.size());
        assertEqualsNumber(2, evaluationResult.values.get(0).number());
    }

    @Test
    public void test_find_any_element() {
        Input input = Input.fromByteArray(hexToBytes("0x820102"));
        QueryStage queryStage = new SequenceMatcherQueryStage(v -> true);
        EvaluationResult evaluationResult = evaluate(input, queryStage, ConsumerQueryStage.INSTANCE, null);
        assertEquals(VisitResult.Continue, evaluationResult.visitResult);
        assertEquals(2, evaluationResult.values.size());
        assertEqualsNumber(1, evaluationResult.values.get(0).number());
        assertEqualsNumber(2, evaluationResult.values.get(1).number());
    }

    @Test
    public void test_break_from_query() {
        Input input = Input.fromByteArray(hexToBytes("0x8101"));
        QueryStage queryStage = new SequenceMatcherQueryStage(v -> true);
        QueryStage breakStage = (previousPipelineStage, pipelineStage, queryContext) -> VisitResult.Break;
        EvaluationResult evaluationResult = evaluate(input, queryStage, breakStage, null);
        assertEquals(VisitResult.Break, evaluationResult.visitResult);
        assertEquals(0, evaluationResult.values.size());
    }

    @Test
    public void test_exit_from_query() {
        Input input = Input.fromByteArray(hexToBytes("0x8101"));
        QueryStage queryStage = new SequenceMatcherQueryStage(v -> true);
        QueryStage exitStage = (previousPipelineStage, pipelineStage, queryContext) -> VisitResult.Exit;
        EvaluationResult evaluationResult = evaluate(input, queryStage, exitStage, null);
        assertEquals(VisitResult.Exit, evaluationResult.visitResult);
        assertEquals(0, evaluationResult.values.size());
    }

    @Test
    public void test_equals() {
        QueryStage qs1 = new SequenceMatcherQueryStage(VALUE_PREDICATE);
        QueryStage qs2 = new SequenceMatcherQueryStage(v -> false);
        QueryStage qs3 = new SequenceMatcherQueryStage(VALUE_PREDICATE);

        assertTrue(qs1.equals(qs1));
        assertFalse(qs1.equals(new Object()));
        assertFalse(qs1.equals(qs2));
        assertTrue(qs1.equals(qs3));
    }

    @Test
    public void test_hashcode() {
        QueryStage qs1 = new SequenceMatcherQueryStage(VALUE_PREDICATE);
        QueryStage qs2 = new SequenceMatcherQueryStage(v -> false);
        QueryStage qs3 = new SequenceMatcherQueryStage(VALUE_PREDICATE);

        assertEquals(qs1.hashCode(), qs1.hashCode());
        assertNotEquals(qs1.hashCode(), qs2.hashCode());
        assertEquals(qs1.hashCode(), qs3.hashCode());
    }

    private static final Predicate<Value> VALUE_PREDICATE = new Predicate<Value>() {
        @Override
        public boolean test(Value value) {
            return false;
        }

        @Override
        public String toString() {
            return "VALUE_PREDICATE";
        }
    };

}
