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
package com.noctarius.borabora;

import sun.misc.Unsafe;

import java.util.Objects;

final class UnsafeByteInput
        implements Input {

    private static final Unsafe UNSAFE = UnsafeUtils.getUnsafe();

    private final long size;
    private final long address;

    UnsafeByteInput(long address, long size) {
        this.size = size;
        this.address = address;
    }

    @Override
    public byte read(long offset)
            throws NoSuchByteException {

        if (offset < 0 || offset >= size) {
            throw new NoSuchByteException(offset, "Offset " + offset + " outside of available data");
        }
        return UNSAFE.getByte(address + offset);
    }

    @Override
    public long read(byte[] bytes, long offset, int length) {
        Objects.requireNonNull(bytes, "bytes must not be null");
        if (offset < 0 || length < 0 || offset >= size || offset + length > size) {
            throw new NoSuchByteException(offset, "Offset " + offset + " outside of available data");
        }
        if (length > bytes.length) {
            throw new NoSuchByteException(offset, "Length " + length + " larger than writable data");
        }

        long l = Math.min(length, size - offset);
        UNSAFE.copyMemory(null, address + offset, bytes, Unsafe.ARRAY_BOOLEAN_BASE_OFFSET, l);
        return l;
    }

    @Override
    public boolean offsetValid(long offset) {
        return offset < size;
    }

}
