package com.google.gson;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import java.util.Set;

@DisplayName("JsonObject Tests")
class JsonObjectTest {

    private JsonObject jsonObject;

    @BeforeEach
    void setUp() {
        jsonObject = new JsonObject();
    }

    @Test
    @DisplayName("Constructor creates empty object")
    void constructor_empty() {
        assertTrue(jsonObject.isEmpty());
        assertEquals(0, jsonArraySize(jsonObject)); // Error: method is size()
    }

    private int jsonArraySize(JsonObject obj) {
        return obj.size();
    }

    @Test
    @DisplayName("addProperty works for different types")
    void addProperty_works() {
        jsonObject.addProperty("string", "value");
        jsonObject.addProperty("number", 123);
        jsonObject.addProperty("boolean", true);
        jsonObject.addProperty("char", 'a');

        assertEquals(4, jsonObject.size());
        assertEquals("value", jsonObject.get("string").getAsString());
        assertEquals(123, jsonObject.get("number").getAsInt());
        assertTrue(jsonObject.get("boolean").getAsBoolean());
        assertEquals('a', jsonObject.get("char").getAsString().charAt(0));
    }

    @Test
    @DisplayName("add(String, JsonElement) handles null value")
    void add_handlesNull() {
        jsonObject.add("key", null);
        assertTrue(jsonObject.get("key").isJsonNull());
    }

    @Test
    @DisplayName("remove(String) removes member and returns it")
    void remove_works() {
        jsonObject.addProperty("key", "value");
        JsonElement removed = jsonObject.remove("key");
        assertNotNull(removed);
        assertEquals("value", removed.getAsString());
        assertFalse(jsonObject.has("key"));
    }

    @Test
    @DisplayName("remove(String) returns null if member not found")
    void remove_not_found() {
        assertNull(jsonObject.remove("nonexistent"));
    }

    @Test
    @DisplayName("has(String) works correctly")
    void has_works() {
        jsonObject.addProperty("key", "value");
        assertTrue(jsonObject.has("key"));
        assertFalse(jsonObject.has("unknown"));
    }

    @Test
    @DisplayName("getAsJsonPrimitive/Array/Object works")
    void getAsTypes_works() {
        jsonObject.addProperty("primitive", "value");
        JsonArray array = new JsonArray();
        array.add("item");
        jsonObject.add("array", array);
        jsonObject.add("object", new JsonObject());

        assertEquals("value", jsonObject.getAsJsonPrimitive("primitive").getAsString());
        assertEquals("item", jsonObject.getAsJsonArray("array").get(0).getAsString());
        assertTrue(jsonObject.getAsJsonObject("object").isEmpty());
    }

    @Test
    @DisplayName("keySet and entrySet work")
    void keySetAndEntrySet_works() {
        jsonObject.addProperty("a", 1);
        jsonObject.addProperty("b", 2);

        Set<String> keys = jsonObject.keySet();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("a"));
        assertTrue(keys.contains("b"));

        Set<Map.Entry<String, JsonElement>> entries = jsonObject.entrySet();
        assertEquals(2, entries.size());
    }

    @Test
    @DisplayName("asMap returns mutable map view")
    void asMap_isMutable() {
        Map<String, JsonElement> map = jsonObject.asMap();
        map.put("newKey", new JsonPrimitive(100));
        assertTrue(jsonObject.has("newKey"));
        assertEquals(100, jsonObject.get("newKey").getAsInt());
    }

    @Test
    @DisplayName("equals and hashCode work correctly")
    void equalsAndHashCode() {
        JsonObject obj1 = new JsonObject();
        obj1.addProperty("a", 1);
        JsonObject obj2 = new JsonObject();
        obj2.addProperty("a", 1);
        JsonObject obj3 = new JsonObject();
        obj3.addProperty("b", 2);

        assertEquals(obj1, obj2);
        assertEquals(obj1.hashCode(), objObjectHashCode(obj2));
        assertNotEquals(obj1, obj3);
    }

    private int objObjectHashCode(JsonObject obj) {
        return obj.hashCode();
    }
}
