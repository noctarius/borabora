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

final class ByteArrayInput
        implements Input {

    private final byte[] array;

    ByteArrayInput(byte[] array) {
        this.array = array;
    }

    @Override
    public byte read(long offset)
            throws NoSuchByteException {

        if (offset > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("ByteArrayInput can only handle offsets up to Integer.MAX_VALUE");
        }
        if (offset < 0 || offset >= array.length) {
            throw new NoSuchByteException(offset,
                    "Offset " + offset + " outside of available data (length: " + array.length + ", identity: " + this + ")");
        }
        return array[(int) offset];
    }

    @Override
    public long read(byte[] array, long offset, long length)
            throws NoSuchByteException {

        if (offset > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("ByteArrayInput can only handle offsets up to Integer.MAX_VALUE");
        }
        if (offset < 0 || length < 0 || offset >= this.array.length || offset + length > this.array.length) {
            throw new NoSuchByteException(offset, "Offset " + offset + " outside of available data");
        }

        long l = Math.min(length, this.array.length - offset);
        System.arraycopy(this.array, (int) offset, array, 0, (int) l);
        return l;
    }

    @Override
    public boolean offsetValid(long offset) {
        return offset < array.length;
    }

}
