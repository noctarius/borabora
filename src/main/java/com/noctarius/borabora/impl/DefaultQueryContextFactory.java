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
package com.noctarius.borabora.impl;

import com.noctarius.borabora.Input;
import com.noctarius.borabora.spi.codec.TagStrategy;
import com.noctarius.borabora.spi.query.ProjectionStrategy;
import com.noctarius.borabora.spi.query.QueryConsumer;
import com.noctarius.borabora.spi.query.QueryContext;
import com.noctarius.borabora.spi.query.QueryContextFactory;

import java.util.List;

public class DefaultQueryContextFactory
        implements QueryContextFactory {

    public static final QueryContextFactory INSTANCE = new DefaultQueryContextFactory();

    private DefaultQueryContextFactory() {
    }

    @Override
    public QueryContext newQueryContext(Input input, QueryConsumer queryConsumer, List<TagStrategy> tagStrategies,
                                        ProjectionStrategy projectionStrategy) {

        return new QueryContextImpl(input, queryConsumer, tagStrategies, projectionStrategy, this);
    }

}
