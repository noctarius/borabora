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

import sun.misc.Unsafe;

final class UnsafeByteOutput
        implements Output {

    private static final Unsafe UNSAFE = UnsafeUtils.getUnsafe();

    private final long size;
    private final long address;

    UnsafeByteOutput(long address, long size) {
        this.size = size;
        this.address = address;
    }

    @Override
    public void write(long offset, byte value) {
        if (offset < 0 || offset >= size) {
            throw new NoSuchByteException(offset, "Offset " + offset + " outside of available data");
        }
        UNSAFE.putByte(address + offset, value);
    }

    @Override
    public long write(byte[] array, long offset, long length) {
        if (offset < 0 || length < 0 || offset >= size || offset + length > size) {
            throw new NoSuchByteException(offset, "Offset " + offset + " outside of writable data");
        }

        long l = Math.min(length, size - offset);
        UNSAFE.copyMemory(array, Unsafe.ARRAY_BYTE_BASE_OFFSET, null, address + offset, l);
        return l;
    }

}
