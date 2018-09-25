/*
 * Copyright (c) 2016-2018, Christoph Engelbert (aka noctarius) and
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
package com.noctarius.borabora.builder.query;

import com.noctarius.borabora.Value;
import com.noctarius.borabora.spi.query.TypeSpec;

import java.util.function.Predicate;

public interface QueryTokenBuilder<T extends QueryTokenBuilder<T>> {

    T sequence(long index);

    T sequenceMatch(Predicate<Value> predicate);

    T dictionary(Predicate<Value> predicate);

    T dictionary(String key);

    T dictionary(double key);

    T dictionary(long key);

    T nullOrType(TypeSpec typeSpec);

    T requireType(TypeSpec typeSpec);

}
