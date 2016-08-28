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

/**
 * Implementations of this interface represent value types to further
 * specify over a {@link MajorType} definition. The parser can be
 * extended by implementing {@link com.noctarius.borabora.spi.codec.TagDecoder}
 * and {@link com.noctarius.borabora.spi.codec.TagEncoder} implementations
 * to support additional value types.
 */
public interface ValueType {

    /**
     * Returns the name of the value type.
     *
     * @return the name of the value type
     */
    String name();

    /**
     * Returns a specific identity in case the value type has a super type.
     * For example {@link ValueTypes#UInt} has the identity of {@link ValueTypes#Int}
     * which itself has the identity of {@link ValueTypes#Number}.
     *
     * @return the identity in case of defined, otherwise the value type itself
     */
    default ValueType identity() {
        return this;
    }

    /**
     * Returns the extracted {@link Value} based on this value type. Calling this method
     * is equivalent {@link #value(Value, boolean)} with <tt>validate</tt> set to
     * <tt>false</tt>.
     *
     * @param value the value instance to extract the value from
     * @param <T>   the type of the extracted value
     * @return the extracted value
     * @throws IllegalArgumentException in case the encoded value is not representable
     *                                  with this ValueType
     */
    default <T> T value(Value value) {
        return value(value, false);
    }

    /**
     * Returns the extracted {@link Value} based on this value type. Calling this method
     * with <tt>validate</tt> set to <tt>false</tt> is equivalent
     * {@link #value(Value, boolean)}.
     *
     * @param value    the value instance to extract the value from
     * @param validate validate the value or not
     * @param <T>      the type of the extracted value
     * @return the extracted value
     * @throws IllegalArgumentException in case the encoded value is not representable
     *                                  with this ValueType
     */
    default <T> T value(Value value, boolean validate) {
        if (!value.valueType().matches(this)) {
            String msg = java.lang.String.format("ValueType does not match: %s, %s", this, value.valueType());
            throw new IllegalArgumentException(msg);
        }
        return value.tag();
    }

    /**
     * Matches the given value type against this instance. This method also matches
     * against the {@link #identity()} if available.
     *
     * @param other the other value type to match against
     * @return true if the value type matches directly or if the identity of any higher
     * level matches
     */
    default boolean matches(ValueType other) {
        if (matchesExact(other)) {
            return true;
        }
        ValueType identity = identity();
        if (identity == this) {
            return false;
        }
        if (identity == other) {
            return true;
        }
        if (identity == null) {
            return false;
        }
        return identity.matches(other);
    }

    /**
     * Matches the given value type against this instance. This method <b>does not</b>
     * match against the {@link #identity()}.
     *
     * @param other the other value type to match against
     * @return true if the value type matches exactly
     */
    default boolean matchesExact(ValueType other) {
        if (this == other) {
            return true;
        }
        return false;
    }

}
