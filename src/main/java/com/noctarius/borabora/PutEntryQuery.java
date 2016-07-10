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

import java.util.function.Supplier;

class PutEntryQuery
        implements Query {

    private final Supplier<Value> keyValueSupplier;

    PutEntryQuery(String key) {
        this.keyValueSupplier = () -> new ObjectValue(MajorType.TextString, ValueTypes.TextString, key);
    }

    PutEntryQuery(long key) {
        if (key < 0) {
            this.keyValueSupplier = () -> new ObjectValue(MajorType.NegativeInteger, ValueTypes.NInt, key);
        } else {
            this.keyValueSupplier = () -> new ObjectValue(MajorType.UnsignedInteger, ValueTypes.UInt, key);
        }
    }

    PutEntryQuery(double key) {
        if (key < 0) {
            this.keyValueSupplier = () -> new ObjectValue(MajorType.FloatingPointOrSimple, ValueTypes.NFloat, key);
        } else {
            this.keyValueSupplier = () -> new ObjectValue(MajorType.FloatingPointOrSimple, ValueTypes.UFloat, key);
        }
    }

    @Override
    public long access(long offset, QueryContext queryContext) {
        Value key = keyValueSupplier.get();
        queryContext.queryStackPush(key);

        return offset;
    }

}
