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
import com.noctarius.borabora.NoSuchByteException;
import com.noctarius.borabora.spi.pipeline.QueryStage;
import com.noctarius.borabora.spi.pipeline.VisitResult;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class SingleStreamElementQueryStageTestCase
        extends AbstractQueryStageTestCase {

    @Test
    public void test_tostring() {
        QueryStage queryStage = new SingleStreamElementQueryStage(0);
        assertEquals("STREAM_INDEX[ 0 ]", queryStage.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_create_singlestreamelementquerystage_negative_index() {
        new SingleStreamElementQueryStage(-1);
    }

    @Test
    public void test_evaluate_first_element() {
        Input input = Input.fromByteArray(new byte[]{(byte) 0x1, (byte) 0x2});
        QueryStage queryStage = new SingleStreamElementQueryStage(0);
        EvaluationResult evaluationResult = evaluate(input, queryStage);
        assertEquals(VisitResult.Continue, evaluationResult.visitResult);
        assertEquals(0, evaluationResult.queryContext.offset());
    }

    @Test
    public void test_evaluate_skip_element() {
        Input input = Input.fromByteArray(new byte[]{(byte) 0x1, (byte) 0x2});
        QueryStage queryStage = new SingleStreamElementQueryStage(1);
        EvaluationResult evaluationResult = evaluate(input, queryStage);
        assertEquals(VisitResult.Continue, evaluationResult.visitResult);
        assertEquals(1, evaluationResult.queryContext.offset());
    }

    @Test(expected = NoSuchByteException.class)
    public void test_evaluate_element_not_found() {
        Input input = Input.fromByteArray(new byte[]{(byte) 0x1, (byte) 0x2});
        QueryStage queryStage = new SingleStreamElementQueryStage(2);
        evaluate(input, queryStage);
    }

    @Test
    public void test_equals() {
        QueryStage qs1 = new SingleStreamElementQueryStage(0);
        QueryStage qs2 = new SingleStreamElementQueryStage(1);
        QueryStage qs3 = new SingleStreamElementQueryStage(0);

        assertTrue(qs1.equals(qs1));
        assertFalse(qs1.equals(new Object()));
        assertFalse(qs1.equals(qs2));
        assertTrue(qs1.equals(qs3));
    }

    @Test
    public void test_hashcode() {
        QueryStage qs1 = new SingleStreamElementQueryStage(0);
        QueryStage qs2 = new SingleStreamElementQueryStage(1);
        QueryStage qs3 = new SingleStreamElementQueryStage(0);

        assertEquals(qs1.hashCode(), qs1.hashCode());
        assertNotEquals(qs1.hashCode(), qs2.hashCode());
        assertEquals(qs1.hashCode(), qs3.hashCode());
    }

}
