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

import java.io.OutputStream;

public interface Output {

    void write(long offset, byte value);

    long write(byte[] array, long offset, long length);

    static Output toCompositeBuffer(CompositeBuffer compositeBuffer) {
        return compositeBuffer;
    }

    static Output toByteArray(byte[] array) {
        return new ByteArrayOutput(array);
    }

    static Output toNative(long address, long size) {
        return new UnsafeByteOutput(address, size);
    }

    static Output toOutputStream(OutputStream out) {
        return new OutputStreamOutput(out);
    }

}
