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
package com.noctarius.borabora.impl.query;

import com.noctarius.borabora.spi.query.pipeline.PipelineStage;
import com.noctarius.borabora.spi.query.pipeline.PipelineStageFactory;
import com.noctarius.borabora.spi.query.pipeline.QueryPipeline;
import org.junit.Test;

import static com.noctarius.borabora.spi.query.pipeline.PipelineStage.NIL;
import static com.noctarius.borabora.spi.query.pipeline.QueryBuilderNode.QUERY_BASE;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class QueryPipelineImplTestCase {

    @Test
    public void test_tostring() {
        String expected = "QueryPipelineImpl{rootPipelineStage=BTreePipelineStage{stage=QUERY_BASE, left=NIL, right=NIL}}";
        PipelineStageFactory pipelineStageFactory = BTreeFactories.newPipelineStageFactory();
        PipelineStage ps = pipelineStageFactory.newPipelineStage(NIL, NIL, QUERY_BASE);
        QueryPipeline qp = new QueryPipelineImpl(ps);
        assertEquals(expected, qp.toString());
    }

    @Test
    public void test_printquerygraph() {
        PipelineStageFactory pipelineStageFactory = BTreeFactories.newPipelineStageFactory();
        PipelineStage ps = pipelineStageFactory.newPipelineStage(NIL, NIL, QUERY_BASE);
        QueryPipeline qp = new QueryPipelineImpl(ps);
        assertEquals("QUERY_BASE\n", qp.printQueryGraph());
    }

    @Test
    public void test_hashcode() {
        PipelineStageFactory pipelineStageFactory = BTreeFactories.newPipelineStageFactory();
        PipelineStage ps1 = pipelineStageFactory.newPipelineStage(NIL, NIL, QUERY_BASE);
        PipelineStage ps2 = pipelineStageFactory.newPipelineStage(NIL, NIL, null);
        PipelineStage ps3 = pipelineStageFactory.newPipelineStage(NIL, NIL, QUERY_BASE);

        QueryPipeline qp1 = new QueryPipelineImpl(ps1);
        QueryPipeline qp2 = new QueryPipelineImpl(ps2);
        QueryPipeline qp3 = new QueryPipelineImpl(ps3);

        assertEquals(qp1.hashCode(), qp1.hashCode());
        assertNotEquals(qp1.hashCode(), qp2.hashCode());
        assertEquals(qp1.hashCode(), qp3.hashCode());
    }

    @Test
    public void test_equals() {
        PipelineStageFactory pipelineStageFactory = BTreeFactories.newPipelineStageFactory();
        PipelineStage ps1 = pipelineStageFactory.newPipelineStage(NIL, NIL, QUERY_BASE);
        PipelineStage ps2 = pipelineStageFactory.newPipelineStage(NIL, NIL, null);
        PipelineStage ps3 = pipelineStageFactory.newPipelineStage(NIL, NIL, QUERY_BASE);

        QueryPipeline qp1 = new QueryPipelineImpl(ps1);
        QueryPipeline qp2 = new QueryPipelineImpl(ps2);
        QueryPipeline qp3 = new QueryPipelineImpl(ps3);

        assertTrue(qp1.equals(qp1));
        assertFalse(qp1.equals(new Object()));
        assertFalse(qp1.equals(qp2));
        assertTrue(qp1.equals(qp3));
    }

}
