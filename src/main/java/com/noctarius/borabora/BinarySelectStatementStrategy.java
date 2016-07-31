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

import com.noctarius.borabora.builder.DictionaryQueryBuilder;
import com.noctarius.borabora.builder.SequenceQueryBuilder;
import com.noctarius.borabora.builder.StreamEntryQueryBuilder;
import com.noctarius.borabora.spi.Decoder;
import com.noctarius.borabora.spi.Encoder;
import com.noctarius.borabora.spi.QueryContext;
import com.noctarius.borabora.spi.SelectStatementStrategy;
import com.noctarius.borabora.spi.StreamValue;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.function.BiFunction;

import static com.noctarius.borabora.spi.Constants.OPCODE_BREAK_MASK;

public class BinarySelectStatementStrategy
        implements SelectStatementStrategy {

    public static final SelectStatementStrategy INSTANCE = new BinarySelectStatementStrategy();

    private BinarySelectStatementStrategy() {
    }

    @Override
    public void beginSelect(QueryContext queryContext) {
        queryContext.queryStackPush(new BinaryQueryContext());
    }

    @Override
    public Value finalizeSelect(QueryContext queryContext) {
        BinaryQueryContext bqc = queryContext.queryStackPop();

        byte[] data = bqc.baos.toByteArray();
        Input input = Input.fromByteArray(data);

        short head = input.read(0);
        MajorType majorType = MajorType.findMajorType(head);
        ValueType valueType = ValueTypes.valueType(input, 0);

        QueryContext newQueryContext = new QueryContextImpl(input, (QueryContextImpl) queryContext);
        return new StreamValue(majorType, valueType, 0, newQueryContext);
    }

    @Override
    public <T> DictionaryQueryBuilder<T> asDictionary(T graphQueryBuilder, List<Query> graphQueries) {
        graphQueries.add(putStructureHead(MajorType.Dictionary));
        return new DictionaryQueryBuilderImpl<>(graphQueryBuilder, graphQueries, this);
    }

    @Override
    public <T> SequenceQueryBuilder<T> asSequence(T graphQueryBuilder, List<Query> graphQueries) {
        graphQueries.add(putStructureHead(MajorType.Sequence));
        return new SequenceQueryBuilderImpl<>(graphQueryBuilder, graphQueries, this);
    }

    @Override
    public <T, D extends DictionaryQueryBuilder<T>> StreamEntryQueryBuilder<D> putDictionaryEntry(String key,
                                                                                                            D queryBuilder,
                                                                                                            List<Query> graphQueries) {

        graphQueries.add(putKey((offset, output) -> Encoder.putString(key, offset, output)));
        graphQueries.add(ResetOffsetQuery.INSTANCE);
        return new StreamEntryQueryBuilderImpl<>(queryBuilder, graphQueries, this::putValue, this);
    }

    @Override
    public <T, D extends DictionaryQueryBuilder<T>> StreamEntryQueryBuilder<D> putDictionaryEntry(long key,
                                                                                                            D queryBuilder,
                                                                                                            List<Query> graphQueries) {

        graphQueries.add(putKey((offset, output) -> Encoder.putNumber(key, offset, output)));
        graphQueries.add(ResetOffsetQuery.INSTANCE);
        return new StreamEntryQueryBuilderImpl<>(queryBuilder, graphQueries, this::putValue, this);
    }

    @Override
    public <T, D extends DictionaryQueryBuilder<T>> StreamEntryQueryBuilder<D> putDictionaryEntry(double key,
                                                                                                            D queryBuilder,
                                                                                                            List<Query> graphQueries) {

        graphQueries.add(putKey((offset, output) -> Encoder.putDouble(key, offset, output)));
        graphQueries.add(ResetOffsetQuery.INSTANCE);
        return new StreamEntryQueryBuilderImpl<>(queryBuilder, graphQueries, this::putValue, this);
    }

    @Override
    public <T> T endDictionary(T queryBuilder, List<Query> graphQueries) {
        graphQueries.add(this::putBreakMask);
        return queryBuilder;
    }

    @Override
    public <T, S extends SequenceQueryBuilder<T>> StreamEntryQueryBuilder<S> putSequenceEntry(S queryBuilder,
                                                                                                        List<Query> graphQueries) {

        graphQueries.add(ResetOffsetQuery.INSTANCE);
        return new StreamEntryQueryBuilderImpl<>(queryBuilder, graphQueries, this::putValue, this);
    }

    @Override
    public <T> T endSequence(T queryBuilder, List<Query> graphQueries) {
        graphQueries.add(this::putBreakMask);
        return queryBuilder;
    }

    private Query putStructureHead(MajorType majorType) {
        return (queryOffset, queryContext) -> {
            BinaryQueryContext bqc = queryContext.queryStackPeek();
            bqc.offset = Encoder.encodeLengthAndValue(majorType, -1, bqc.offset, bqc.output);
            return queryOffset;
        };
    }

    private Query putKey(BiFunction<Long, Output, Long> function) {
        return (queryOffset, queryContext) -> {
            BinaryQueryContext bqc = queryContext.queryStackPeek();
            bqc.offset = function.apply(bqc.offset, bqc.output);
            return queryOffset;
        };
    }

    private long putBreakMask(long queryOffset, QueryContext queryContext) {
        BinaryQueryContext bqc = queryContext.queryStackPeek();
        bqc.output.write(bqc.offset++, (byte) OPCODE_BREAK_MASK);
        return -2;
    }

    private long putValue(long queryOffset, QueryContext queryContext) {
        if (queryOffset >= 0) {
            BinaryQueryContext bqc = queryContext.queryStackPeek();

            Input input = queryContext.input();
            short head = Decoder.readUInt8(input, queryOffset);

            MajorType majorType = MajorType.findMajorType(head);
            byte[] data = Decoder.readRaw(input, majorType, queryOffset);
            bqc.offset = bqc.output.write(data, bqc.offset, data.length);

            return queryOffset + data.length;
        }
        return queryOffset;
    }

    private static class BinaryQueryContext {
        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        private final Output output = Output.toOutputStream(baos);

        private long offset = 0;
    }

}
