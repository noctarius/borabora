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
import com.noctarius.borabora.Value;
import com.noctarius.borabora.ValueTypes;
import com.noctarius.borabora.spi.transformation.Mutator;
import com.noctarius.borabora.spi.transformation.Transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PredicateMutator<T>
        implements Mutator<T> {

    private final Predicate<Value> predicate;
    private final Transformation transformation;

    public PredicateMutator(Predicate<Value> predicate, Transformation transformation) {
        this.predicate = predicate;
        this.transformation = transformation;
    }

    @Override
    public void mutate(Value value, T builder) {
        transformation.transform(value, builder);
    }

    @Override
    public Stream<Value> matchValues(Parser parser, Input input) {
        List<Value> values = new ArrayList<>();
        parser.read(input, parser.newQueryBuilder().multiStream().build(), value -> recursivelyScanTree(value, values));
        return values.stream();
    }

    private void recursivelyScanTree(Value value, List<Value> values) {
        if (predicate.test(value)) {
            values.add(value);
        }

        if (value.valueType() == ValueTypes.Sequence) {
            value.sequence().forEach(v -> recursivelyScanTree(v, values));

        } else if (value.valueType() == ValueTypes.Dictionary) {
            value.dictionary().forEach(e -> {
                recursivelyScanTree(e.getKey(), values);
                recursivelyScanTree(e.getValue(), values);
            });
        }
    }

}
