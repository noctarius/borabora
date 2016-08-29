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
package com.noctarius.borabora.spi.codec;

import com.noctarius.borabora.Input;
import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.ValueType;
import com.noctarius.borabora.spi.StreamValue;
import com.noctarius.borabora.spi.query.QueryContext;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;

public enum TagReaders
        implements TagReader<Object> {

    DateTime((valueType, offset, length, queryContext) -> {
        Input input = queryContext.input();
        int byteSize = ByteSizes.intByteSize(input, offset);
        String date = Decoder.readString(input, offset + byteSize);
        return Decoder.parseDate(date);
    }),

    UBigNum((valueType, offset, length, queryContext) -> {
        Input input = queryContext.input();
        // We need to extract raw bytes to prevent creating strings
        // and since new String(..) transforms the byte array for char[]
        byte[] bytes = Decoder.extractStringBytes(input, offset + 1);
        return new BigInteger(1, bytes);
    }),

    NBigNum((valueType, offset, length, queryContext) -> //
            BigInteger.valueOf(-1).xor((BigInteger) UBigNum.process(valueType, offset, length, queryContext))),

    Fraction((valueType, offset, length, queryContext) -> {
        Input input = queryContext.input();
        return Decoder.readFraction(input, offset);
    }),

    URI((valueType, offset, length, queryContext) -> {
        Input input = queryContext.input();
        int byteSize = ByteSizes.intByteSize(input, offset);
        String uri = Decoder.readString(input, offset + byteSize);
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException("URI could not be read", e);
        }
    }),

    Timestamp((vt, offset, length, queryContext) -> {
        Input input = queryContext.input();
        // Move offset to the actual data item
        offset += ByteSizes.headByteSize(input, offset);
        // Timestamp might have different types of items
        ValueType valueType = queryContext.valueType(offset);
        return Decoder.readNumber(input, valueType, offset);
    }),

    EncCBOR((vt, offset, length, queryContext) -> {
        Input input = queryContext.input();
        int headByteSize = ByteSizes.intByteSize(input, offset);

        offset += headByteSize;
        short head = Decoder.readUInt8(input, offset);
        MajorType majorType = MajorType.findMajorType(head);
        // Normally this is a bytestring for real
        ValueType valueType = queryContext.valueType(offset);
        return new StreamValue(majorType, valueType, offset, queryContext);
    });

    private final TagReader<Object> tagReader;

    TagReaders(TagReader<Object> tagReader) {
        this.tagReader = tagReader;
    }

    @Override
    public Object process(ValueType valueType, long offset, long length, QueryContext queryContext) {
        return tagReader.process(valueType, offset, length, queryContext);
    }

}
