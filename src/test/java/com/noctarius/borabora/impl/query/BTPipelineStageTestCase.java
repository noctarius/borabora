package com.noctarius.borabora.impl.query;

import com.noctarius.borabora.AbstractTestCase;
import com.noctarius.borabora.impl.query.stages.ConsumeSelectedQueryStage;
import com.noctarius.borabora.impl.query.stages.ConsumerQueryStage;
import com.noctarius.borabora.spi.pipeline.PipelineStage;
import com.noctarius.borabora.spi.pipeline.QueryBuilderNode;
import com.noctarius.borabora.spi.pipeline.QueryStage;
import com.noctarius.borabora.spi.pipeline.VisitResult;
import org.junit.Test;

import static com.noctarius.borabora.spi.pipeline.PipelineStage.NIL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
/*
 * Copyright (c) 2008-2016, Hazelcast, Inc. All Rights Reserved.
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

public class BTPipelineStageTestCase
        extends AbstractTestCase {

    @Test
    public void test_btree_visit_stage_null() {
        PipelineStage pipelineStage = new BTreePipelineStage(NIL, NIL, null);
        VisitResult visitResult = pipelineStage.visit(NIL, newQueryContext());
        assertEquals(VisitResult.Continue, visitResult);
    }

    @Test
    public void test_btree_visit_stage_loop() {
        int[] executed = new int[1];
        QueryStage queryStage = (previousPipelineStage, pipelineStage, pipelineContext) -> {
            executed[0]++;
            return executed[0] == 2 ? VisitResult.Continue : VisitResult.Loop;
        };
        PipelineStage pipelineStage = new BTreePipelineStage(NIL, NIL, queryStage);
        VisitResult visitResult = pipelineStage.visit(NIL, newQueryContext());
        assertEquals(VisitResult.Continue, visitResult);
        assertEquals(2, executed[0]);
    }

    @Test
    public void test_btree_visit_stage_exit() {
        QueryStage queryStage = (previousPipelineStage, pipelineStage, pipelineContext) -> VisitResult.Exit;
        PipelineStage pipelineStage = new BTreePipelineStage(NIL, NIL, queryStage);
        VisitResult visitResult = pipelineStage.visit(NIL, newQueryContext());
        assertEquals(VisitResult.Exit, visitResult);
    }

    @Test
    public void test_btree_visitchildren_left_nil() {
        QueryStage queryStage = (previousPipelineStage, pipelineStage, pipelineContext) -> VisitResult.Exit;
        PipelineStage pipelineStage = new BTreePipelineStage(NIL, NIL, queryStage);
        VisitResult visitResult = pipelineStage.visitChildren(newQueryContext());
        assertEquals(VisitResult.Continue, visitResult);
    }

    @Test
    public void test_btree_equals() {
        BTreePipelineStage expected = new BTreePipelineStage(NIL, NIL, QueryBuilderNode.QUERY_BASE);
        BTreePipelineStage actual = new BTreePipelineStage(NIL, NIL, QueryBuilderNode.QUERY_BASE);
        assertEquals(expected, actual);

        assertFalse(BTreePipelineStage.treeEquals(null, expected));
        assertFalse(BTreePipelineStage.treeEquals(expected, null));

        expected = new BTreePipelineStage(NIL, NIL, ConsumerQueryStage.INSTANCE);
        actual = new BTreePipelineStage(NIL, NIL, ConsumeSelectedQueryStage.INSTANCE);
        assertFalse(BTreePipelineStage.treeEquals(expected, actual));

        expected = new BTreePipelineStage(NIL, NIL, null);
        actual = new BTreePipelineStage(NIL, NIL, ConsumerQueryStage.INSTANCE);
        assertFalse(BTreePipelineStage.treeEquals(expected, actual));

        expected = new BTreePipelineStage(NIL, NIL, null);
        actual = new BTreePipelineStage(NIL, NIL, ConsumerQueryStage.INSTANCE);
        assertFalse(BTreePipelineStage.treeEquals(expected, actual));

        PipelineStage pipelineStage = new BTreePipelineStage(NIL, NIL, QueryBuilderNode.QUERY_BASE);

        expected = new BTreePipelineStage(NIL, NIL, QueryBuilderNode.QUERY_BASE);
        actual = new BTreePipelineStage(pipelineStage, NIL, QueryBuilderNode.QUERY_BASE);
        assertFalse(BTreePipelineStage.treeEquals(expected, actual));

        expected = new BTreePipelineStage(NIL, NIL, QueryBuilderNode.QUERY_BASE);
        actual = new BTreePipelineStage(NIL, pipelineStage, QueryBuilderNode.QUERY_BASE);
        assertFalse(BTreePipelineStage.treeEquals(expected, actual));
    }

    @Test
    public void test_btree_equals_false() {
        BTreePipelineStage expected = new BTreePipelineStage(NIL, NIL, QueryBuilderNode.QUERY_BASE);
        assertFalse(expected.equals(new Object()));
    }

    @Test
    public void test_btree_equals_same() {
        BTreePipelineStage expected = new BTreePipelineStage(NIL, NIL, QueryBuilderNode.QUERY_BASE);
        assertTrue(expected.equals(expected));
    }

    @Test
    public void test_btree_hashcode() {
        BTreePipelineStage expected = new BTreePipelineStage(NIL, NIL, QueryBuilderNode.QUERY_BASE);
        BTreePipelineStage actual = new BTreePipelineStage(NIL, NIL, QueryBuilderNode.QUERY_BASE);
        assertEquals(expected.hashCode(), actual.hashCode());
    }

    @Test
    public void test_btree_hashcode_left_null() {
        BTreePipelineStage expected = new BTreePipelineStage(null, NIL, QueryBuilderNode.QUERY_BASE);
        BTreePipelineStage actual = new BTreePipelineStage(null, NIL, QueryBuilderNode.QUERY_BASE);
        assertEquals(expected.hashCode(), actual.hashCode());
    }

    @Test
    public void test_btree_hashcode_right_null() {
        BTreePipelineStage expected = new BTreePipelineStage(NIL, null, QueryBuilderNode.QUERY_BASE);
        BTreePipelineStage actual = new BTreePipelineStage(NIL, null, QueryBuilderNode.QUERY_BASE);
        assertEquals(expected.hashCode(), actual.hashCode());
    }

    @Test
    public void test_btree_hashcode_stage_null() {
        BTreePipelineStage expected = new BTreePipelineStage(NIL, NIL, null);
        BTreePipelineStage actual = new BTreePipelineStage(NIL, NIL, null);
        assertEquals(expected.hashCode(), actual.hashCode());
    }

    @Test
    public void test_printTree() {
        String expected = " /----- <null>\n" //
                + " |       \\----- QUERY_BASE\n" //
                + "<null>\n" //
                + " |       /----- QUERY_BASE\n" //
                + " \\----- <null>\n";

        BTreePipelineStage five = new BTreePipelineStage(NIL, NIL, QueryBuilderNode.QUERY_BASE);
        BTreePipelineStage four = new BTreePipelineStage(NIL, NIL, QueryBuilderNode.QUERY_BASE);
        BTreePipelineStage three = new BTreePipelineStage(four, NIL, null);
        BTreePipelineStage two = new BTreePipelineStage(NIL, five, null);
        BTreePipelineStage one = new BTreePipelineStage(two, three, null);

        String actual = PipelineStagePrinter.printTree(one);
        assertEquals(expected, actual);
    }

}