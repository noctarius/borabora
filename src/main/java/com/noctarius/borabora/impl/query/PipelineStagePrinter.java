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
package com.noctarius.borabora.impl.query;

import com.noctarius.borabora.spi.query.pipeline.PipelineStage;

import java.util.Objects;

import static com.noctarius.borabora.spi.query.pipeline.PipelineStage.NIL;

final class PipelineStagePrinter {

    private PipelineStagePrinter() {
    }

    static String printTree(PipelineStage node) {
        Objects.requireNonNull(node, "node must not be null");
        StringBuilder sb = new StringBuilder();
        printTree(node, sb);
        return sb.toString();
    }

    private static void printTree(PipelineStage node, StringBuilder sb) {
        if (node.right() != NIL) {
            printTree(node.right(), sb, true, "");
        }

        printNodeValue(node, sb);

        if (node.left() != NIL) {
            printTree(node.left(), sb, false, "");
        }
    }

    private static void printNodeValue(PipelineStage node, StringBuilder sb) {
        if (node.stage() == null) {
            sb.append("<null>");
        } else {
            sb.append(node.stage().toString());
        }
        sb.append('\n');
    }

    private static void printTree(PipelineStage node, StringBuilder sb, boolean isRight, String indent) {
        if (node.right() != NIL) {
            printTree(node.right(), sb, true, indent + (isRight ? "        " : " |      "));
        }

        sb.append(indent);
        if (isRight) {
            sb.append(" /");
        } else {
            sb.append(" \\");
        }
        sb.append("----- ");

        printNodeValue(node, sb);

        if (node.left() != NIL) {
            printTree(node.left(), sb, false, indent + (isRight ? " |      " : "        "));
        }
    }
}
