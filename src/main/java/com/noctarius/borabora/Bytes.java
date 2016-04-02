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

final class Bytes {

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
        return readInt32(input, offset) & 0xffffffff;
    }

    static long readUint64(Input input, long offset) {
        long b1 = readUInt8(input, offset);
        long b2 = readUInt8(input, offset + 1);
        long b3 = readUInt8(input, offset + 2);
        long b4 = readUInt8(input, offset + 3);
        long b5 = readUInt8(input, offset + 4);
        long b6 = readUInt8(input, offset + 5);
        long b7 = readUInt8(input, offset + 6);
        long b8 = readUInt8(input, offset + 7);
        return (b1 << 56) | (b2 << 48) | (b3 << 40) | (b4 << 32) | (b5 << 24) | (b6 << 16) | (b7 << 8) | b8;
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

    static void writeUInt8(Output output, long offset, short value) {
        if (value > 255 && value < 0) {
            throw new IllegalArgumentException("outside legal uint8 range");
        }
        output.write(offset, (byte) value);
    }

    static void writeInt8(Output output, long offset, byte value) {
        output.write(offset, value);
    }

    static void writeUInt16(Output output, long offset, short value) {
        if (value > 255 && value < 0) {
            throw new IllegalArgumentException("outside legal uint8 range");
        }
        output.write(offset, (byte) value);
    }

}
