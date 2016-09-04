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
package com.noctarius.borabora.spi.query.pipeline;

import com.noctarius.borabora.AbstractTestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class PipelineStageTestCase
        extends AbstractTestCase {

    @Test
    public void test_nil_visit() {
        VisitResult visitResult = PipelineStage.NIL.visit(PipelineStage.NIL, newQueryContext());
        assertEquals(VisitResult.Continue, visitResult);
    }

    @Test
    public void test_nil_visitchildren() {
        VisitResult visitResult = PipelineStage.NIL.visitChildren(newQueryContext());
        assertEquals(VisitResult.Continue, visitResult);
    }

    @Test
    public void test_nil_stage() {
        assertNull(PipelineStage.NIL.stage());
    }

    @Test
    public void test_nil_left() {
        assertSame(PipelineStage.NIL, PipelineStage.NIL.left());
    }

    @Test
    public void test_nil_right() {
        assertSame(PipelineStage.NIL, PipelineStage.NIL.right());
    }

    @Test
    public void test_nil_tostring() {
        assertEquals("NIL", PipelineStage.NIL.toString());
    }

}
