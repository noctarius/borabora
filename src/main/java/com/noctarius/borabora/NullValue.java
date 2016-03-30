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
    public byte[] raw() {
        return new byte[0];
    }

    @Override
    public <V> V byValueType() {
        return null;
    }

}
