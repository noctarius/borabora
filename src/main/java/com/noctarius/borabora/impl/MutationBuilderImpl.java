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

import com.noctarius.borabora.Input;
import com.noctarius.borabora.Output;
import com.noctarius.borabora.Parser;
import com.noctarius.borabora.Query;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.ValueTypes;
import com.noctarius.borabora.Writer;
import com.noctarius.borabora.builder.encoder.DictionaryBuilder;
import com.noctarius.borabora.builder.encoder.DictionaryEntryBuilder;
import com.noctarius.borabora.builder.encoder.GraphBuilder;
import com.noctarius.borabora.builder.encoder.SequenceBuilder;
import com.noctarius.borabora.builder.encoder.ValueBuilder;
import com.noctarius.borabora.spi.query.optimizer.QueryOptimizer;
import com.noctarius.borabora.spi.query.optimizer.QueryOptimizerStrategy;
import com.noctarius.borabora.spi.query.optimizer.QueryOptimizerStrategyFactory;
import com.noctarius.borabora.spi.query.pipeline.PipelineStageFactory;
import com.noctarius.borabora.spi.query.pipeline.QueryPipelineFactory;
import com.noctarius.borabora.spi.transformation.ChainingMutationBuilder;
import com.noctarius.borabora.spi.transformation.Mutation;
import com.noctarius.borabora.spi.transformation.MutationBuilder;
import com.noctarius.borabora.spi.transformation.MutationQueryBuilder;
import com.noctarius.borabora.spi.transformation.Mutator;
import com.noctarius.borabora.spi.transformation.MutatorBuilder;
import com.noctarius.borabora.spi.transformation.StreamMutationQueryBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

class MutationBuilderImpl
        implements ChainingMutationBuilder {

    private final QueryOptimizerStrategyFactory queryOptimizerStrategyFactory;
    private final PipelineStageFactory pipelineStageFactory;
    private final QueryPipelineFactory queryPipelineFactory;
    private final List<QueryOptimizer> queryOptimizers;
    private final Parser parser;

    private final List<Mutator> mutators = new ArrayList<>();

    MutationBuilderImpl(Parser parser, QueryOptimizerStrategyFactory queryOptimizerStrategyFactory,
                        List<QueryOptimizer> queryOptimizers, PipelineStageFactory pipelineStageFactory,
                        QueryPipelineFactory queryPipelineFactory) {

        this.parser = parser;
        this.queryOptimizerStrategyFactory = queryOptimizerStrategyFactory;
        this.queryOptimizers = queryOptimizers;
        this.pipelineStageFactory = pipelineStageFactory;
        this.queryPipelineFactory = queryPipelineFactory;
    }

    @Override
    public StreamMutationQueryBuilder withQuery() {
        QueryOptimizerStrategy queryOptimizerStrategy = queryOptimizerStrategyFactory.newQueryOptimizerStrategy(queryOptimizers);
        return new MutationQueryBuilderImpl(this, mutators, queryOptimizerStrategy, pipelineStageFactory, queryPipelineFactory);
    }

    @Override
    public MutatorBuilder withQuery(Query query) {
        return new QueryMutatorBuilder(this, query, mutators);
    }

    @Override
    public MutatorBuilder withPredicate(Predicate<Value> predicate) {
        return new PredicateMutatorBuilder(this, predicate, mutators);
    }

    @Override
    public Mutation build() {
        return new MutationImpl(parser, mutators);
    }

    @Override
    public MutationBuilder and() {
        return this;
    }

    private static class MutationImpl
            implements Mutation {

        private final Mutator[] mutators;
        private final Parser parser;

        private MutationImpl(Parser parser, List<Mutator> mutators) {
            this.mutators = mutators.toArray(new Mutator[0]);
            this.parser = parser;
        }

        @Override
        public void mutate(Input input, Output output) {
            Map<Value, Mutator> mutatorMapping = new HashMap<>();
            for (Mutator<?> mutator : mutators) { // Java Puzzler
                mutator.matchValues(parser, input).forEach(v -> mutatorMapping.put(v, mutator));
            }

            Writer writer = Writer.newBuilder().build();
            GraphBuilder graphBuilder = writer.newGraphBuilder(output);
            parser.read(input, parser.newQueryBuilder().multiStream().build(),
                    value -> handleValue(value, graphBuilder, mutatorMapping));

            graphBuilder.finishStream();
        }

        private <T extends ValueBuilder<T>> void handleValue(Value value, T builder, Map<Value, Mutator> mutators) {
            Mutator mutator = mutators.get(value);
            if (mutator != null) {
                mutator.mutate(value, builder);
            } else if (value.valueType() == ValueTypes.Dictionary) {
                DictionaryBuilder<T> dictionaryBuilder = builder.putDictionary();
                value.dictionary().forEach(entry -> putDictionaryEntry(entry, dictionaryBuilder, mutators));
                dictionaryBuilder.endDictionary();
            } else if (value.valueType() == ValueTypes.Sequence) {
                SequenceBuilder<T> sequenceBuilder = builder.putSequence();
                value.sequence().forEach(v -> handleValue(v, sequenceBuilder, mutators));
                sequenceBuilder.endSequence();
            } else {
                builder.putValue(value.byValueType());
            }
        }

        private void putDictionaryEntry(Map.Entry<Value, Value> entry, DictionaryBuilder dictionaryBuilder,
                                        Map<Value, Mutator> mutators) {

            Value key = entry.getKey();
            Value value = entry.getValue();

            DictionaryEntryBuilder dictionaryEntryBuilder = dictionaryBuilder.putEntry();
            handleValue(key, dictionaryEntryBuilder, mutators);
            handleValue(value, dictionaryEntryBuilder, mutators);
            dictionaryEntryBuilder.endEntry();
        }
    }

}
