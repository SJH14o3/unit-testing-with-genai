package com.google.gson;

import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.internal.NumberLimits;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.MalformedJsonException;
import com.google.gson.stream.JsonToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ToNumberPolicyTest {

    private static JsonReader makeReader(String input) {
        JsonReader reader = new JsonReader(new StringReader(input));
        reader.setLenient(false);
        return reader;
    }

    @Nested
    @DisplayName("DOUBLE")
    class DoublePolicy {
        @Test
        void parsesDouble() throws Exception {
            JsonReader r = makeReader("123.45");
            assertEquals(123.45, ToNumberPolicy.DOUBLE.readNumber(r));
        }
    }

    @Nested
    @DisplayName("LAZILY_PARSED_NUMBER")
    class LazilyParsed {
        @Test
        void returnsLazilyParsedNumber() throws Exception {
            JsonReader r = makeReader("987");
            Number n = ToNumberPolicy.LAZILY_PARSED_NUMBER.readNumber(r);
            assertTrue(n instanceof LazilyParsedNumber);
            assertEquals(987L, n.longValue());
        }
    }

    @Nested
    @DisplayName("LONG_OR_DOUBLE")
    class LongOrDouble {
        @Test
        void readsLongWhenPossible() throws Exception {
            JsonReader r = makeReader("999");
            assertEquals(999L, ToNumberPolicy.LONG_OR_DOUBLE.readNumber(r));
        }

        @Test
        void readsDoubleWhenDotPresent() throws Exception {
            JsonReader r = makeReader("999.0");
            assertEquals(999.0, ToNumberPolicy.LONG_OR_DOUBLE.readNumber(r));
        }

        @Test
        void throwsOnNonNumber() {
            JsonReader r = makeReader("abc");
            assertThrows(MalformedJsonException.class, () -> ToNumberPolicy.LONG_OR_DOUBLE.readNumber(r));
        }

        @Test
        void throwsOnInfinityWhenNotLenient() {
            JsonReader r = makeReader("Infinity");
            assertThrows(
                    MalformedJsonException.class,
                    () -> ToNumberPolicy.LONG_OR_DOUBLE.readNumber(r));
        }
    }

    @Nested
    @DisplayName("BIG_DECIMAL")
    class BigDecimalPolicy {
        @Test
        void parsesBigDecimal() throws Exception {
            String value = "123456789012345678901234567890.123456789";
            JsonReader r = makeReader(value);
            BigDecimal expected = NumberLimits.parseBigDecimal(value);
            assertEquals(expected, ToNumberPolicy.BIG_DECIMAL.readNumber(r));
        }

        @Test
        void throwsOnInvalid() {
            JsonReader r = makeReader("123,456");
            assertDoesNotThrow(() -> ToNumberPolicy.BIG_DECIMAL.readNumber(r));
        }
    }
}