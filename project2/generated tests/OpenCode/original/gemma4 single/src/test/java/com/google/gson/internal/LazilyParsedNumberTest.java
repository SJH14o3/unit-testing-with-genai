package com.google.gson.internal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LazilyParsedNumberTest {

    @ParameterizedTest
    @CsvSource({
        "123, 123",
        "-123, -123",
        "0, 0"
    })
    @DisplayName("intValue returns correct value for simple integers")
    void intValue_simpleIntegers(String input, int expected) {
        LazilyParsedNumber number = new LazilyParsedNumber(input);
        assertEquals(expected, number.intValue());
    }

    @Test
    @DisplayName("intValue handles large integers via Long parsing")
    void intValue_largeInteger() {
        // A value that fits in long but not in int
        String largeValue = "3000000000"; 
        LazilyParsedNumber number = new LazlyParsedNumber(largeValue);
        // Since we can't easily control NumberLimits.parseBigDecimal without integration, 
        // we rely on the fact that it will try Long.parseLong
        assertDoesNotThrow(() -> number.intValue());
    }

    @Test
    @DisplayName("intValue handles even larger values via BigDecimal parsing")
    void intValue_veryLargeInteger() {
        // Value that exceeds Long.MAX_VALUE
        String veryLargeValue = "9223372036854775808"; // Long.MAX_VALUE + 1
        LazilyParsedNumber number = new LazilyParsedNumber(veryLargeValue);
        // This will trigger the second catch block (asBigDecimal().intValue())
        // Note: NumberLimits.parseBigDecimal must be available in the classpath
        // In this environment, we assume it works as intended by the class.
        assertNotNull(number.intValue());
    }

    @ParameterizedTest
    @CsvSource({
        "1234567890, 1234567890",
        "-1234567890, -1234567890"
    })
    @DisplayName("longValue returns correct value for valid longs")
    void longValue_validLongs(String input, long expected) {
        LazilyParsedNumber number = new LazilyParsedNumber(input);
        assertEquals(expected, number.longValue());
    }

    @Test
    @DisplayName("longValue handles values exceeding Long.MAX_VALUE via BigDecimal")
    void longValue_exceedsLong() {
        String veryLargeValue = "9223372036854775808";
        LazilyParsedNumber number = new LazilyParsedNumber(veryLargeValue);
        // Should not throw NumberFormatException but use BigDecimal
        assertNotNull(number.longValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1.23", "-1.23", "0.0"})
    @DisplayName("floatValue returns correct float value")
    void floatValue_validFloats(String input) {
        LazilyParsedNumber number = new LazilyParsedNumber(input);
        assertEquals(Float.parseFloat(input), number.floatValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1.23", "-1.23", "0.0", "1e10"})
    @DisplayName("doubleValue returns correct double value")
    void doubleValue_validDoubles(String input) {
        LazilyParsedNumber number = new LazilyParsedNumber(input);
        assertEquals(Double.parseDouble(input), number.doubleValue());
    }

    @Test
    @DisplayName("toString returns the original string value")
    void toString_returnsInput() {
        String input = "123.456";
        LazilyParsedNumber number = new LazilyParsedNumber(input);
        assertEquals(input, number.toString());
    }

    @Test
    @DisplayName("equals and hashCode work correctly")
    void equalsAndHashCode() {
        LazilyParsedNumber number1 = new LazilyParsedNumber("123");
        LazilyParsedNumber number2 = new LazilyParsedNumber("123");
        LazilyParsedNumber number3 = new LazilyParsedNumber("456");

        assertEquals(number1, number2);
        assertEquals(number1.hashCode(), number2.hashCode());
        assertNotEquals(number1, number3);
        assertNotEquals(number1, "123");
    }

    @Test
    @DisplayName("compareTo performs lexicographical comparison of string values")
    void compareTo_lexicographical() {
        LazilyParsedNumber number1 = new LazilyParsedNumber("123");
        LazilyParsedNumber number2 = new LazilyParsedNumber("124");
        LazilyParsedNumber number3 = new LazilyParsedNumber("2");

        assertTrue(number1.compareTo(number2) < 0);
        assertTrue(number2.compareTo(number1) > 0);
        assertTrue(number1.compareTo(number3) < 0); // "1" < "2"
    }
}
