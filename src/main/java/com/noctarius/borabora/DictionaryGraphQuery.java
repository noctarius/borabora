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

import java.util.Collection;
import java.util.function.Predicate;

final class DictionaryGraphQuery
        implements GraphQuery {

    private final Predicate<Value> predicate;

    DictionaryGraphQuery(Predicate<Value> predicate) {
        this.predicate = predicate;
    }

    @Override
    public long access(Decoder stream, long index, Collection<SemanticTagProcessor> processors) {
        short head = stream.transientUint8(index);
        MajorType majorType = MajorType.findMajorType(head);
        if (majorType != MajorType.Dictionary) {
            throw new IllegalStateException("Not a dictionary");
        }

        // Skip head
        long headByteSize = ByteSizes.headByteSize(stream, index);
        long keyCount = ElementCounts.dictionaryElementCount(stream, index);
        index += headByteSize;

        return stream.findByDictionaryKey(predicate, index, keyCount, processors);
    }

}
