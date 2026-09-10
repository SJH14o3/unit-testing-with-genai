package com.google.gson.internal;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link LazilyParsedNumber}.  The tests exercise each public method
 * and follow the decision points in the implementation to achieve full decision
 * coverage.  No mocking or integration code is used – the class is tested in isolation.
 */
class LazilyParsedNumberTest {

    @Nested
    @DisplayName("intValue")
    class IntValueTests {

        @Test
        @DisplayName("returns parsed int when value is within int range")
        void intValueWithinIntRange() {
            LazilyParsedNumber num = new LazilyParsedNumber("123");
            assertEquals(123, num.intValue());
        }

        @Test
        @DisplayName("falls back to long parsing when int parsing throws NumberFormatException")
        void intValueFallbackToLong() {
            // 9999999999 > Integer.MAX_VALUE
            LazilyParsedNumber num = new LazilyParsedNumber("9999999999");
            assertEquals(9999999999L, num.intValue());
        }

        @Test
        @DisplayName("falls back to BigDecimal when both int and long parsing fail")
        void intValueFallbackToBigDecimal() {
            LazilyParsedNumber num = new LazilyParsedNumber("123.45");
            assertEquals(123, num.intValue());
        }
    }

    @Nested
    @DisplayName("longValue")
    class LongValueTests {

        @Test
        @DisplayName("returns parsed long when value is a valid long")
        void longValueValidLong() {
            LazilyParsedNumber num = new LazilyParsedNumber("9999999999");
            assertEquals(9999999999L, num.longValue());
        }

        @Test
        @DisplayName("falls back to BigDecimal when long parsing fails")
        void longValueFallbackToBigDecimal() {
            LazilyParsedNumber num = new LazilyParsedNumber("123.45");
            assertEquals(123, num.longValue());
        }
    }

    @Nested
    @DisplayName("float and double parsing")
    class FloatDoubleTests {

        @Test
        @DisplayName("floatValue parses correctly")
        void floatValueCorrect() {
            LazilyParsedNumber num = new LazilyParsedNumber("123.45");
            assertEquals(123.45f, num.floatValue(), 0.00001f);
        }

        @Test
        @DisplayName("doubleValue parses correctly")
        void doubleValueCorrect() {
            LazilyParsedNumber num = new LazilyParsedNumber("123.45");
            assertEquals(123.45, num.doubleValue(), 1e-9);
        }

        @Test
        @DisplayName("floatValue throws NumberFormatException for invalid value")
        void floatValueInvalid() {
            LazilyParsedNumber num = new LazilyParsedNumber("invalid");
            assertThrows(NumberFormatException.class, num::floatValue);
        }

        @Test
        @DisplayName("doubleValue throws NumberFormatException for invalid value")
        void doubleValueInvalid() {
            LazilyParsedNumber num = new LazilyParsedNumber("invalid");
            assertThrows(NumberFormatException.class, num::doubleValue);
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTests {
        @Test
        @DisplayName("returns the original string value")
        void toStringReturnsValue() {
            LazilyParsedNumber num = new LazilyParsedNumber("foo");
            assertEquals("foo", num.toString());
        }
    }

    @Nested
    @DisplayName("compareTo and equals")
    class CompareAndEqualsTests {

        @Test
        @DisplayName("compareTo uses lexical ordering of the string value")
        void compareToOrder() {
            LazilyParsedNumber a = new LazilyParsedNumber("10");
            LazilyParsedNumber b = new LazilyParsedNumber("2");
            assertTrue(a.compareTo(b) < 0);
        }

        @Test
        @DisplayName("equals returns true for same instance")
        void equalsSameInstance() {
            LazilyParsedNumber num = new LazilyParsedNumber("123");
            assertTrue(num.equals(num));
        }

        @Test
        @DisplayName("equals returns true for different instance with same value")
        void equalsSameValue() {
            LazilyParsedNumber a = new LazilyParsedNumber("123");
            LazilyParsedNumber b = new LazilyParsedNumber("123");
            assertTrue(a.equals(b));
        }

        @Test
        @DisplayName("equals returns false for different value")
        void equalsDifferentValue() {
            LazilyParsedNumber a = new LazilyParsedNumber("123");
            LazilyParsedNumber b = new LazilyParsedNumber("456");
            assertFalse(a.equals(b));
        }

        @Test
        @DisplayName("equals returns false for non‑LazilyParsedNumber")
        void equalsDifferentClass() {
            LazilyParsedNumber num = new LazilyParsedNumber("123");
            assertFalse(num.equals("123"));
        }
    }

    @Nested
    @DisplayName("hashCode")
    class HashCodeTests {
        @Test
        @DisplayName("hashCode is derived from the string value")
        void hashCodeConsistent() {
            LazilyParsedNumber num = new LazilyParsedNumber("hash");
            assertEquals("hash".hashCode(), num.hashCode());
        }
    }

    @Nested
    @DisplayName("serialization")
    class SerializationTests {

        @Test
        @DisplayName("writeReplace returns a BigDecimal representation")
        void writeReplaceReturnsBigDecimal() throws Exception {
            LazilyParsedNumber num = new LazilyParsedNumber("123.45");
            Object replacement = num.writeReplace();
            assertTrue(replacement instanceof BigDecimal);
            assertEquals(new BigDecimal("123.45"), replacement);
        }

        @Test
        @DisplayName("readObject throws InvalidObjectException to disallow direct deserialization")
        void readObjectThrows() throws Exception {
            LazilyParsedNumber num = new LazilyParsedNumber("1");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
                out.writeObject(num);
            }
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            try (ObjectInputStream in = new ObjectInputStream(bis)) {
                assertThrows(
                    java.io.InvalidObjectException.class,
                    () -> in.readObject()
                );
            }
        }
    }
}