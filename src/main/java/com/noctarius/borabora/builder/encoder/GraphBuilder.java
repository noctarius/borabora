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
package com.noctarius.borabora.builder.encoder;

import com.noctarius.borabora.Output;

/**
 * The <tt>GraphBuilder</tt> interface defines the entry and exit point for generating
 * a CBOR encoded stream. An instance is retrieved by binding an
 * {@link com.noctarius.borabora.Output} instance and is retrieved from a call to
 * {@link com.noctarius.borabora.Writer#newGraphBuilder(Output)}.
 * <p>In contrast to reading a CBOR encoded stream, writing the stream and elements
 * has an immediate effect, therefore the order of the builder calls is important as
 * it directly represents the data item structure inside the resulting CBOR stream.</p>
 *
 * @see ValueBuilder
 * @see DictionaryBuilder
 * @see SequenceBuilder
 * @see IndefiniteStringBuilder
 * @see DictionaryEntryBuilder
 */
public interface GraphBuilder
        extends ValueBuilder<GraphBuilder> {

    /**
     * Finalizes the stream and writes possibly expected or necessary tags to the stream.
     */
    void finishStream();

}
