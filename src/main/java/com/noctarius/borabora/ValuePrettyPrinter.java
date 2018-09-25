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

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * The <tt>ValuePrettyPrinter</tt> class provides a pretty printer implementation for
 * borabora CBOR {@link Value}s.
 */
public final class ValuePrettyPrinter {

    private ValuePrettyPrinter() {
    }

    /**
     * Returns a pretty printed version of {@link Value#toString()}, traversed over any
     * depths of data.
     *
     * @param value the value to return as a pretty printed string
     * @return a string of the pretty printed representation of toString
     */
    public static String toStringPrettyPrint(Value value) {
        Objects.requireNonNull(value, "value must not be null");
        StringBuilder sb = new StringBuilder();
        prettyPrint(value, 0, sb, Value::toString);
        return sb.toString();
    }

    /**
     * Returns a pretty printed version of {@link Value#asString()}, traversed over any
     * depths of data.
     *
     * @param value the value to return as a pretty printed string
     * @return a string of the pretty printed representation of asString
     */
    public static String asStringPrettyPrint(Value value) {
        Objects.requireNonNull(value, "value must not be null");
        StringBuilder sb = new StringBuilder();
        prettyPrint(value, 0, sb, Value::asString);
        return sb.toString();
    }

    private static void prettyPrint(Value value, int indentation, StringBuilder sb, Function<Value, String> extractor) {
        if (value.valueType().matches(ValueTypes.Dictionary)) {
            sb.append("Dictionary [").append("\n");
            for (Map.Entry<Value, Value> entry : value.dictionary()) {
                printIndentation(indentation + 1, sb);
                prettyPrint(entry.getKey(), indentation + 1, sb, extractor);
                sb.append("=");
                prettyPrint(entry.getValue(), indentation + 1, sb, extractor);
                sb.append(", \n");
            }
            // Remove last ", "
            sb.deleteCharAt(sb.length() - 2).deleteCharAt(sb.length() - 2);
            printIndentation(indentation, sb);
            sb.append("]");

        } else if (value.valueType().matches(ValueTypes.Sequence)) {
            sb.append("Sequence [").append("\n");
            for (Value seqValue : value.sequence()) {
                printIndentation(indentation + 1, sb);
                prettyPrint(seqValue, indentation + 1, sb, extractor);
                sb.append(", \n");
            }
            // Remove last ", "
            sb.deleteCharAt(sb.length() - 2).deleteCharAt(sb.length() - 2);
            printIndentation(indentation, sb);
            sb.append("]");

        } else {
            sb.append(extractor.apply(value));
        }
    }

    private static void printIndentation(int indentation, StringBuilder sb) {
        for (int i = 0; i < indentation; i++) {
            sb.append("  ");
        }
    }
}
