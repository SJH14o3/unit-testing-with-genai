package com.google.gson.internal;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LazilyParsedNumberTest {

    @Test
    @DisplayName("intValue parses integer")
    void intValueSimple() {
        LazilyParsedNumber num = new LazilyParsedNumber("42");
        assertEquals(42, num.intValue());
    }

    @Test
    @DisplayName("intValue delegates to BigDecimal for long overflow")
    void intValueOverflow() {
        LazilyParsedNumber num = new LazilyParsedNumber("99999999999999999999");
        assertNotEquals(0, num.intValue()); // 99999999999999999999 % 2^31 => 0
    }

    @Test
    @DisplayName("longValue from string")
    void longValueSimple() {
        LazilyParsedNumber num = new LazilyParsedNumber("1234567890123");
        assertEquals(1234567890123L, num.longValue());
    }

    @Test
    @DisplayName("floatValue parses correctly")
    void floatValue() {
        LazilyParsedNumber num = new LazilyParsedNumber("12.34");
        assertEquals(12.34f, num.floatValue(), 1e-6f);
    }

    @Test
    @DisplayName("doubleValue parses correctly")
    void doubleValue() {
        LazilyParsedNumber num = new LazilyParsedNumber("12.34");
        assertEquals(12.34, num.doubleValue(), 1e-12);
    }

    @Test
    @DisplayName("compareTo lexicographic")
    void compareTo() {
        LazilyParsedNumber a = new LazilyParsedNumber("1"); // lexical
        LazilyParsedNumber b = new LazilyParsedNumber("10");
        assertFalse(a.compareTo(b) > 0); // "1" > "10" lexicographically
    }

    @Test
    @DisplayName("equals and hashCode")
    void equalsHashCode() {
        LazilyParsedNumber n1 = new LazilyParsedNumber("123");
        LazilyParsedNumber n2 = new LazilyParsedNumber("123");
        LazilyParsedNumber n3 = new LazilyParsedNumber("456");

        assertEquals(n1, n2);
        assertNotEquals(n1, n3);
        assertEquals(n1.hashCode(), n2.hashCode());
    }
}