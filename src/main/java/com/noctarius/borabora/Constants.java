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

interface Constants {

    int ADDITIONAL_INFORMATION_MASK = 0b000_11111;
    int OPCODE_BREAK_MASK = 0b111_11111;

    int FP_VALUE_FALSE = 20;
    int FP_VALUE_TRUE = 21;
    int FP_VALUE_NULL = 22;
    int FP_VALUE_UNDEF = 23;

    int TAG_DATE_TIME = 0;
    int TAG_TIMESTAMP = 1;
    int TAG_UNSIGNED_BIGNUM = 2;
    int TAG_SIGNED_BIGNUM = 3;
    int TAG_FRACTION = 4;
    int TAG_BIGFLOAT = 5;
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

}
