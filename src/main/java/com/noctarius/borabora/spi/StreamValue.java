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
package com.noctarius.borabora.spi;

import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.ValueType;
import com.noctarius.borabora.spi.query.QueryContext;

import java.util.Objects;

/**
 * The <tt>StreamValue</tt> class is the actual, standard implementation of a stream based
 * {@link com.noctarius.borabora.Value}. It binds the underlying {@link QueryContext}, the
 * values <tt>offset</tt>, the {@link MajorType} and {@link ValueType}.
 */
public final class StreamValue
        extends AbstractStreamValue {

    private final QueryContext queryContext;
    private final MajorType majorType;
    private final ValueType valueType;
    private final long offset;

    /**
     * Creates a new instance of the <tt>StreamValue</tt> based on the passed <tt>majorType</tt>,
     * <tt>valueType</tt>, <tt>offset</tt> and <tt>queryContext</tt>.
     *
     * @param majorType    the MajorType
     * @param valueType    the ValueType
     * @param offset       the offset
     * @param queryContext the QueryContext
     * @throws NullPointerException     if one of majorType, valueType or queryContext is null
     * @throws IllegalArgumentException if offset is less than 0
     */
    public StreamValue(MajorType majorType, ValueType valueType, long offset, QueryContext queryContext) {
        Objects.requireNonNull(queryContext, "queryContext must not be null");
        Objects.requireNonNull(majorType, "majorType must not be null");
        Objects.requireNonNull(valueType, "valueType must not be null");
        if (offset <= -1) {
            throw new IllegalArgumentException("No offset available for CBOR type, offset=" + offset);
        }

        this.queryContext = queryContext;
        this.offset = offset;
        this.majorType = majorType;
        this.valueType = valueType;
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
    public String toString() {
        return "StreamValue{" + "valueType=" + valueType + ", offset=" + offset + ", value=" + byValueType() + '}';
    }

    @Override
    public long offset() {
        return offset;
    }

    @Override
    protected <T> T extractTag() {
        return queryContext().applyDecoder(offset(), majorType(), valueType());
    }

    @Override
    public QueryContext queryContext() {
        return queryContext;
    }
}
