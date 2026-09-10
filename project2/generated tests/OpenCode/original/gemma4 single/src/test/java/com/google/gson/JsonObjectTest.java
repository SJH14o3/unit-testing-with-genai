package com.google.gson;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonObject Test")
class JsonObjectTest {

    private JsonObject jsonObject;

    @BeforeEach
    void setUp() {
        jsonObject = new JsonObject();
    }

    @Nested
    @DisplayName("Constructor and Basic Properties")
    class BasicProperties {

        @Test
        @DisplayName("New JsonObject should be empty")
        void newJsonObjectIsEmpty() {
            assertTrue(jsonObject.isEmpty());
            assertEquals(0, jsontoObject.size());
        }

        @Test
        @DisplayName("size() should return correct number of members")
        void sizeReturnsCorrectCount() {
            jsonObject.add("key1", JsonNull.INSTANCE);
            jsonObject.add("key2", new JsonPrimitive("value"));
            assertEquals(2, jsonObject.size());
        }
    }

    @Nested
    @DisplayName("Adding Members")
    class AddingMembers {

        @Test
        @DisplayName("add(String, JsonElement) adds element")
        void addElement() {
            JsonElement value = new JsonPrimitive("test");
            jsonObject.add("key", value);
            assertTrue(jsonObject.has("key"));
            assertEquals(value, jsonObject.get("key"));
        }

        @Test
        @DisplayName("add(String, JsonElement) converts null value to JsonNull")
        void addNullValueConvertsToJsonNull() {
            jsonObject.add("key", null);
            assertTrue(jsonObject.has("key"));
            assertTrue(jsonObject.get("key").isJsonNull());
        }

        @ParameterizedTest
        @DisplayName("addProperty(String, String, Number, Boolean, Character) with various types")
        @CsvSource({
            "strKey, \"strValue\"",
            "numKey, 123",
            "boolKey, true",
            "charKey, 'a'"
        })
        <Note: CsvSource doesn't support all types easily in one go, but let's split by type for clarity>
        void addStringProperty(String key, String value) {
            jsonObject.addProperty(key, value);
            assertEquals(value, jsonObject.getAsJsonPrimitive(key).getAsString());
        }

        @Test
        @DisplayName("addProperty(String, String) handles null")
        void addStringPropertyNull() {
            jsonObject.addProperty("key", null);
            assertTrue(jsonObject.get("key").isJsonNull());
        }

        @Test
        @DisplayName("addProperty(String, Number) handles null")
        void addNumberPropertyNull() {
            jsonObject.addProperty("key", (Number) null);
            assertTrue(jsonObject.get("key").isJsonNull());
        }

        @Test
        @DisplayName("addProperty(String, Boolean) handles null")
        void addBooleanPropertyNull() {
            jsonObject.addProperty("key", (Boolean) null);
            assertTrue(jsonObject.get("key").isJsonNull());
        }

        @Test
        @DisplayName("addProperty(String, Character) handles null")
        void addCharPropertyNull() {
            jsonObject.addProperty("key", (Character) null);
            assertTrue(jsonObject.get("key").isJsonNull());
        }
    }

    @Nested
    @DisplayName("Removing Members")
    class RemovingMembers {

        @Test
        @DisplayName("remove(String) removes existing key and returns element")
        void removeExistingKey() {
            jsonObject.add("key", new JsonPrimitive(1));
            JsonElement removed = jsonObject.remove("key");
            assertNull(jsonObject.get("key"));
            assertEquals(new JsonPrimitive(1), removed);
        }

        @Test
        @DisplayName("remove(String) returns null for non-existent key")
        void removeNonExistentKey() {
            assertNull(jsonObject.remove("nonexistent"));
        }
    }

    @Nested
    @DisplayName("Accessing Members")
    class AccessingMembers {

        @Test
        @DisplayName("get(String) returns correct element")
        void getElement() {
            JsonPrimitive prim = new JsonPrimitive("val");
            jsonObject.add("key", prim);
            assertEquals(prim, jsonObject.get("key"));
        }

        @Test
        @DisplayName("getAsJsonPrimitive(String) returns JsonPrimitive")
        void getAsJsonPrimitive() {
            jsonObject.addProperty("key", "value");
            JsonPrimitive primitive = jsonObject.getAsJsonPrimitive("key");
            assertNotNull(primitive);
            assertEquals("value", primitive.getAsString());
        }

        @Test
        @DisplayName("getAsJsonArray(String) returns JsonArray")
        void getAsJsonArray() {
            JsonArray array = new JsonArray();
            array.add("item");
            jsonObject.add("arrayKey", array);
            assertEquals(array, jsonObject.getAsJsonArray("arrayKey"));
        }

        @Test
        @DisplayName("getAsJsonObject(String) returns JsonObject")
        void getAsJsonObject() {
            JsonObject obj = new JsonObject();
            obj.addProperty("child", "value");
            jsonObject.add("objKey", obj);
            assertEquals(obj, jsonObject.getAsJsonObject("objKey"));
        }

        @Test
        @DisplayName("getAsJsonPrimitive throws ClassCastException if not a primitive")
        void getAsJsonPrimitiveThrowsException() {
            JsonArray array = new JsonArray();
            array.add("item");
            jsonObject.add("arrayKey", array);
            assertThrows(ClassCastException.class, () -> jsonObject.getAsJsonPrimitive("arrayKey"));
        }

        @Test
        @DisplayName("has(String) checks for key existence")
        void hasKey() {
            jsonObject.add("exists", JsonNull.INSTANCE);
            assertTrue(jsonObject.has("exists"));
            assertFalse(jsonObject.has("notExists"));
        }
    }

    @Nested
    @DisplayName("Collections and Views")
    class CollectionsAndViews {

        @Test
        @DisplayName("entrySet() returns all entries")
        void entrySet() {
            jsonObject.add("a", new JsonPrimitive(1));
            jsonObject.add("b", new JsonPrimitive(2));
            Set<Map.Entry<String, JsonElement>> entries = jsonObject.entrySet();
            assertEquals(2, entries.size());
        }

        @Test
        @DisplayName("keySet() returns all keys")
        void keySet() {
            jsonObject.add("a", JsonNull.INSTANCE);
            jsonObject.add("b", JsonNull.INSTANCE);
            Set<String> keys = jsonObject.keySet();
            assertTrue(keys.contains("a"));
            assertTrue(keys.contains("b"));
        }

        @Test
        @DisplayName("asMap() returns mutable map view")
        void asMap() {
            Map<String, JsonElement> map = jsonObject.asMap();
            map.put("newKey", new JsonPrimitive("newValue"));
            assertTrue(jsonObject.has("newKey"));
            assertEquals(new JsonPrimitive("newValue"), jsonObject.get("newKey"));
        }
    }

    @Nested
    @DisplayName("Equality and Hashing")
    class EqualityAndHashing {

        @Test
        @DisplayName("equals() compares objects with same members")
        void equalsSameMembers() {
            JsonObject obj1 = new JsonObject();
            obj1.addProperty("key", "value");

            JsonObject obj2 = new JsonObject();
            obj2.addProperty("key", "value");

            assertEquals(obj1, obj2);
            assertEquals(obj1.hashCode(), objermedObj2.hashCode());
        }

        @Test
        @DisplayName("equals() returns false for different members")
        void equalsDifferentMembers() {
            JsonObject obj1 = new JsonObject();
            obj1.addProperty("key", "value");

            JsonObject obj2 = new JsonObject();
            obj2.addProperty("key", "different");

            assertNotEquals(obj1, obj2);
        }

        @Test
        @DisplayName("equals() handles identity and null")
        void equalsIdentityAndNull() {
            assertEquals(jsonObject, jsonObject);
            assertNotEquals(null, jsonObject);
        }
    }

    @Nested
    @DisplayName("Deep Copy")
    class DeepCopy {

        @Test
        @DisplayName("deepCopy() creates an independent copy")
        void deepCopyImplementation() {
            JsonObject child = new JsonObject();
            child.addProperty("prop", "val");
            jsonObject.add("parent", child);

            JsonObject copy = jsonObject.deepCopy();

            assertEquals(jsonObject, copy);
            assertNotSame(jsonObject, copy);
            
            // Verify deepness
            copy.getAsJsonObject("parent").addProperty("prop", "changed");
            assertEquals("val", jsonObject.getAsJsonObject("parent").getAsJsonPrimitive("prop").getAsString());
        }
    }
}