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

import com.noctarius.borabora.builder.GraphBuilder;
import com.noctarius.borabora.builder.StreamQueryBuilder;
import com.noctarius.borabora.spi.TypeSpecs;
import com.noctarius.borabora.spi.query.BinarySelectStatementStrategy;
import com.noctarius.borabora.spi.query.ObjectSelectStatementStrategy;
import com.noctarius.borabora.spi.query.SelectStatementStrategy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.noctarius.borabora.Predicates.matchString;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class SelectStatementTestCase
        extends AbstractTestCase {

    @Parameterized.Parameters(name = "{1}")
    public static Iterable<Object[]> parameters() {
        return Arrays.asList( //
                new Object[][]{ //
                                {BinarySelectStatementStrategy.INSTANCE, "BinarySelectStatementStrategy"}, //
                                {ObjectSelectStatementStrategy.INSTANCE, "ObjectSelectStatementStrategy"} //
                });
    }

    private final SelectStatementStrategy selectStatementStrategy;

    public SelectStatementTestCase(SelectStatementStrategy selectStatementStrategy, String typename) {
        this.selectStatementStrategy = selectStatementStrategy;
    }

    @Test
    public void test_select_dictionary_wrong_type() {
        executeExercise( //
                sgb -> sgb.putNumber(1), //
                () -> "(a: #->?dictionary{'foo'})", //
                gqb -> gqb.asDictionary().putEntry("a") //
                          .stream(0).nullOrType(TypeSpecs.Dictionary).dictionary(matchString("foo")) //
                          .endEntry().endDictionary(), //
                v -> assertEquals(ValueTypes.Null, v.dictionary().get(matchString("a")).valueType()) //
        );
    }

    @Test
    public void test_select_dictionary_non_existing_lookup() {
        executeExercise( //
                sgb -> sgb.putDictionary(1).putEntry().putString("foo").putString("test").endEntry().endDictionary(), //
                () -> "(a: #{'bar'})", //
                gqb -> gqb.asDictionary().putEntry("a") //
                          .stream(0).dictionary(matchString("bar")) //
                          .endEntry().endDictionary(), //
                v -> assertEquals(ValueTypes.Null, v.dictionary().get(matchString("a")).valueType()) //
        );
    }

    @Test
    public void test_select_sequence_wrong_type() {
        executeExercise( //
                sgb -> sgb.putNumber(1), //
                () -> "(#0->?dictionary{'foo'})", //
                gqb -> gqb.asSequence().putEntry() //
                          .stream(0).nullOrType(TypeSpecs.Dictionary).dictionary(matchString("foo")) //
                          .endEntry().endSequence(), //
                v -> assertEquals(ValueTypes.Null, v.sequence().get(0).valueType()) //
        );
    }

    @Test
    public void test_select_sequence_non_existing_lookup() {
        executeExercise( //
                sgb -> sgb.putDictionary(1).putEntry().putString("foo").putString("test").endEntry().endDictionary(), //
                () -> "(#{'bar'})", //
                gqb -> gqb.asSequence().putEntry() //
                          .stream(0).dictionary(matchString("bar")) //
                          .endEntry().endSequence(), //
                v -> assertEquals(ValueTypes.Null, v.sequence().get(0).valueType()) //
        );
    }

    @Test
    public void test_select_first_stream_element_as_dictionary() {
        executeExercise( //
                sgb -> sgb.putNumber(1), //
                () -> "(a: #)", //
                gqb -> gqb.asDictionary().putEntry("a").stream(0).endEntry().endDictionary(), //
                v -> assertEqualsNumber(1, v.dictionary().get(matchString("a")).number()) //
        );
    }

    @Test
    public void test_select_first_stream_element_as_sequence() {
        executeExercise( //
                sgb -> sgb.putNumber(1), //
                () -> "(#)", //
                gqb -> gqb.asSequence().putEntry().stream(0).endEntry().endSequence(), //
                v -> assertEqualsNumber(1, v.sequence().get(0).number()) //
        );
    }

    @Test
    public void test_select_second_stream_element_as_dictionary() {
        executeExercise( //
                sgb -> sgb.putNumber(1).putNumber(2), //
                () -> "(a: #1)", //
                gqb -> gqb.asDictionary().putEntry("a").stream(1).endEntry().endDictionary(), //
                v -> assertEqualsNumber(2, v.dictionary().get(matchString("a")).number()) //
        );
    }

    @Test
    public void test_select_multiple_stream_elements_as_dictionary() {
        executeExercise( //
                sgb -> sgb.putNumber(1).putNumber(2).putNumber(3), //
                () -> "(a: #1, 2: #0, 3.0: #2)", //
                gqb -> gqb.asDictionary()

                          .putEntry("a").stream(1).endEntry()

                          .putEntry(2).stream(0).endEntry()

                          .putEntry(3.0).stream(2).endEntry().endDictionary(), //
                v -> {
                    assertEqualsNumber(2, v.dictionary().get(matchString("a")).number());
                    assertEqualsNumber(1, v.dictionary().get(Predicates.matchInt(2)).number());
                    assertEqualsNumber(3, v.dictionary().get(Predicates.matchFloat(3.0)).number());
                } //
        );
    }

    @Test
    public void test_select_multiple_stream_elements_as_dictionary_in_subdictionary() {
        executeExercise( //
                sgb -> sgb.putNumber(1).putNumber(2), //
                () -> "(a: (c: #1, d: #0))", //
                gqb -> gqb.asDictionary().putEntry("a").asDictionary()

                          .putEntry("c").stream(1).endEntry()

                          .putEntry("d").stream(0).endEntry().endDictionary()

                          .endEntry().endDictionary(), //
                v -> {
                    Value v2 = v.dictionary().get(matchString("a"));
                    assertEqualsNumber(2, v2.dictionary().get(matchString("c")).number());
                    assertEqualsNumber(1, v2.dictionary().get(matchString("d")).number());
                } //
        );
    }

    @Test
    public void test_select_multiple_stream_elements_as_dictionary_in_subsequence() {
        executeExercise( //
                sgb -> sgb.putNumber(1).putNumber(2), //
                () -> "(a: (#1, #0))", //
                gqb -> gqb.asDictionary().putEntry("a").asSequence()

                          .putEntry().stream(1).endEntry()

                          .putEntry().stream(0).endEntry().endSequence()

                          .endEntry().endDictionary(), //
                v -> {
                    Value v2 = v.dictionary().get(matchString("a"));
                    assertEqualsNumber(2, v2.sequence().get(0).number());
                    assertEqualsNumber(1, v2.sequence().get(1).number());
                } //
        );
    }

    @Test
    public void test_select_second_stream_element_as_sequence() {
        executeExercise( //
                sgb -> sgb.putNumber(1).putNumber(2), //
                () -> "(#1)", //
                gqb -> gqb.asSequence().putEntry().stream(1).endEntry().endSequence(), //
                v -> assertEqualsNumber(2, v.sequence().get(0).number()) //
        );
    }

    @Test
    public void test_select_multiple_stream_elements_as_sequence() {
        executeExercise( //
                sgb -> sgb.putNumber(1).putNumber(2), //
                () -> "(#1, #0)", //
                gqb -> gqb.asSequence()

                          .putEntry().stream(1).endEntry()

                          .putEntry().stream(0).endEntry().endSequence(), //
                v -> {
                    assertEqualsNumber(2, v.sequence().get(0).number());
                    assertEqualsNumber(1, v.sequence().get(1).number());
                } //
        );
    }

    @Test
    public void test_select_multiple_stream_elements_as_sequence_in_subdictionary() {
        executeExercise( //
                sgb -> sgb.putNumber(1).putNumber(2), //
                () -> "((c: #1, d: #0))", //
                gqb -> gqb.asSequence().putEntry().asDictionary()

                          .putEntry("c").stream(1).endEntry()

                          .putEntry("d").stream(0).endEntry().endDictionary()

                          .endEntry().endSequence(), //
                v -> {
                    Value v2 = v.sequence().get(0);
                    assertEqualsNumber(2, v2.dictionary().get(matchString("c")).number());
                    assertEqualsNumber(1, v2.dictionary().get(matchString("d")).number());
                } //
        );
    }

    @Test
    public void test_select_multiple_stream_elements_as_sequence_in_subsequence() {
        executeExercise( //
                sgb -> sgb.putNumber(1).putNumber(2), //
                () -> "((#1, #0))", //
                gqb -> gqb.asSequence().putEntry().asSequence()

                          .putEntry().stream(1).endEntry()

                          .putEntry().stream(0).endEntry().endSequence()

                          .endEntry().endSequence(), //
                v -> {
                    Value v2 = v.sequence().get(0);
                    assertEqualsNumber(2, v2.sequence().get(0).number());
                    assertEqualsNumber(1, v2.sequence().get(1).number());
                } //
        );
    }

    @Test
    public void test_select_dictionary_predicate() {
        executeExercise( //
                sgb -> sgb.putDictionary(1).putEntry().putString("foo").putString("bar").endEntry().endDictionary(),
                () -> "(a: #{'foo'})", //
                gqb -> gqb.asDictionary().putEntry("a").stream(0).dictionary(matchString("foo")).endEntry().endDictionary(),
                v -> {
                    Value v2 = v.dictionary().get(matchString("a"));
                    assertEquals("bar", v2.string());

                } //
        );
    }

    @Test
    public void test_select_dictionary_string() {
        executeExercise( //
                sgb -> sgb.putDictionary(1).putEntry().putString("foo").putString("bar").endEntry().endDictionary(), //
                () -> "(a: #{'foo'})", //
                gqb -> gqb.asDictionary().putEntry("a").stream(0).dictionary("foo").endEntry().endDictionary(), //
                v -> {
                    Value v2 = v.dictionary().get(matchString("a"));
                    assertEquals("bar", v2.string());

                } //
        );
    }

    @Test
    public void test_select_dictionary_double() {
        executeExercise( //
                sgb -> sgb.putDictionary(1).putEntry().putNumber(12.d).putString("bar").endEntry().endDictionary(), //
                () -> "(a: #{12.0})", //
                gqb -> gqb.asDictionary().putEntry("a").stream(0).dictionary(12.d).endEntry().endDictionary(), //
                v -> {
                    Value v2 = v.dictionary().get(matchString("a"));
                    assertEquals("bar", v2.string());

                } //
        );
    }

    @Test
    public void test_select_dictionary_long() {
        executeExercise( //
                sgb -> sgb.putDictionary(1).putEntry().putNumber(12l).putString("bar").endEntry().endDictionary(), //
                () -> "(a: #{12})", //
                gqb -> gqb.asDictionary().putEntry("a").stream(0).dictionary(12l).endEntry().endDictionary(), //
                v -> {
                    Value v2 = v.dictionary().get(matchString("a"));
                    assertEquals("bar", v2.string());

                } //
        );
    }

    private void executeExercise(Consumer<GraphBuilder> streamProducer, Supplier<String> textQueryProducer,
                                 Consumer<StreamQueryBuilder> graphQueryConfigurator, Consumer<Value> assertionTest) {

        byte[] data = createDataSource(streamProducer);

        Input input = Input.fromByteArray(data);
        Parser parser = Parser.newBuilder().withSelectStatementStrategy(selectStatementStrategy).build();

        StreamQueryBuilder graphQueryBuilder = Query.newBuilder(selectStatementStrategy);
        graphQueryConfigurator.accept(graphQueryBuilder);

        Query textQuery = parser.prepareQuery(textQueryProducer.get());
        Query query = graphQueryBuilder.build();

        assertionTest.accept(parser.read(input, textQuery));
        assertionTest.accept(parser.read(input, query));
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
