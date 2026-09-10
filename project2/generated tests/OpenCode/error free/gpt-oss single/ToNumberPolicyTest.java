package com.google.gson;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.internal.NumberLimits;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.MalformedJsonException;
import java.io.StringReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;
/**
 - Unit tests for {@link ToNumberPolicy}.  The tests exercise each {@code readNumber}
 - implementation and cover all decision points—normal parsing, overflow handling,
 - lenient vs strict modes, and exceptional parsing paths.
 -
 - <p>Decision coverage:
 - <ul>
 - <li>DOUBLE: always returns a {@code Double} from the reader.</li>
 - <li>LAZILY_PARSED_NUMBER: returns a {@link LazilyParsedNumber} backed by a string.</li>
 - <li>LONG_OR_DOUBLE:
 - <ul>
 -   <li>contains a decimal point → {@code parseAsDouble}</li>
 -   <li>integer within long range → {@link Long#parseLong}</li>
 -   <li>integer out of long range → fallback to double</li>
 -   <li>double resulting in Infinity/NaN and non‑lenient → throws</li>
 -   <li>otherwise parses to {@link Double}</li>
 - </ul>
 - </li>
 - <li>BIG_DECIMAL: delegates to {@link NumberLimits#parseBigDecimal}, throws
 - when parsing fails.</li>
 - </ul>
 */
@DisplayName("ToNumberPolicy")
class ToNumberPolicyTest {
    /**
     - Minimal {@link JsonReader} that reads a single token from a string.
     */
    private JsonReader singleValueReader(String value, boolean lenient) {
        JsonReader reader = new JsonReader(new StringReader(String.format("{\"v\":%s}", value)));
        reader.setLenient(lenient);
        // Move to the value token
        try {
            reader.beginObject();
            reader.nextName(); // "v"
            return reader;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
    @Nested
    @DisplayName("DOUBLE")
    class DoubleTest {
        @Test
        void readReturnsDouble() throws IOException {
            JsonReader r = singleValueReader("3.14", true);
            Number d = ToNumberPolicy.DOUBLE.readNumber(r);
            assertEquals(3.14, d);
        }
    }
    @Nested
    @DisplayName("LAZILY_PARSED_NUMBER")
    class LazilyParsedTest {
        @Test
        void readReturnsLazilyParsedNumber() throws IOException {
            JsonReader r = singleValueReader("42", true);
            Number n = ToNumberPolicy.LAZILY_PARSED_NUMBER.readNumber(r);
            assertTrue(n instanceof LazilyParsedNumber);
            assertEquals(42, n.longValue());
        }
    }
    @Nested
    @DisplayName("LONG_OR_DOUBLE")
    class LongOrDoubleTest {
        @Test
        void integerWithinLongRange() throws IOException, JsonParseException {
            JsonReader r = singleValueReader("12345", true);
            Number n = ToNumberPolicy.LONG_OR_DOUBLE.readNumber(r);
            assertTrue(n instanceof Long);
            assertEquals(12345L, n.longValue());
        }

        @Test
        void integerOutOfLongRangeFallsBackToDouble() throws IOException, JsonParseException {
            JsonReader r = singleValueReader("9223372036854775808", true); // Long.MAX + 1
            Number n = ToNumberPolicy.LONG_OR_DOUBLE.readNumber(r);
            assertTrue(n instanceof Double);
            assertEquals(9.223372036854776E18, n.doubleValue(), 1e-6);
        }

        @Test
        void decimalNumberParsedAsDouble() throws IOException, JsonParseException {
            JsonReader r = singleValueReader("1.23", true);
            Number n = ToNumberPolicy.LONG_OR_DOUBLE.readNumber(r);
            assertTrue(n instanceof Double);
            assertEquals(1.23, n.doubleValue());
        }

        @Test
        void infinityNonLenientThrowsMalformedJsonException() {
            JsonReader r = singleValueReader("Infinity", false);
            assertThrows(MalformedJsonException.class, () -> ToNumberPolicy.LONG_OR_DOUBLE.readNumber(r));
        }

        @Test
        void infinityLenientReturnsDoubleInfinity() throws IOException, JsonParseException {
            JsonReader r = singleValueReader("Infinity", true);
            Number n = ToNumberPolicy.LONG_OR_DOUBLE.readNumber(r);
            assertEquals(Double.POSITIVE_INFINITY, n.doubleValue());
        }

        @Test
        void nanNonLenientThrowsMalformedJsonException() {
            JsonReader r = singleValueReader("NaN", false);
            assertThrows(MalformedJsonException.class, () -> ToNumberPolicy.LONG_OR_DOUBLE.readNumber(r));
        }

        @Test
        void nanLenientReturnsDoubleNaN() throws IOException, JsonParseException {
            JsonReader r = singleValueReader("NaN", true);
            Number n = ToNumberPolicy.LONG_OR_DOUBLE.readNumber(r);
            assertTrue(Double.isNaN(n.doubleValue()));
        }

        @Test
        void malformedNumberThrowsJsonParseException() {
            JsonReader r = singleValueReader("abc", true);
            assertThrows(JsonParseException.class, () -> ToNumberPolicy.LONG_OR_DOUBLE.readNumber(r));
        }
    }
    @Nested
    @DisplayName("BIG_DECIMAL")
    class BigDecimalTest {
        @Test
        void validBigDecimal() throws IOException, JsonParseException {
            JsonReader r = singleValueReader("\"12345678901234567890.1234567890\"", true);
            BigDecimal bd = (BigDecimal) ToNumberPolicy.BIG_DECIMAL.readNumber(r);
            assertEquals(new BigDecimal("12345678901234567890.1234567890"), bd);
        }

        @Test
        void invalidBigDecimalThrowsJsonParseException() {
            JsonReader r = singleValueReader("\"notanumber\"", true);
            assertThrows(JsonParseException.class, () -> ToNumberPolicy.BIG_DECIMAL.readNumber(r));
        }
    }
}