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

import com.noctarius.borabora.spi.SelectStatementStrategy;
import com.noctarius.borabora.spi.TypeSpec;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static com.noctarius.borabora.DictionaryQuery.floatMatcher;
import static com.noctarius.borabora.DictionaryQuery.intMatcher;
import static com.noctarius.borabora.DictionaryQuery.predicateMatcher;
import static com.noctarius.borabora.DictionaryQuery.stringMatcher;

abstract class AbstractGraphQueryBuilder {

    protected final List<Query> graphQueries;
    protected final SelectStatementStrategy selectStatementStrategy;

    protected AbstractGraphQueryBuilder(List<Query> graphQueries, SelectStatementStrategy selectStatementStrategy) {
        this.graphQueries = graphQueries;
        this.selectStatementStrategy = selectStatementStrategy;
    }

    public void sequence0(long index) {
        graphQueries.add(new SequenceQuery(index));
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
        graphQueries.add(new TypeMatcherQuery(typeSpec, false));
    }

    public void requireType0(TypeSpec typeSpec) {
        graphQueries.add(new TypeMatcherQuery(typeSpec, true));
    }

}
