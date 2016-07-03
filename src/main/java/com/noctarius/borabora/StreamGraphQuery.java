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

import com.noctarius.borabora.spi.Decoder;
import com.noctarius.borabora.spi.QueryContext;

final class StreamGraphQuery
        implements GraphQuery {

    private final long streamIndex;

    StreamGraphQuery(long streamIndex) {
        this.streamIndex = streamIndex;
    }

    @Override
    public long access(long offset, QueryContext queryContext) {
        // Stream direct access (return actual object itself)
        if (streamIndex <= 0) {
            return offset;
        }

        // Stream objects
        return skip(queryContext.input(), offset);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StreamGraphQuery)) {
            return false;
        }

        StreamGraphQuery that = (StreamGraphQuery) o;
        return streamIndex == that.streamIndex;
    }

    @Override
    public int hashCode() {
        return (int) (streamIndex ^ (streamIndex >>> 32));
    }

    @Override
    public String toString() {
        return "StreamGraphQuery{" + "streamIndex=" + streamIndex + '}';
    }

    private long skip(Input input, long offset) {
        // Skip unnecessary objects
        for (int i = 0; i < streamIndex; i++) {
            offset = Decoder.skip(input, offset);
        }
        return offset;
    }

}
