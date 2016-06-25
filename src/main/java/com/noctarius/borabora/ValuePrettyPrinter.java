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

import java.util.Map;

public class ValuePrettyPrinter {

    public static String prettyPrint(Value value) {
        StringBuilder sb = new StringBuilder();
        prettyPrint(value, 0, sb);
        return sb.toString();
    }

    private static void prettyPrint(Value value, int indentation, StringBuilder sb) {
        if (value.valueType().matches(ValueTypes.Dictionary)) {
            sb.append("Dictionary [").append("\n");
            for (Map.Entry<Value, Value> entry : value.dictionary()) {
                printIndentation(indentation + 1, sb);
                prettyPrint(entry.getKey(), indentation + 1, sb);
                sb.append("=");
                prettyPrint(entry.getValue(), indentation + 1, sb);
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
                prettyPrint(seqValue, indentation + 1, sb);
                sb.append(", \n");
            }
            // Remove last ", "
            sb.deleteCharAt(sb.length() - 2).deleteCharAt(sb.length() - 2);
            printIndentation(indentation, sb);
            sb.append("]");

        } else {
            sb.append(value);
        }
    }

    private static void printIndentation(int indentation, StringBuilder sb) {
        for (int i = 0; i < indentation; i++) {
            sb.append("  ");
        }
    }
}
