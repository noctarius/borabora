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
import com.noctarius.borabora.spi.query.QueryConsumer;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public interface Constants {

    int ADDITIONAL_INFORMATION_MASK = 0b000_11111;
    int OPCODE_BREAK_MASK = 0b111_11111;

    short OFFSET_CODE_NULL = -1;
    short OFFSET_CODE_EXIT = -2;

    short MT_UNSINGED_INT = 0;
    short MT_NEGATIVE_INT = 1;
    short MT_BYTESTRING = 2;
    short MT_TEXTSTRING = 3;
    short MT_SEQUENCE = 4;
    short MT_DICTIONARY = 5;
    short MT_SEMANTIC_TAG = 6;
    short MT_FLOAT_SIMPLE = 7;

    int FP_VALUE_FALSE = 20;
    int FP_VALUE_TRUE = 21;
    int FP_VALUE_NULL = 22;
    int FP_VALUE_UNDEF = 23;
    int FP_VALUE_HALF_PRECISION = 25;
    int FP_VALUE_SINGLE_PRECISION = 26;
    int FP_VALUE_DOUBLE_PRECISION = 27;

    int TAG_DATE_TIME = 0;
    int TAG_TIMESTAMP = 1;
    int TAG_UNSIGNED_BIGNUM = 2;
    int TAG_SIGNED_BIGNUM = 3;
    int TAG_FRACTION = 4;
    int TAG_BIGFLOAT = 5;
    int TAG_ENCCBOR = 24;//55799;
    int TAG_URI = 32;
    int TAG_REGEX = 35;
    int TAG_MIME = 36;

    int ADD_INFO_ONE_BYTE = 24;
    int ADD_INFO_TWO_BYTES = 25;
    int ADD_INFO_FOUR_BYTES = 26;
    int ADD_INFO_EIGHT_BYTES = 27;
    int ADD_INFO_RESERVED_1 = 28;
    int ADD_INFO_RESERVED_2 = 29;
    int ADD_INFO_RESERVED_3 = 30;
    int ADD_INFO_INDEFINITE = 31;

    byte SIMPLE_VALUE_NULL_BYTE = (byte) ((MajorType.FloatingPointOrSimple.typeId() << 5) | FP_VALUE_NULL);
    byte SIMPLE_VALUE_FALSE_BYTE = (byte) ((MajorType.FloatingPointOrSimple.typeId() << 5) | FP_VALUE_FALSE);
    byte SIMPLE_VALUE_TRUE_BYTE = (byte) ((MajorType.FloatingPointOrSimple.typeId() << 5) | FP_VALUE_TRUE);

    short DECIMAL_FRACTION_TWO_ELEMENT_SEQUENCE_HEAD = (short) ((MajorType.Sequence.typeId() << 5) | 2);

    long NUMBER_VAL_ONE_BYTE = 23;
    long NUMBER_VAL_TWO_BYTE = 256;
    long NUMBER_VAL_THREE_BYTE = 65536;
    long NUMBER_VAL_FIVE_BYTE = 4294967296L;

    BigInteger BI_VAL_MINUS_ONE = BigInteger.valueOf(-1);
    BigInteger BI_VAL_24 = BigInteger.valueOf(24);
    BigInteger BI_VAL_256 = BigInteger.valueOf(256);
    BigInteger BI_VAL_65536 = BigInteger.valueOf(65536);
    BigInteger BI_VAL_4294967296 = BigInteger.valueOf(4294967296L);
    BigInteger BI_VAL_MAX_VALUE = new BigInteger("18446744073709551616");
    BigInteger BI_MASK = BigInteger.valueOf(0xff);

    Charset ASCII = Charset.forName("ASCII");
    Charset UTF8 = Charset.forName("UTF8");

    int COMPARATOR_LESS_THAN = -1;

    ZoneId UTC = ZoneId.of("UTC");

    DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd't'HH:mm:ss.SSSz", Locale.US);

    byte[] EMPTY_BYTE_ARRAY = new byte[0];

    QueryConsumer EMPTY_QUERY_CONSUMER = ((offset) -> false);

    int MATCH_STRING_FAST_PATH_TRESHOLD = 1024;

}
