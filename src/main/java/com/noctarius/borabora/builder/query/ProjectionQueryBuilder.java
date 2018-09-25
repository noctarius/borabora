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
package com.noctarius.borabora.builder.query;

import com.noctarius.borabora.spi.builder.BuilderStackPush;

/**
 * The <tt>ProjectionQueryBuilder</tt> interface is used to place a runtime
 * created sequence or dictionary (projection) at the current position on the
 * query. Projections are allowed at the base of the query or inside other
 * projected collections created by the same query instance.
 *
 * @param <T> the parent's builder type
 */
public interface ProjectionQueryBuilder<T> {

    /**
     * Defines a new projected dictionary. The returned {@link DictionaryQueryBuilder}
     * is used to define entries inside the new projected dictionary.
     *
     * @return the DictionaryQueryBuilder to define entries of the projected dictionary
     */
    @BuilderStackPush
    DictionaryQueryBuilder<T> asDictionary();

    /**
     * Defines a new projected sequence. The returned {@link SequenceQueryBuilder}
     * is used to define elements inside the new projected sequence.
     *
     * @return the SequenceQueryBuilder to define elements of the projected sequence
     */
    @BuilderStackPush
    SequenceQueryBuilder<T> asSequence();

}
