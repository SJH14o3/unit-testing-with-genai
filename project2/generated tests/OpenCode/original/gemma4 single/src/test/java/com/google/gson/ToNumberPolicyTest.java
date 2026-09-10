package com.google.gson;

import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.MalformedJsonException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ToNumberPolicyTest {

    private JsonReader createReader(String json) {
        return new JsonReader(new StringReader(json));
    }

    @Test
    @DisplayName("DOUBLE policy: reads double correctly")
    void doublePolicy_readsDouble() throws IOException {
        JsonReader reader = createReader("123.45");
        assertEquals(123.45, ToNumberPolicy.DOUBLE.readNumber(reader));
    }

    @Test
    @DisplayName("LAZILY_PARSED_NUMBER policy: reads lazily parsed number")
    void lazilyParsedNumberPolicy_readsLazilyParsedNumber() throws IOException {
        JsonReader reader = createStringReader("\"123.45\"");
        Number result = ToNumberPolicy.LAZILY_PARSED_NUMBER.readNumber(reader);
        assertTrue(result instanceof LazilyParsedNumber);
        assertEquals("123.45", result.toString());
    }

    // Helper to handle string input for LAZILY_PARSED_NUMBER which uses nextString()
    private JsonReader createStringReader(String json) {
        return new JsonReader(new StringReader(json));
    }

    @ParameterizedTest
    @DisplayName("LONG_OR_DOUBLE policy: handles integers and decimals")
    @CsvSource({
        "123, 123",
        "123.45, 123.45",
        "-123, -123",
        "123.0, 123.0"
    })
    void longOrDoublePolicy_readsCorrectly(String input, String expected) throws IOException {
        JsonReader reader = createStringReader("\"" + input + "\"");
        Number result = ToNumberPolicy.LONG_OR_DOUBLE.readNumber(reader);
        if (input.contains(".")) {
            assertEquals(Double.valueOf(expected), result.doubleValue());
        } else {
            assertEquals(Long.valueOf(expected), result);
        }
    }

    @Test
    @DisplayName("LONG_OR_DOUBLE policy: handles large long values by falling back to double")
    void longOrDoublePolicy_fallbackToDoubleOnOverflow() throws IOException {
        // A value that is too large for Long but valid for Double
        String largeValue = "9223372036854775808"; 
        JsonReader reader = createStringReader("\"" + largeValue + "\"");
        Number result = To0NumberPolicy.LONG_OR_DOUBLE.readNumber(reader);
        assertEquals(Double.valueOf(largeValue), result.doubleValue());
    }

    @ParameterizedTest
    @DisplayName("LONG_OR_DOUBLE policy: throws MalformedJsonException for NaN/Infinity when not lenient")
    @ValueSource(strings = {"NaN", "Infinity", "-Infinity"})
    void longOrDoublePolicy_throwsExceptionForNonLenientNaNOrInfinity(String value) {
        JsonReader reader = createStringReader("\"" + value + "\"");
        assertThrows(MalformedJsonException.class, () -> ToNumberPolicy.LONG_OR_DOUBLE.readNumber(reader));
    }

    @Test
    @DisplayName("LONG_OR_DOUBLE policy: allows NaN/Infinity when lenient")
    @ValueSource(strings = {"NaN", "Infinity", "-Infinity"})
    void longOrDoublePolicy_allowsNaNOrInfinityWhenLenient(String value) throws IOException {
        JsonReader reader = createStringReader("\"" + value + "\"");
        reader.setLenient(true);
        Number result = ToNumberPolicy.LONG_OR_DOUBLE.readNumber(reader);
        assertTrue(Double.valueOf(value).isNaN() || Double.valueOf(value).isInfinite());
    }

    @Test
    @DisplayName("LONG_OR_DOUBLE policy: throws JsonParseException for unparseable strings")
    void longOrDoublePolicy_throwsJsonParseException() {
        JsonReader reader = createStringReader("\"not-a-number\"");
        assertThrows(JsonParseException.class, ()rightarrow ToNumberPolicy.LONG_OR_DOUBLE.readNumber(reader));
    }

    @Test
    @DisplayName("BIG_DECIMAL policy: reads big decimal correctly")
    void bigDecimalPolicy_readsBigDecimal() throws IOException {
        JsonReader reader = createStringReader("\"12345678901234567890.1234567890\"");
        BigDecimal result = ToNumberPolicy.BIG_DECIMAL.readNumber(reader);
        assertEquals(new BigDecimal("12345678901234567890.1234567890"), result);
    }

    @Test
    @DisplayName("BIG_DECIMAL policy: throws JsonParseException for invalid format")
    void bigDecimalPolicy_throwsJsonParseException() {
        JsonReader reader = createStringReader("\"invalid\"");
        assertThrows(JsonParseException.class, () -> ToNumberPolicy.BIG_DECIMAL.readNumber(reader));
    }
}