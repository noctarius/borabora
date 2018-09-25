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

import java.time.Instant;
import java.util.Date;

/**
 * The <tt>DateTimeBuilder</tt> interface is designed to be used with the
 * {@link com.noctarius.borabora.spi.builder.TagSupport} API.
 * <p>To use the builder, a {@link com.noctarius.borabora.builder.encoder.GraphBuilder} instance
 * is required. The following example shows how to retrieve and use the builder's instance:</p>
 * <pre>
 *     GraphBuilder graphBuilder = writer.newGraphBuilder( output );
 *     graphBuilder.putTag(
 *         TagSupport.semanticTag( DateTimeBuilder.class ).putDateTime( dateTime )
 *             .endSemanticTag() ).finishStream();
 * </pre>
 */
public interface DateTimeBuilder {

    /**
     * Writes the given date, time or date-time value as a semantic tag. The <tt>date</tt> value
     * is transformed to an {@link Instant} instance first, then written. The returned {@link TagBuilder}
     * instance must be used to finalize the building by calling {@link TagBuilder#endSemanticTag()}.
     *
     * @param date the {@link Date} value to write as date-time
     * @return the semantic tag finalizing TagBuilder
     */
    @BuilderStackPush
    TagBuilder putDateTime(Date date);

    /**
     * Writes the given <tt>date</tt>, time or date-time value as a semantic tag. The returned {@link TagBuilder}
     * instance must be used to finalize the building by calling {@link TagBuilder#endSemanticTag()}.
     *
     * @param instant the {@link Instant} value to write as date-time
     * @return the semantic tag finalizing TagBuilder
     */
    @BuilderStackPush
    TagBuilder putDateTime(Instant instant);

}
