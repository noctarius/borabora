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

import com.noctarius.borabora.spi.ByteSizes;
import com.noctarius.borabora.spi.Decoder;
import com.noctarius.borabora.spi.QueryContext;

import java.util.Objects;
import java.util.function.Predicate;

abstract class DictionaryQuery
        implements Query {

    protected final Predicate<Value> predicate;

    DictionaryQuery(Predicate<Value> predicate) {
        Objects.requireNonNull(predicate, "predicate must be set");
        this.predicate = predicate;
    }

    @Override
    public long access(long offset, QueryContext queryContext) {
        Input input = queryContext.input();
        short head = Decoder.readUInt8(input, offset);
        MajorType majorType = MajorType.findMajorType(head);
        if (majorType != MajorType.Dictionary) {
            throw new WrongTypeException("Not a dictionary");
        }

        // Skip head
        long headByteSize = ByteSizes.headByteSize(input, offset);
        offset += headByteSize;

        return Decoder.findByDictionaryKey(predicate, offset, queryContext);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof DictionaryQuery;
    }

    @Override
    public String toString() {
        return "DictionaryQuery{" + "predicate=" + predicate + '}';
    }

    static DictionaryQuery stringMatcher(String key) {
        return new StringDictionaryQuery(key);
    }

    static DictionaryQuery intMatcher(long key) {
        return new IntDictionaryQuery(key);
    }

    static DictionaryQuery floatMatcher(double key) {
        return new FloatDictionaryQuery(key);
    }

    static DictionaryQuery predicateMatcher(Predicate<Value> predicate) {
        return new PredicateDictionaryQuery(predicate);
    }

    private static final class StringDictionaryQuery
            extends DictionaryQuery {

        private final String value;

        private StringDictionaryQuery(String value) {
            super(Predicates.matchString(value));
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof StringDictionaryQuery)) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }

            StringDictionaryQuery that = (StringDictionaryQuery) o;
            return value.equals(that.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public String toString() {
            return "StringDictionaryQuery{" + "value='" + value + '\'' + '}';
        }
    }

    private static final class IntDictionaryQuery
            extends DictionaryQuery {

        private final long value;

        private IntDictionaryQuery(long value) {
            super(Predicates.matchInt(value));
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof IntDictionaryQuery)) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }

            IntDictionaryQuery that = (IntDictionaryQuery) o;
            return value == that.value;
        }

        @Override
        public int hashCode() {
            return (int) (value ^ (value >>> 32));
        }

        @Override
        public String toString() {
            return "IntDictionaryQuery{" + "value=" + value + '}';
        }
    }

    private static final class FloatDictionaryQuery
            extends DictionaryQuery {

        private final double value;

        private FloatDictionaryQuery(double value) {
            super(Predicates.matchFloat(value));
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof FloatDictionaryQuery)) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }

            FloatDictionaryQuery that = (FloatDictionaryQuery) o;
            return Double.compare(that.value, value) == 0;
        }

        @Override
        public int hashCode() {
            long temp;
            temp = Double.doubleToLongBits(value);
            return (int) (temp ^ (temp >>> 32));
        }

        @Override
        public String toString() {
            return "FloatDictionaryQuery{" + "value=" + value + '}';
        }
    }

    private static final class PredicateDictionaryQuery
            extends DictionaryQuery {

        private PredicateDictionaryQuery(Predicate<Value> predicate) {
            super(predicate);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof PredicateDictionaryQuery)) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }

            PredicateDictionaryQuery that = (PredicateDictionaryQuery) o;
            String predicateClass = predicate.getClass().getName();
            String otherPredicateClass = that.predicate.getClass().getName();

            if (predicateClass.contains("$$Lambda$") && otherPredicateClass.contains("$$Lambda$")) {
                return predicateClass.equals(otherPredicateClass);
            }

            return predicate != null ? predicate.equals(that.predicate) : that.predicate == null;
        }

        @Override
        public int hashCode() {
            if (predicate == null) {
                return 0;
            }
            String predicateClass = predicate.getClass().getName();
            if (predicateClass.contains("$$Lambda$")) {
                return predicateClass.hashCode();
            }
            return predicate.hashCode();
        }
    }

}
