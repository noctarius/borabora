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

/**
 * The <tt>NoSuchByteException</tt> signals the state of reaching an unexpected
 * end of the stream or the read destination (in case of a byte-array).
 */
public class NoSuchByteException
        extends RuntimeException {

    private final long offset;

    /**
     * Returns a new exception instance representing the current <tt>offset</tt>
     * inside the stream, as well as the a more detailed exception message.
     *
     * @param offset  the offset of the stream where the exception occurred
     * @param message the detailed exception message
     */
    public NoSuchByteException(long offset, String message) {
        super(message);
        this.offset = offset;
    }

    /**
     * Returns the offset inside the stream when the exception happened.
     *
     * @return the offset inside the stream
     */
    public long getOffset() {
        return offset;
    }

}
