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

import com.noctarius.borabora.spi.codec.CompositeBuffer;

final class CompositeBufferInput
        implements Input {

    private final CompositeBuffer compositeBuffer;

    CompositeBufferInput(CompositeBuffer compositeBuffer) {
        this.compositeBuffer = compositeBuffer;
    }

    @Override
    public byte read(long offset)
            throws NoSuchByteException {

        if (offset < 0 || offset >= compositeBuffer.size()) {
            throw new NoSuchByteException(offset, "Offset " + offset + " outside of available data (length: " //
                    + compositeBuffer.size() + ", identity: " + this + ")");
        }
        return compositeBuffer.read(offset);
    }

    @Override
    public long read(byte[] array, long offset, long length)
            throws NoSuchByteException {

        if (length > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("length cannot be larger than Integer.MAX_VALUE");
        }
        if (offset < 0 || length < 0 || offset >= compositeBuffer.size() || offset + length > compositeBuffer.size()) {
            throw new NoSuchByteException(offset, "Offset " + offset + " outside of available data");
        }
        if (length > array.length) {
            throw new NoSuchByteException(offset, "Length " + length + " larger than writable data");
        }

        long l = Math.min(length, compositeBuffer.size() - offset);
        compositeBuffer.read(array, offset, l);
        return l;
    }

    @Override
    public boolean offsetValid(long offset) {
        return compositeBuffer.offsetValid(offset);
    }

}
