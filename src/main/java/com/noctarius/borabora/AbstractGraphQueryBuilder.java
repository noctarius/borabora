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

import com.noctarius.borabora.spi.TypeSpec;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static com.noctarius.borabora.DictionaryGraphQuery.floatMatcher;
import static com.noctarius.borabora.DictionaryGraphQuery.intMatcher;
import static com.noctarius.borabora.DictionaryGraphQuery.predicateMatcher;
import static com.noctarius.borabora.DictionaryGraphQuery.stringMatcher;

abstract class AbstractGraphQueryBuilder {

    protected final List<GraphQuery> graphQueries;

    protected AbstractGraphQueryBuilder(List<GraphQuery> graphQueries) {
        this.graphQueries = graphQueries;
    }

    public void sequence0(long index) {
        graphQueries.add(new SequenceGraphQuery(index));
    }

    public void dictionary0(Predicate<Value> predicate) {
        Objects.requireNonNull(predicate, "predicate must not be null");
        graphQueries.add(predicateMatcher(predicate));
    }

    public void dictionary0(String key) {
        Objects.requireNonNull(key, "key must not be null");
        graphQueries.add(stringMatcher(key));
    }

    public void dictionary0(double key) {
        graphQueries.add(floatMatcher(key));
    }

    public void dictionary0(long key) {
        graphQueries.add(intMatcher(key));
    }

    public void nullOrType0(TypeSpec typeSpec) {
        graphQueries.add(new TypeMatcherGraphQuery(typeSpec, false));
    }

    public void requireType0(TypeSpec typeSpec) {
        graphQueries.add(new TypeMatcherGraphQuery(typeSpec, true));
    }

}
