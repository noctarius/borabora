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
package com.noctarius.borabora.builder;

import com.noctarius.borabora.Writer;
import com.noctarius.borabora.spi.codec.TagStrategy;

/**
 * The <tt>WriterBuilder</tt> class is used to configure a new {@link Writer} instance. The
 * configuration is meant to include {@link TagStrategy}s for new, non-standard semantic tags
 * and data types.
 */
public interface WriterBuilder {

    /**
     * Adds the given {@link TagStrategy} instance to the new {@link Writer} configuration.
     *
     * @param tagStrategy the TagStrategy to add to the configuration
     * @return this builder instance
     * @throws NullPointerException if tagStrategy is null
     */
    WriterBuilder addTagStrategy(TagStrategy tagStrategy);

    /**
     * Adds the given {@link TagStrategy} instances to the new {@link Writer} configuration.
     *
     * @param tagStrategy1 the first TagStrategy to add to the configuration
     * @param tagStrategy2 the second TagStrategy to add to the configuration
     * @return this builder instance
     * @throws NullPointerException if either tagStrategy instance is null
     */
    WriterBuilder addTagStrategies(TagStrategy tagStrategy1, TagStrategy tagStrategy2);

    /**
     * Adds the given {@link TagStrategy} instances to the new {@link Writer} configuration.
     *
     * @param tagStrategy1  the first TagStrategy to add to the configuration
     * @param tagStrategy2  the second TagStrategy to add to the configuration
     * @param tagStrategies the TagStrategy array to add to the configuration
     * @return this builder instance
     * @throws NullPointerException if any tagStrategies instance is null
     */
    WriterBuilder addTagStrategies(TagStrategy tagStrategy1, TagStrategy tagStrategy2, TagStrategy... tagStrategies);

    /**
     * Adds the given {@link TagStrategy} instances to the new {@link Writer} configuration.
     *
     * @param tagStrategies the TagStrategy array to add to the configuration
     * @return this builder instance
     * @throws NullPointerException if any tagStrategies instance is null
     */
    WriterBuilder addTagStrategies(Iterable<TagStrategy> tagStrategies);

    /**
     * Returns a new {@link Writer} instance based on the internal configuration. The returned writer
     * is fully thread-safe and stateless and can be stored and shared by multiple threads, however
     * {@link com.noctarius.borabora.builder.encoder.GraphBuilder}s created using this {@link Writer}
     * are not.
     *
     * @return the new Writer instance
     */
    Writer build();

}
