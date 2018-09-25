/*
 * Copyright (c) 2016-2018, Christoph Engelbert (aka noctarius) and
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

import static com.noctarius.borabora.spi.io.Constants.EMPTY_BYTE_ARRAY;

final class NullValue
        implements Value {

    NullValue() {
    }

    @Override
    public MajorType majorType() {
        return MajorType.Unknown;
    }

    @Override
    public ValueType valueType() {
        return ValueTypes.Null;
    }

    @Override
    public <V> V tag() {
        return null;
    }

    @Override
    public Number number() {
        return null;
    }

    @Override
    public Sequence sequence() {
        return null;
    }

    @Override
    public Dictionary dictionary() {
        return null;
    }

    @Override
    public String string() {
        return null;
    }

    @Override
    public Boolean bool() {
        return null;
    }

    @Override
    public byte[] bytes() {
        return null;
    }

    @Override
    public byte[] raw() {
        return EMPTY_BYTE_ARRAY;
    }

    @Override
    public <V> V byValueType() {
        return null;
    }

    @Override
    public long offset() {
        return -1;
    }

    @Override
    public Input input() {
        return null;
    }

    @Override
    public String toString() {
        return "NullValue{valueType=Null, offset=-1, value=null}";
    }

    @Override
    public String asString() {
        return "Null{ null }";
    }

}
