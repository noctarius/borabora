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
package com.noctarius.borabora.impl.query;

import com.noctarius.borabora.AbstractTestCase;
import com.noctarius.borabora.spi.query.pipeline.PipelineStage;
import com.noctarius.borabora.spi.query.pipeline.PipelineStageFactory;
import org.junit.Test;

import static com.noctarius.borabora.spi.query.pipeline.PipelineStage.NIL;
import static com.noctarius.borabora.spi.query.pipeline.QueryStage.QUERY_BASE;
import static org.junit.Assert.assertEquals;

public class PipelineStagePrinterTestCase
        extends AbstractTestCase {

    @Test
    public void call_constructor() {
        callConstructor(PipelineStagePrinter.class);
    }

    @Test
    public void test_printtree_single_node() {
        PipelineStageFactory pipelineStageFactory = BTreeFactories.newPipelineStageFactory();
        PipelineStage pipelineStage = pipelineStageFactory.newPipelineStage(NIL, NIL, QUERY_BASE);
        String tree = PipelineStagePrinter.printTree(pipelineStage);
        assertEquals("QUERY_BASE\n", tree);
    }

    @Test
    public void test_printtree_single_node_null() {
        PipelineStageFactory pipelineStageFactory = BTreeFactories.newPipelineStageFactory();
        PipelineStage pipelineStage = pipelineStageFactory.newPipelineStage(NIL, NIL, null);
        String tree = PipelineStagePrinter.printTree(pipelineStage);
        assertEquals("<null>\n", tree);
    }

    @Test
    public void test_printtree_left() {
        PipelineStageFactory pipelineStageFactory = BTreeFactories.newPipelineStageFactory();
        PipelineStage left = pipelineStageFactory.newPipelineStage(NIL, NIL, QUERY_BASE);
        PipelineStage pipelineStage = pipelineStageFactory.newPipelineStage(left, NIL, QUERY_BASE);
        String tree = PipelineStagePrinter.printTree(pipelineStage);
        assertEquals("QUERY_BASE\n \\----- QUERY_BASE\n", tree);
    }

    @Test
    public void test_printtree_left_null() {
        PipelineStageFactory pipelineStageFactory = BTreeFactories.newPipelineStageFactory();
        PipelineStage left = pipelineStageFactory.newPipelineStage(NIL, NIL, null);
        PipelineStage pipelineStage = pipelineStageFactory.newPipelineStage(left, NIL, QUERY_BASE);
        String tree = PipelineStagePrinter.printTree(pipelineStage);
        assertEquals("QUERY_BASE\n \\----- <null>\n", tree);
    }

    @Test
    public void test_printtree_right() {
        PipelineStageFactory pipelineStageFactory = BTreeFactories.newPipelineStageFactory();
        PipelineStage right = pipelineStageFactory.newPipelineStage(NIL, NIL, QUERY_BASE);
        PipelineStage pipelineStage = pipelineStageFactory.newPipelineStage(NIL, right, QUERY_BASE);
        String tree = PipelineStagePrinter.printTree(pipelineStage);
        assertEquals(" /----- QUERY_BASE\nQUERY_BASE\n", tree);
    }

    @Test
    public void test_printtree_right_null() {
        PipelineStageFactory pipelineStageFactory = BTreeFactories.newPipelineStageFactory();
        PipelineStage right = pipelineStageFactory.newPipelineStage(NIL, NIL, null);
        PipelineStage pipelineStage = pipelineStageFactory.newPipelineStage(NIL, right, QUERY_BASE);
        String tree = PipelineStagePrinter.printTree(pipelineStage);
        assertEquals(" /----- <null>\nQUERY_BASE\n", tree);
    }

    @Test
    public void test_printtree_left_left() {
        PipelineStageFactory pipelineStageFactory = BTreeFactories.newPipelineStageFactory();
        PipelineStage left_left = pipelineStageFactory.newPipelineStage(NIL, NIL, QUERY_BASE);
        PipelineStage left = pipelineStageFactory.newPipelineStage(left_left, NIL, QUERY_BASE);
        PipelineStage pipelineStage = pipelineStageFactory.newPipelineStage(left, NIL, QUERY_BASE);
        String tree = PipelineStagePrinter.printTree(pipelineStage);
        assertEquals("QUERY_BASE\n \\----- QUERY_BASE\n         \\----- QUERY_BASE\n", tree);
    }

    @Test
    public void test_printtree_right_right() {
        PipelineStageFactory pipelineStageFactory = BTreeFactories.newPipelineStageFactory();
        PipelineStage right_right = pipelineStageFactory.newPipelineStage(NIL, NIL, QUERY_BASE);
        PipelineStage right = pipelineStageFactory.newPipelineStage(NIL, right_right, QUERY_BASE);
        PipelineStage pipelineStage = pipelineStageFactory.newPipelineStage(NIL, right, QUERY_BASE);
        String tree = PipelineStagePrinter.printTree(pipelineStage);
        assertEquals("         /----- QUERY_BASE\n /----- QUERY_BASE\nQUERY_BASE\n", tree);
    }

}
