package com.google.gson.internal;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NumberLimitsTest {

  @Test
  @DisplayName("parseBigDecimal normal")
  void parseBigDecimalNormal() {
    BigDecimal dec = NumberLimits.parseBigDecimal("1234.567");
    assertEquals(new BigDecimal("1234.567"), dec);
  }

  @Test
  @DisplayName("parseBigDecimal too long string throws")
  void parseBigDecimalTooLong() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 10_001; i++) sb.append('1');
    String longNum = sb.toString();
    assertThrows(NumberFormatException.class,
        () -> NumberLimits.parseBigDecimal(longNum));
  }

  @Test
  @DisplayName("parseBigDecimal scale too large throws")
  void parseBigDecimalScaleTooLarge() {
    // 1 followed by 10_001 zeros after decimal point
    StringBuilder sb = new StringBuilder("1.");
    for (int i = 0; i < 10_001; i++) sb.append('0');
    String scaleNum = sb.toString();
    assertThrows(NumberFormatException.class,
        () -> NumberLimits.parseBigDecimal(scaleNum));
  }

  @Test
  @DisplayName("parseBigInteger normal")
  void parseBigIntegerNormal() {
    BigInteger bi = NumberLimits.parseBigInteger("987654321");
    assertEquals(new BigInteger("987654321"), bi);
  }

  @Test
  @DisplayName("parseBigInteger too long string throws")
  void parseBigIntegerTooLong() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 10_001; i++) sb.append('9');
    assertThrows(NumberFormatException.class,
        () -> NumberLimits.parseBigInteger(sb.toString()));
  }
}