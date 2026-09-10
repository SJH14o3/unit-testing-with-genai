package com.google.gson;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonNull Test")
class JsonNullTest {

    @Test
    @DisplayName("INSTANCE should not be null")
    void testInstanceNotNull() {
        assertNotNull(JsonNull.INSTANCE);
    }

    @Test
    @DisplayName("deepCopy should return the same instance")
    void testDeepCopy() {
        assertSame(JsonNull.INSTANCE, JsonNull.INSTANCE.deepCopy());
    }

    @Test
    @DisplayName("hashCode should be consistent with class hash code")
    void testHashCode() {
        assertEquals(JsonNull.class.hashCode(), JsonNull.INSTANCE.hashCode());
    }

    @Test
    @DisplayName("equals should work correctly for JsonNull instances")
    void testEquals() {
        // Test equality with itself
        assertEquals(JsonNull.INSTANCE, JsonNull.INSTANCE);
        
        // Test equality with another instance (using deprecated constructor)
        JsonNull anotherInstance = new JsonNull();
        assertEquals(JsonNull.INSTANCE, anotherInstance);
        
        // Test non-equality with null
        assertNotEquals(null, JsonNull.INSTANCE);
        
        // Test non-equality with different type
        assertNotEquals("Not a JsonNull", JsonNull.INSTANCE);
    }
}