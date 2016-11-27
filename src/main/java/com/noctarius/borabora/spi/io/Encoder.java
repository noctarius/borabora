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
package com.noctarius.borabora.spi.io;

import com.noctarius.borabora.Dictionary;
import com.noctarius.borabora.Input;
import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.Output;
import com.noctarius.borabora.Sequence;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.spi.AbstractStreamValue;
import com.noctarius.borabora.spi.builder.EncoderContext;
import com.noctarius.borabora.spi.codec.TagStrategies;
import com.noctarius.borabora.spi.codec.TagStrategy;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Map;

public final class Encoder
        implements Constants {

    private Encoder() {
    }

    public static long putValue(Value value, long offset, Output output, EncoderContext encoderContext) {
        MajorType majorType = value.majorType();
        if (value instanceof AbstractStreamValue) {
            AbstractStreamValue asv = (AbstractStreamValue) value;
            Input itemInput = asv.input();
            long itemOffset = asv.offset();
            long itemLength = Decoder.length(itemInput, majorType, itemOffset);

            int chunkSize = 1024;
            byte[] chunk = new byte[chunkSize];
            long sourceOffset = itemOffset;
            long remaining = itemLength;
            while (remaining > 0) {
                long length = Math.min(chunkSize, remaining);
                sourceOffset += itemInput.read(chunk, sourceOffset, (int) length);
                output.write(chunk, offset, (int) length);
                remaining -= length;
                offset += length;
            }

        } else {
            Object byValueType = value.byValueType();
            if (majorType == MajorType.Dictionary) {
                Dictionary dictionary = value.dictionary();
                offset = encodeLengthAndValue(majorType, dictionary.size(), offset, output);
                for (Map.Entry<Value, Value> entry : dictionary.entries()) {
                    offset = putValue(entry.getKey(), offset, output, encoderContext);
                    offset = putValue(entry.getValue(), offset, output, encoderContext);
                }

            } else if (majorType == MajorType.Sequence) {
                Sequence sequence = value.sequence();
                offset = encodeLengthAndValue(majorType, sequence.size(), offset, output);
                for (Value entry : sequence) {
                    offset = putValue(entry, offset, output, encoderContext);
                }

            } else if (byValueType instanceof String) {
                offset = putString((String) byValueType, offset, output);
            } else if (byValueType instanceof BigInteger) {
                offset = putBigInteger((BigInteger) byValueType, offset, output);
            } else if (byValueType instanceof BigDecimal) {
                offset = putFraction((BigDecimal) byValueType, offset, output);
            } else if (byValueType instanceof Float) {
                offset = putFloat(((Number) byValueType).floatValue(), offset, output);
            } else if (byValueType instanceof Double) {
                offset = putDouble(((Number) byValueType).doubleValue(), offset, output);
            } else if (byValueType instanceof Number) {
                offset = putNumber(((Number) byValueType).longValue(), offset, output);
            } else if (majorType == MajorType.SemanticTag) {
                TagStrategy tagStrategy = TagStrategies.tagStrategy(value.valueType());
                if (tagStrategy == null) {
                    throw new IllegalStateException("Unsupported data type: " + byValueType.getClass());
                }
                encoderContext.offset(offset);
                offset = tagStrategy.process(value.byValueType(), offset, encoderContext);

            } else {
                throw new IllegalStateException("Unsupported data type: " + byValueType.getClass());
            }

        }
        return offset;
    }

    public static long putString(String value, long offset, Output output) {
        if (StringEncoders.ASCII_ENCODER.canEncode(value)) {
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
        byte[] data = StringEncoders.UTF8_ENCODER.encode(value);
        return putString(data, MajorType.TextString, offset, output);
    }

    public static long putByteString(String value, long offset, Output output) {
        byte[] data = StringEncoders.ASCII_ENCODER.encode(value);
        return putString(data, MajorType.ByteString, offset, output);
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
        if (value.compareTo(BigInteger.ZERO) <= COMPARATOR_LESS_THAN) {
            majorType = MajorType.NegativeInteger;
            absValue = BI_VAL_MINUS_ONE.subtract(value).abs();

        } else {
            majorType = MajorType.UnsignedInteger;
            absValue = value;
        }
        return encodeLengthAndValue(majorType, absValue, offset, output);
    }

    public static long putBigInteger(BigInteger value, long offset, Output output) {
        // Otherwise write as XBigNum
        MajorType majorType;
        BigInteger absValue;
        if (value.compareTo(BigInteger.ZERO) <= COMPARATOR_LESS_THAN) {
            majorType = MajorType.NegativeInteger;
            absValue = BI_VAL_MINUS_ONE.subtract(value).abs();

        } else {
            majorType = MajorType.UnsignedInteger;
            absValue = value;
        }
        if (majorType == MajorType.NegativeInteger) {
            offset = putSemanticTag(TAG_NEGATIVE_BIGNUM, offset, output);
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
        String string = dateTime.format(DATE_TIME_FRACTION_OFFSET_FORMAT);
        return putString(string.getBytes(UTF8), MajorType.TextString, offset, output);
    }

    public static long putTimestamp(long timestamp, long offset, Output output) {
        offset = putSemanticTag(TAG_TIMESTAMP, offset, output);
        return putNumber(timestamp, offset, output);
    }

    public static long putFraction(BigDecimal value, long offset, Output output) {
        offset = putSemanticTag(TAG_FRACTION, offset, output);
        offset = encodeLengthAndValue(MajorType.Sequence, 2, offset, output);
        int scale = value.scale();
        BigInteger unscaledValue = value.unscaledValue();
        offset = putNumber(scale, offset, output);
        return putNumber(unscaledValue, offset, output);
    }

    public static long encodeLengthAndValue(MajorType majorType, long length, long offset, Output output) {
        int head = majorType.typeId() << 5;
        if (length == -1) {
            return Bytes.putInt8((byte) (head | ADD_INFO_INDEFINITE), offset, output);

        } else if (length <= NUMBER_VAL_MAX_ONE_BYTE) {
            return Bytes.putInt8((byte) (head | length), offset, output);

        } else if (length <= NUMBER_VAL_MAX_TWO_BYTE) {
            head |= ADD_INFO_ONE_BYTE;
            offset = Bytes.putInt8((byte) head, offset, output);
            offset = Bytes.putInt8((byte) length, offset, output);

        } else if (length <= NUMBER_VAL_MAX_THREE_BYTE) {
            head |= ADD_INFO_TWO_BYTES;
            offset = Bytes.putInt8((byte) head, offset, output);
            offset = Bytes.putInt16((short) length, offset, output);

        } else if (length <= NUMBER_VAL_MAX_FIVE_BYTE) {
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
        if (length.compareTo(BI_VAL_24) <= COMPARATOR_LESS_THAN) {
            return Bytes.putInt8((byte) (head | length.intValue()), offset, output);

        } else if (length.compareTo(BI_VAL_256) <= COMPARATOR_LESS_THAN) {
            head |= ADD_INFO_ONE_BYTE;
            offset = Bytes.putInt8((byte) head, offset, output);
            offset = Bytes.putInt8((byte) length.intValue(), offset, output);

        } else if (length.compareTo(BI_VAL_65536) <= COMPARATOR_LESS_THAN) {
            head |= ADD_INFO_TWO_BYTES;
            offset = Bytes.putInt8((byte) head, offset, output);
            offset = Bytes.putInt16((short) length.intValue(), offset, output);

        } else if (length.compareTo(BI_VAL_4294967296) <= COMPARATOR_LESS_THAN) {
            head |= ADD_INFO_FOUR_BYTES;
            offset = Bytes.putInt8((byte) head, offset, output);
            offset = Bytes.putInt32((int) length.longValue(), offset, output);

        } else if (length.compareTo(BI_VAL_MAX_VALUE) <= COMPARATOR_LESS_THAN) {
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
                offset = putSemanticTag(TAG_NEGATIVE_BIGNUM, offset, output);
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
