/**
 * Unit tests for {@link com.google.gson.JsonPrimitive}.
 * Covers all decision points, including constructors, type checks,
 * value conversions and equality/hashcode logic.
 */
package com.google.gson;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

class JsonPrimitiveTest {

    @Test
    void booleanConstructorAndAccess() {
        JsonPrimitive jp = new JsonPrimitive(Boolean.TRUE);
        assertTrue(jp.isBoolean());
        assertEquals(true, jp.getAsBoolean());
        assertEquals("true", jp.getAsString());
    }

    @Test
    void numberConstructorAndAccess() {
        JsonPrimitive jpInt = new JsonPrimitive(42);
        assertTrue(jpInt.isNumber());
        assertEquals(42, jpInt.getAsInt());
        assertEquals(42L, jpInt.getAsLong());
        assertEquals(42.0, jpInt.getAsDouble(), 1e-9);

        JsonPrimitive jpDouble = new JsonPrimitive(3.14);
        assertTrue(jpDouble.isNumber());
        assertEquals(3.14, jpDouble.getAsDouble(), 1e-9);
        assertEquals(3.14f, jpDouble.getAsFloat(), 1e-6f);
    }

    @Test
    void stringConstructorAndAccess() {
        JsonPrimitive jp = new JsonPrimitive("foo");
        assertTrue(jp.isString());
        assertEquals("foo", jp.getAsString());
        assertEquals(3, jp.getAsString().length());
    }

    @Test
    void characterConstructorAndAccess() {
        JsonPrimitive jp = new JsonPrimitive('x');
        assertTrue(jp.isString());
        assertEquals("x", jp.getAsString());
        assertEquals('x', jp.getAsCharacter());
    }

    @Test
    void getAsNumberFromString() {
        JsonPrimitive jp = new JsonPrimitive("123");
        // isNumber is false but getAsNumber should parse lazily
        assertEquals(123, jp.getAsNumber().intValue());
    }

    @Test
    void getAsNumberFromNonNumericStringThrows() {
        JsonPrimitive jp = new JsonPrimitive("notANumber");
        assertThrows(RuntimeException.class, () -> jp.getAsNumber().intValue());
    }

    @Test
    void getAsCharacterEmptyThrows() {
        JsonPrimitive jp = new JsonPrimitive("");
        assertThrows(UnsupportedOperationException.class, jp::getAsCharacter);
    }

    @Test
    void equalsAndHashCodeConsistency() {
        JsonPrimitive jpInt = new JsonPrimitive(42);
        JsonPrimitive jpLong = new JsonPrimitive(42L);
        assertEquals(jpInt, jpLong);
        assertEquals(jpInt.hashCode(), jpLong.hashCode());

        JsonPrimitive jpString = new JsonPrimitive("42");
        assertEquals(jpInt, jpString);
        assertEquals(jpInt.hashCode(), jpString.hashCode());
    }

    @Test
    void bigDecimalEqualityWithScale() {
        JsonPrimitive jp1 = new JsonPrimitive(new BigDecimal("1.0"));
        JsonPrimitive jp2 = new JsonPrimitive(new BigDecimal("1.000"));
        assertEquals(jp1, jp2);
    }

    @Test
    void negativeZeroEquality() {
        JsonPrimitive jpPosZero = new JsonPrimitive(0.0);
        JsonPrimitive jpNegZero = new JsonPrimitive(-0.0);
        assertEquals(jpPosZero, jpNegZero);
    }

    @Test
    void nanEquality() {
        JsonPrimitive jpNaN1 = new JsonPrimitive(Double.NaN);
        JsonPrimitive jpNaN2 = new JsonPrimitive(Float.NaN);
        assertEquals(jpNaN1, jpNaN2);
    }

    @Test
    void nonNumberValueEqualsFalse() {
        JsonPrimitive jpNumber = new JsonPrimitive(42);
        JsonPrimitive jpString = new JsonPrimitive("foo");
        assertNotEquals(jpNumber, jpString);
    }
}
