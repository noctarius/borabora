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
import com.noctarius.borabora.spi.StreamableIterable;

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
            extends AbstractJavaBackedDataStructure
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
            return findValue(predicate, keys().iterator(), queryContext) != null;
        }

        @Override
        public boolean containsValue(Predicate<Value> predicate) {
            return findValue(predicate, values().iterator(), queryContext) != null;
        }

        @Override
        public Value get(Predicate<Value> predicate) {
            Value key = findValue(predicate, keys().iterator(), queryContext);
            return entries.get(key);
        }

        @Override
        public StreamableIterable<Value> keys() {
            return new SimpleStreamableIterable<>(entries.keySet());
        }

        @Override
        public StreamableIterable<Value> values() {
            return new SimpleStreamableIterable<>(entries.values());
        }

        @Override
        public StreamableIterable<Map.Entry<Value, Value>> entries() {
            return new SimpleStreamableIterable<>(entries.entrySet());
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

        @Override
        public String asString() {
            StringBuilder sb = new StringBuilder("[");
            for (Map.Entry<Value, Value> entry : entries.entrySet()) {
                Value key = entry.getKey();
                Value value = entry.getValue();
                sb.append(key.asString()).append('=').append(value.asString()).append(", ");
            }
            return sb.deleteCharAt(sb.length() - 1).deleteCharAt(sb.length() - 1).append(']').toString();
        }
    }

    private static class SimpleStreamableIterable<T>
            implements StreamableIterable<T> {

        private final Iterable<T> iterable;

        private SimpleStreamableIterable(Iterable<T> iterable) {
            this.iterable = iterable;
        }

        @Override
        public Iterator<T> iterator() {
            return iterable.iterator();
        }
    }

}
