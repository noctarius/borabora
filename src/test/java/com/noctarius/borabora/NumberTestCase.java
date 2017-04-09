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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;

import static com.noctarius.borabora.HalfPrecisionFloat.valueOf;
import static org.junit.Assert.assertArrayEquals;

@RunWith(Parameterized.class)
public class NumberTestCase
        extends AbstractTestCase {

    private static final TestValueCollection<Number> NUMBER_TEST_VALUES = new TestValueCollection<>(new TestValue<>(0, "0x00"), //
            new TestValue<>(1, "0x01"), //
            new TestValue<>(10, "0x0a"), //
            new TestValue<>(23, "0x17"), //
            new TestValue<>(24, "0x1818"), //
            new TestValue<>(25, "0x1819"), //
            new TestValue<>(100, "0x1864"), //
            new TestValue<>(1000, "1903e8"), new TestValue<>(1000000, "1a000f4240"), //
            new TestValue<>(1000000000000L, "1b000000e8d4a51000"), //
            new TestValue<>(new BigInteger("18446744073709551615"), "1bffffffffffffffff"), //
            new TestValue<>(new BigInteger("18446744073709551616"), "c249010000000000000000"), //
            new TestValue<>(new BigInteger("-18446744073709551616"), "3bffffffffffffffff"), //
            new TestValue<>(new BigInteger("-18446744073709551617"), "c349010000000000000000"), //
            new TestValue<>(-1, "0x20"), //
            new TestValue<>(-10, "0x29"), //
            new TestValue<>(-100, "0x3863"), //
            new TestValue<>(-1000, "3903e7"), //
            new TestValue<>(valueOf(0.0f), "0xf90000"), //
            new TestValue<>(valueOf(-0.0f), "0xf98000"), //
            new TestValue<>(valueOf(1.0f), "0xf93c00"), //
            new TestValue<>(1.1d, "0xfb3ff199999999999a"), //
            new TestValue<>(valueOf(1.5f), "0xf93e00"), //
            new TestValue<>(valueOf(65504.0f), "0xf97bff"), //
            new TestValue<>(100000.0f, "0xfa47c35000"), //
            new TestValue<>(3.4028234663852886e+38f, "0xfa7f7fffff"), //
            new TestValue<>(1.0e+300, "0xfb7e37e43c8800759c"), //
            new TestValue<>(valueOf(5.960464477539063e-8f), "0xf90001"), //
            new TestValue<>(valueOf(0.00006103515625f), "0xf90400"), //
            new TestValue<>(valueOf(-4.0f), "0xf9c400"), //
            new TestValue<>(-4.1, "0xfbc010666666666666"), //
            new TestValue<>(valueOf(Float.POSITIVE_INFINITY), "0xf97c00"), //
            new TestValue<>(valueOf(Float.NaN), "0xf97e00"), //
            new TestValue<>(valueOf(Float.NEGATIVE_INFINITY), "0xf9fc00"), //
            new TestValue<>(Float.POSITIVE_INFINITY, "0xfa7f800000"), //
            new TestValue<>(Float.NaN, "0xfa7fc00000"), //
            new TestValue<>(Float.NEGATIVE_INFINITY, "0xfaff800000"), //
            new TestValue<>(Double.POSITIVE_INFINITY, "0xfb7ff0000000000000"), //
            new TestValue<>(Double.NaN, "0xfb7ff8000000000000"), //
            new TestValue<>(Double.NEGATIVE_INFINITY, "0xfbfff0000000000000"));

    @Parameterized.Parameters(name = "test_parse_number - value1 {0}")
    public static Collection<Object[]> parameters() {
        Object[][] parameters = new Object[NUMBER_TEST_VALUES.getTestValues().length][];
        int i = 0;
        for (TestValue<Number> testValue : NUMBER_TEST_VALUES.getTestValues()) {
            parameters[i] = new Object[2];
            parameters[i][0] = testValue.getValue1();
            parameters[i++][1] = testValue.getValue2();
        }
        return Arrays.asList(parameters);
    }

    private final Number value1;
    private final byte[] value2;

    public NumberTestCase(Number value1, byte[] value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    @Test
    public void test_parse_majortype0_majortype1_numbers()
            throws Exception {

        Input input = Input.fromByteArray(this.value2);
        Parser parser = Parser.newParser();

        Query query = parser.newQueryBuilder().build();
        Value value = parser.read(input, query);

        Number result = value.number();
        assertEqualsNumber(this.value1, result);
    }

    @Test
    public void test_write_majortype0_majortype1_numbers()
            throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toOutputStream(baos);
        Writer writer = Writer.newWriter();
        writer.newGraphBuilder(output).putNumber(value1).finishStream();
        assertArrayEquals(value2, baos.toByteArray());
    }

}
