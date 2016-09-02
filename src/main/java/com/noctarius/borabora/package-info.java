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

/**
 * borabora is a (<a href="http://www.cbor.io">CBOR</a>, the Concise Binary Object
 * Representation, skip-scan processor which consists of a generator as well as a
 * parser. While parsing all operations are implemented as lazily as possible,
 * whereas writing is immediate.
 * <p>CBOR itself is a very effective binary protocol, modelled after the basic ideas
 * of JSON, however employs more diverse data types and is extensible to future
 * purpose.</p>
 * <p>Skip-scan means, that the parser scans the actual data type of an item in the
 * stream, calculates its byte length and skips the bytes if the item if of no
 * interest.</p>
 * <p>Using borabora, you're not directly working with native types directly but all
 * queries result in a generalized type, called {@link com.noctarius.borabora.Value},
 * which will be lazily (on user request) de-serialized into the actual data type and
 * value. Up to this point nothing is actually read.</p>
 * <p>The API is kept simple but powerful. Reading CBOR data with borabora consists
 * mainly of creating a {@link com.noctarius.borabora.Parser} instance, wrapping any
 * king of source into an {@link com.noctarius.borabora.Input} implementation, parsing
 * or fluent-building a query and putting all parts together into an actual read
 * request. The following code snippet shows the basic flow of how borabora is used.</p>
 * <pre>
 *     Parser parser = Parser.newBuilder().build();
 *     Input input = Input.fromByteArray( getByteArray() );
 *     Query query = Query.newBuilder().build();
 *     Value value = parser.read(input, query);
 * </pre>
 * <p>The previous example wraps the byte-array returned from the method
 * <tt>getByteArry</tt> into an <tt>Input</tt> instance and executes it against the
 * <tt>Parser</tt> and <tt>Query</tt> to return the actual result of the query as
 * a <tt>Value</tt> instance.</p>
 * <p>Furthermore it is important to note, that {@link com.noctarius.borabora.Parser}
 * and {@link com.noctarius.borabora.Query} instances are stateless and thread-safe
 * which means they can easily be shared between different threads and can be used
 * concurrently. This is true as long as the {@link com.noctarius.borabora.Input}
 * implementation is thread-safe as well and employs the provided offset as a read
 * position. There might be implementations where this assumption does not hold true,
 * for example if backed by a <tt>java.net.SocketInputStream</tt>, whereas those kind
 * of implementations are not provided out of the box.</p>
 */
package com.noctarius.borabora;
