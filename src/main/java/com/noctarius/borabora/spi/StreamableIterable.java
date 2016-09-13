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
package com.noctarius.borabora.spi;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * The <tt>StreamableIterable</tt> interface extends the common {@link Iterable} with Java 8
 * stream supporting methods.
 *
 * @param <T> the value type
 */
public interface StreamableIterable<T>
        extends Iterable<T> {

    /**
     * Returns a sequential {@code Stream} with this collection as its source.
     *
     * @return a sequential  Stream over the elements in this collection
     */
    default Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Returns a possibly parallel {@code Stream} with this collection as its
     * source.  It is allowable for this method to return a sequential stream.
     *
     * @return a possibly parallel {@code Stream} over the elements in this
     * collection
     */
    default Stream<T> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }

}
