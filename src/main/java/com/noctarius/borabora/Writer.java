/*
 * Copyright (c) 2016-2018, Christoph Engelbert (aka noctarius) and
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

import com.noctarius.borabora.builder.WriterBuilder;
import com.noctarius.borabora.builder.encoder.GraphBuilder;
import com.noctarius.borabora.impl.WriterBuilderImpl;

/**
 * The <tt>Writer</tt> interface is used to create new writers to generate the
 * CBOR data streams from Java data structures and values. A Writer instance is
 * created using a {@link WriterBuilder} retrieved using the {@link #newBuilder()}
 * method and configured to the users needs.
 * <p>The same Writer instance can be used to generate multiple new streams in
 * parallel, however {@link Output} implementations and {@link GraphBuilder}
 * instances are <b>not</b> considered to be thread-safe and should only be used
 * by a single thread. The Writer instance can be configured once and used
 * concurrently though.</p>
 */
public interface Writer {

    /**
     * Returns a new {@link GraphBuilder} instance bound to the given <tt>output</tt>.
     * GraphBuilders are not thread-safe by design and so are {@link Output} instances.
     * Any returns instance should only be used by a single thread!
     *
     * @param output the Output instance to bind the new builder to
     * @return a new GraphBuilder instance bound to the given output instance @
     */
    GraphBuilder newGraphBuilder(Output output);

    /**
     * Returns a new {@link WriterBuilder} instance to configure and create the final
     * Writer.
     *
     * @return a new WriterBuilder instance
     */
    static WriterBuilder newBuilder() {
        return new WriterBuilderImpl();
    }

    /**
     * Creates a new {@link Writer} instance with the default configuration. This method is a shorthand for
     * <pre>Writer.newBuilder().build()</pre> and the result is equivalent.
     *
     * @return a new <tt>Writer</tt> instance with default configuration
     */
    static Writer newWriter() {
        return newBuilder().build();
    }

}
