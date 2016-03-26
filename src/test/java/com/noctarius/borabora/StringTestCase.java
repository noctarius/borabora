package com.noctarius.borabora;

import org.junit.Test;

import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

public class StringTestCase
        extends AbstractTestCase {

    private static final TestValueCollection<String> BYTE_STRING_TEST_VALUES = new TestValueCollection<>(
            new TestValue<>("", (byte) 0x40), new TestValue<>("a", "4161"), new TestValue<>("IETF", "4449455446"),
            new TestValue<>("\"\\", "42225c"));

    private static final TestValueCollection<String> TEXT_STRING_TEST_VALUES = new TestValueCollection<>(
            new TestValue<>("", (byte) 0x60), new TestValue<>("a", "6161"), new TestValue<>("IETF", "6449455446"),
            new TestValue<>("\"\\", "62225c"), new TestValue<>("\u00fc", "62c3bc"), new TestValue<>("\u6c34", "63e6b0b4"),
            new TestValue<>("\ud800\udd51", "64f0908591"));

    @Test
    public void test_indefinite_byte_string()
            throws Exception {

        byte[] data = hexToBytes("0x5f44aabbccdd43eeff99ff");
        String expected = new String(hexToBytes("0xaabbccddeeff99"));

        Input input = Input.fromByteArray(data);
        Parser parser = Parser.newBuilder(input).build();

        Value value = parser.read(GraphQuery.newBuilder().sequence(0).build());

        assertEquals(expected, value.string());
    }

    @Test
    public void test_indefinite_text_string()
            throws Exception {

        byte[] data = hexToBytes("0x5f62c3bc63e6b0b464f0908591ff");
        String expected = new String(hexToBytes("0xc3bce6b0b4f0908591"), Charset.forName("UTF8"));

        Input input = Input.fromByteArray(data);
        Parser parser = Parser.newBuilder(input).build();

        Value value = parser.read(GraphQuery.newBuilder().sequence(0).build());

        assertEquals(expected, value.string());
    }

    @Test
    public void test_parse_majortype2_bytestring()
            throws Exception {

        for (TestValue<String> testValue : BYTE_STRING_TEST_VALUES.getTestValues()) {
            testString(ValueTypes.ByteString, testValue);
        }
    }

    @Test
    public void test_parse_majortype3_textstring()
            throws Exception {

        for (TestValue<String> testValue : TEXT_STRING_TEST_VALUES.getTestValues()) {
            testString(ValueTypes.TextString, testValue);
        }
    }

    private void testString(ValueType valueType, TestValue<String> testValue) {
        Input input = Input.fromByteArray(testValue.getInputData());
        Parser parser = Parser.newBuilder(input).build();
        Value value = parser.read(new SequenceGraphQuery(0));

        assertEquals(valueType, value.valueType());
        assertEquals(testValue.getExpectedValue(), value.string());
    }

}
