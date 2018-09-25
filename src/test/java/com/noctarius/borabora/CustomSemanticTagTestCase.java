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
package com.noctarius.borabora;

import com.noctarius.borabora.builder.encoder.GraphBuilder;
import com.noctarius.borabora.builder.encoder.ValueBuilder;
import com.noctarius.borabora.spi.builder.AbstractStreamValueBuilder;
import com.noctarius.borabora.spi.builder.BuilderStackPop;
import com.noctarius.borabora.spi.builder.BuilderStackPush;
import com.noctarius.borabora.spi.builder.EncoderContext;
import com.noctarius.borabora.spi.builder.TagBuilder;
import com.noctarius.borabora.spi.builder.TagBuilderConsumer;
import com.noctarius.borabora.spi.codec.TagStrategy;
import com.noctarius.borabora.spi.io.ByteSizes;
import com.noctarius.borabora.spi.io.Decoder;
import com.noctarius.borabora.spi.io.Encoder;
import com.noctarius.borabora.spi.query.QueryContext;
import com.noctarius.borabora.spi.query.TypeSpec;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.function.Function;

import static com.noctarius.borabora.spi.builder.TagSupport.semanticTag;
import static com.noctarius.borabora.spi.io.Constants.OPCODE_BREAK_MASK;
import static org.junit.Assert.assertEquals;

public class CustomSemanticTagTestCase
        extends AbstractTestCase {

    @Test
    public void test_encode_decode_custom_semantic_tag_type_value_tag() {
        execute(Value::tag);
    }

    @Test
    public void test_encode_decode_custom_semantic_tag_type_value_byvaluetype() {
        execute(Value::byValueType);
    }

    private void execute(Function<Value, Sequence> function) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer writer = Writer.newBuilder().addTagStrategy(new CustomTableTagStrategy()).build();

        GraphBuilder graphBuilder = writer.newGraphBuilder(Output.toOutputStream(baos));

        graphBuilder.putTag( //
                semanticTag(CustomTableBuilder.class)

                        .putHeaders("Header 1", "Header 2", "Header 3")

                        .putRow().putString("Row 1").putNumber(42).putNumber(1337).endRow()

                        .putRow().putString("Row 2").putString("24").putNumber(1337.f).endRow()

                        .putRow().putString("Row 3").endRow()

                        .endSemanticTag() //
        ).finishStream();

        Parser parser = Parser.newBuilder().addTagStrategy(new CustomTableTagStrategy()).build();
        Input input = Input.fromByteArray(baos.toByteArray());

        Value value = parser.read(input, parser.newQueryBuilder().build());

        Sequence table = function.apply(value);

        assertEquals(4, table.size());

        Sequence headers = table.get(0).sequence();
        assertEquals(3, headers.size());
        assertEquals("Header 1", headers.get(0).string());
        assertEquals("Header 2", headers.get(1).string());
        assertEquals("Header 3", headers.get(2).string());

        Sequence row1 = table.get(1).sequence();
        assertEquals(3, row1.size());
        assertEquals("Row 1", row1.get(0).string());
        assertEqualsNumber(42, row1.get(1).number());
        assertEqualsNumber(1337, row1.get(2).number());

        Sequence row2 = table.get(2).sequence();
        assertEquals(3, row2.size());
        assertEquals("Row 2", row2.get(0).string());
        assertEquals("24", row2.get(1).string());
        assertEqualsNumber(1337.f, row2.get(2).number());

        Sequence row3 = table.get(3).sequence();
        assertEquals(1, row3.size());
        assertEquals("Row 3", row3.get(0).string());
    }

    public interface CustomTableBuilder {

        @BuilderStackPush
        CustomTableRowBuilder putHeaders(String... headers);

    }

    public interface CustomTableRowBuilder
            extends TagBuilder {

        @BuilderStackPush
        CustomTableColumnBuilder<CustomTableRowBuilder> putRow();

    }

    public interface CustomTableColumnBuilder<B>
            extends ValueBuilder<CustomTableColumnBuilder<B>> {

        @BuilderStackPop
        B endRow();

    }

    public static class CustomTableTagStrategy
            implements TagStrategy<CustomTableBuilder, Object> {

        @Override
        public CustomTableBuilder newTagBuilder(EncoderContext encoderContext) {
            return new CustomTableBuilderImpl(encoderContext);
        }

        @Override
        public int tagId() {
            return Integer.MAX_VALUE;
        }

        @Override
        public ValueType valueType() {
            return null;
        }

        @Override
        public Class<CustomTableBuilder> tagBuilderType() {
            return CustomTableBuilder.class;
        }

        @Override
        public long process(Object value, long offset, EncoderContext encoderContext) {
            return 0;
        }

        @Override
        public boolean handles(Object value) {
            return false;
        }

        @Override
        public Sequence process(ValueType valueType, long offset, long length, QueryContext queryContext) {
            Input input = queryContext.input();
            offset += ByteSizes.headByteSize(input, offset);
            return Decoder.readSequence(offset, queryContext);
        }

        @Override
        public boolean handles(Input input, long offset) {
            return valueType(input, offset) != ValueTypes.Unknown;
        }

        @Override
        public TypeSpec handles(long tagId) {
            return null;
        }

        @Override
        public ValueType valueType(Input input, long offset) {
            Number tagType = Decoder.readUint(input, offset);
            return tagType.longValue() == (Integer.MAX_VALUE) ? CustomTableValueType.CustomTable : ValueTypes.Unknown;
        }

        private static class CustomTableBuilderImpl
                implements CustomTableBuilder {

            private final EncoderContext encoderContext;

            private CustomTableBuilderImpl(EncoderContext encoderContext) {
                this.encoderContext = encoderContext;
            }

            @Override
            public CustomTableRowBuilder putHeaders(String... headers) {
                encoderContext.encode((offset, output) -> Encoder.putSemanticTag(Integer.MAX_VALUE, offset, output));
                encoderContext.encode((offset, output) -> Encoder.encodeLengthAndValue(MajorType.Sequence, -1, offset, output));
                encoderContext.encode( //
                        (offset, output) -> Encoder.encodeLengthAndValue(MajorType.Sequence, headers.length, offset, output));

                for (String header : headers) {
                    encoderContext.encode((offset, output) -> Encoder.putString(header, offset, output));
                }
                return new CustomTableRowBuilderImpl(headers.length, encoderContext);
            }
        }

        private static class CustomTableRowBuilderImpl
                implements CustomTableRowBuilder {

            private final EncoderContext encoderContext;
            private final int maxElements;

            private CustomTableRowBuilderImpl(int maxElements, EncoderContext encoderContext) {
                this.maxElements = maxElements;
                this.encoderContext = encoderContext;
            }

            @Override
            public CustomTableColumnBuilder<CustomTableRowBuilder> putRow() {
                encoderContext.encode((offset, output) -> Encoder.encodeLengthAndValue(MajorType.Sequence, -1, offset, output));
                return new CustomTableColumnBuilderImpl(maxElements, this, encoderContext);
            }

            @Override
            public <B> TagBuilderConsumer<B> endSemanticTag() {
                encoderContext.encode((offset, output) -> output.write(offset++, (byte) OPCODE_BREAK_MASK));
                return null;
            }
        }

        private static class CustomTableColumnBuilderImpl
                extends AbstractStreamValueBuilder<CustomTableColumnBuilder<CustomTableRowBuilder>>
                implements CustomTableColumnBuilder<CustomTableRowBuilder> {

            private final CustomTableRowBuilder builder;
            private final int maxElements;

            private int elements;

            protected CustomTableColumnBuilderImpl(int maxElements, CustomTableRowBuilder builder,
                                                   EncoderContext encoderContext) {
                super(encoderContext);
                this.builder = builder;
                this.maxElements = maxElements;
            }

            @Override
            public CustomTableRowBuilder endRow() {
                encoderContext.encode((offset, output) -> output.write(offset++, (byte) OPCODE_BREAK_MASK));
                return builder;
            }

            @Override
            protected void validate() {
                if (maxElements > -1 && elements >= maxElements) {
                    throw new IllegalStateException("Cannot add another element, maximum element count reached");
                }
                elements++;
            }
        }
    }

    public enum CustomTableValueType
            implements ValueType {

        CustomTable
    }

}
