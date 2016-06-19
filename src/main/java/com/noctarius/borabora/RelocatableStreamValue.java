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

final class RelocatableStreamValue
        extends AbstractStreamValue {

    private MajorType majorType;
    private ValueType valueType;
    private long offset;

    RelocatableStreamValue(QueryContext queryContext) {
        super(queryContext);

        if (offset == -1) {
            throw new IllegalArgumentException("No offset available for CBOR type");
        }
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
    public long offset() {
        return offset;
    }

    @Override
    protected <T> T extractTag() {
        return queryContext().applyProcessors(offset(), majorType());
    }

    void relocate(MajorType majorType, ValueType valueType, long offset) {
        this.majorType = majorType;
        this.valueType = valueType;
        this.offset = offset;
    }

}
