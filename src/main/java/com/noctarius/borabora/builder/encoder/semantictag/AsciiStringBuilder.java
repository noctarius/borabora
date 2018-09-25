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

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * The <tt>URIBuilder</tt> interface is designed to be used with the
 * {@link com.noctarius.borabora.spi.builder.TagSupport} API.
 * <p>To use the builder, a {@link com.noctarius.borabora.builder.encoder.GraphBuilder} instance
 * is required. The following example shows how to retrieve and use the builder's instance:</p>
 * <pre>
 *     GraphBuilder graphBuilder = writer.newGraphBuilder( output );
 *     graphBuilder.putTag(
 *         TagSupport.semanticTag( AsciiStringBuilder.class ).putAsciiString( string )
 *             .endSemanticTag() ).finishStream();
 * </pre>
 */
public interface AsciiStringBuilder {

    /**
     * Writes the given <tt>string</tt> value as a ASCII semantic tag. The returned {@link TagBuilder}
     * instance must be used to finalize the building by calling {@link TagBuilder#endSemanticTag()}.
     *
     * @param value the {@link String} value to write as a ASCII string
     * @return the semantic tag finalizing TagBuilder
     */
    @BuilderStackPush
    TagBuilder putAsciiString(String value);

}
