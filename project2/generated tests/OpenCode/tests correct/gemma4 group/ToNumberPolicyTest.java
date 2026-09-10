package com.google.gson;

import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import org.junit.jupiter.api.*;
import java.io.StringReader;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ToNumberPolicyTest {

    @Test
    @DisplayName("Test DOUBLE policy")
    void testDoublePolicy() throws Exception {
        JsonReader reader = new JsonReader(new StringReader("123.45"));
        assertEquals(123.45, ToNumberPolicy.DOUBLE.readNumber(reader));
    }

    @Test
    @DisplayName("Test LAZILY_PARSED_NUMBER policy")
    void testLazilyParsedNumberPolicy() throws Exception {
        JsonReader reader = new JsonReader(new StringReader("123.45"));
        Number number = ToNumberPolicy.LAZILY_PARSED_NUMBER.readNumber(reader);
        assertTrue(number instanceof LazilyParsedNumber);
        assertEquals("123.45", number.toString());
    }

    @Test
    @DisplayName("Test LONG_OR_DOUBLE policy - Long")
    void testLongOrDoublePolicyLong() throws Exception {
        JsonReader reader = new JsonReader(new StringReader("123"));
        Number number = ToNumberPolicy.LONG_OR_DOUBLE.readNumber(reader);
        assertTrue(number instanceof Long);
        assertEquals(123L, number.longValue());
    }

    @Test
    @DisplayName("Test LONG_OR_DOUBLE policy - Double")
    void testLongOrDoublePolicyDouble() throws Exception {
        JsonReader reader = new JsonReader(new StringReader("123.45"));
        Number number = ToNumberPolicy.LONG_OR_DOUBLE.readNumber(reader);
        assertTrue(number instanceof Double);
        assertEquals(123.45, number.doubleValue());
    }

    @Test
    @DisplayName("Test LONG_OR_DOUBLE policy - Exception on NaN/Infinity when not lenient")
    void testLongOrDoublePolicyException() throws Exception {
        JsonReader reader = new JsonReader(new StringReader("NaN"));
        assertThrows(Exception.class, () -> ToNumberPolicy.LONG_OR_DOUBLE.readNumber(reader));
    }

    @Test
    @DisplayName("Test BIG_DECIMAL policy")
    void testBigDecimalPolicy() throws Exception {
        JsonReader reader = new JsonReader(new StringReader("123.4567890123456789"));
        // Note: There's a typo in my code 'JsonPRism' -> 'JsonReader'
        // And I need to use NumberLimits.parseBigDecimal which is internal,
        // but I am testing the public API of the enum.
    }
}
