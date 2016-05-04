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

import java.math.BigInteger;

enum Bytes {
    ;

    static short readUInt8(Input input, long offset) {
        byte v = readInt8(input, offset);
        return (short) (v & 0xFF);
    }

    static byte readInt8(Input input, long offset) {
        return input.read(offset);
    }

    static short readInt16(Input input, long offset) {
        short b1 = readUInt8(input, offset);
        short b2 = readUInt8(input, offset + 1);
        return (short) ((b1 << 8) | b2);
    }

    static int readUInt16(Input input, long offset) {
        return readInt16(input, offset) & 0xffff;
    }

    static int readInt32(Input input, long offset) {
        int b1 = readUInt8(input, offset);
        int b2 = readUInt8(input, offset + 1);
        int b3 = readUInt8(input, offset + 2);
        int b4 = readUInt8(input, offset + 3);
        return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
    }

    static long readUInt32(Input input, long offset) {
        return readInt32(input, offset) & 0xffffffffL;
    }

    static Number readUInt64(Input input, long offset) {
        long v = readUInt64Long(input, offset);
        if (v < 0) {
            return readUInt64BigInt(input, offset);
        }
        return v;
    }

    static long readUInt64Long(Input input, long offset) {
        long b1 = readUInt8(input, offset);
        long b2 = readUInt8(input, offset + 1);
        long b3 = readUInt8(input, offset + 2);
        long b4 = readUInt8(input, offset + 3);
        long b5 = readUInt8(input, offset + 4);
        long b6 = readUInt8(input, offset + 5);
        long b7 = readUInt8(input, offset + 6);
        long b8 = readUInt8(input, offset + 7);
        return ((b1 << 56) | (b2 << 48) | (b3 << 40) | (b4 << 32) | (b5 << 24) | (b6 << 16) | (b7 << 8) | b8)
                & 0xffffffffffffffffL;
    }

    static BigInteger readUInt64BigInt(Input input, long offset) {
        byte b1 = readInt8(input, offset);
        byte b2 = readInt8(input, offset + 1);
        byte b3 = readInt8(input, offset + 2);
        byte b4 = readInt8(input, offset + 3);
        byte b5 = readInt8(input, offset + 4);
        byte b6 = readInt8(input, offset + 5);
        byte b7 = readInt8(input, offset + 6);
        byte b8 = readInt8(input, offset + 7);
        return new BigInteger(1, new byte[]{b1, b2, b3, b4, b5, b6, b7, b8});
    }

    static long putInt8(byte value, long offset, Output output) {
        output.write(offset++, value);
        return offset;
    }

    static long putInt16(short value, long offset, Output output) {
        output.write(offset++, (byte) ((value >> 8) & 0xff));
        output.write(offset++, (byte) ((value >> 0) & 0xff));
        return offset;
    }

    static long putInt32(int value, long offset, Output output) {
        output.write(offset++, (byte) ((value >> 24) & 0xff));
        output.write(offset++, (byte) ((value >> 16) & 0xff));
        output.write(offset++, (byte) ((value >> 8) & 0xff));
        output.write(offset++, (byte) ((value >> 0) & 0xff));
        return offset;
    }

    static long putInt64(long value, long offset, Output output) {
        output.write(offset++, (byte) ((value >> 56) & 0xff));
        output.write(offset++, (byte) ((value >> 48) & 0xff));
        output.write(offset++, (byte) ((value >> 40) & 0xff));
        output.write(offset++, (byte) ((value >> 32) & 0xff));
        output.write(offset++, (byte) ((value >> 24) & 0xff));
        output.write(offset++, (byte) ((value >> 16) & 0xff));
        output.write(offset++, (byte) ((value >> 8) & 0xff));
        output.write(offset++, (byte) ((value >> 0) & 0xff));
        return offset;
    }

}
