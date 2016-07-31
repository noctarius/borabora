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
package com.noctarius.borabora;

import com.noctarius.borabora.spi.ByteSizes;
import com.noctarius.borabora.spi.Decoder;
import com.noctarius.borabora.spi.ElementCounts;
import com.noctarius.borabora.spi.QueryContext;

import static com.noctarius.borabora.spi.Constants.QUERY_RETURN_CODE_NULL;

final class SequenceQuery
        implements Query {

    private final long sequenceIndex;

    SequenceQuery(long sequenceIndex) {
        if (sequenceIndex < 0) {
            throw new IllegalArgumentException("sequenceIndex must be greater or equal to 0");
        }
        this.sequenceIndex = sequenceIndex;
    }

    @Override
    public long access(long offset, QueryContext queryContext) {
        Input input = queryContext.input();
        short head = Decoder.readUInt8(input, offset);
        MajorType majorType = MajorType.findMajorType(head);

        // Stream direct access (return actual object itself)
        if (sequenceIndex == QUERY_RETURN_CODE_NULL) {
            return offset;
        }

        if (MajorType.Sequence != majorType) {
            throw new WrongTypeException("Not a sequence");
        }

        // Sequences need head skipped
        long elementCount = ElementCounts.elementCountByMajorType(majorType, input, offset);
        if (elementCount < sequenceIndex) {
            return QUERY_RETURN_CODE_NULL;
        }

        // Element access
        long headByteSize = ByteSizes.headByteSize(input, offset);
        offset += headByteSize;

        // Stream objects
        return skip(input, offset);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SequenceQuery)) {
            return false;
        }

        SequenceQuery that = (SequenceQuery) o;
        return sequenceIndex == that.sequenceIndex;
    }

    @Override
    public int hashCode() {
        return (int) (sequenceIndex ^ (sequenceIndex >>> 32));
    }

    @Override
    public String toString() {
        return "SequenceQuery{" + "sequenceIndex=" + sequenceIndex + '}';
    }

    private long skip(Input input, long offset) {
        // Skip unnecessary objects
        for (int i = 0; i < sequenceIndex; i++) {
            offset = Decoder.skip(input, offset);
        }
        return offset;
    }

}
