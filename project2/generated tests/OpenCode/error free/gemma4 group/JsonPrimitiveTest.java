package com.google.gson;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonPrimitive Tests")
class JsonPrimitiveTest {

    @Test
    @DisplayName("Constructor with Boolean works")
    void constructor_boolean() {
        JsonPrimitive primitive = new JsonPrimitive(true);
        assertTrue(primitive.isBoolean());
        assertTrue(primitive.getAsBoolean());
    }

    @Test
    @DisplayName("Constructor with Number works")
    void constructor_number() {
        JsonPrimitive primitive = new JsonPrimitive(123); // Wait, I can't use number directly if I don't have helper
        // Let's use real number
        JsonPrimitive p = new JsonPrimitive(123);
        assertTrue(p.isNumber());
        assertEquals(123, p.getAsInt());
    }

//    // Helper class to avoid using non-existent class
//    private static class JsonableNumber extends JsonPrimitive {
//        JsonableNumber(int n) { super(n); }
//    }

    @Test
    @DisplayName("Constructor with String works")
    void constructor_string() {
        JsonPrimitive primitive = new JsonPrimitive("test");
        assertTrue(primitive.isString());
        assertEquals("test", primitive.getAsString());
    }

    @Test
    @DisplayName("Constructor with Character works")
    void constructor_character() {
        JsonPrimitive primitive = new JsonPrimitive('a');
        assertTrue(primitive.isString()); // Character is converted to String
        assertEquals("a", primitive.getAsString());
    }

    @Test
    @DisplayName("deepCopy returns this")
    void deepCopy_returnsThis() {
        JsonPrimitive primitive = new JsonPrimitive("test");
        assertSame(primitive, primitive.deepCopy());
    }

    @Test
    @DisplayName("getAsBoolean parses string 'true'")
    void getAsBoolean_parsesString() {
        JsonPrimitive primitive = new JsonPrimitive("true");
        assertTrue(primitive.getAsBoolean());
        JsonPrimitive primitiveFalse = new JsonPrimitive("false");
        assertFalse(primitiveFalse.getAsBoolean());
    }

    @Test
    @DisplayName("getAsNumber handles String")
    void getAsNumber_fromString() {
        JsonPrimitive primitive = new JsonPrimitive("123");
        assertEquals(123, primitive.getAsInt());
    }

    @Test
    @DisplayName("getAsString works for Number and Boolean")
    void getAsString_works() {
        JsonPrimitive p1 = new JsonPrimitive(123);
        assertEquals("123", p1.getAsString());
        JsonPrimitive p2 = new JsonPrimitive(true);
        assertEquals("true", p2.getAsString());
    }

    @Test
    @DisplayName("getAsBigDecimal works")
    void getAsBigDecimal_works() {
        JsonPrimitive primitive = new JsonPrimitive("123.45");
        assertEquals(new java.math.BigDecimal("123.45"), primitive.getAsBigDecimal());
    }

    @Test
    @DisplayName("getAsBigInteger works")
    void getAsBigInteger_works() {
        JsonPrimitive primitive = new JsonPrimitive("123");
        assertEquals(new java.math.BigInteger("123"), primitive.getAsBigInteger());
    }

    @Test
    @DisplayName("getAsFloat works")
    void getAsFloat_works() {
        // JsonDimension(primitive) where primitive = new JsonPrimitive("1.5");
        // Let's fix the syntax
        JsonPrimitive p = new JsonPrimitive("1.5");
        assertEquals(1.5f, p.getAsFloat());
    }

    @Test
    @DisplayName("getAsLong works")
    void getAsLong_works() {
        JsonPrimitive primitive = new JsonPrimitive("123");
        assertEquals(123L, primitive.getAsLong());
    }

    @Test
    @DisplayName("getAsShort works")
    void getAsShort_works() {
        JsonPrimitive primitive = new JsonPrimitive("123");
        assertEquals((short)123, primitive.getAsShort());
    }

    @Test
    @DisplayName("getAsInt works")
    void getAsInt_works() {
        JsonPrimitive primitive = new JsonPrimitive("123");
        assertEquals(123, primitive.getAsInt());
    }

    @Test
    @DisplayName("getAsByte works")
    void getAsByte_works() {
        JsonPrimitive primitive = new JsonPrimitive(true); // Wait, I'll just use number
        JsonPrimitive p = new JsonPrimitive(123);
        assertEquals((byte)123, p.getAsByte());
    }

    @Test
    @DisplayName("getAsCharacter is deprecated but works")
    void getAsCharacter_works() {
        JsonPrimitive primitive = new JsonPrimitive("abc");
        assertEquals('a', primitive.getAsCharacter());
    }

    @Test
    @DisplayName("hashCode and equals work correctly")
    void equalsAndHashCode() {
        JsonPrimitive p1 = new JsonPrimitive("test");
        JsonPrimitive p2 = new JsonPrimitive("test");
        JsonPrimitive p3 = new JsonPrimitive("other");

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1, p3);
    }
}

//// Dummy class for the broken test above
//class BooleanPrimitive extends JsonPrimitive {
//    BooleanPrimitive(boolean b) { super(b); }
//}
