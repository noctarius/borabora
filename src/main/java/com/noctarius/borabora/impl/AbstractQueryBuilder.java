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
package com.noctarius.borabora.impl;

import com.noctarius.borabora.Value;
import com.noctarius.borabora.impl.query.stages.DictionaryLookupQueryStage;
import com.noctarius.borabora.impl.query.stages.SequenceIndexQueryStage;
import com.noctarius.borabora.impl.query.stages.SequenceMatcherQueryStage;
import com.noctarius.borabora.impl.query.stages.TypeMatcherQueryStage;
import com.noctarius.borabora.spi.query.TypeSpec;
import com.noctarius.borabora.spi.query.pipeline.QueryBuilderNode;

import java.util.Objects;
import java.util.function.Predicate;

abstract class AbstractQueryBuilder {

    protected final QueryBuilderNode parentTreeNode;

    protected QueryBuilderNode currentTreeNode;

    protected AbstractQueryBuilder(QueryBuilderNode parentTreeNode) {
        Objects.requireNonNull(parentTreeNode, "parentTreeNode must not be null");
        this.parentTreeNode = parentTreeNode;
        this.currentTreeNode = parentTreeNode;
    }

    protected void sequenceMatch0(Predicate<Value> predicate) {
        Tracer.traceInfo("AbstractQueryBuilder#sequenceMatch0", this);
        Objects.requireNonNull(predicate, "predicate must not be null");
        currentTreeNode = currentTreeNode.pushChild(new SequenceMatcherQueryStage(predicate));
    }

    protected void sequence0(long index) {
        Tracer.traceInfo("AbstractQueryBuilder#sequence0", this);
        if (index < 0) {
            throw new IllegalArgumentException("index must not be negative");
        }
        currentTreeNode = currentTreeNode.pushChild(new SequenceIndexQueryStage(index));
    }

    protected void dictionary0(Predicate<Value> predicate) {
        Tracer.traceInfo("AbstractQueryBuilder#dictionary0-predicate", this);
        Objects.requireNonNull(predicate, "predicate must not be null");
        currentTreeNode = currentTreeNode.pushChild(DictionaryLookupQueryStage.predicateMatcher(predicate));
    }

    protected void dictionary0(String key) {
        Tracer.traceInfo("AbstractQueryBuilder#dictionary0-string", this);
        Objects.requireNonNull(key, "key must not be null");
        currentTreeNode = currentTreeNode.pushChild(DictionaryLookupQueryStage.stringMatcher(key));
    }

    protected void dictionary0(double key) {
        Tracer.traceInfo("AbstractQueryBuilder#dictionary0-double", this);
        currentTreeNode = currentTreeNode.pushChild(DictionaryLookupQueryStage.floatMatcher(key));
    }

    protected void dictionary0(long key) {
        Tracer.traceInfo("AbstractQueryBuilder#dictionary0-long", this);
        currentTreeNode = currentTreeNode.pushChild(DictionaryLookupQueryStage.intMatcher(key));
    }

    protected void nullOrType0(TypeSpec typeSpec) {
        Tracer.traceInfo("AbstractQueryBuilder#nullOrType0", this);
        Objects.requireNonNull(typeSpec, "typeSpec must not be null");
        currentTreeNode = currentTreeNode.pushChild(new TypeMatcherQueryStage(typeSpec, false));
    }

    protected void requireType0(TypeSpec typeSpec) {
        Tracer.traceInfo("AbstractQueryBuilder#requireType0", this);
        Objects.requireNonNull(typeSpec, "typeSpec must not be null");
        currentTreeNode = currentTreeNode.pushChild(new TypeMatcherQueryStage(typeSpec, true));
    }

}
