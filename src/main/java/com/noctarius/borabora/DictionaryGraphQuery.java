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

import com.noctarius.borabora.spi.QueryContext;

import java.util.Objects;
import java.util.function.Predicate;

import static com.noctarius.borabora.Bytes.readUInt8;

abstract class DictionaryGraphQuery
        implements GraphQuery {

    protected final StreamPredicate predicate;

    DictionaryGraphQuery(StreamPredicate predicate) {
        Objects.requireNonNull(predicate, "predicate must be set");
        this.predicate = predicate;
    }

    @Override
    public long access(long offset, QueryContext queryContext) {
        Input input = queryContext.input();
        short head = readUInt8(input, offset);
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
        return o instanceof DictionaryGraphQuery;
    }

    @Override
    public String toString() {
        return "DictionaryGraphQuery{" + "predicate=" + predicate + '}';
    }

    static DictionaryGraphQuery stringMatcher(String key) {
        return new StringDictionaryGraphQuery(key);
    }

    static DictionaryGraphQuery intMatcher(long key) {
        return new IntDictionaryGraphQuery(key);
    }

    static DictionaryGraphQuery floatMatcher(double key) {
        return new FloatDictionaryGraphQuery(key);
    }

    static DictionaryGraphQuery predicateMatcher(Predicate<Value> predicate) {
        return new PredicateDictionaryGraphQuery(predicate);
    }

    private static final class StringDictionaryGraphQuery
            extends DictionaryGraphQuery {

        private final String value;

        private StringDictionaryGraphQuery(String value) {
            super(StreamPredicates.matchString(value));
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof StringDictionaryGraphQuery)) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }

            StringDictionaryGraphQuery that = (StringDictionaryGraphQuery) o;
            return value.equals(that.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public String toString() {
            return "StringDictionaryGraphQuery{" + "value='" + value + '\'' + '}';
        }
    }

    private static final class IntDictionaryGraphQuery
            extends DictionaryGraphQuery {

        private final long value;

        private IntDictionaryGraphQuery(long value) {
            super(StreamPredicates.matchInt(value));
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof IntDictionaryGraphQuery)) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }

            IntDictionaryGraphQuery that = (IntDictionaryGraphQuery) o;
            return value == that.value;
        }

        @Override
        public int hashCode() {
            return (int) (value ^ (value >>> 32));
        }

        @Override
        public String toString() {
            return "IntDictionaryGraphQuery{" + "value=" + value + '}';
        }
    }

    private static final class FloatDictionaryGraphQuery
            extends DictionaryGraphQuery {

        private final double value;

        private FloatDictionaryGraphQuery(double value) {
            super(StreamPredicates.matchFloat(value));
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof FloatDictionaryGraphQuery)) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }

            FloatDictionaryGraphQuery that = (FloatDictionaryGraphQuery) o;
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
            return "FloatDictionaryGraphQuery{" + "value=" + value + '}';
        }
    }

    private static final class PredicateDictionaryGraphQuery
            extends DictionaryGraphQuery {

        private PredicateDictionaryGraphQuery(Predicate<Value> predicate) {
            super(StreamPredicates.matchValue(predicate));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof PredicateDictionaryGraphQuery)) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }

            PredicateDictionaryGraphQuery that = (PredicateDictionaryGraphQuery) o;
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
