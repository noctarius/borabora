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

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import static com.noctarius.borabora.Constants.ADD_INFO_EIGHT_BYTES;
import static com.noctarius.borabora.Constants.ADD_INFO_FOUR_BYTES;
import static com.noctarius.borabora.Constants.ADD_INFO_INDEFINITE;
import static com.noctarius.borabora.Constants.ADD_INFO_ONE_BYTE;
import static com.noctarius.borabora.Constants.ADD_INFO_TWO_BYTES;
import static com.noctarius.borabora.Constants.FP_VALUE_FALSE;
import static com.noctarius.borabora.Constants.FP_VALUE_NULL;
import static com.noctarius.borabora.Constants.FP_VALUE_TRUE;
import static com.noctarius.borabora.MajorType.ByteString;
import static com.noctarius.borabora.MajorType.FloatingPointOrSimple;
import static com.noctarius.borabora.MajorType.TextString;

final class Encoder {

    private static final Charset ASCII = Charset.forName("ASCII");
    private static final Charset UTF8 = Charset.forName("UTF8");

    private static final CharsetEncoder ASCII_ENCODER = ASCII.newEncoder();
    private static final CharsetDecoder ASCII_DECODER = ASCII.newDecoder();

    private static final CharsetEncoder UTF8_ENCODER = UTF8.newEncoder();
    private static final CharsetDecoder UTF8_DECODER = UTF8.newDecoder();

    private static final byte SIMPLE_VALUE_NULL_BYTE = (byte) ((FloatingPointOrSimple.typeId() << 5) | FP_VALUE_NULL);
    private static final byte SIMPLE_VALUE_FALSE_BYTE = (byte) ((FloatingPointOrSimple.typeId() << 5) | FP_VALUE_FALSE);
    private static final byte SIMPLE_VALUE_TRUE_BYTE = (byte) ((FloatingPointOrSimple.typeId() << 5) | FP_VALUE_TRUE);

    static long putString(String value, long offset, Output output) {
        if (value == null) {
            return putNull(offset, output);
        }
        if (ASCII_ENCODER.canEncode(value)) {
            return putByteString(value, offset, output);
        }
        return putTextString(value, offset, output);
    }

    static long putBoolean(boolean value, long offset, Output output) {
        output.write(offset++, value ? SIMPLE_VALUE_TRUE_BYTE : SIMPLE_VALUE_FALSE_BYTE);
        return offset;
    }

    static long putNull(long offset, Output output) {
        output.write(offset++, SIMPLE_VALUE_NULL_BYTE);
        return offset;
    }

    static long putTextString(String value, long offset, Output output) {
        byte[] data = value.getBytes(UTF8);
        return putString(data, TextString, offset, output);
    }

    static long putByteString(String value, long offset, Output output) {
        byte[] data = value.getBytes(ASCII);
        return putString(data, ByteString, offset, output);
    }

    static long encodeLength(MajorType majorType, long length, long offset, Output output) {
        int head = majorType.typeId() << 5;
        if (length == -1) {
            output.write(offset++, (byte) (head | ADD_INFO_INDEFINITE));

        } else if (length <= 23) {
            output.write(offset++, (byte) (head | length));

        } else if (length <= 255) {
            head |= ADD_INFO_ONE_BYTE;
            output.write(offset++, (byte) head);
            output.write(offset++, (byte) length);

        } else if (length <= 65535) {
            head |= ADD_INFO_TWO_BYTES;
            output.write(offset++, (byte) head);
            output.write(offset++, (byte) ((length >> 8) & 0xff));
            output.write(offset++, (byte) ((length >> 0) & 0xff));

        } else if (length <= 4294967295L) {
            head |= ADD_INFO_FOUR_BYTES;
            output.write(offset++, (byte) head);
            output.write(offset++, (byte) ((length >> 24) & 0xff));
            output.write(offset++, (byte) ((length >> 16) & 0xff));
            output.write(offset++, (byte) ((length >> 8) & 0xff));
            output.write(offset++, (byte) ((length >> 0) & 0xff));

        } else {
            head |= ADD_INFO_EIGHT_BYTES;
            output.write(offset++, (byte) head);
            output.write(offset++, (byte) ((length >> 56) & 0xff));
            output.write(offset++, (byte) ((length >> 48) & 0xff));
            output.write(offset++, (byte) ((length >> 40) & 0xff));
            output.write(offset++, (byte) ((length >> 32) & 0xff));
            output.write(offset++, (byte) ((length >> 24) & 0xff));
            output.write(offset++, (byte) ((length >> 16) & 0xff));
            output.write(offset++, (byte) ((length >> 8) & 0xff));
            output.write(offset++, (byte) ((length >> 0) & 0xff));
        }

        return offset;
    }

    private static long putString(byte[] data, MajorType majorType, long offset, Output output) {
        offset = encodeLength(majorType, data.length, offset, output);
        for (int i = 0; i < data.length; i++) {
            output.write(offset++, data[i]);
        }
        return offset;
    }

}
