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
import com.noctarius.borabora.ValueTypes;
import com.noctarius.borabora.spi.pipeline.VisitResult;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ConsumerQueryStageTestCase
        extends AbstractQueryStageTestCase {

    @Test
    public void test_toString() {
        assertEquals("CONSUME", ConsumerQueryStage.INSTANCE.toString());
    }

    @Test
    public void test_evaluate() {
        Input input = Input.fromByteArray(new byte[]{(byte) 0x1});
        EvaluationResult evaluationResult = evaluate(input, ConsumerQueryStage.INSTANCE);

        assertEquals(VisitResult.Continue, evaluationResult.visitResult);

        List<Value> values = evaluationResult.values;
        assertEquals(1, values.size());
        Value value = values.get(0);
        assertEquals(ValueTypes.UInt, value.valueType());
        assertEqualsNumber(1, value.number());
    }

}
