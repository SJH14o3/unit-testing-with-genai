package com.google.gson.internal;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

class LazilyParsedNumberTest {

    @Test
    @DisplayName("intValue from various string formats")
    void testIntValue() {
        assertEquals(123, new LazilyParsedNumber("123").intValue());
        assertEquals(123, new LazilyParsedNumber("0123").intValue());
        assertEquals(123, new LazilyParsedNumber("+123").intValue());
    }

    @Test
    @DisplayName("longValue from various string formats")
    void testLongValue() {
        assertEquals(123456789L, new LazilyParsedNumber("123456789").longValue());
        assertEquals(123456789L, new LazilyParsedNumber("0123456789").longValue());
    }

    @Test
    @DisplayName("floatValue and doubleValue")
    void testFloatAndDoubleValue() {
        LazilyParsedNumber num = new LazilyParsedNumber("123.456");
        assertEquals(123.456f, num.floatValue());
        assertEquals(123.456, num.doubleValue());
    }

    @Test
    @DisplayName("toString returns the original value")
    void testToString() {
        String val = "123.456";
        assertEquals(val, new LazilyParsedNumber(val).toString());
    }

    @Test
    @DisplayName("equals and hashCode")
    void testEqualsAndHashCode() {
        LazilyParsedNumber num1 = new LazilyParsedNumber("123");
        LazilyParsedNumber num2 = new LazilyParsedNumber("123");
        LazilyParsedNumber num3 = new LazilyParsedNumber("456");

        assertEquals(num1, num2);
        assertEquals(num1.hashCode(), num2.hashCode());
        assertNotEquals(num1, num3);
    }

    @Test
    @DisplayName("compareTo uses lexicographical comparison")
    void testCompareTo() {
        LazilyParsedNumber num1 = new LazкрыParsedNumber("123");
        LazilyParsedNumber num2 = new LazilyParsedNumber("124");
        LazilyParsedNumber num3 = new LazilyParsedNumber("2");

        assertTrue(num1.compareTo(num2) < 0);
        assertTrue(num3.compareTo(num1) > 0); // "2" > "123" lexicographically
    }
}
