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

import com.noctarius.borabora.spi.ByteSizes;
import com.noctarius.borabora.spi.Decoder;
import com.noctarius.borabora.spi.QueryContext;
import com.noctarius.borabora.spi.StreamValue;
import com.noctarius.borabora.spi.TagDecoder;
import com.noctarius.borabora.spi.TypeSpec;
import com.noctarius.borabora.spi.TypeSpecs;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Locale;

final class BuiltInTagDecoder
        implements TagDecoder<Object> {

    static final TagDecoder INSTANCE = new BuiltInTagDecoder();

    private BuiltInTagDecoder() {
    }

    @Override
    public boolean handles(Input input, long offset) {
        return ValueTypes.valueType(input, offset) != null;
    }

    @Override
    public Object process(long offset, long length, QueryContext queryContext) {
        ValueTypes valueType = ValueTypes.valueType(queryContext.input(), offset);
        return valueType.process(offset, length, queryContext);
    }

    @Override
    public TypeSpec handles(int tagId) {
        for (TypeSpec typeSpec : TypeSpecs.values()) {
            if (typeSpec.tagId() == tagId) {
                return typeSpec;
            }
        }
        return null;
    }

    static Date readDateTime(long offset, long length, QueryContext queryContext) {
        Input input = queryContext.input();
        int byteSize = ByteSizes.intByteSize(input, offset);
        String date = Decoder.readString(input, offset + byteSize);
        return DateParser.parseDate(date, Locale.ENGLISH);
    }

    static BigInteger readUBigNum(long offset, long length, QueryContext queryContext) {
        Input input = queryContext.input();
        int byteSize = ByteSizes.intByteSize(input, offset);
        String bigNum = Decoder.readString(input, offset + byteSize);
        try {
            byte[] bytes = bigNum.getBytes("ASCII");
            return new BigInteger(1, bytes);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("NBigNum could not be read", e);
        }
    }

    static BigInteger readNBigNum(long offset, long length, QueryContext queryContext) {
        return BigInteger.valueOf(-1).xor(readUBigNum(offset, length, queryContext));
    }

    static URI readURI(long offset, long length, QueryContext queryContext) {
        Input input = queryContext.input();
        int byteSize = ByteSizes.intByteSize(input, offset);
        String uri = Decoder.readString(input, offset + byteSize);
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException("URI could not be read", e);
        }
    }

    static Number readTimestamp(long offset, long length, QueryContext queryContext) {
        Input input = queryContext.input();
        ValueType valueType = ValueTypes.valueType(input, offset + 1);
        return Decoder.readNumber(input, valueType, offset + 1);
    }

    static Value readEncCBOR(long offset, long length, QueryContext queryContext) {
        Input input = queryContext.input();
        int headByteSize = ByteSizes.intByteSize(input, offset);

        offset += headByteSize;
        short head = Decoder.readUInt8(input, offset);
        MajorType majorType = MajorType.findMajorType(head);
        ValueType valueType = ValueTypes.valueType(input, offset);
        return new StreamValue(majorType, valueType, offset, queryContext);
    }

    interface TagProcessor {
        Object process(long offset, long length, QueryContext queryContext);
    }

}
