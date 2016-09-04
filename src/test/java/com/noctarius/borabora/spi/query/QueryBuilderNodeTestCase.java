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
package com.noctarius.borabora.spi.query;

import com.noctarius.borabora.impl.query.BTreeFactories;
import com.noctarius.borabora.spi.query.pipeline.PipelineStage;
import com.noctarius.borabora.spi.query.pipeline.PipelineStageFactory;
import com.noctarius.borabora.spi.query.pipeline.QueryBuilderNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class QueryBuilderNodeTestCase {

    @Test
    public void test_tostring() {
        String expected = "QueryBuilderNode{stage=QUERY_BASE, children=[QueryBuilderNode{stage=QUERY_BASE, children=[]}]}";
        QueryBuilderNode parent = new QueryBuilderNode(QueryBuilderNode.QUERY_BASE);
        parent.pushChild(QueryBuilderNode.QUERY_BASE);
        assertEquals(expected, parent.toString());
    }

    @Test
    public void test_empty_pipeline() {
        String expected = "BTreePipelineStage{stage=QUERY_BASE, left=NIL, right=NIL}";
        QueryBuilderNode root = new QueryBuilderNode(QueryBuilderNode.QUERY_BASE);
        PipelineStageFactory pipelineStageFactory = BTreeFactories.newPipelineStageFactory();
        PipelineStage stage = QueryBuilderNode.build(root, pipelineStageFactory);
        assertEquals(expected, stage.toString());
    }

}
