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
package com.noctarius.borabora.spi.query;

import com.noctarius.borabora.Dictionary;
import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.Sequence;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.ValueType;
import com.noctarius.borabora.ValueTypes;
import com.noctarius.borabora.spi.ObjectValue;
import com.noctarius.borabora.spi.RelocatableStreamValue;
import com.noctarius.borabora.spi.StreamableIterable;
import com.noctarius.borabora.spi.codec.Decoder;
import com.noctarius.borabora.spi.pipeline.PipelineStage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ObjectSelectStatementStrategy
        implements SelectStatementStrategy {

    public static final SelectStatementStrategy INSTANCE = new ObjectSelectStatementStrategy();

    private ObjectSelectStatementStrategy() {
    }

    @Override
    public void beginSelect(QueryContext queryContext) {
    }

    @Override
    public void finalizeSelect(QueryContext queryContext) {
        Value value = queryContext.queryStackPop();
        queryContext.consume(value);
    }

    @Override
    public void beginDictionary(QueryContext queryContext) {
        // Create a new Map to store entries, thanks to thread-safetyness :)
        Map<Value, Value> entries = new HashMap<>();

        // Push to query context stack
        queryContext.queryStackPush(entries);
    }

    @Override
    public void endDictionary(QueryContext queryContext) {
        // Generated underlying map
        Map<Value, Value> entries = queryContext.queryStackPop();

        // Build the new Dictionary based by these entries
        Dictionary dictionary = new MapBackedDictionary(entries, queryContext);

        // Push dictionary value
        queryContext.queryStackPush(new ObjectValue(MajorType.Dictionary, ValueTypes.Dictionary, dictionary));
    }

    @Override
    public void putDictionaryKey(String key, QueryContext queryContext) {
        Value keyValue = new ObjectValue(MajorType.TextString, ValueTypes.TextString, key);
        queryContext.queryStackPush(keyValue);
    }

    @Override
    public void putDictionaryKey(long key, QueryContext queryContext) {
        Value keyValue;
        if (key < 0) {
            keyValue = new ObjectValue(MajorType.NegativeInteger, ValueTypes.NInt, key);
        } else {
            keyValue = new ObjectValue(MajorType.UnsignedInteger, ValueTypes.UInt, key);
        }
        queryContext.queryStackPush(keyValue);
    }

    @Override
    public void putDictionaryKey(double key, QueryContext queryContext) {
        Value keyValue = new ObjectValue(MajorType.FloatingPointOrSimple, ValueTypes.Float, key);
        queryContext.queryStackPush(keyValue);
    }

    @Override
    public void putDictionaryValue(PipelineStage previousPipelineStage, QueryContext queryContext) {
        long offset = queryContext.offset();

        Value v1 = queryContext.queryStackPop();
        Object v2 = queryContext.queryStackPeek();

        Value key;
        Value value;
        if (!(v2 instanceof Value)) {
            key = v1;
            if (offset == -2) {
                value = queryContext.queryStackPop();
            } else if (offset == -1) {
                value = Value.NULL_VALUE;
            } else {
                value = Decoder.readValue(offset, queryContext);
            }
        } else {
            value = v1;
            key = queryContext.queryStackPop();
        }

        Map<Value, Value> entries = queryContext.queryStackPeek();
        entries.put(key, value);
    }

    @Override
    public void putDictionaryNullValue(QueryContext queryContext) {
        Value key = queryContext.queryStackPop();
        Value value = Value.NULL_VALUE;
        Map<Value, Value> entries = queryContext.queryStackPeek();
        entries.put(key, value);
    }

    @Override
    public void beginSequence(QueryContext queryContext) {
        // Create a new List to store entries, thanks to thread-safetyness :)
        List<Value> entries = new ArrayList<>();

        // Push to query context stack
        queryContext.queryStackPush(entries);
    }

    @Override
    public void endSequence(QueryContext queryContext) {
        // Generated underlying list
        List<Value> entries = queryContext.queryStackPop();

        // Build the new Sequence based by these entries
        Sequence sequence = new ListBackedSequence(entries, queryContext);

        // Push dictionary value
        queryContext.queryStackPush(new ObjectValue(MajorType.Sequence, ValueTypes.Sequence, sequence));
    }

    @Override
    public void putSequenceValue(PipelineStage previousPipelineStage, QueryContext queryContext) {
        Value value;
        if (queryContext.queryStackPeek() instanceof Value) {
            value = queryContext.queryStackPop();
        } else {
            long offset = queryContext.offset();

            if (offset == -2) {
                value = queryContext.queryStackPop();
            } else if (offset == -1) {
                value = Value.NULL_VALUE;
            } else {
                value = Decoder.readValue(offset, queryContext);
            }
        }
        List<Value> entries = queryContext.queryStackPeek();
        entries.add(value);
    }

    @Override
    public void putSequenceNullValue(QueryContext queryContext) {
        List<Value> entries = queryContext.queryStackPeek();
        entries.add(Value.NULL_VALUE);
    }

    private static abstract class AbstractJavaBackedDataStructure {

        protected Value findValue(Predicate<Value> predicate, Iterator<Value> iterator, QueryContext queryContext) {
            RelocatableStreamValue streamValue = new RelocatableStreamValue();
            while (iterator.hasNext()) {
                Value value = iterator.next();

                MajorType majorType = value.majorType();
                ValueType valueType = value.valueType();

                Value candidate = value;
                if (!(value instanceof ObjectValue)) {
                    streamValue.relocate(queryContext, majorType, valueType, value.offset());
                    candidate = streamValue;
                }

                if (predicate.test(candidate)) {
                    return value;
                }
            }
            return Value.NULL_VALUE;
        }

    }

    private static class ListBackedSequence
            extends AbstractJavaBackedDataStructure
            implements Sequence {

        private final List<Value> entries;
        private final QueryContext queryContext;

        private ListBackedSequence(List<Value> entries, QueryContext queryContext) {
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
        public boolean contains(Predicate<Value> predicate) {
            return findValue(predicate, iterator(), queryContext) != null;
        }

        @Override
        public Iterator<Value> iterator() {
            return entries.iterator();
        }

        @Override
        public Value[] toArray() {
            return entries.toArray(new Value[entries.size()]);
        }

        @Override
        public Value get(long sequenceIndex) {
            return entries.get((int) sequenceIndex);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            for (long i = 0; i < entries.size(); i++) {
                sb.append(get(i)).append(", ");
            }
            return sb.deleteCharAt(sb.length() - 1).deleteCharAt(sb.length() - 1).append(']').toString();
        }

        @Override
        public String asString() {
            StringBuilder sb = new StringBuilder("[");
            for (long i = 0; i < entries.size(); i++) {
                sb.append(get(i).asString()).append(", ");
            }
            return sb.deleteCharAt(sb.length() - 1).deleteCharAt(sb.length() - 1).append(']').toString();
        }
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
