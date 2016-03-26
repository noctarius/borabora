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

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

final class TagProcessors {

    static Object readDateTime(Decoder stream, long index, long length) {
        short head = stream.transientUint8(index);
        int byteSize = ByteSizes.intByteSize(head);
        String date = stream.readString(index + byteSize);
        return DateParser.parseDate(date, Locale.ENGLISH);
    }

    static BigInteger readUBigNum(Decoder stream, long index, long length) {
        short head = stream.transientUint8(index);
        int byteSize = ByteSizes.intByteSize(head);
        String bigNum = stream.readString(index + byteSize);
        try {
            byte[] bytes = bigNum.getBytes("ASCII");
            return new BigInteger(1, bytes);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("NBigNum could not be read", e);
        }
    }

    static BigInteger readNBigNum(Decoder stream, long index, long length) {
        return BigInteger.valueOf(-1).xor(readUBigNum(stream, index, length));
    }

    static Object readURI(Decoder stream, long index, long length) {
        short head = stream.transientUint8(index);
        int byteSize = ByteSizes.intByteSize(head);
        String uri = stream.readString(index + byteSize);
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException("URI could not be read", e);
        }
    }
}
