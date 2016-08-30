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
import com.noctarius.borabora.spi.pipeline.QueryStage;
import com.noctarius.borabora.spi.pipeline.VisitResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class MultiStreamElementQueryStageTestCase
        extends AbstractQueryStageTestCase {

    @Test
    public void test_tostring() {
        assertEquals("ANY_STREAM_INDEX", MultiStreamElementQueryStage.INSTANCE.toString());
    }

    @Test
    public void test_evaluate_exit() {
        Input input = Input.fromByteArray(new byte[]{(byte) 01});

        QueryStage queryStage = MultiStreamElementQueryStage.INSTANCE;
        QueryStage exitStage = (previousPipelineStage, pipelineStage, queryContext) -> VisitResult.Exit;

        EvaluationResult evaluationResult = evaluate(input, queryStage, null, exitStage, null);
        assertEquals(VisitResult.Exit, evaluationResult.visitResult);
    }

    @Test
    public void test_evaluate_break() {
        Input input = Input.fromByteArray(new byte[]{(byte) 01});

        QueryStage queryStage = MultiStreamElementQueryStage.INSTANCE;
        QueryStage breakStage = (previousPipelineStage, pipelineStage, queryContext) -> {
            queryContext.offset(-1);
            return VisitResult.Break;
        };

        EvaluationResult evaluationResult = evaluate(input, queryStage, null, breakStage, null);
        assertEquals(VisitResult.Continue, evaluationResult.visitResult);
        assertEquals(1, evaluationResult.values.size());
        assertSame(Value.NULL_VALUE, evaluationResult.values.get(0));
    }

    @Test
    public void test_evaluate_multi_elements() {
        Input input = Input.fromByteArray(hexToBytes("0x0102"));

        QueryStage queryStage = MultiStreamElementQueryStage.INSTANCE;

        EvaluationResult evaluationResult = evaluate(input, queryStage, ConsumerQueryStage.INSTANCE, null);
        assertEquals(VisitResult.Continue, evaluationResult.visitResult);
        assertEquals(2, evaluationResult.values.size());
        assertEqualsNumber(1, evaluationResult.values.get(0).number());
        assertEqualsNumber(2, evaluationResult.values.get(1).number());
    }

}
