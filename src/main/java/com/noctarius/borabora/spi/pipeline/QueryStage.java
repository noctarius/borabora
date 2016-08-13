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
package com.noctarius.borabora.spi.pipeline;

import com.noctarius.borabora.spi.query.QueryContext;

public interface QueryStage {

    VisitResult evaluate(PipelineStage previousPipelineStage, PipelineStage pipelineStage, QueryContext pipelineContext);

    static boolean equals(Object first, Object second) {
        if (!(first instanceof QueryStage) || !(second instanceof QueryStage)) {
            return false;
        }

        String name = first.getClass().getName();
        String otherName = second.getClass().getName();

        if (name.contains("$$Lambda$") && !otherName.contains("$$Lambda$") //
                || !name.contains("$$Lambda$") && otherName.contains("$$Lambda$")) {

            return false;
        }

        if (!name.contains("$$Lambda$") && !otherName.contains("$$Lambda$")) {
            return first.equals(second);
        }

        int nameIndex = name.indexOf("$$Lambda$");
        int otherNameIndex = otherName.indexOf("$$Lambda$");

        int nameEndIndex = name.indexOf('/', nameIndex);
        int otherNameEndIndex = name.indexOf('/', otherNameIndex);

        return name.substring(nameIndex, nameEndIndex).equals(otherName.substring(otherNameIndex, otherNameEndIndex));
    }

}
