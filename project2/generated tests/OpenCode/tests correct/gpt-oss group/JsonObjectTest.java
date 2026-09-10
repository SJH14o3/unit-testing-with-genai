package com.google.gson;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonObject unit tests")
class JsonObjectTest {

    @Test
    void addAndRemoveWorks() {
        JsonObject obj = new JsonObject();
        obj.addProperty("k", 1);
        assertTrue(obj.has("k"));
        assertEquals(1, obj.get("k").getAsInt());

        JsonElement removed = obj.remove("k");
        assertNotNull(removed);
        assertFalse(obj.has("k"));
    }

    @Test
    void getAsJsonXThrowsOnWrongType() {
        JsonObject obj = new JsonObject();
        obj.add("int", new JsonPrimitive(5));
        assertThrows(ClassCastException.class, () -> obj.getAsJsonArray("int"));
        obj.add("arr", new JsonArray());
        assertThrows(ClassCastException.class, () -> obj.getAsJsonPrimitive("arr"));
    }

    @Test
    void asMapMutability() {
        JsonObject obj = new JsonObject();
        obj.add("x", new JsonPrimitive(1));
        java.util.Map<String, com.google.gson.JsonElement> map = obj.asMap();
        map.remove("x");
        assertFalse(obj.has("x"));
    }

    @Test
    void equalsAndHashCode() {
        JsonObject a = new JsonObject();
        a.add("k", new JsonPrimitive(1));
        JsonObject b = new JsonObject();
        b.add("k", new JsonPrimitive(1));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
