package com.google.gson;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonElement Tests")
class JsonElementTest {

    @Test
    @DisplayName("isJsonArray correctly identifies JsonArray")
    void isJsonArray_works() {
        JsonArray array = new JsonArray();
        assertTrue(array.isJsonArray());
        JsonPrimitive primitive = new JsonPrimitive("test");
        assertFalse(primitive.isJsonArray());
    }

    @Test
    @DisplayName("isJsonObject correctly identifies JsonObject")
    void isJsonObject_works() {
        JsonObject obj = new JsonObject();
        assertTrue(obj.isJsonObject());
        JsonPrimitive primitive = new JsonPrimitive("test");
        assertFalse(primitive.isJsonObject());
    }

    @Test
    @DisplayName("isJsonPrimitive correctly identifies JsonPrimitive")
    void isJsonPrimitive_works() {
        JsonPrimitive primitive = new JsonPrimitive("test");
        assertTrue(primitive.isJsonPrimitive());
        JsonArray array = new JsonArray();
        assertFalse(array.isJsonPrimitive());
    }

    @Test
    @DisplayName("isJsonNull correctly identifies JsonNull")
    void isJsonNull_works() {
        assertTrue(JsonNull.INSTANCE.isJsonNull());
        JsonPrimitive primitive = new JsonPrimitive("test");
        assertFalse(primitive.isJsonNull());
    }

    @Test
    @DisplayName("getAsJsonObject throws exception if not an object")
    void getAsJsonObject_throwsException() {
        JsonPrimitive primitive = new JsonPrimitive("test");
        assertThrows(IllegalStateException.class, primitive::getAsJsonObject);
    }

    @Test
    @DisplayName("getAsJsonArray throws exception if not an array")
    void getAsJsonArray_throwsException() {
        JsonPrimitive primitive = new JsonPrimitive("test");
        assertThrows(IllegalStateException.class, primitive::getAsJsonArray);
    }

    @Test
    @DisplayName("getAsJsonPrimitive throws exception if not a primitive")
    void getAsJsonPrimitive_throwsException() {
        JsonArray array = new JsonArray();
        assertThrows(IllegalStateException.class, array::getAsJsonPrimitive);
    }

    @Test
    @DisplayName("getAsJsonNull throws exception if not a null")
    void getAsJsonNull_throwsException() {
        JsonPrimitive primitive = new JsonPrimitive("test");
        assertThrows(IllegalStateException.class, primitive::getAsJsonNull);
    }

    @Test
    @DisplayName("getAsBoolean throws exception if not a primitive or array")
    void getAsBoolean_throwsException() {
        JsonArray array = new JsonArray();
        assertThrows(UnsupportedOperationException.class, array::getAsBoolean);
    }

    @Test
    @DisplayName("getAsNumber throws exception if not a primitive or array")
    void getAsNumber_throwsException() {
        JsonArray array = new JsonArray();
        assertThrows(UnsupportedOperationException.class, array::getAsNumber);
    }

    @Test
    @DisplayName("getAsString throws exception if not a primitive or array")
    void getAsString_throwsException() {
        JsonArray array = new JsonArray();
        assertThrows(UnsupportedOperationException.class, array::getAsString);
    }

    @Test
    @DisplayName("getAsDouble throws exception if not a primitive or array")
    void getAsDouble_throwsException() {
        JsonArray array = new JsonArray();
        assertThrows(UnsupportedOperationException.class, array::getAsDouble);
    }

    @Test
    @DisplayName("toString returns correct JSON representation")
    void toString_works() {
        JsonPrimitive primitive = new JsonPrimitive("test");
        assertEquals("\"test\"", primitive.toString());
        
        JsonArray array = new JsonArray();
        array.add("a");
        assertEquals("[\"a\"]", array.toString());

        JsonObject obj = new JsonObject();
        obj.addProperty("key", "value");
        assertEquals("{\"key\":\"value\"}", obj.toString());
    }
    
    @Test
    @DisplayName("deepCopy for JsonElement subclasses")
    void deepCopy_works() {
        JsonPrimitive primitive = new JsonPrimitive("test");
        assertSame(primitive, primitive.deepCopy());

        JsonNull nullElement = JsonNull.INSTANCE;
        assertSame(nullElement, nullElement.deepCopy());
    }
}
