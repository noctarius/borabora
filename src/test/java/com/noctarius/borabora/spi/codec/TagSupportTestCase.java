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

import com.noctarius.borabora.AbstractTestCase;
import com.noctarius.borabora.Input;
import com.noctarius.borabora.Output;
import com.noctarius.borabora.ValueType;
import com.noctarius.borabora.Writer;
import com.noctarius.borabora.spi.TypeSpec;
import com.noctarius.borabora.spi.query.QueryContext;
import org.junit.Test;

import javax.xml.soap.SOAPException;

import static com.noctarius.borabora.spi.Constants.EMPTY_BYTE_ARRAY;
import static com.noctarius.borabora.spi.codec.TagSupport.semanticTag;

public class TagSupportTestCase
        extends AbstractTestCase {

    @Test
    public void call_constructor() {
        callConstructor(TagSupport0.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_class_not_an_interface() {
        Writer writer = Writer.newBuilder().build();
        writer.newGraphBuilder(Output.toByteArray(EMPTY_BYTE_ARRAY)) //
              .putTag(semanticTag(NonInterface.class));
    }

    @Test(expected = NullPointerException.class)
    public void fail_execute_nullpointer_exception1() {
        Writer writer = Writer.newBuilder().addTagStrategy(NullPointer1Factory.INSTANCE).build();
        writer.newGraphBuilder(Output.toByteArray(EMPTY_BYTE_ARRAY)) //
              .putTag(semanticTag(NullPointer1.class).endSemanticTag()).finishStream();
    }

    @Test(expected = ArithmeticException.class)
    public void fail_execute_invocation_target_exception_unwrapped_arithmetic_exception() {
        Writer writer = Writer.newBuilder().addTagStrategy(InvocationTargetException1Factory.INSTANCE).build();
        writer.newGraphBuilder(Output.toByteArray(EMPTY_BYTE_ARRAY)) //
              .putTag(semanticTag(InvocationTargetException1.class).test().endSemanticTag()).finishStream();
    }

    @Test(expected = SOAPException.class)
    public void fail_execute_invocation_target_exception_unwrapped_soap_exception()
            throws Exception {

        Writer writer = Writer.newBuilder().addTagStrategy(InvocationTargetException2Factory.INSTANCE).build();

        try {
            writer.newGraphBuilder(Output.toByteArray(EMPTY_BYTE_ARRAY)) //
                  .putTag(semanticTag(InvocationTargetException2.class).test().endSemanticTag()).finishStream();
        } catch (RuntimeException e) {
            if (e.getCause() instanceof SOAPException) {
                throw (SOAPException) e.getCause();
            }
            throw e;
        }
    }

    public static class NonInterface {
    }

    public interface NullPointer1
            extends TagBuilder {
    }

    private static class NullPointer1Factory
            implements TagStrategy<NullPointer1, Object> {

        private static final TagStrategy INSTANCE = new NullPointer1Factory();

        @Override
        public NullPointer1 newTagBuilder(EncoderContext encoderContext) {
            return null;
        }

        @Override
        public int tagId() {
            return 0;
        }

        @Override
        public ValueType valueType() {
            return null;
        }

        @Override
        public Class<NullPointer1> tagBuilderType() {
            return NullPointer1.class;
        }

        @Override
        public TagEncoder<Object> tagEncoder() {
            return null;
        }

        @Override
        public TagDecoder<Object> tagDecoder() {
            return null;
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
        public Object process(ValueType valueType, long offset, long length, QueryContext queryContext) {
            return null;
        }

        @Override
        public boolean handles(Input input, long offset) {
            return false;
        }

        @Override
        public TypeSpec handles(long tagId) {
            return null;
        }

        @Override
        public ValueType valueType(Input input, long offset) {
            return null;
        }
    }

    public interface InvocationTargetException1
            extends TagBuilder {

        InvocationTargetException1 test();
    }

    private static class InvocationTargetException1Factory
            implements TagStrategy<InvocationTargetException1, Object> {

        private static final TagStrategy INSTANCE = new InvocationTargetException1Factory();

        @Override
        public InvocationTargetException1 newTagBuilder(EncoderContext encoderContext) {
            return new InvocationTargetException1Impl();
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
        public Class<InvocationTargetException1> tagBuilderType() {
            return InvocationTargetException1.class;
        }

        @Override
        public TagEncoder<Object> tagEncoder() {
            return null;
        }

        @Override
        public TagDecoder<Object> tagDecoder() {
            return null;
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
        public Object process(ValueType valueType, long offset, long length, QueryContext queryContext) {
            return null;
        }

        @Override
        public boolean handles(Input input, long offset) {
            return false;
        }

        @Override
        public TypeSpec handles(long tagId) {
            return null;
        }

        @Override
        public ValueType valueType(Input input, long offset) {
            return null;
        }

        private static class InvocationTargetException1Impl
                implements InvocationTargetException1 {

            @Override
            public <B> TagBuilderConsumer<B> endSemanticTag() {
                return null;
            }

            @Override
            public InvocationTargetException1 test() {
                throw new ArithmeticException();
            }
        }
    }

    public interface InvocationTargetException2
            extends TagBuilder {

        InvocationTargetException2 test()
                throws Exception;
    }

    private static class InvocationTargetException2Factory
            implements TagStrategy<InvocationTargetException2, Object> {

        private static final TagStrategy INSTANCE = new InvocationTargetException2Factory();

        @Override
        public InvocationTargetException2 newTagBuilder(EncoderContext encoderContext) {
            return new InvocationTargetException2Impl();
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
        public Class<InvocationTargetException2> tagBuilderType() {
            return InvocationTargetException2.class;
        }

        @Override
        public TagEncoder<Object> tagEncoder() {
            return null;
        }

        @Override
        public TagDecoder<Object> tagDecoder() {
            return null;
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
        public Object process(ValueType valueType, long offset, long length, QueryContext queryContext) {
            return null;
        }

        @Override
        public boolean handles(Input input, long offset) {
            return false;
        }

        @Override
        public TypeSpec handles(long tagId) {
            return null;
        }

        @Override
        public ValueType valueType(Input input, long offset) {
            return null;
        }

        private static class InvocationTargetException2Impl
                implements InvocationTargetException2 {

            @Override
            public <B> TagBuilderConsumer<B> endSemanticTag() {
                return null;
            }

            @Override
            public InvocationTargetException2 test()
                    throws Exception {

                throw new SOAPException();
            }
        }
    }

}
