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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

final class OutputStreamOutput
        implements Output {

    private final OutputStream out;

    OutputStreamOutput(OutputStream out) {
        Objects.requireNonNull(out, "out must not be null");
        this.out = out;
    }

    @Override
    public long write(long offset, byte value) {
        try {
            out.write(value);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return ++offset;
    }

    @Override
    public long write(byte[] bytes, long offset, int length) {
        Objects.requireNonNull(bytes, "bytes must not be null");
        if (length > bytes.length) {
            throw new NoSuchByteException(offset, "Length " + length + " larger than writable data");
        }
        try {
            out.write(bytes, 0, length);
            return length;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
