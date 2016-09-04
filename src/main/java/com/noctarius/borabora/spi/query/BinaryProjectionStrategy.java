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
package com.noctarius.borabora.spi.query;

import com.noctarius.borabora.Input;
import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.Output;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.ValueType;
import com.noctarius.borabora.impl.query.stages.AsDictionarySelectorQueryStage;
import com.noctarius.borabora.impl.query.stages.AsSequenceSelectorQueryStage;
import com.noctarius.borabora.spi.StreamValue;
import com.noctarius.borabora.spi.io.Decoder;
import com.noctarius.borabora.spi.io.Encoder;
import com.noctarius.borabora.spi.query.pipeline.PipelineStage;

import java.io.ByteArrayOutputStream;

import static com.noctarius.borabora.spi.io.Constants.EMPTY_QUERY_CONSUMER;
import static com.noctarius.borabora.spi.io.Constants.OFFSET_CODE_NULL;
import static com.noctarius.borabora.spi.io.Constants.OPCODE_BREAK_MASK;
import static com.noctarius.borabora.spi.io.Constants.SIMPLE_VALUE_NULL_BYTE;

public class BinaryProjectionStrategy
        implements ProjectionStrategy {

    public static final ProjectionStrategy INSTANCE = new BinaryProjectionStrategy();

    private BinaryProjectionStrategy() {
    }

    @Override
    public void beginSelect(QueryContext queryContext) {
        queryContext.queryStackPush(new BinaryQueryContext());
    }

    @Override
    public void finalizeSelect(QueryContext queryContext) {
        BinaryQueryContext bqc = queryContext.queryStackPop();

        byte[] data = bqc.baos.toByteArray();
        Input input = Input.fromByteArray(data);

        short head = input.read(0);
        MajorType majorType = MajorType.findMajorType(head);

        QueryContextFactory queryContextFactory = queryContext.queryContextFactory();
        QueryContext newQueryContext = queryContextFactory.newQueryContext(input, EMPTY_QUERY_CONSUMER, //
                queryContext.tagStrategies(), queryContext.projectionStrategy());

        ValueType valueType = newQueryContext.valueType(0);
        Value value = new StreamValue(majorType, valueType, 0, newQueryContext);
        queryContext.consume(value);
    }

    @Override
    public void beginDictionary(QueryContext queryContext) {
        putStructureHead(MajorType.Dictionary, queryContext);
    }

    @Override
    public void endDictionary(QueryContext queryContext) {
        putBreakMask(queryContext);
    }

    @Override
    public void putDictionaryKey(String key, QueryContext queryContext) {
        BinaryQueryContext bqc = queryContext.queryStackPeek();
        bqc.offset = Encoder.putString(key, bqc.offset, bqc.output);
    }

    @Override
    public void putDictionaryKey(long key, QueryContext queryContext) {
        BinaryQueryContext bqc = queryContext.queryStackPeek();
        bqc.offset = Encoder.putNumber(key, bqc.offset, bqc.output);
    }

    @Override
    public void putDictionaryKey(double key, QueryContext queryContext) {
        BinaryQueryContext bqc = queryContext.queryStackPeek();
        bqc.offset = Encoder.putDouble(key, bqc.offset, bqc.output);
    }

    @Override
    public void putDictionaryValue(PipelineStage previousPipelineStage, QueryContext queryContext) {
        if (!(previousPipelineStage.stage() instanceof AsDictionarySelectorQueryStage) //
                && !(previousPipelineStage.stage() instanceof AsSequenceSelectorQueryStage)) {

            queryContext.offset(putValue(queryContext));
        }
    }

    @Override
    public void putDictionaryNullValue(QueryContext queryContext) {
        putValue(queryContext);
    }

    @Override
    public void beginSequence(QueryContext queryContext) {
        putStructureHead(MajorType.Sequence, queryContext);
    }

    @Override
    public void endSequence(QueryContext queryContext) {
        putBreakMask(queryContext);
    }

    @Override
    public void putSequenceValue(PipelineStage previousPipelineStage, QueryContext queryContext) {
        if (!(previousPipelineStage.stage() instanceof AsDictionarySelectorQueryStage) //
                && !(previousPipelineStage.stage() instanceof AsSequenceSelectorQueryStage)) {

            queryContext.offset(putValue(queryContext));
        }
    }

    @Override
    public void putSequenceNullValue(QueryContext queryContext) {
        putValue(queryContext);
    }

    private void putStructureHead(MajorType majorType, QueryContext queryContext) {
        BinaryQueryContext bqc = queryContext.queryStackPeek();
        bqc.offset = Encoder.encodeLengthAndValue(majorType, -1, bqc.offset, bqc.output);
    }

    private void putBreakMask(QueryContext queryContext) {
        BinaryQueryContext bqc = queryContext.queryStackPeek();
        bqc.output.write(bqc.offset++, (byte) OPCODE_BREAK_MASK);
    }

    private long putValue(QueryContext queryContext) {
        long offset = queryContext.offset();
        BinaryQueryContext bqc = queryContext.queryStackPeek();
        if (offset == OFFSET_CODE_NULL) {
            bqc.output.write(bqc.offset, SIMPLE_VALUE_NULL_BYTE);
            bqc.offset++;

        } else {
            Input input = queryContext.input();
            short head = Decoder.readUInt8(input, offset);

            MajorType majorType = MajorType.findMajorType(head);
            byte[] data = Decoder.readRaw(input, majorType, offset);
            bqc.offset = bqc.output.write(data, bqc.offset, data.length);

            return offset + data.length;
        }
        return offset;
    }

    private static class BinaryQueryContext {
        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        private final Output output = Output.toOutputStream(baos);

        private long offset = 0;
    }

}
