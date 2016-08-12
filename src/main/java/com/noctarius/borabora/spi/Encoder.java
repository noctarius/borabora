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
package com.noctarius.borabora.spi;

import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.Output;

import java.math.BigInteger;
import java.net.URI;
import java.time.ZonedDateTime;

import static com.noctarius.borabora.MajorType.ByteString;
import static com.noctarius.borabora.MajorType.TextString;
import static com.noctarius.borabora.spi.Constants.ADD_INFO_EIGHT_BYTES;
import static com.noctarius.borabora.spi.Constants.ADD_INFO_FOUR_BYTES;
import static com.noctarius.borabora.spi.Constants.ADD_INFO_INDEFINITE;
import static com.noctarius.borabora.spi.Constants.ADD_INFO_ONE_BYTE;
import static com.noctarius.borabora.spi.Constants.ADD_INFO_TWO_BYTES;
import static com.noctarius.borabora.spi.Constants.ASCII;
import static com.noctarius.borabora.spi.Constants.BI_MASK;
import static com.noctarius.borabora.spi.Constants.BI_VAL_24;
import static com.noctarius.borabora.spi.Constants.BI_VAL_256;
import static com.noctarius.borabora.spi.Constants.BI_VAL_4294967296;
import static com.noctarius.borabora.spi.Constants.BI_VAL_65536;
import static com.noctarius.borabora.spi.Constants.BI_VAL_MAX_VALUE;
import static com.noctarius.borabora.spi.Constants.BI_VAL_MINUS_ONE;
import static com.noctarius.borabora.spi.Constants.COMPARATOR_LESS_THAN;
import static com.noctarius.borabora.spi.Constants.DATE_TIME_FORMAT;
import static com.noctarius.borabora.spi.Constants.FP_VALUE_DOUBLE_PRECISION;
import static com.noctarius.borabora.spi.Constants.FP_VALUE_HALF_PRECISION;
import static com.noctarius.borabora.spi.Constants.FP_VALUE_SINGLE_PRECISION;
import static com.noctarius.borabora.spi.Constants.SIMPLE_VALUE_FALSE_BYTE;
import static com.noctarius.borabora.spi.Constants.SIMPLE_VALUE_NULL_BYTE;
import static com.noctarius.borabora.spi.Constants.SIMPLE_VALUE_TRUE_BYTE;
import static com.noctarius.borabora.spi.Constants.TAG_DATE_TIME;
import static com.noctarius.borabora.spi.Constants.TAG_SIGNED_BIGNUM;
import static com.noctarius.borabora.spi.Constants.TAG_TIMESTAMP;
import static com.noctarius.borabora.spi.Constants.TAG_UNSIGNED_BIGNUM;
import static com.noctarius.borabora.spi.Constants.TAG_URI;
import static com.noctarius.borabora.spi.Constants.UTF8;
import static com.noctarius.borabora.spi.StringEncoders.ASCII_ENCODER;
import static com.noctarius.borabora.spi.StringEncoders.UTF8_ENCODER;

public enum Encoder {
    ;

    public static long putInt8(byte value, long offset, Output output) {
        return Bytes.putInt8(value, offset, output);
    }

    public static long putString(String value, long offset, Output output) {
        if (ASCII_ENCODER.canEncode(value)) {
            return putByteString(value, offset, output);
        }
        return putTextString(value, offset, output);
    }

    public static long putBoolean(boolean value, long offset, Output output) {
        return Bytes.putInt8(value ? SIMPLE_VALUE_TRUE_BYTE : SIMPLE_VALUE_FALSE_BYTE, offset, output);
    }

    public static long putNull(long offset, Output output) {
        return Bytes.putInt8(SIMPLE_VALUE_NULL_BYTE, offset, output);
    }

    public static long putTextString(String value, long offset, Output output) {
        byte[] data = UTF8_ENCODER.encode(value);
        return putString(data, TextString, offset, output);
    }

    public static long putByteString(String value, long offset, Output output) {
        byte[] data = ASCII_ENCODER.encode(value);
        return putString(data, ByteString, offset, output);
    }

    public static long putNumber(long value, long offset, Output output) {
        MajorType majorType;
        long absValue;
        if (value < 0) {
            majorType = MajorType.NegativeInteger;
            absValue = Math.abs(-1 - value);

        } else {
            majorType = MajorType.UnsignedInteger;
            absValue = value;
        }
        return encodeLengthAndValue(majorType, absValue, offset, output);
    }

    public static long putNumber(BigInteger value, long offset, Output output) {
        MajorType majorType;
        BigInteger absValue;
        if (value.compareTo(BigInteger.ZERO) == COMPARATOR_LESS_THAN) {
            majorType = MajorType.NegativeInteger;
            absValue = BI_VAL_MINUS_ONE.subtract(value).abs();

        } else {
            majorType = MajorType.UnsignedInteger;
            absValue = value;
        }
        return encodeLengthAndValue(majorType, absValue, offset, output);
    }

    public static long putBigInteger(BigInteger value, long offset, Output output) {
        MajorType majorType;
        BigInteger absValue;
        if (value.compareTo(BigInteger.ZERO) == COMPARATOR_LESS_THAN) {
            majorType = MajorType.NegativeInteger;
            absValue = BI_VAL_MINUS_ONE.subtract(value).abs();

        } else {
            majorType = MajorType.UnsignedInteger;
            absValue = value;
        }
        if (majorType == MajorType.NegativeInteger) {
            offset = putSemanticTag(TAG_SIGNED_BIGNUM, offset, output);
        } else {
            offset = putSemanticTag(TAG_UNSIGNED_BIGNUM, offset, output);
        }
        return putString(absValue.toByteArray(), MajorType.ByteString, offset, output);
    }

    public static long putHalfPrecision(float value, long offset, Output output) {
        int intValue = HalfPrecision.fromFloat(value);
        return encodeFloat(FP_VALUE_HALF_PRECISION, intValue, offset, output);
    }

    public static long putFloat(float value, long offset, Output output) {
        int intValue = Float.floatToIntBits(value);
        return encodeFloat(FP_VALUE_SINGLE_PRECISION, intValue, offset, output);
    }

    public static long putDouble(double value, long offset, Output output) {
        long longValue = Double.doubleToLongBits(value);
        return encodeFloat(FP_VALUE_DOUBLE_PRECISION, longValue, offset, output);
    }

    public static long putSemanticTag(int tagId, long offset, Output output) {
        return encodeLengthAndValue(MajorType.SemanticTag, tagId, offset, output);
    }

    public static long putUri(URI uri, long offset, Output output) {
        offset = putSemanticTag(TAG_URI, offset, output);
        String string = uri.toString();
        return putString(string.getBytes(UTF8), MajorType.TextString, offset, output);
    }

    public static long putDateTime(ZonedDateTime dateTime, long offset, Output output) {
        offset = putSemanticTag(TAG_DATE_TIME, offset, output);
        String string = dateTime.format(DATE_TIME_FORMAT);
        return putString(string.getBytes(UTF8), MajorType.TextString, offset, output);
    }

    public static long putTimestamp(long timestamp, long offset, Output output) {
        offset = putSemanticTag(TAG_TIMESTAMP, offset, output);
        return putNumber(timestamp, offset, output);
    }

    public static long encodeLengthAndValue(MajorType majorType, long length, long offset, Output output) {
        int head = majorType.typeId() << 5;
        if (length == -1) {
            return Bytes.putInt8((byte) (head | ADD_INFO_INDEFINITE), offset, output);

        } else if (length <= 23L) {
            return Bytes.putInt8((byte) (head | length), offset, output);

        } else if (length <= 255L) {
            head |= ADD_INFO_ONE_BYTE;
            offset = Bytes.putInt8((byte) head, offset, output);
            offset = Bytes.putInt8((byte) length, offset, output);

        } else if (length <= 65535L) {
            head |= ADD_INFO_TWO_BYTES;
            offset = Bytes.putInt8((byte) head, offset, output);
            offset = Bytes.putInt16((short) length, offset, output);

        } else if (length <= 4294967295L) {
            head |= ADD_INFO_FOUR_BYTES;
            offset = Bytes.putInt8((byte) head, offset, output);
            offset = Bytes.putInt32((int) length, offset, output);

        } else {
            head |= ADD_INFO_EIGHT_BYTES;
            offset = Bytes.putInt8((byte) head, offset, output);
            offset = Bytes.putInt64(length, offset, output);
        }
        return offset;
    }

    public static long encodeLengthAndValue(MajorType majorType, BigInteger length, long offset, Output output) {
        int head = majorType.typeId() << 5;
        if (length.compareTo(BI_VAL_24) == COMPARATOR_LESS_THAN) {
            return Bytes.putInt8((byte) (head | length.intValue()), offset, output);

        } else if (length.compareTo(BI_VAL_256) == COMPARATOR_LESS_THAN) {
            head |= ADD_INFO_ONE_BYTE;
            offset = Bytes.putInt8((byte) head, offset, output);
            offset = Bytes.putInt8((byte) length.intValue(), offset, output);

        } else if (length.compareTo(BI_VAL_65536) == COMPARATOR_LESS_THAN) {
            head |= ADD_INFO_TWO_BYTES;
            offset = Bytes.putInt8((byte) head, offset, output);
            offset = Bytes.putInt16((short) length.intValue(), offset, output);

        } else if (length.compareTo(BI_VAL_4294967296) == COMPARATOR_LESS_THAN) {
            head |= ADD_INFO_FOUR_BYTES;
            offset = Bytes.putInt8((byte) head, offset, output);
            offset = Bytes.putInt32((int) length.longValue(), offset, output);

        } else if (length.compareTo(BI_VAL_MAX_VALUE) == COMPARATOR_LESS_THAN) {
            head |= ADD_INFO_EIGHT_BYTES;
            offset = Bytes.putInt8((byte) head, offset, output);
            output.write(offset++, (byte) length.shiftRight(56).and(BI_MASK).intValue());
            output.write(offset++, (byte) length.shiftRight(48).and(BI_MASK).intValue());
            output.write(offset++, (byte) length.shiftRight(40).and(BI_MASK).intValue());
            output.write(offset++, (byte) length.shiftRight(32).and(BI_MASK).intValue());
            output.write(offset++, (byte) length.shiftRight(24).and(BI_MASK).intValue());
            output.write(offset++, (byte) length.shiftRight(16).and(BI_MASK).intValue());
            output.write(offset++, (byte) length.shiftRight(8).and(BI_MASK).intValue());
            output.write(offset++, (byte) length.shiftRight(0).and(BI_MASK).intValue());

        } else {
            if (majorType == MajorType.NegativeInteger) {
                offset = putSemanticTag(TAG_SIGNED_BIGNUM, offset, output);
            } else {
                offset = putSemanticTag(TAG_UNSIGNED_BIGNUM, offset, output);
            }
            offset = putString(length.toByteArray(), MajorType.ByteString, offset, output);
        }
        return offset;
    }

    private static long putString(byte[] data, MajorType majorType, long offset, Output output) {
        offset = encodeLengthAndValue(majorType, data.length, offset, output);
        offset += output.write(data, offset, data.length);
        return offset;
    }

    private static long encodeFloat(int fpType, long bits, long offset, Output output) {
        int head = MajorType.FloatingPointOrSimple.typeId() << 5;
        offset = Bytes.putInt8((byte) (head | fpType), offset, output);
        switch (fpType) {
            case FP_VALUE_HALF_PRECISION:
                return Bytes.putInt16((short) bits, offset, output);
            case FP_VALUE_SINGLE_PRECISION:
                return Bytes.putInt32((int) bits, offset, output);
            default:
                return Bytes.putInt64(bits, offset, output);
        }
    }

}
