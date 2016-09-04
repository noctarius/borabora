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

import com.noctarius.borabora.spi.io.CompositeBuffer;

import java.util.Objects;

final class CompositeBufferInput
        implements Input {

    private final CompositeBuffer compositeBuffer;

    CompositeBufferInput(CompositeBuffer compositeBuffer) {
        Objects.requireNonNull(compositeBuffer, "compositeBuffer must not be null");
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
    public long read(byte[] bytes, long offset, int length)
            throws NoSuchByteException {

        Objects.requireNonNull(bytes, "bytes must not be null");
        if (offset < 0 || length < 0 || offset >= compositeBuffer.size() || offset + length > compositeBuffer.size()) {
            throw new NoSuchByteException(offset, "Offset " + offset + " outside of available data");
        }
        if (length > bytes.length) {
            throw new NoSuchByteException(offset, "Length " + length + " larger than writable data");
        }

        int l = (int) Math.min(length, compositeBuffer.size() - offset);
        compositeBuffer.read(bytes, offset, l);
        return l;
    }

    @Override
    public boolean offsetValid(long offset) {
        return compositeBuffer.offsetValid(offset);
    }

}
