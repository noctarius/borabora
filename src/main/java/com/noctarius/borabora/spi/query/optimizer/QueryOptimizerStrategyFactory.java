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
package com.noctarius.borabora.spi.query.optimizer;

import java.util.List;

/**
 * The <tt>QueryOptimizerStrategyFactory</tt> interface defines the factory to create an
 * instance of a {@link QueryOptimizerStrategy} to apply rules and priorities specific to
 * an implementation. The returned <tt>QueryOptimizerStrategy</tt> binds the provided set
 * of {@link QueryOptimizer}s.
 */
public interface QueryOptimizerStrategyFactory {

    /**
     * Creates a new instance of a {@link QueryOptimizerStrategy} that binds the set of
     * {@link QueryOptimizer}s.
     *
     * @param queryOptimizers the QueryOptimizers to apply with the created strategy
     * @return a new QueryOptimizerStrategy binding the given set of optimiizers
     */
    QueryOptimizerStrategy newQueryOptimizerStrategy(List<QueryOptimizer> queryOptimizers);

}
