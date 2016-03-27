package com.noctarius.borabora;

import org.junit.Test;

import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

public class StringTestCase
        extends AbstractTestCase {

    private static final TestValueCollection<String> BYTE_STRING_TEST_VALUES = new TestValueCollection<>(
            new TestValue<>("", (byte) 0x40), new TestValue<>("a", "0x4161"), new TestValue<>("IETF", "0x4449455446"),
            new TestValue<>("\"\\", "0x42225c"));

    private static final TestValueCollection<String> TEXT_STRING_TEST_VALUES = new TestValueCollection<>(
            new TestValue<>("", (byte) 0x60), new TestValue<>("a", "0x6161"), new TestValue<>("IETF", "0x6449455446"),
            new TestValue<>("\"\\", "0x62225c"), new TestValue<>("\u00fc", "0x62c3bc"), new TestValue<>("\u6c34", "0x63e6b0b4"),
            new TestValue<>("\ud800\udd51", "0x64f0908591"), new TestValue<>("streaming", "0x7f657374726561646d696e67ff"));

    @Test
    public void test_indefinite_byte_string()
            throws Exception {

        String expected = new String(hexToBytes("0xaabbccddeeff99"));
        Parser parser = buildParser("0x5f44aabbccdd43eeff99ff");

        Value value = parser.read(GraphQuery.newBuilder().sequence(0).build());

        assertEquals(expected, value.string());
    }

    @Test
    public void test_indefinite_text_string()
            throws Exception {

        String expected = new String(hexToBytes("0xc3bce6b0b4f0908591"), Charset.forName("UTF8"));
        Parser parser = buildParser("0x5f62c3bc63e6b0b464f0908591ff");

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
