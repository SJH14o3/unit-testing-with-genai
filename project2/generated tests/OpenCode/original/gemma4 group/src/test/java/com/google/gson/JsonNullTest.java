package com.google.gson;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonNull Tests")
class JsonNullTest {

    @Test
    @DisplayName("JsonNull.INSTANCE is a singleton")
    void singleton_isSame() {
        assertSame(JsonNull.INSTANCE, JsonNull.INSTANCE);
    }

    @Test
    @DisplayName("deepCopy returns the same instance")
    void deepCopy_returnsSame() {
        assertSame(JsonNull.INSTANCE, JsonNull.INSTANCE.deepCopy());
    }

    @Test
    @DisplayName("hashCode is consistent")
    void hashCode_isConsistent() {
        assertEquals(JsonNull.INSTANCE.hashCode(), JsonNull.INSTANCE.hashCode());
    }

    @Test
    @DisplayName("equals works correctly")
    void equals_works() {
        assertTrue(JsonNull.INSTANCE.equals(JsonNull.INSTANCE));
        assertTrue(JsonNull.INSTANCE.equals(new JsonNull())); // if constructor is used
        assertFalse(JsonNull.INSTANCE.equals(new JsonPrimitive("null")));
        assertFalse(JsonNull.INSTANCE.equals(null));
    }
}
