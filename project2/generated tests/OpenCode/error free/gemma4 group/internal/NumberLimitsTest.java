package com.google.gson.internal;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

class NumberLimitsTest {

    @Test
    @DisplayName("parseBigDecimal valid strings")
    void parseBigDecimalValid() throws NumberFormatException {
        assertEquals(new BigDecimal("123.456"), NumberLimits.parseBigDecimal("123.456"));
        assertEquals(new BigDecimal("0"), NumberLimits.parseBigDecimal("0"));
    }

    @Test
    @DisplayName("parseBigDecimal invalid scale")
    void parseBigDecimalInvalidScale() {
        // Scale of 10,000 is too large
        String largeScale = "0." + "0".repeat(10000) + "1";
        assertThrows(NumberFormatException.class, () -> NumberLimits.parseBigDecimal(largeScale));
    }

    @Test
    @DisplayName("parseBigDecimal string too long")
    void parseBigDecimalTooLong() {
        String tooLong = "1" + "0".repeat(10001);
        assertThrows(NumberFormatException.class, () -> NumberLimits.parseBigDecimal(tooLong));
    }

    @Test
    @DisplayName("parseBigInteger valid strings")
    void parseBigIntegerValid() throws NumberFormatException {
        assertEquals(new java.math.BigInteger("123"), NumberLimits.parseBigInteger("123"));
        assertEquals(new java.math.BigInteger("0"), NumberLimits.parseBigInteger("0"));
    }

    @Test
    @DisplayName("parseBigInteger string too long")
    void parseBigIntegerTooLong() {
        String tooLong = "1" + "0".repeat(10001);
        assertThrows(NumberFormatException.class, () -> NumberLimits.parseBigInteger(tooLong));
    }
}
