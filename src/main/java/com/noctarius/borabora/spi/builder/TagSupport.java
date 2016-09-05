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
package com.noctarius.borabora.spi.builder;

import com.noctarius.borabora.spi.codec.TagStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * The <tt>TagSupport</tt> class provides the base support method for encoding a builtin or custom
 * semantic tag using the fluent API. The {@link #semanticTag(Class)} method is used in conjunction
 * with builder pattern interfaces for collecting information and executing them in the right order.
 * <p>Builders can be built from multiple steps, however the last step in each builder chain
 * <b>MUST</b> be an extension-interface of type {@link TagBuilder}, or the type itself, to provide the
 * {@link TagBuilder#endSemanticTag()} method.</p>
 * <p>The support API uses proxies to collect method calls to execute them in the right order afterwards.
 * For implementation purpose, two annotations are necessary to support the proxy API.</p>
 * <p>The annotation {@link BuilderStackPush} has to be added when a new proxy level is added, for methods
 * like <tt>putMultiItem</tt> and {@link BuilderStackPop} correspondingly when the proxy level is decreased,
 * like for methods such as <tt>endMultiItem</tt>.</p>
 * <p>Builtin semantic tags are directly supported using the {@link com.noctarius.borabora.builder.encoder.ValueBuilder}
 * API, anyhow custom user provided data type encoders have to be encoded using this support API.</p>
 * <p>A simple, one level builder would look like the following snippet:</p>
 * <pre>
 * public interface MyTagBuilder {
 *  {@literal @}BuilderStackPush
 *   TagBuilder putMyTag(MyTag value)
 * }
 * </pre>
 * <p>Followed by the actual implementation of the builder:</p>
 * <pre>
 * public final class MyTagBuilderImpl implements TagBuilder {
 *   private final EncoderContext encoderContext;
 *
 *   public MyTagBuilderImpl(EncoderContext encoderContext) {
 *     this.encoderContext = encoderContext;
 *   }
 *
 *  {@literal @}Override
 *   public TagBuilder putMyTag(MyTag value) {
 *     // Encode the actual value or a null element in case of value is null
 *     encoderContext.encodeNullOrType(value, (offset, output) -&gt; {
 *         // Write a semantic tag header first
 *         offset = Encoder.putSemanticTag(MyTagStrategy.TAG_ID, offset, output);
 *         // Write the actual element
 *         return Encoder.putNumber(value.realValue, offset, output);
 *     }
 *   }
 * }
 * </pre>
 * <p>To use this builder the implementation has to be provided by a
 * {@link com.noctarius.borabora.spi.codec.TagStrategy#newTagBuilder(EncoderContext)} and the interface-type of the
 * builder must be returned from the same TagStrategy instance with {@link TagStrategy#tagBuilderType()}. The TagStrategy
 * also has to be registered with the {@link com.noctarius.borabora.Writer} during the configuration with either of
 * the following methods:</p>
 * <ul>
 * <li>{@link com.noctarius.borabora.builder.WriterBuilder#addTagStrategy(TagStrategy)}</li>
 * <li>{@link com.noctarius.borabora.builder.WriterBuilder#addTagStrategies(TagStrategy, TagStrategy)} </li>
 * <li>{@link com.noctarius.borabora.builder.WriterBuilder#addTagStrategies(TagStrategy, TagStrategy, TagStrategy[])}</li>
 * <li>{@link com.noctarius.borabora.builder.WriterBuilder#addTagStrategies(Iterable)}</li>
 * </ul>
 * <p>Usage of this new semantic tag builder is now easy:</p>
 * <pre>
 * Writer writer = Writer.newBuilder().addTagStrategy( new MyTagStrategy() ).build();
 * Output output = ...;
 * GraphBuilder graphBuilder = writer.newGraphBuilder( output );
 * graphBuilder.putTag(
 *     semanticTag( MyTagBuilder.class ).putMyTag( getMyTag() ).endSemanticTag() )
 * ).finishStream();
 * </pre>
 * <p>Since the result of the {@link #semanticTag(Class)} and {@link TagBuilder#endSemanticTag()} is a lazy
 * evaluator of type {@link TagBuilderConsumer} it can also be stored in a variable and be used multiple times,
 * for example like static values in network protocols:</p>
 * <pre>
 * TagBuilderConsumer myTagWriter = semanticTag( MyTagBuilder.class ).putMyTag( getMyTag() ).endSemanticTag() );
 *
 * ...
 *
 * graphBuilder.putTag( myTagWriter );
 * </pre>
 *
 * @see TagStrategy
 * @see TagBuilder
 * @see EncoderContext
 * @see com.noctarius.borabora.ValueType
 * @see com.noctarius.borabora.spi.codec.TagEncoder
 * @see com.noctarius.borabora.spi.codec.TagDecoder
 */
public interface TagSupport {

    /**
     * Creates a semantic tag writer pipeline. The given type must be an interface that
     * represents the start of a builder. Passing a type of a non-interface will throw an
     * {@link IllegalArgumentException}. Calls on the interface methods are captured and
     * stored. When {@link TagBuilder#endSemanticTag()} is called the returned
     * {@link TagBuilderConsumer} is automatically generated by all captured calls
     * and immediately replayed on the actual stream.
     *
     * @param type the interface type of the builder
     * @param <S>  the type of the builder
     * @return a proxy to the given interface type
     * @throws IllegalArgumentException if the passed class does not represent an interface type
     */
    static <S> S semanticTag(Class<S> type) {
        List<TagSupport0.MethodInvocation> methodInvocations = new ArrayList<>();
        TagSupport0.MethodInvocationHandler methodInvocationHandler = new TagSupport0.MethodInvocationHandler(type,
                methodInvocations);
        return TagSupport0.proxy(type, methodInvocationHandler);
    }

}
