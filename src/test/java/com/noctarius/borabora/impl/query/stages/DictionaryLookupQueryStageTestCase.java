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
package com.noctarius.borabora.impl.query.stages;

import com.noctarius.borabora.Input;
import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.Output;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.WrongTypeException;
import com.noctarius.borabora.spi.io.CompositeBuffer;
import com.noctarius.borabora.spi.io.Encoder;
import com.noctarius.borabora.spi.query.pipeline.QueryStage;
import com.noctarius.borabora.spi.query.pipeline.VisitResult;
import org.junit.Test;

import java.util.function.Predicate;

import static com.noctarius.borabora.Predicates.matchString;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;

public class DictionaryLookupQueryStageTestCase
        extends AbstractQueryStageTestCase {

    private static final Input INPUT = input();

    @Test
    public void test_tostring() {
        QueryStage queryStage = new DictionaryLookupQueryStage(VALUE_PREDICATE);
        assertEquals("DIC_LOOKUP[ VALUE_PREDICATE ]", queryStage.toString());
    }

    @Test(expected = NullPointerException.class)
    public void fail_create_dictionarylookupquerystage() {
        new DictionaryLookupQueryStage(null);
    }

    @Test(expected = WrongTypeException.class)
    public void fail_evaluate_no_dictionary() {
        Input input = Input.fromByteArray(new byte[]{(byte) 0x1});
        evaluate(input, new DictionaryLookupQueryStage(VALUE_PREDICATE));
    }

    @Test
    public void test_find_first_element() {
        QueryStage queryStage = new DictionaryLookupQueryStage(matchString("foo"));
        EvaluationResult evaluationResult = evaluate(INPUT, queryStage, ConsumerQueryStage.INSTANCE, null);
        assertEquals(VisitResult.Continue, evaluationResult.visitResult);
        assertEquals(1, evaluationResult.values.size());
        assertEqualsNumber(1, evaluationResult.values.get(0).number());
    }

    @Test
    public void test_find_second_element() {
        QueryStage queryStage = new DictionaryLookupQueryStage(matchString("bar"));
        EvaluationResult evaluationResult = evaluate(INPUT, queryStage, ConsumerQueryStage.INSTANCE, null);
        assertEquals(VisitResult.Continue, evaluationResult.visitResult);
        assertEquals(1, evaluationResult.values.size());
        assertEqualsNumber(2, evaluationResult.values.get(0).number());
    }

    @Test
    public void test_find_non_element() {
        QueryStage queryStage = new DictionaryLookupQueryStage(matchString("test"));
        EvaluationResult evaluationResult = evaluate(INPUT, queryStage, null, ConsumerQueryStage.INSTANCE);
        assertEquals(VisitResult.Continue, evaluationResult.visitResult);
        assertEquals(1, evaluationResult.values.size());
        assertSame(Value.NULL_VALUE, evaluationResult.values.get(0));
    }

    @Test
    public void test_equals() {
        QueryStage qs1 = new DictionaryLookupQueryStage(VALUE_PREDICATE);
        QueryStage qs2 = new DictionaryLookupQueryStage(v -> false);
        QueryStage qs3 = new DictionaryLookupQueryStage(VALUE_PREDICATE);

        assertTrue(qs1.equals(qs1));
        assertFalse(qs1.equals(new Object()));
        assertFalse(qs1.equals(qs2));
        assertTrue(qs1.equals(qs3));
    }

    @Test
    public void test_hashcode() {
        QueryStage qs1 = new DictionaryLookupQueryStage(VALUE_PREDICATE);
        QueryStage qs2 = new DictionaryLookupQueryStage(v -> false);
        QueryStage qs3 = new DictionaryLookupQueryStage(VALUE_PREDICATE);

        assertEquals(qs1.hashCode(), qs1.hashCode());
        assertNotEquals(qs1.hashCode(), qs2.hashCode());
        assertEquals(qs1.hashCode(), qs3.hashCode());
    }

    private static Input input() {
        CompositeBuffer compositeBuffer = CompositeBuffer.newCompositeBuffer();
        Output output = Output.toCompositeBuffer(compositeBuffer);
        long offset = Encoder.encodeLengthAndValue(MajorType.Dictionary, 2, 0, output);
        offset = Encoder.putString("foo", offset, output);
        offset = Encoder.encodeLengthAndValue(MajorType.UnsignedInteger, 1, offset, output);
        offset = Encoder.putString("bar", offset, output);
        Encoder.encodeLengthAndValue(MajorType.UnsignedInteger, 2, offset, output);
        return Input.fromCompositeBuffer(compositeBuffer);
    }

    private static final Predicate<Value> VALUE_PREDICATE = new Predicate<Value>() {
        @Override
        public boolean test(Value value) {
            return false;
        }

        @Override
        public String toString() {
            return "VALUE_PREDICATE";
        }
    };

}
