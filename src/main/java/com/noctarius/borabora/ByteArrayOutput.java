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

import java.util.Objects;

final class ByteArrayOutput
        implements Output {

    private final byte[] bytes;

    ByteArrayOutput(byte[] bytes) {
        Objects.requireNonNull(bytes, "bytes must not be null");
        this.bytes = bytes;
    }

    @Override
    public long write(long offset, byte value) {
        if (offset > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("ByteArrayOutput can only handle offsets up to Integer.MAX_VALUE");
        }
        if (offset < 0 || offset >= bytes.length) {
            throw new NoSuchByteException(offset, "Offset " + offset + " outside of available data");
        }
        bytes[(int) offset] = value;
        return ++offset;
    }

    @Override
    public long write(byte[] array, long offset, long length) {
        Objects.requireNonNull(bytes, "bytes must not be null");
        if (offset > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("ByteArrayOutput can only handle offsets up to Integer.MAX_VALUE");
        }
        if (offset < 0 || length < 0 || offset >= this.bytes.length || offset + length > this.bytes.length) {
            throw new NoSuchByteException(offset, "Offset " + offset + " outside of writable data");
        }

        long l = Math.min(length, this.bytes.length - offset);
        System.arraycopy(array, (int) offset, this.bytes, 0, (int) l);
        return l;
    }

}
