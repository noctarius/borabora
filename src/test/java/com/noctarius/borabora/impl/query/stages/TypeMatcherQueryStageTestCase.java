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
import com.noctarius.borabora.WrongTypeException;
import com.noctarius.borabora.spi.query.TypeSpecs;
import com.noctarius.borabora.spi.query.pipeline.QueryStage;
import com.noctarius.borabora.spi.query.pipeline.VisitResult;
import org.junit.Test;

import static com.noctarius.borabora.spi.io.Constants.OFFSET_CODE_NULL;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class TypeMatcherQueryStageTestCase
        extends AbstractQueryStageTestCase {

    @Test(expected = NullPointerException.class)
    public void fail_create_typematcherquerystage_with_null_typespec() {
        new TypeMatcherQueryStage(null, false);
    }

    @Test(expected = WrongTypeException.class)
    public void fail_evaluate_wrongtype_type_required() {
        Input input = Input.fromByteArray(new byte[]{(byte) 0x1});
        QueryStage queryStage = new TypeMatcherQueryStage(TypeSpecs.NInt, true);
        evaluate(input, queryStage);
    }

    @Test
    public void test_evaluate_wrongtype_type_optional_expected_null() {
        Input input = Input.fromByteArray(new byte[]{(byte) 0x1});
        QueryStage queryStage = new TypeMatcherQueryStage(TypeSpecs.NInt, false);
        EvaluationResult evaluationResult = evaluate(input, queryStage);
        assertEquals(VisitResult.Break, evaluationResult.visitResult);
        assertEquals(OFFSET_CODE_NULL, evaluationResult.queryContext.offset());
    }

    @Test
    public void test_evaluate_matching_type() {
        Input input = Input.fromByteArray(new byte[]{(byte) 0x1});
        QueryStage queryStage = new TypeMatcherQueryStage(TypeSpecs.UInt, false);
        EvaluationResult evaluationResult = evaluate(input, queryStage);
        assertEquals(VisitResult.Continue, evaluationResult.visitResult);
        assertEquals(0, evaluationResult.queryContext.offset());
    }

    @Test
    public void test_tostring() {
        QueryStage qs1 = new TypeMatcherQueryStage(TypeSpecs.Int, false);
        QueryStage qs2 = new TypeMatcherQueryStage(TypeSpecs.Int, true);
        assertEquals("TYPE_MATCH[ type=Int, optional=true ]", qs1.toString());
        assertEquals("TYPE_MATCH[ type=Int, optional=false ]", qs2.toString());
    }

    @Test
    public void test_equals() {
        QueryStage qs1 = new TypeMatcherQueryStage(TypeSpecs.Int, false);
        QueryStage qs2 = new TypeMatcherQueryStage(TypeSpecs.Int, true);
        QueryStage qs3 = new TypeMatcherQueryStage(TypeSpecs.Int, false);
        QueryStage qs4 = new TypeMatcherQueryStage(TypeSpecs.Int, true);
        QueryStage qs5 = new TypeMatcherQueryStage(TypeSpecs.UInt, false);

        assertTrue(qs1.equals(qs1));
        assertFalse(qs1.equals(new Object()));
        assertFalse(qs1.equals(qs2));
        assertTrue(qs1.equals(qs3));
        assertTrue(qs2.equals(qs4));
        assertFalse(qs2.equals(qs1));
        assertFalse(qs1.equals(qs5));
    }

    @Test
    public void test_hashcode() {
        QueryStage qs1 = new TypeMatcherQueryStage(TypeSpecs.Int, false);
        QueryStage qs2 = new TypeMatcherQueryStage(TypeSpecs.Int, true);
        QueryStage qs3 = new TypeMatcherQueryStage(TypeSpecs.Int, false);

        assertNotEquals(qs1.hashCode(), qs2.hashCode());
        assertEquals(qs1.hashCode(), qs1.hashCode());
        assertEquals(qs1.hashCode(), qs3.hashCode());
    }

}
