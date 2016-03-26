package com.noctarius.borabora;

import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public abstract class AbstractTestCase {

    public static void assertEqualsNumber(Number n1, Number n2) {
        if (n1.getClass().equals(n2.getClass())) {
            assertEquals(n1, n2);
        }

        BigInteger b1 = n1 instanceof BigInteger ? (BigInteger) n1 : BigInteger.valueOf(n1.longValue());
        BigInteger b2 = n2 instanceof BigInteger ? (BigInteger) n2 : BigInteger.valueOf(n2.longValue());
        assertEquals(b1, b2);
    }

    public static byte[] hexToBytes(String hex) {
        hex = hex.toLowerCase();
        if (hex.startsWith("0x")) {
            hex = hex.substring(2);
        }
        return DatatypeConverter.parseHexBinary(hex);
    }

}
