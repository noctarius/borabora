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
package com.noctarius.borabora.impl.transformation;

import com.noctarius.borabora.Input;
import com.noctarius.borabora.Parser;
import com.noctarius.borabora.Query;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.spi.transformation.Mutator;
import com.noctarius.borabora.spi.transformation.Transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class QueryMutator<T> implements Mutator<T> {

    private final Query query;
    private final Transformation transformation;

    public QueryMutator(Query query, Transformation transformation) {
        this.query = query;
        this.transformation = transformation;
    }

    @Override
    public void mutate(Value value, T builder) {
        transformation.transform(value, builder);
    }

    @Override
    public Stream<Value> matchValues(Parser parser, Input input) {
        List<Value> values = new ArrayList<>();
        parser.read(input, query, values::add);
        return values.stream();
    }
}
