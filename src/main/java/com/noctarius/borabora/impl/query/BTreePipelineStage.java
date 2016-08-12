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
package com.noctarius.borabora.impl.query;

import com.noctarius.borabora.spi.pipeline.PipelineStage;
import com.noctarius.borabora.spi.pipeline.Stage;
import com.noctarius.borabora.spi.pipeline.VisitResult;

public class BTreePipelineStage<PC, T extends Stage<PC, T>>
        implements PipelineStage<PC, T> {

    public static final BTreePipelineStage NIL = new BTreePipelineStage(null, null, null) {
        @Override
        public String toString() {
            return "NIL";
        }
    };

    final BTreePipelineStage<PC, T> left;
    final BTreePipelineStage<PC, T> right;
    final T stage;

    public BTreePipelineStage(BTreePipelineStage<PC, T> left, BTreePipelineStage<PC, T> right, T stage) {
        this.left = left;
        this.right = right;
        this.stage = stage;
    }

    @Override
    public VisitResult visit(PipelineStage<PC, T> previousPipelineStage, PC pipelineContext) {
        VisitResult visitResult = VisitResult.Continue;

        if (stage != null) {
            do {
                visitResult = stage.evaluate(previousPipelineStage, this, pipelineContext);
            } while (visitResult == VisitResult.Loop);
        }

        // Evaluate possibly existing siblings
        if (right != NIL) {
            if (visitResult != VisitResult.Exit) {
                visitResult = right.visit(this, pipelineContext);
            }
        }

        return visitResult;
    }

    @Override
    public VisitResult visitChildren(PC pipelineContext) {
        if (left != NIL) {
            return left.visit(this, pipelineContext);
        }
        return VisitResult.Continue;
    }

    @Override
    public T stage() {
        return stage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BTreePipelineStage)) {
            return false;
        }

        BTreePipelineStage<?, ?> that = (BTreePipelineStage<?, ?>) o;
        return treeEquals(this, that);
    }

    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        result = 31 * result + (stage != null ? stage.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BTreePipelineStage{stage=" + stage + ", left=" + left + ", right=" + right + '}';
    }

    public static boolean treeEquals(BTreePipelineStage a, BTreePipelineStage b) {
        // check for reference equality and nulls
        if (a == b) {
            return true; // note this picks up case of two nulls
        }
        if (a == null) {
            return false;
        }
        if (b == null) {
            return false;
        }

        // check for data inequality
        if (a.stage != b.stage) {
            if ((a.stage == null) || (b.stage == null)) {
                return false;
            }
            if (!Stage.equals(a.stage, b.stage)) {
                return false;
            }
        }

        // recursively check branches
        if (!treeEquals(a.left, b.left)) {
            return false;
        }
        if (!treeEquals(a.right, b.right)) {
            return false;
        }

        // we've eliminated all possibilities for non-equality, so trees must be equal
        return true;
    }
}
