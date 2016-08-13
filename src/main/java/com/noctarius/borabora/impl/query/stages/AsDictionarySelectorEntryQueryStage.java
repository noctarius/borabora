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
package com.noctarius.borabora.impl.query.stages;

import com.noctarius.borabora.spi.pipeline.PipelineStage;
import com.noctarius.borabora.spi.pipeline.QueryStage;
import com.noctarius.borabora.spi.pipeline.VisitResult;
import com.noctarius.borabora.spi.query.QueryContext;

import java.util.function.Consumer;

public abstract class AsDictionarySelectorEntryQueryStage
        implements QueryStage {

    protected AsDictionarySelectorEntryQueryStage() {
    }

    @Override
    public VisitResult evaluate(PipelineStage previousPipelineStage, PipelineStage pipelineStage, QueryContext pipelineContext) {
        Consumer<QueryContext> keyWriter = keyWriter();
        keyWriter.accept(pipelineContext);

        long offset = pipelineContext.offset();
        pipelineContext.offset(0);

        // Try execution of the child query subtree
        VisitResult visitResult = pipelineStage.visitChildren(pipelineContext);
        if (visitResult == VisitResult.Break || visitResult == VisitResult.Exit) {
            return visitResult;
        }

        pipelineContext.offset(offset);
        return VisitResult.Continue;
    }

    protected abstract Consumer<QueryContext> keyWriter();

    public static AsDictionarySelectorEntryQueryStage withStringKey(String key) {
        return new StringKey(key);
    }

    public static AsDictionarySelectorEntryQueryStage withIntKey(long key) {
        return new IntKey(key);
    }

    public static AsDictionarySelectorEntryQueryStage withFloatKey(double key) {
        return new FloatKey(key);
    }

    private static class StringKey
            extends AsDictionarySelectorEntryQueryStage {

        private final String key;

        private StringKey(String key) {
            this.key = key;
        }

        @Override
        protected Consumer<QueryContext> keyWriter() {
            return (queryContext -> {
                queryContext.selectStatementStrategy().putDictionaryKey(key, queryContext);
            });
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof StringKey)) {
                return false;
            }

            StringKey stringKey = (StringKey) o;

            return key != null ? key.equals(stringKey.key) : stringKey.key == null;
        }

        @Override
        public int hashCode() {
            return key != null ? key.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "DIC_ENTRY_BEGIN[ " + key + " ]";
        }
    }

    private static class IntKey
            extends AsDictionarySelectorEntryQueryStage {

        private final long key;

        private IntKey(long key) {
            this.key = key;
        }

        @Override
        protected Consumer<QueryContext> keyWriter() {
            return (queryContext -> {
                queryContext.selectStatementStrategy().putDictionaryKey(key, queryContext);
            });
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof IntKey)) {
                return false;
            }

            IntKey intKey = (IntKey) o;

            return key == intKey.key;
        }

        @Override
        public int hashCode() {
            return (int) (key ^ (key >>> 32));
        }

        @Override
        public String toString() {
            return "DIC_ENTRY_BEGIN[ " + key + " ]";
        }
    }

    private static class FloatKey
            extends AsDictionarySelectorEntryQueryStage {

        private final double key;

        private FloatKey(double key) {
            this.key = key;
        }

        @Override
        protected Consumer<QueryContext> keyWriter() {
            return (queryContext -> {
                queryContext.selectStatementStrategy().putDictionaryKey(key, queryContext);
            });
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof FloatKey)) {
                return false;
            }

            FloatKey floatKey = (FloatKey) o;

            return Double.compare(floatKey.key, key) == 0;
        }

        @Override
        public int hashCode() {
            long temp = Double.doubleToLongBits(key);
            return (int) (temp ^ (temp >>> 32));
        }

        @Override
        public String toString() {
            return "DIC_ENTRY_BEGIN[ " + key + " ]";
        }
    }

}
