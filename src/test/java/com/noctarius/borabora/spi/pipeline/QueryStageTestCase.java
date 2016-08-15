package com.noctarius.borabora.spi.pipeline;

import com.noctarius.borabora.spi.query.QueryContext;
import org.junit.Test;

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

public class QueryStageTestCase {

    private static final QueryStage QUERY_STAGE = new QueryStageTestImpl();

    @Test
    public void test_equals_lambda_nonlambda() {
        QueryStage lambda = (previousPipelineStage, pipelineStage, pipelineContext) -> null;

        assertFalse(QueryStage.equals(lambda, QUERY_STAGE));
        assertFalse(QueryStage.equals(QUERY_STAGE, lambda));
        assertTrue(QueryStage.equals(QUERY_STAGE, QUERY_STAGE));
        assertTrue(QueryStage.equals(lambda, lambda));

        QueryStage lambda2 = (previousPipelineStage, pipelineStage, pipelineContext) -> null;
        assertFalse(QueryStage.equals(lambda, lambda2));
    }

    private static class QueryStageTestImpl
            implements QueryStage {

        @Override
        public VisitResult evaluate(PipelineStage previousPipelineStage, PipelineStage pipelineStage,
                                    QueryContext pipelineContext) {
            return null;
        }
    }

}