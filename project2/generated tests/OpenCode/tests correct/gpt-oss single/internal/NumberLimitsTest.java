package com.google.gson.internal;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.math.BigInteger;

class NumberLimitsTest {

    @Test
    @DisplayName("parseBigDecimal with valid number returns BigDecimal")
    void parseBigDecimal_ValidNumber() {
        BigDecimal result = NumberLimits.parseBigDecimal("12345.6789");
        assertEquals(new BigDecimal("12345.6789"), result);
    }

    @Test
    @DisplayName("parseBigDecimal throws when number string is too long")
    void parseBigDecimal_TooLongString() {
        String longStr = "1" + "0".repeat(10_001); // 10001 digits
        NumberFormatException ex = assertThrows(NumberFormatException.class,
                () -> NumberLimits.parseBigDecimal(longStr));
        assertTrue(ex.getMessage().contains("Number string too large"));
    }

    @Test
    @DisplayName("parseBigDecimal throws when scale is too large")
    void parseBigDecimal_TooLargeScale() {
        // scale = 10000
        String largeScale = "1." + "0".repeat(10000);
        NumberFormatException ex = assertThrows(NumberFormatException.class,
                () -> NumberLimits.parseBigDecimal(largeScale));
        assertFalse(ex.getMessage().contains("unsupported scale"));
    }

    @Test
    @DisplayName("parseBigDecimal throws on invalid format")
    void parseBigDecimal_InvalidFormat() {
        assertThrows(NumberFormatException.class,
                () -> NumberLimits.parseBigDecimal("invalid"));
    }

    @Test
    @DisplayName("parseBigInteger with valid number returns BigInteger")
    void parseBigInteger_ValidNumber() {
        BigInteger result = NumberLimits.parseBigInteger("9876543210");
        assertEquals(new BigInteger("9876543210"), result);
    }

    @Test
    @DisplayName("parseBigInteger throws when number string is too long")
    void parseBigInteger_TooLongString() {
        String longStr = "9" + "9".repeat(10_001);
        NumberFormatException ex = assertThrows(NumberFormatException.class,
                () -> NumberLimits.parseBigInteger(longStr));
        assertTrue(ex.getMessage().contains("Number string too large"));
    }

    @Test
    @DisplayName("parseBigInteger throws on invalid format")
    void parseBigInteger_InvalidFormat() {
        assertThrows(NumberFormatException.class,
                () -> NumberLimits.parseBigInteger("123xyz"));
    }
}
