package com.noctarius.borabora;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class NumberTestCase
        extends AbstractTestCase {

    private static final TestValueCollection<Number> NUMBER_TEST_VALUES = new TestValueCollection<>(
            new TestValue<>(0, (byte) 0x0), new TestValue<>(1, (byte) 0x1), new TestValue<>(10, (byte) 0x0a),
            new TestValue<>(23, (byte) 0x17), new TestValue<>(24, (byte) 0x18, (byte) 0x18),
            new TestValue<>(25, (byte) 0x18, (byte) 0x19), new TestValue<>(100, (byte) 0x18, (byte) 0x64),
            new TestValue<>(1000, "1903e8"), new TestValue<>(1000000, "1a000f4240"),
            new TestValue<>(1000000000000L, "1b000000e8d4a51000"),
            new TestValue<>(new BigInteger("18446744073709551615"), "1bffffffffffffffff"),
            new TestValue<>(new BigInteger("18446744073709551616"), "c249010000000000000000"),
            new TestValue<>(new BigInteger("-18446744073709551616"), "3bffffffffffffffff"),
            new TestValue<>(new BigInteger("-18446744073709551617"), "c349010000000000000000"), new TestValue<>(-1, (byte) 0x20),
            new TestValue<>(-10, (byte) 0x29), new TestValue<>(-100, (byte) 0x38, (byte) 0x63), new TestValue<>(-1000, "3903e7"));

    @Parameterized.Parameters(name = "test_parse_number - expected {0}")
    public static Collection<Object[]> parameters() {
        Object[][] parameters = new Object[NUMBER_TEST_VALUES.getTestValues().length][];
        int i = 0;
        for (TestValue<Number> testValue : NUMBER_TEST_VALUES.getTestValues()) {
            parameters[i] = new Object[2];
            parameters[i][0] = testValue.getExpectedValue();
            parameters[i++][1] = testValue.getInputData();
        }
        return Arrays.asList(parameters);
    }

    private final Number expected;
    private final byte[] input;

    public NumberTestCase(Number expected, byte[] input) {
        this.expected = expected;
        this.input = input;
    }

    @Test
    public void test_parse_majortype0_majortype1_numbers()
            throws Exception {

        Input input = Input.fromByteArray(this.input);
        Parser parser = Parser.newBuilder(input).build();
        Value value = parser.read(new SequenceGraphQuery(0));

        Number result = value.number();
        assertEqualsNumber(this.expected, result);
    }

}
