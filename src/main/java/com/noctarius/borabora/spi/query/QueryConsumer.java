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
package com.noctarius.borabora.spi.query;

import com.noctarius.borabora.Input;
import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.ValueType;
import com.noctarius.borabora.spi.StreamValue;
import com.noctarius.borabora.spi.codec.Decoder;

import static com.noctarius.borabora.spi.Constants.OFFSET_CODE_EXIT;
import static com.noctarius.borabora.spi.Constants.OFFSET_CODE_NULL;

public interface QueryConsumer {

    default boolean accept(long offset, QueryContext queryContext) {
        Input input = queryContext.input();

        Value value;
        if (offset == OFFSET_CODE_NULL) {
            value = Value.NULL_VALUE;
        } else if (offset == OFFSET_CODE_EXIT) {
            return false;
        } else {
            short head = Decoder.readUInt8(input, offset);
            MajorType majorType = MajorType.findMajorType(head);
            ValueType valueType = queryContext.valueType(offset);
            value = new StreamValue(majorType, valueType, offset, queryContext);
        }

        return consume(value);
    }

    boolean consume(Value value);

}
