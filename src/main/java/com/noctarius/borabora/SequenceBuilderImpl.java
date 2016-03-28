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
package com.noctarius.borabora;

import java.util.List;

final class SequenceBuilderImpl<B>
        extends AbstractValueBuilder<SequenceBuilder<B>>
        implements SequenceBuilder<B> {

    private final List<Object> outerValues;
    private final int maxElements;
    private final B builder;

    SequenceBuilderImpl(B builder, List<Object> outerValues) {
        this(-1, builder, outerValues);
    }

    SequenceBuilderImpl(int maxElements, B builder, List<Object> outerValues) {
        this.outerValues = outerValues;
        this.maxElements = maxElements;
        this.builder = builder;
    }

    @Override
    public B endSequence() {
        outerValues.add(values());
        return builder;
    }

    @Override
    protected void validate() {
        if (maxElements > -1 && values().size() >= maxElements) {
            throw new IllegalStateException("Cannot add another element, maximum element count reached");
        }
    }

}
