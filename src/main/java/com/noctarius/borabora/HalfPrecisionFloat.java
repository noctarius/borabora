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

public final class HalfPrecisionFloat
        extends Number {

    private final float value;

    private HalfPrecisionFloat(float value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        return Float.valueOf(value).intValue();
    }

    @Override
    public long longValue() {
        return Float.valueOf(value).longValue();
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return Float.valueOf(value).doubleValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HalfPrecisionFloat)) {
            return false;
        }

        HalfPrecisionFloat that = (HalfPrecisionFloat) o;

        return Float.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return (value != +0.0f ? Float.floatToIntBits(value) : 0);
    }

    @Override
    public String toString() {
        return "HalfPrecisionFloat{" + "value=" + value + '}';
    }

    public static HalfPrecisionFloat valueOf(float value) {
        return new HalfPrecisionFloat(value);
    }

}
