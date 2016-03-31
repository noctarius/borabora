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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

final class DictionaryGraphQuery
        implements GraphQuery {

    private final Predicate<Value> predicate;

    DictionaryGraphQuery(Predicate<Value> predicate) {
        Objects.requireNonNull(predicate, "predicate must be set");
        this.predicate = predicate;
    }

    @Override
    public long access(Decoder stream, long offset, Collection<SemanticTagProcessor> processors) {
        short head = stream.transientUint8(offset);
        MajorType majorType = MajorType.findMajorType(head);
        if (majorType != MajorType.Dictionary) {
            throw new WrongTypeException("Not a dictionary");
        }

        // Skip head
        long headByteSize = ByteSizes.headByteSize(stream, offset);
        long keyCount = ElementCounts.dictionaryElementCount(stream, offset);
        offset += headByteSize;

        return stream.findByDictionaryKey(predicate, offset, keyCount, processors);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DictionaryGraphQuery)) {
            return false;
        }

        DictionaryGraphQuery that = (DictionaryGraphQuery) o;

        String predicateClass = predicate.getClass().getName();
        String otherPredicateClass = that.predicate.getClass().getName();

        if (predicateClass.contains("$$Lambda$") && otherPredicateClass.contains("$$Lambda$")) {
            return predicateClass.equals(otherPredicateClass);
        }

        return predicate != null ? predicate.equals(that.predicate) : that.predicate == null;
    }

    @Override
    public int hashCode() {
        return predicate != null ? predicate.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DictionaryGraphQuery{" + "predicate=" + predicate + '}';
    }

    static Predicate<Value> matchInt(long value) {
        return (v) -> {
            if (!v.valueType().matches(ValueTypes.Number)) {
                return false;
            }
            Number n = v.number();
            if (n instanceof BigInteger) {
                return n.equals(BigInteger.valueOf(value));
            }
            return value == n.longValue();
        };
    }

    static Predicate<Value> matchFloat(double value) {
        return (v) -> {
            Number n = v.number();
            if (n instanceof BigDecimal) {
                return n.equals(BigDecimal.valueOf(value));
            }
            return value == n.doubleValue();
        };
    }

    static Predicate<Value> matchString(String value) {
        return (v) -> v.string().equals(value);
    }

}
