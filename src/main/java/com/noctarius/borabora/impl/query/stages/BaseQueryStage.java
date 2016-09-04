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
import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.spi.io.ByteSizes;
import com.noctarius.borabora.spi.io.Decoder;
import com.noctarius.borabora.spi.query.QueryContext;
import com.noctarius.borabora.spi.query.pipeline.PipelineStage;
import com.noctarius.borabora.spi.query.pipeline.QueryStage;
import com.noctarius.borabora.spi.query.pipeline.VisitResult;

import static com.noctarius.borabora.spi.io.Constants.TAG_MAGIC_CBOR_HEADER;

public class BaseQueryStage
        implements QueryStage {

    public static final QueryStage INSTANCE = new BaseQueryStage();

    private BaseQueryStage() {
    }

    @Override
    public VisitResult evaluate(PipelineStage previousPipelineStage, PipelineStage pipelineStage, QueryContext queryContext) {
        Input input = queryContext.input();
        long offset = queryContext.offset();

        // Is the first item a semantic tag?
        short head = Decoder.readUInt8(input, offset);
        MajorType majorType = MajorType.findMajorType(head);
        if (MajorType.SemanticTag == majorType) {
            Number tagType = Decoder.readUint(input, offset);
            if (tagType.intValue() == TAG_MAGIC_CBOR_HEADER) {
                // Seems like so skip the header and handle as normal CBOR encoded data
                offset += ByteSizes.headByteSize(input, offset);
                queryContext.offset(offset);
            }
        }

        return pipelineStage.visitChildren(queryContext);
    }

    @Override
    public String toString() {
        return "QUERY_BASE";
    }

}
