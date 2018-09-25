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
package com.noctarius.borabora.spi.io;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.function.Supplier;

import static com.noctarius.borabora.spi.io.Constants.ASCII;
import static com.noctarius.borabora.spi.io.Constants.UTF8;

public enum StringEncoders
        implements StringEncoder {

    ASCII_ENCODER(ASCII, () -> ASCII.newEncoder()), //
    UTF8_ENCODER(UTF8, () -> UTF8.newEncoder());

    private final ThreadLocal<CharsetEncoder> encoder;
    private final Charset charset;

    StringEncoders(Charset charset, Supplier<CharsetEncoder> charsetEncoderSupplier) {
        this.charset = charset;
        this.encoder = new ThreadLocal<CharsetEncoder>() {
            @Override
            protected CharsetEncoder initialValue() {
                return charsetEncoderSupplier.get();
            }
        };
    }

    @Override
    public boolean canEncode(String value) {
        return encoder.get().canEncode(value);
    }

    @Override
    public byte[] encode(String value) {
        return value.getBytes(charset);
    }
}
