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

import com.noctarius.borabora.builder.ParserBuilder;
import com.noctarius.borabora.spi.SelectStatementStrategy;
import com.noctarius.borabora.spi.TagDecoder;

import java.util.ArrayList;
import java.util.List;

final class ParserBuilderImpl
        implements ParserBuilder {

    private final List<TagDecoder> processors = new ArrayList<>();
    private SelectStatementStrategy selectStatementStrategy;
    private boolean binarySelectStatement = true;

    public ParserBuilderImpl(SelectStatementStrategy selectStatementStrategy) {
        withSemanticTagProcessor(BuiltInTagDecoder.INSTANCE);
        this.selectStatementStrategy = selectStatementStrategy;
    }

    @Override
    public <V> ParserBuilder withSemanticTagProcessor(TagDecoder<V> processor) {
        processors.add(processor);
        return this;
    }

    @Override
    public ParserBuilder asBinarySelectStatement() {
        binarySelectStatement = true;
        return this;
    }

    @Override
    public ParserBuilder asObjectSelectStatement() {
        binarySelectStatement = false;
        return this;
    }

    @Override
    public Parser build() {
        SelectStatementStrategy selectStatementStrategy = this.selectStatementStrategy;
        if (selectStatementStrategy == null) {
            selectStatementStrategy = binarySelectStatement ? //
                    BinarySelectStatementStrategy.INSTANCE : ObjectSelectStatementStrategy.INSTANCE;
        }

        return new ParserImpl(processors, selectStatementStrategy);
    }

}
