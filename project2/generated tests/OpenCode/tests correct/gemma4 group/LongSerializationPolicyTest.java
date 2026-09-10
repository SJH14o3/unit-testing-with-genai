package com.google.gson;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class LongSerializationPolicyTest {

    @Test
    @DisplayName("Test DEFAULT policy - null value")
    void testDefaultNull() {
        assertEquals(JsonNull.INSTANCE, LongSerializationPolicy.DEFAULT.serialize(null));
    }

    @Test
    @DisplayName("Test DEFAULT policy - non-null value")
    void testDefaultNonNull() {
        JsonElement element = LongSerializationPolicy.DEFAULT.serialize(123L);
        assertTrue(element.isJsonPrimitive());
        assertEquals(123L, element.getAsLong());
    }

    @Test
    @DisplayName("Test STRING policy - null value")
    void testStringNull() {
        assertEquals(JsonNull.INSTANCE, LongSerializationPolicy.STRING.serialize(null));
    }

    @Test
    @DisplayName("Test STRING policy - non-null value")
    void testStringNonNull() {
        JsonElement element = LongSerializationPolicy.STRING.serialize(123L);
        assertTrue(element.isJsonPrimitive());
        assertEquals("123", element.getAsString());
    }
}
