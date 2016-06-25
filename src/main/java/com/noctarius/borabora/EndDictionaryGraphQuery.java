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

import com.noctarius.borabora.spi.Dictionary;
import com.noctarius.borabora.spi.QueryContext;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;

class EndDictionaryGraphQuery
        implements GraphQuery {

    static final GraphQuery INSTANCE = new EndDictionaryGraphQuery();

    private EndDictionaryGraphQuery() {
    }

    @Override
    public long access(long offset, QueryContext queryContext) {
        // Generated underlying map
        Map<Value, Value> entries = queryContext.queryStackPop();

        // Build the new Dictionary based by these entries
        Dictionary dictionary = new MapBackedDictionary(entries, queryContext);

        // Push dictionary value
        queryContext.queryStackPush(new ObjectValue(MajorType.Dictionary, ValueTypes.Dictionary, dictionary));

        return -2;
    }

    private static class MapBackedDictionary
            implements Dictionary {

        private final Map<Value, Value> entries;
        private final QueryContext queryContext;

        private MapBackedDictionary(Map<Value, Value> entries, QueryContext queryContext) {
            this.entries = entries;
            this.queryContext = queryContext;
        }

        @Override
        public long size() {
            return entries.size();
        }

        @Override
        public boolean isEmpty() {
            return entries.isEmpty();
        }

        @Override
        public boolean containsKey(Predicate<Value> predicate) {
            return findValue(predicate, keys()) != null;
        }

        @Override
        public boolean containsKey(StreamPredicate predicate) {
            throw new UnsupportedOperationException("No underlying stream available for a runtime generated dictionary");
        }

        @Override
        public boolean containsValue(Predicate<Value> predicate) {
            return findValue(predicate, values()) != null;
        }

        @Override
        public boolean containsValue(StreamPredicate predicate) {
            return findStreamValue(predicate, values()) != null;
        }

        @Override
        public Value get(Predicate<Value> predicate) {
            Value key = findValue(predicate, keys());
            return entries.get(key);
        }

        @Override
        public Value get(StreamPredicate predicate) {
            Value key = findStreamValue(predicate, keys());
            return entries.get(key);
        }

        @Override
        public Iterable<Value> keys() {
            return entries.keySet();
        }

        @Override
        public Iterable<Value> values() {
            return entries.values();
        }

        @Override
        public Iterator<Map.Entry<Value, Value>> iterator() {
            return entries.entrySet().iterator();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            for (Map.Entry<Value, Value> entry : entries.entrySet()) {
                Value key = entry.getKey();
                Value value = entry.getValue();
                sb.append(key).append('=').append(value).append(", ");
            }
            return sb.deleteCharAt(sb.length() - 1).deleteCharAt(sb.length() - 1).append(']').toString();
        }

        private Value findValue(Predicate<Value> predicate, Iterable<Value> iterable) {
            Iterator<Value> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                Value value = iterator.next();
                if (predicate.test(value)) {
                    return value;
                }
            }
            return Value.NULL_VALUE;
        }

        private Value findStreamValue(StreamPredicate predicate, Iterable<Value> iterable) {
            Iterator<Value> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                Value value = iterator.next();

                if (value.offset() < 0) {
                    throw new IllegalStateException("At least one element is not a valid stream value");
                }

                MajorType majorType = value.majorType();
                ValueType valueType = value.valueType();

                if (predicate.test(majorType, valueType, value.offset(), queryContext)) {
                    return value;
                }
            }
            return Value.NULL_VALUE;
        }

    }

}
