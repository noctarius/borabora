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
package com.noctarius.borabora.builder.encoder.semantictag;

import com.noctarius.borabora.spi.builder.BuilderStackPush;
import com.noctarius.borabora.spi.builder.TagBuilder;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * The <tt>TimestampBuilder</tt> interface is designed to be used with the
 * {@link com.noctarius.borabora.spi.builder.TagSupport} API.
 * <p>To use the builder, a {@link com.noctarius.borabora.builder.encoder.GraphBuilder} instance
 * is required. The following example shows how to retrieve and use the builder's instance:</p>
 * <pre>
 *     GraphBuilder graphBuilder = writer.newGraphBuilder( output );
 *     graphBuilder.putTag(
 *         TagSupport.semanticTag( TimestampBuilder.class ).putTimestamp( timestamp )
 *             .endSemanticTag() ).finishStream();
 * </pre>
 */
public interface TimestampBuilder {

    /**
     * Writes the given <tt>timestamp</tt> value as a timestamp semantic tag. The returned {@link TagBuilder}
     * instance must be used to finalize the building by calling {@link TagBuilder#endSemanticTag()}.
     *
     * @param timestamp the <tt>long</tt> value to write as a timestamp
     * @return the semantic tag finalizing TagBuilder
     */
    @BuilderStackPush
    TagBuilder putTimestamp(long timestamp);

    /**
     * Writes the given {@link Instant} value as a timestamp semantic tag. The <tt>timestamp</tt> value
     * is transformed to an <tt>long</tt> value first, then written. The returned {@link TagBuilder}
     * instance must be used to finalize the building by calling {@link TagBuilder#endSemanticTag()}.
     *
     * @param timestamp the {@link Instant} value to write as a timestamp
     * @return the semantic tag finalizing TagBuilder
     */
    @BuilderStackPush
    TagBuilder putTimestamp(Instant timestamp);

    /**
     * Writes the given {@link Timestamp} value as a timestamp semantic tag. The <tt>timestamp</tt> value
     * is transformed to an <tt>long</tt> value first, then written. The returned {@link TagBuilder}
     * instance must be used to finalize the building by calling {@link TagBuilder#endSemanticTag()}.
     *
     * @param timestamp the {@link Timestamp} value to write as a timestamp
     * @return the semantic tag finalizing TagBuilder
     */
    @BuilderStackPush
    TagBuilder putTimestamp(Timestamp timestamp);

}
