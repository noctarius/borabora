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

import com.noctarius.borabora.Query;
import com.noctarius.borabora.spi.pipeline.PipelineStage;
import com.noctarius.borabora.spi.pipeline.PipelineStageFactory;
import com.noctarius.borabora.spi.pipeline.QueryPipeline;
import com.noctarius.borabora.spi.pipeline.QueryStage;
import com.noctarius.borabora.spi.query.BinaryProjectionStrategy;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static com.noctarius.borabora.spi.pipeline.PipelineStage.NIL;
import static com.noctarius.borabora.spi.pipeline.QueryBuilderNode.QUERY_BASE;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class QueryImplTestCase {

    @Test
    public void test_equals() {
        Query q1 = query(NIL, NIL, QUERY_BASE);
        Query q2 = query(NIL, NIL, null);
        Query q3 = query(NIL, NIL, QUERY_BASE);

        assertTrue(q1.equals(q1));
        assertFalse(q1.equals(new Object()));
        assertFalse(q1.equals(q2));
        assertTrue(q1.equals(q3));
    }

    @Test
    public void test_hashcode() {
        Query q1 = query(NIL, NIL, QUERY_BASE);
        Query q2 = query(NIL, NIL, null);
        Query q3 = query(NIL, NIL, QUERY_BASE);

        assertEquals(q1.hashCode(), q1.hashCode());
        assertNotEquals(q1.hashCode(), q2.hashCode());
        assertEquals(q1.hashCode(), q3.hashCode());
    }

    @Test
    public void test_tostring() {
        String expected = "Query{queryPipeline=QueryPipelineImpl{rootPipelineStage="
                + "BTreePipelineStage{stage=QUERY_BASE, left=NIL, right=NIL}}}";
        Query query = query(NIL, NIL, QUERY_BASE);
        assertEquals(expected, query.toString());
    }

    @Test
    public void test_printquerygraph()
            throws Exception {

        PrintStream out = System.out;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PrintStream ps = new PrintStream(baos);
            Query query = query(NIL, NIL, QUERY_BASE);
            System.setOut(ps);
            query.printQueryGraph();

        } finally {
            System.setOut(out);
        }

        String content = baos.toString("ASCII");
        assertEquals("QUERY_BASE\n\n", content);
    }

    private Query query(PipelineStage left, PipelineStage right, QueryStage stage) {
        PipelineStageFactory pipelineStageFactory = BTreeFactories.newPipelineStageFactory();
        PipelineStage pipelineStage = pipelineStageFactory.newPipelineStage(left, right, stage);
        QueryPipeline queryPipeline = new QueryPipelineImpl(pipelineStage);
        return new QueryImpl(queryPipeline, BinaryProjectionStrategy.INSTANCE);
    }

}
