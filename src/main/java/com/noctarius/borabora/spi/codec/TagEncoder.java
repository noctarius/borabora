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
package com.noctarius.borabora.spi.codec;

/**
 * The <tt>TagEncoder</tt> is a sub-interface of {@link TagWriter} to add functionality
 * for automatic recognition of data types.
 *
 * @param <V> the type of the value
 */
public interface TagEncoder<V>
        extends TagWriter<V> {

    /**
     * Returns <tt>true</tt> if the given <tt>value</tt> can be handled by this
     * <tt>TagEncoder</tt>, otherwise <tt>false</tt>.
     *
     * @param value the value to encode
     * @return true if this encoder can handle the given value, otherwise false
     */
    boolean handles(V value);

}
