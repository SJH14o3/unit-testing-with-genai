package com.google.gson;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

class JsonObjectTest {

    @Test
    @DisplayName("add handles null values")
    void testAddNull() {
        JsonObject obj = new JsonObject();
        obj.add("nullKey", null);
        assertTrue(obj.get("nullKey") instanceof JsonNull);
        obj.addProperty("nullProp", (String) null);
        assertTrue(obj.get("nullProp") instanceof JsonNull);
        obj.addProperty("nullNum", (Number) null);
        assertTrue(obj.get("nullNum") instanceof JsonNull);
        obj.addProperty("nullBool", (Boolean) null);
        assertTrue(obj.get("nullBool") instanceof JsonNull);
        obj.addProperty("nullChar", (Character) null);
        assertTrue(obj.get("nullChar") instanceof JsonNull);
    }

    @Test
    @DisplayName("addProperty overloads store correct primitive types")
    void testAddPropertyTypes() {
        JsonObject obj = new JsonObject();
        obj.addProperty("str", "hello");
        obj.addProperty("num", 42L);
        obj.addProperty("bool", true);
        obj.addProperty("char", 'c');

        JsonPrimitive pStr = obj.getAsJsonPrimitive("str");
        assertEquals("hello", pStr.getAsString());
        assertTrue(pStr.isString());

        JsonPrimitive pNum = obj.getAsJsonPrimitive("num");
        assertEquals(42L, pNum.getAsLong());
        assertTrue(pNum.isNumber());

        JsonPrimitive pBool = obj.getAsJsonPrimitive("bool");
        assertTrue(pBool.getAsBoolean());
        assertTrue(pBool.isBoolean());

        JsonPrimitive pChar = obj.getAsJsonPrimitive("char");
        assertEquals('c', pChar.getAsCharacter());
        assertTrue(pChar.isString()); // characters are stored as strings
    }

    @Test
    @DisplayName("deepCopy creates an independent copy")
    void testDeepCopyIndependence() {
        JsonObject original = new JsonObject();
        original.addProperty("a", 1);
        JsonObject copy = original.deepCopy();
        assertEquals(original, copy);
        // modify original
        original.addProperty("b", 2);
        assertNotEquals(original, copy);
        assertNull(copy.get("b"));
    }

    @Test
    @DisplayName("remove returns removed element and handles missing key")
    void testRemove() {
        JsonObject obj = new JsonObject();
        JsonPrimitive value = new JsonPrimitive("val");
        obj.add("key", value);
        JsonElement removed = obj.remove("key");
        assertSame(value, removed);
        assertNull(obj.get("key"));
        assertNull(obj.remove("nonexistent"));
    }

    @Test
    @DisplayName("has, get, entrySet, keySet, size, isEmpty work correctly")
    void testCollectionMethods() {
        JsonObject obj = new JsonObject();
        assertTrue(obj.isEmpty());
        assertEquals(0, obj.size());
        assertFalse(obj.has("x"));

        obj.addProperty("x", 10);
        obj.addProperty("y", 20);

        assertFalse(obj.isEmpty());
        assertEquals(2, obj.size());
        assertTrue(obj.has("x"));
        assertEquals(new JsonPrimitive(10), obj.get("x"));

        assertEquals(2, obj.entrySet().size());
        assertTrue(obj.keySet().contains("x"));
        assertTrue(obj.keySet().contains("y"));
    }

    @Test
    @DisplayName("asMap provides a mutable view of the underlying members")
    void testAsMapMutable() {
        JsonObject obj = new JsonObject();
        obj.addProperty("a", 1);
        java.util.Map<String, JsonElement> map = obj.asMap();
        map.put("b", new JsonPrimitive(2));
        assertTrue(obj.has("b"));
        ((com.google.gson.JsonElement) map.remove("a")).equals(JsonNull.INSTANCE); // just to suppress unused
        assertFalse(obj.has("a"));
    }

    @Test
    @DisplayName("getAsJsonPrimitive and others throw ClassCastException on wrong type")
    void testGetAsTypedWrong() {
        JsonObject obj = new JsonObject();
        obj.addProperty("str", "text");
        obj.add("arr", new JsonArray());
        obj.add("obj", new JsonObject());

        assertThrows(ClassCastException.class, () -> obj.getAsJsonArray("str"));
        assertThrows(ClassCastException.class, () -> obj.getAsJsonObject("arr"));
        assertThrows(ClassCastException.class, () -> obj.getAsJsonPrimitive("arr"));
    }

    @Test
    @DisplayName("equals and hashCode are consistent irrespective of insertion order")
    void testEqualsAndHash() {
        JsonObject a = new JsonObject();
        a.addProperty("x", 1);
        a.addProperty("y", 2);

        JsonObject b = new JsonObject();
        b.addProperty("y", 2);
        b.addProperty("x", 1);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
