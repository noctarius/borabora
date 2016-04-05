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

final class BuiltInSemanticTagProcessor
        implements SemanticTagProcessor<Object> {

    static final SemanticTagProcessor INSTANCE = new BuiltInSemanticTagProcessor();

    private BuiltInSemanticTagProcessor() {
    }

    @Override
    public boolean handles(Input input, long offset) {
        return ValueTypes.valueType(input, offset) != null;
    }

    @Override
    public Object process(Input input, long offset, long length, Collection<SemanticTagProcessor> processors) {
        ValueTypes valueType = ValueTypes.valueType(input, offset);
        return valueType.process(input, offset, length, processors);
    }

    @Override
    public TypeSpec handles(int tagId) {
        for (TypeSpec typeSpec : TypeSpecs.values()) {
            if (typeSpec.tagId() == tagId) {
                return typeSpec;
            }
        }
        return null;
    }

}
