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

import com.noctarius.borabora.builder.ParserBuilder;
import com.noctarius.borabora.builder.query.QueryBuilder;
import com.noctarius.borabora.builder.query.StreamQueryBuilder;
import com.noctarius.borabora.impl.ParserBuilderImpl;
import com.noctarius.borabora.spi.transformation.MutationBuilder;

import java.util.function.Consumer;

/**
 * <p>The <tt>Parser</tt> class is the entry point for all parsing related activities. This includes element
 * query, as well as any kind of CBOR encoded stream extraction. The parser is implemented as a so-called
 * skip-scan parser, reading the type and length of an element and skipping right over when it is not
 * needed and further necessary. Returned {@link Value} instances are fully lazily read whenever the
 * actual represented value is requested.</p>
 * <p><tt>Parser</tt> implementation is thread-safe and stateless, therefore a single parser instance
 * can be shared and used concurrently by multiple threads.</p>
 * <p>A common example how to query an element from the CBOR stream is shown in the following snippet:</p>
 * <pre>
 *     Parser parser = Parser.newBuilder().build();
 *     Input input = Input.fromByteArray( getByteArray() );
 *     Query query = parser.newQueryBuilder().stream( 0 ).sequence( 10 ).build();
 *     Value value = parser.read( input, query );
 * </pre>
 *
 * @see Input
 * @see Query
 */
public interface Parser {

    /**
     * <p>Searches and reads a {@link Value} from the given {@link Input} instance, representing the
     * the CBOR encoded input stream, and tries to search and match a single element against the given
     * {@link Query} instance.</p>
     * <p>If a matching value is found it will be returned, otherwise {@link Value#NULL_VALUE} is
     * returned to represent a non-existing value.</p>
     * <p>The method is completely thread-safe and the same parser instance can be used concurrently
     * from multiple threads.</p>
     *
     * @param input the input stream instance to parse
     * @param query the query instance to execute against the input stream
     * @return the first, or only matched value
     * @throws IllegalStateException is thrown whenever an illegal state has occurred while parsing
     * @throws WrongTypeException    is thrown whenever an unexpected type was found while parsing
     */
    Value read(Input input, Query query);

    /**
     * <p>Searches and reads a {@link Value} from the given {@link Input} instance, representing the
     * the CBOR encoded input stream, and tries to search and match a single element against the given
     * query string, which will be first prepared / transformed into a {@link Query} instance and
     * afterwards used to match the elements.</p>
     * <p>It is generally recommended to either use the Query-API to generate the queries or to
     * use {@link #prepareQuery(String)} once and store the {@link Query} instance for reuse.</p>
     * <p>If a matching value is found it will be returned, otherwise {@link Value#NULL_VALUE} is
     * returned to represent a non-existing value.</p>
     * <p>The method is completely thread-safe and the same parser instance can be used concurrently
     * from multiple threads.</p>
     *
     * @param input the input stream instance to parse
     * @param query the query string to transform and execute against the input stream
     * @return the first, or only matched value
     * @throws IllegalStateException is thrown whenever an illegal state has occurred while parsing
     * @throws WrongTypeException    is thrown whenever an unexpected type was found while parsing
     * @throws QueryParserException  is thrown whenever an error occurred while parsing the actual
     *                               query string into a {@link Query} instance
     */
    Value read(Input input, String query);

    /**
     * <p>Reads a {@link Value} from the given {@link Input} instance, representing the
     * the CBOR encoded input stream and returns the {@link Value} instance at the given offset.
     * The offset is only lightly validated by trying to read {@link MajorType} and {@link ValueType}
     * but does not analyze it any further. That means it is possible to see further errors when
     * try to access the actual content of the returned value instance.</p>
     * <p>The method is completely thread-safe and the same parser instance can be used concurrently
     * from multiple threads.</p>
     *
     * @param input  the input stream instance to parse
     * @param offset the offset of the request element inside the CBOR stream
     * @return the first, or only matched value
     */
    Value read(Input input, long offset);

    /**
     * <p>Searches and reads {@link Value}s from the given {@link Input} instance, representing the
     * the CBOR encoded input stream, and tries to search and match elements against the given
     * {@link Query} instance.</p>
     * <p>If a matching value is found the {@link Value} instance is passed to the <tt>consumer</tt>
     * for further usage by the customer application.</p>
     * <p>The method is completely thread-safe and the same parser instance can be used concurrently
     * from multiple threads.</p>
     *
     * @param input    the input stream instance to parse
     * @param query    the query instance to execute against the input stream
     * @param consumer the consumer to receive all matching values
     * @throws IllegalStateException is thrown whenever an illegal state has occurred while parsing
     * @throws WrongTypeException    is thrown whenever an unexpected type was found while parsing
     */
    void read(Input input, Query query, Consumer<Value> consumer);

    /**
     * <p>Searches and reads {@link Value}s from the given {@link Input} instance, representing the
     * the CBOR encoded input stream, and tries to search and match elements against the given
     * query string, which will be first prepared / transformed into a {@link Query} instance and
     * afterwards used to match the elements.</p>
     * <p>It is generally recommended to either use the Query-API to generate the queries or to
     * use {@link #prepareQuery(String)} once and store the {@link Query} instance for reuse.</p>
     * <p>If a matching value is found the {@link Value} instance is passed to the <tt>consumer</tt>
     * for further usage by the customer application.</p>
     * <p>The method is completely thread-safe and the same parser instance can be used concurrently
     * from multiple threads.</p>
     *
     * @param input    the input stream instance to parse
     * @param query    the query string to transform and execute against the input stream
     * @param consumer the consumer to receive all matching values
     * @throws IllegalStateException is thrown whenever an illegal state has occurred while parsing
     * @throws WrongTypeException    is thrown whenever an unexpected type was found while parsing
     * @throws QueryParserException  is thrown whenever an error occurred while parsing the actual
     *                               query string into a {@link Query} instance
     */
    void read(Input input, String query, Consumer<Value> consumer);

    /**
     * <p>Searches and extracts a {@link Value} from the given {@link Input} instance, representing the
     * the CBOR encoded input stream, and tries to search and match a single element against the given
     * {@link Query} instance.</p>
     * <p>If a matching value is found it will be returned as a CBOR encoded byte-array, otherwise an
     * empty (zero-length) byte-array.</p>
     * <p>The method is completely thread-safe and the same parser instance can be used concurrently
     * from multiple threads.</p>
     *
     * @param input the input stream instance to parse
     * @param query the query instance to execute against the input stream
     * @return the extracted byte-array containing the CBOR encoded object
     * @throws IllegalStateException is thrown whenever an illegal state has occurred while parsing
     * @throws WrongTypeException    is thrown whenever an unexpected type was found while parsing
     */
    byte[] extract(Input input, Query query);

    /**
     * <p>Searches and extracts a {@link Value} from the given {@link Input} instance, representing the
     * the CBOR encoded input stream, and tries to search and match a single element against the given
     * query string, which will be first prepared / transformed into a {@link Query} instance and
     * afterwards used to match the elements.</p>
     * <p>It is generally recommended to either use the Query-API to generate the queries or to
     * use {@link #prepareQuery(String)} once and store the {@link Query} instance for reuse.</p>
     * <p>If a matching value is found it will be returned as a CBOR encoded byte-array, otherwise an
     * empty (zero-length) byte-array.</p>
     * <p>The method is completely thread-safe and the same parser instance can be used concurrently
     * from multiple threads.</p>
     *
     * @param input the input stream instance to parse
     * @param query the query string to transform and execute against the input stream
     * @return the extracted byte-array containing the CBOR encoded object
     * @throws IllegalStateException is thrown whenever an illegal state has occurred while parsing
     * @throws WrongTypeException    is thrown whenever an unexpected type was found while parsing
     * @throws QueryParserException  is thrown whenever an error occurred while parsing the actual
     *                               query string into a {@link Query} instance
     */
    byte[] extract(Input input, String query);

    /**
     * <p>Extracts a {@link Value} from the given {@link Input} instance, representing the
     * the CBOR encoded input stream and returns the {@link Value} instance at the given offset.
     * The offset is only lightly validated by trying to read {@link MajorType} and {@link ValueType}
     * but does not analyze it any further. That means it is possible to see further errors when
     * try to access the actual content of the returned value instance.</p>
     * <p>The method is completely thread-safe and the same parser instance can be used concurrently
     * from multiple threads.</p>
     *
     * @param input  the input stream instance to parse
     * @param offset the offset of the request element inside the CBOR stream
     * @return the extracted byte-array containing the CBOR encoded object
     */
    byte[] extract(Input input, long offset);

    MutationBuilder newMutationBuilder();

    /**
     * Parses and prepares a given query string into a {@link Query} instance. The returned query
     * instance is fully thread-safe and stateless and can be stored and shared by multiple threads.
     *
     * @param query the query string to transform
     * @return the parsed, transformed and prepared query to store for further
     * execution
     * @throws WrongTypeException   is thrown whenever an unexpected type was found while preparing
     * @throws QueryParserException is thrown whenever an error occurred while parsing the actual
     *                              query string into a {@link Query} instance
     */
    Query prepareQuery(String query);

    /**
     * Returns a new {@link QueryBuilder} instance to configure a {@link Query} instance. The eventually
     * build query instance is fully thread-safe and stateless and can be stored and shared by multiple
     * threads.
     *
     * @return the new QueryBuilder instance
     */
    StreamQueryBuilder newQueryBuilder();

    /**
     * Creates a new {@link ParserBuilder} instance to fluently configure and create a {@link Parser}
     * instance.
     *
     * @return a new builder to configure and create a <tt>Parser</tt> instance
     */
    static ParserBuilder newBuilder() {
        return new ParserBuilderImpl();
    }

}
