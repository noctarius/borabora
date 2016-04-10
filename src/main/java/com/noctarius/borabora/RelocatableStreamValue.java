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

import java.util.Collection;
import java.util.Optional;

final class RelocatableStreamValue
        extends AbstractStreamValue {

    private final Collection<SemanticTagProcessor> processors;

    private MajorType majorType;
    private ValueType valueType;
    private Input input;
    private long offset;

    RelocatableStreamValue(Input input, Collection<SemanticTagProcessor> processors) {
        super(input, processors);

        if (offset == -1) {
            throw new IllegalArgumentException("No offset available for CBOR type");
        }

        this.input = input;
        this.processors = processors;
    }

    @Override
    public MajorType majorType() {
        return majorType;
    }

    @Override
    public ValueType valueType() {
        return valueType;
    }

    @Override
    protected long offset() {
        return offset;
    }

    @Override
    protected <T> T extractTag() {
        SemanticTagProcessor<T> processor = findProcessor(offset);
        if (processor == null) {
            return null;
        }
        long length = Decoder.length(input, majorType(), offset);
        return processor.process(input, offset, length, processors);
    }

    void relocate(MajorType majorType, ValueType valueType, long offset) {
        this.majorType = majorType;
        this.valueType = valueType;
        this.offset = offset;
    }

    private <V> SemanticTagProcessor<V> findProcessor(long offset) {
        Optional<SemanticTagProcessor> optional = processors.stream().filter(p -> p.handles(input, offset)).findFirst();
        return optional.isPresent() ? optional.get() : null;
    }

}
