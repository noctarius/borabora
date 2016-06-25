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

import java.util.Map;

class EndDictionaryEntryGraphQuery
        implements GraphQuery {

    static final GraphQuery INSTANCE = new EndDictionaryEntryGraphQuery();

    private EndDictionaryEntryGraphQuery() {
    }

    @Override
    public long access(long offset, QueryContext queryContext) {
        Value value;
        if (offset == -2) {
            value = queryContext.queryStackPop();
        } else {
            value = Decoder.readValue(offset, queryContext);
        }

        Value key = queryContext.queryStackPop();

        Map<Value, Value> entries = queryContext.queryStackPeek();
        entries.put(key, value);

        return offset;
    }

}
