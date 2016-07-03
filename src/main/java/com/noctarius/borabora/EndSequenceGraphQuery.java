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

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

class EndSequenceGraphQuery
        implements GraphQuery {

    static final GraphQuery INSTANCE = new EndSequenceGraphQuery();

    private EndSequenceGraphQuery() {
    }

    @Override
    public long access(long offset, QueryContext queryContext) {
        // Generated underlying list
        List<Value> entries = queryContext.queryStackPop();

        // Build the new Sequence based by these entries
        Sequence sequence = new ListBackedSequence(entries, queryContext);

        // Push dictionary value
        queryContext.queryStackPush(new ObjectValue(MajorType.Sequence, ValueTypes.Sequence, sequence));

        return -2;
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

}
