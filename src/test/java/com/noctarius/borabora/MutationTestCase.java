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

import com.noctarius.borabora.builder.encoder.GraphBuilder;
import com.noctarius.borabora.spi.transformation.ChainingMutationBuilder;
import com.noctarius.borabora.spi.transformation.Mutation;
import com.noctarius.borabora.spi.transformation.MutationBuilder;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.function.Consumer;

import static com.noctarius.borabora.Predicates.matchInt;

public class MutationTestCase
        extends AbstractTestCase {

    @Test
    public void simple_query_replace() {
        executeExercise(

                gb -> gb.putByteString("Foo"),

                mb -> mb.withQuery().replaceWith().putNumber(12).endReplace(),

                v -> assertEqualsNumber(12, v.number()));
    }

    @Test
    public void simple_predicate_replace() {
        executeExercise(

                gb -> gb.putByteString("Foo"),

                mb -> mb.withPredicate(v -> v.valueType().matches(ValueTypes.String)) //
                        .replaceWith().putNumber(12).endReplace(),

                v -> assertEqualsNumber(12, v.number()));
    }

    @Test
    public void sequence_query_replace() {
        executeExercise(

                gb -> gb.putSequence().putNumber(12).putNumber(24).endSequence(),

                mb -> mb.withQuery().sequenceMatch(matchInt(24)).replaceWith().putNumber(36).endReplace(),

                v -> {
                    Sequence sequence = v.sequence();
                    assertEqualsNumber(12, sequence.get(0).number());
                    assertEqualsNumber(36, sequence.get(1).number());
                });
    }

    @Test
    public void sequence_predicate_replace() {
        executeExercise(

                gb -> gb.putSequence().putNumber(12).putNumber(24).endSequence(),

                mb -> mb.withPredicate(v -> v.valueType().matches(ValueTypes.Int) && v.number().intValue() == 24) //
                        .replaceWith().putNumber(36).endReplace(),

                v -> {
                    Sequence sequence = v.sequence();
                    assertEqualsNumber(12, sequence.get(0).number());
                    assertEqualsNumber(36, sequence.get(1).number());
                });
    }

    private void executeExercise(Consumer<GraphBuilder> streamProducer, Consumer<MutationBuilder> mutationProducer,
                                 Consumer<Value> assertionTest) {

        byte[] data = createDataSource(streamProducer);

        Input input = Input.fromByteArray(data);
        Parser parser = Parser.newBuilder().build();

        MutationBuilder mutationBuilder = parser.newMutationBuilder();
        mutationProducer.accept(mutationBuilder);
        Mutation mutation = ((ChainingMutationBuilder) mutationBuilder).build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toOutputStream(baos);
        mutation.mutate(input, output);

        Input result = Input.fromByteArray(baos.toByteArray());
        assertionTest.accept(parser.read(result, parser.newQueryBuilder().build()));
    }

    private static byte[] createDataSource(Consumer<GraphBuilder> consumer) {
        Writer writer = Writer.newBuilder().build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GraphBuilder graphBuilder = writer.newGraphBuilder(Output.toOutputStream(baos));

        consumer.accept(graphBuilder);
        graphBuilder.finishStream();

        return baos.toByteArray();
    }
}
