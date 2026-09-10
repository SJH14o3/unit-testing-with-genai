package com.google.gson.internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("NumberLimits Test")
class NumberLimitsTest {

  private static final String MAX_LENGTH_STRING = "1".repeat(10_000);
  private static final String TOO_LONG_STRING = "1".repeat(10_001);

  @ParameterizedTest
  @DisplayName("parseBigDecimal: valid inputs")
  @ValueSource(strings = {"123", "0", "-123.456", "0.000001"})
  void parseBigDecimal_validInput_returnsBigDecimal(String input) {
    assertEquals(new BigDecimal(input), NumberLimits.parseBigDecimal(input));
  }

  @Test
  @DisplayName("parseBigDecimal: string length exceeds limit")
  void parseBigDecimal_stringTooLong_throwsException() {
    assertThrows(NumberFormatException.class, () -> NumberLimits.parseBigDecimal(TOO_LONG_STRING));
  }

  @Test
  @DisplayName("parseBigDecimal: scale exceeds limit")
  void parseBigDecimal_scaleTooLarge_throwsException() {
    // Create a string with 10,000 decimal places
    String input = "1." + "0".repeat(10_000);
    assertThrows(NumberFormatException.class, () -> NumberLimits.parseBigDecimal(input));
  }

  @ParameterizedTest
  @DisplayName("parseBigInteger: valid inputs")
  @ValueSource(strings = {"123", "0", "-123"})
  void parseBigInteger_validInput_returnsBigInteger(String input) {
    assertEquals(new BigInteger(input), NumberLimits.parseBigInteger(input));
  }

  @Test
  @DisplayName("parseBigInteger: string length exceeds limit")
  void parseBigInteger_stringTooLong_throwsException() {
    assertThrows(NumberFormatException.class, () -> NumberLimits.parseBigInteger(TOO_LONG_STRING));
  }
}