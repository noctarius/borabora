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

import com.noctarius.borabora.Query;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.builder.encoder.ValueBuilder;
import com.noctarius.borabora.builder.query.StreamQueryBuilder;
import com.noctarius.borabora.impl.transformation.QueryMutator;
import com.noctarius.borabora.impl.transformation.SkipTransformation;
import com.noctarius.borabora.spi.query.TypeSpec;
import com.noctarius.borabora.spi.query.optimizer.QueryOptimizerStrategy;
import com.noctarius.borabora.spi.query.pipeline.PipelineStageFactory;
import com.noctarius.borabora.spi.query.pipeline.QueryPipelineFactory;
import com.noctarius.borabora.spi.transformation.ChainingMutationBuilder;
import com.noctarius.borabora.spi.transformation.MutationQueryBuilder;
import com.noctarius.borabora.spi.transformation.Mutator;
import com.noctarius.borabora.spi.transformation.StreamMutationQueryBuilder;
import com.noctarius.borabora.spi.transformation.Transformation;
import com.noctarius.borabora.spi.transformation.TransformationBuilder;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

class MutationQueryBuilderImpl
        implements StreamMutationQueryBuilder {

    private final ChainingMutationBuilder parentBuilder;
    private final StreamQueryBuilder queryBuilder;
    private final List<Mutator> mutators;

    MutationQueryBuilderImpl(ChainingMutationBuilder parentBuilder, List<Mutator> mutators,
                             QueryOptimizerStrategy queryOptimizerStrategy, PipelineStageFactory pipelineStageFactory,
                             QueryPipelineFactory queryPipelineFactory) {

        Objects.requireNonNull(queryOptimizerStrategy, "queryOptimizerStrategy must not be null");
        Objects.requireNonNull(pipelineStageFactory, "pipelineStageFactory must not be null");
        Objects.requireNonNull(queryPipelineFactory, "queryPipelineFactory must not be null");
        this.queryBuilder = new QueryBuilderImpl(queryOptimizerStrategy, pipelineStageFactory, queryPipelineFactory);
        this.parentBuilder = parentBuilder;
        this.mutators = mutators;
    }

    @Override
    public ChainingMutationBuilder skip() {
        Query query = queryBuilder.build();
        mutators.add(new QueryMutator<>(query, SkipTransformation.SKIP));
        return parentBuilder;
    }

    @Override
    public ValueBuilder<TransformationBuilder> replaceWith() {
        Query query = queryBuilder.build();
        return TransformationBuilderImpl.newTransformationBuilder(createMutator(query), parentBuilder, mutators);
    }

    @Override
    public ChainingMutationBuilder transformWith(Transformation transformation) {
        Query query = queryBuilder.build();
        mutators.add(new QueryMutator<>(query, transformation));
        return parentBuilder;
    }

    @Override
    public MutationQueryBuilder sequence(long index) {
        queryBuilder.sequence(index);
        return this;
    }

    @Override
    public MutationQueryBuilder sequenceMatch(Predicate<Value> predicate) {
        queryBuilder.sequenceMatch(predicate);
        return this;
    }

    @Override
    public MutationQueryBuilder dictionary(Predicate<Value> predicate) {
        queryBuilder.dictionary(predicate);
        return this;
    }

    @Override
    public MutationQueryBuilder dictionary(String key) {
        queryBuilder.dictionary(key);
        return this;
    }

    @Override
    public MutationQueryBuilder dictionary(double key) {
        queryBuilder.dictionary(key);
        return this;
    }

    @Override
    public MutationQueryBuilder dictionary(long key) {
        queryBuilder.dictionary(key);
        return this;
    }

    @Override
    public MutationQueryBuilder nullOrType(TypeSpec typeSpec) {
        queryBuilder.nullOrType(typeSpec);
        return this;
    }

    @Override
    public MutationQueryBuilder requireType(TypeSpec typeSpec) {
        queryBuilder.requireType(typeSpec);
        return this;
    }

    @Override
    public MutationQueryBuilder stream(long streamIndex) {
        queryBuilder.stream(streamIndex);
        return this;
    }

    @Override
    public MutationQueryBuilder multiStream() {
        queryBuilder.multiStream();
        return this;
    }

    private Function<Transformation, Mutator> createMutator(Query query) {
        return t -> new QueryMutator<>(query, t);
    }
}
