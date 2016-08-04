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

import com.noctarius.borabora.impl.query.stages.DictionaryLookupQueryStage;
import com.noctarius.borabora.impl.query.stages.SequenceIndexQueryStage;
import com.noctarius.borabora.impl.query.stages.SequenceMatcherQueryStage;
import com.noctarius.borabora.impl.query.stages.TypeMatcherQueryStage;
import com.noctarius.borabora.spi.QueryBuilderTreeNode;
import com.noctarius.borabora.spi.SelectStatementStrategy;
import com.noctarius.borabora.spi.TypeSpec;

import java.util.Objects;
import java.util.function.Predicate;

abstract class AbstractQueryBuilder {

    protected final QueryBuilderTreeNode parentTreeNode;
    protected final SelectStatementStrategy selectStatementStrategy;

    protected QueryBuilderTreeNode currentTreeNode;

    protected AbstractQueryBuilder(QueryBuilderTreeNode parentTreeNode, SelectStatementStrategy selectStatementStrategy) {
        this.parentTreeNode = parentTreeNode;
        this.currentTreeNode = parentTreeNode;

        this.selectStatementStrategy = selectStatementStrategy;
    }

    public void sequenceMatch0(Predicate<Value> predicate) {
        Tracer.traceInfo("AbstractQueryBuilder#sequenceMatch0", this);
        Objects.requireNonNull(predicate, "predicate must not be null");
        currentTreeNode = currentTreeNode.pushChild(new SequenceMatcherQueryStage(predicate));
    }

    public void sequence0(long index) {
        Tracer.traceInfo("AbstractQueryBuilder#sequence0", this);
        if (index < 0) {
            throw new IllegalArgumentException("index must not be negative");
        }
        currentTreeNode = currentTreeNode.pushChild(new SequenceIndexQueryStage(index));
    }

    public void dictionary0(Predicate<Value> predicate) {
        Tracer.traceInfo("AbstractQueryBuilder#dictionary0-predicate", this);
        Objects.requireNonNull(predicate, "predicate must not be null");
        currentTreeNode = currentTreeNode.pushChild(DictionaryLookupQueryStage.predicateMatcher(predicate));
    }

    public void dictionary0(String key) {
        Tracer.traceInfo("AbstractQueryBuilder#dictionary0-string", this);
        Objects.requireNonNull(key, "key must not be null");
        currentTreeNode = currentTreeNode.pushChild(DictionaryLookupQueryStage.stringMatcher(key));
    }

    public void dictionary0(double key) {
        Tracer.traceInfo("AbstractQueryBuilder#dictionary0-double", this);
        currentTreeNode = currentTreeNode.pushChild(DictionaryLookupQueryStage.floatMatcher(key));
    }

    public void dictionary0(long key) {
        Tracer.traceInfo("AbstractQueryBuilder#dictionary0-long", this);
        currentTreeNode = currentTreeNode.pushChild(DictionaryLookupQueryStage.intMatcher(key));
    }

    public void nullOrType0(TypeSpec typeSpec) {
        Tracer.traceInfo("AbstractQueryBuilder#nullOrType0", this);
        currentTreeNode = currentTreeNode.pushChild(new TypeMatcherQueryStage(typeSpec, false));
    }

    public void requireType0(TypeSpec typeSpec) {
        Tracer.traceInfo("AbstractQueryBuilder#requireType0", this);
        currentTreeNode = currentTreeNode.pushChild(new TypeMatcherQueryStage(typeSpec, true));
    }

}
