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

import com.noctarius.borabora.spi.QueryContext;

import java.util.Iterator;
import java.util.function.Predicate;

abstract class AbstractJavaBackedDataStructure {

    protected Value findValue(Predicate<Value> predicate, Iterator<Value> iterator, QueryContext queryContext) {
        RelocatableStreamValue streamValue = new RelocatableStreamValue();
        while (iterator.hasNext()) {
            Value value = iterator.next();

            MajorType majorType = value.majorType();
            ValueType valueType = value.valueType();

            Value candidate = value;
            if (!(value instanceof ObjectValue)) {
                streamValue.relocate(queryContext, majorType, valueType, value.offset());
                candidate = streamValue;
            }

            if (predicate.test(candidate)) {
                return value;
            }
        }
        return Value.NULL_VALUE;
    }

}
