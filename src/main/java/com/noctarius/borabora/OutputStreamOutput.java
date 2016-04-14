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

import java.io.IOException;
import java.io.OutputStream;

final class OutputStreamOutput
        implements Output {

    private final OutputStream out;

    OutputStreamOutput(OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(long offset, byte value) {
        try {
            out.write(value);
        } catch (IOException e) {
            throw new NoSuchByteException(offset, e);
        }
    }

    @Override
    public long write(byte[] array, long offset, long length) {
        try {
            out.write(array, 0, (int) length);
            return length;
        } catch (IOException e) {
            throw new NoSuchByteException(offset, e);
        }
    }

}
