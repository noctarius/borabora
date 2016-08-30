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
import com.noctarius.borabora.Value;
import com.noctarius.borabora.WrongTypeException;
import com.noctarius.borabora.spi.pipeline.QueryStage;
import com.noctarius.borabora.spi.pipeline.VisitResult;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;

public class SequenceIndexQueryStageTestCase
        extends AbstractQueryStageTestCase {

    @Test
    public void test_tostring() {
        QueryStage queryStage = new SequenceIndexQueryStage(0);
        assertEquals("SEQ_INDEX[ 0 ]", queryStage.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_create_sequenceindexquerystage_negative_index() {
        new SequenceIndexQueryStage(-1);
    }

    @Test(expected = WrongTypeException.class)
    public void fail_evaluate_no_sequence() {
        Input input = Input.fromByteArray(new byte[]{(byte) 0x1});
        evaluate(input, new SequenceIndexQueryStage(0));
    }

    @Test
    public void test_evaluate_first_element() {
        Input input = Input.fromByteArray(hexToBytes("0x820102"));
        QueryStage queryStage = new SequenceIndexQueryStage(0);
        EvaluationResult evaluationResult = evaluate(input, queryStage, ConsumerQueryStage.INSTANCE, null);
        assertEquals(VisitResult.Continue, evaluationResult.visitResult);
        assertEquals(1, evaluationResult.queryContext.offset());
        assertEquals(1, evaluationResult.values.size());
        assertEqualsNumber(1, evaluationResult.values.get(0).number());
    }

    @Test
    public void test_evaluate_skip_element() {
        Input input = Input.fromByteArray(hexToBytes("0x820102"));
        QueryStage queryStage = new SequenceIndexQueryStage(1);
        EvaluationResult evaluationResult = evaluate(input, queryStage, ConsumerQueryStage.INSTANCE, null);
        assertEquals(VisitResult.Continue, evaluationResult.visitResult);
        assertEquals(2, evaluationResult.queryContext.offset());
        assertEquals(1, evaluationResult.values.size());
        assertEqualsNumber(2, evaluationResult.values.get(0).number());
    }

    @Test
    public void test_evaluate_multiple_skip_element() {
        Input input = Input.fromByteArray(hexToBytes("0x83010203"));
        QueryStage queryStage = new SequenceIndexQueryStage(2);
        EvaluationResult evaluationResult = evaluate(input, queryStage, ConsumerQueryStage.INSTANCE, null);
        assertEquals(VisitResult.Continue, evaluationResult.visitResult);
        assertEquals(3, evaluationResult.queryContext.offset());
        assertEquals(1, evaluationResult.values.size());
        assertEqualsNumber(3, evaluationResult.values.get(0).number());
    }

    @Test
    public void test_evaluate_element_not_found() {
        Input input = Input.fromByteArray(hexToBytes("0x8101"));
        QueryStage queryStage = new SequenceIndexQueryStage(1);
        EvaluationResult evaluationResult = evaluate(input, queryStage, null, ConsumerQueryStage.INSTANCE);
        assertEquals(-1, evaluationResult.queryContext.offset());
        assertSame(Value.NULL_VALUE, evaluationResult.values.get(0));
    }

    @Test
    public void test_equals() {
        QueryStage qs1 = new SequenceIndexQueryStage(0);
        QueryStage qs2 = new SequenceIndexQueryStage(1);
        QueryStage qs3 = new SequenceIndexQueryStage(0);

        assertTrue(qs1.equals(qs1));
        assertFalse(qs1.equals(new Object()));
        assertFalse(qs1.equals(qs2));
        assertTrue(qs1.equals(qs3));
    }

    @Test
    public void test_hashcode() {
        QueryStage qs1 = new SequenceIndexQueryStage(0);
        QueryStage qs2 = new SequenceIndexQueryStage(1);
        QueryStage qs3 = new SequenceIndexQueryStage(0);

        assertEquals(qs1.hashCode(), qs1.hashCode());
        assertNotEquals(qs1.hashCode(), qs2.hashCode());
        assertEquals(qs1.hashCode(), qs3.hashCode());
    }

}
