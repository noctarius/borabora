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

class TypeMatcherGraphQuery
        implements GraphQuery {

    private final TypeSpec typeSpec;
    private final boolean required;

    TypeMatcherGraphQuery(TypeSpec typeSpec, boolean required) {
        this.typeSpec = typeSpec;
        this.required = required;
    }

    @Override
    public long access(Decoder stream, long offset, Collection<SemanticTagProcessor> processors) {
        short head = stream.transientUint8(offset);
        MajorType majorType = MajorType.findMajorType(head);
        ValueType valueType = ValueTypes.valueType(stream, offset);
        if (typeSpec.valid(majorType, stream, offset)) {
            return offset;
        }
        if (required) {
            String msg = String.format("Element at offset %s is not of type %s but %s", offset, this.typeSpec, valueType);
            throw new WrongTypeException(msg);
        }
        return offset;
    }

}
