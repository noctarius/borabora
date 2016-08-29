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
package com.noctarius.borabora.spi.codec;

import com.noctarius.borabora.ValueType;

public interface TagStrategy<S, V>
        extends TagDecoder<V>, TagEncoder<V> {

    S newTagBuilder(EncoderContext encoderContext);

    int tagId();

    ValueType valueType();

    Class<S> tagBuilderType();

    /**
     * Returns a {@link TagEncoder} instance in case the automatic encoding
     * is possible, which in turn is possible in case of simple values or
     * special runtime support classes (for example {@link java.math.BigInteger}).
     * In this case {@link com.noctarius.borabora.builder.ValueBuilder#putValue(Object)} or
     * {@link com.noctarius.borabora.builder.ValueBuilder#putTag(Object)} are
     * able to support implicit encoding using the returned tag encoder.
     * <p>For complex types, if implicit encoding is not desired or possible because
     * of type collisions, <tt>null</tt> can be returned to make only explicit
     * encoding using {@link TagSupport#semanticTag(Class)} possible.</p>
     *
     * @return a <tt>TagEncoder</tt> instance or <tt>null</tt> to deactivate implicit encoding
     */
    TagEncoder<V> tagEncoder();

    TagDecoder<V> tagDecoder();

}
