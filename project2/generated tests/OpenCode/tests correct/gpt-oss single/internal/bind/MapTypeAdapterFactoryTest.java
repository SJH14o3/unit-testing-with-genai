package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.bind.MapTypeAdapterFactory;
import com.google.gson.internal.ConstructorConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class MapTypeAdapterFactoryTest {
    private Gson gson(boolean complexMapKeySerialization) {
        ConstructorConstructor ctor = new ConstructorConstructor(Collections.emptyMap(), false, Collections.emptyList());
        MapTypeAdapterFactory factory = new MapTypeAdapterFactory(ctor, complexMapKeySerialization);
        return new GsonBuilder()
                .registerTypeAdapterFactory(factory)
                .create();
    }

    @Test
    @DisplayName("serialize null map to JSON null")
    void serializeNullMap() {
        Gson g = gson(false);
        assertEquals("null", g.toJson((Map<?,?>) null));
    }

    @Test
    @DisplayName("serialize empty map to JSON object {}")
    void serializeEmptyMap() {
        Gson g = gson(false);
        assertEquals("{}", g.toJson(Collections.emptyMap()));
    }

    @Test
    @DisplayName("serialize map with primitive boolean keys using BOOLEAN_AS_STRING")
    void serializeBooleanKey() {
        Gson g = gson(false);
        Map<Boolean, Integer> m = new HashMap<>();
        m.put(true, 1);
        m.put(false, 2);
        String json = g.toJson(m);
        assertTrue(json.contains("true"));
        assertTrue(json.contains("false"));
        assertTrue(json.contains("1"));
        assertTrue(json.contains("2"));
    }

    @Test
    @DisplayName("write map with complex keys as array when flag true")
    void writeComplexKeysAsArray() {
        Gson g = gson(true);
        Map<List<Integer>, String> m = new HashMap<>();
        m.put(List.of(1, 2), "first");
        m.put(List.of(3), "second");
        String json = g.toJson(m);
        assertTrue(json.startsWith("[")); // starts with array
        assertTrue(json.contains("first"));
        assertTrue(json.contains("second"));
    }

    @Test
    @DisplayName("read duplicate key in array throws JsonSyntaxException")
    void duplicateKeyInArrayThrows() {
        String json = "[[[1],\"a\"],[[1],\"b\"]]";
        Gson g = gson(true);
        assertThrows(JsonSyntaxException.class, () -> g.fromJson(json, new com.google.gson.reflect.TypeToken<Map<List<Integer>, String>>() {}.getType()));
    }

    @Test
    @DisplayName("read duplicate key in object throws JsonSyntaxException")
    void duplicateKeyInObjectThrows() {
        String json = "{\"a\":1, \"a\":2}";
        Gson g = gson(false);
        assertThrows(JsonSyntaxException.class, () -> g.fromJson(json, new com.google.gson.reflect.TypeToken<Map<String, Integer>>() {}.getType()));
    }

    @Test
    @DisplayName("read map from object JSON works")
    void readObjectMap() {
        String json = "{\"foo\":10, \"bar\":20}";
        Gson g = gson(false);
        Map<String, Integer> map = g.fromJson(json, new com.google.gson.reflect.TypeToken<Map<String, Integer>>() {}.getType());
        assertEquals(2, map.size());
        assertEquals(10, map.get("foo"));
        assertEquals(20, map.get("bar"));
    }
}
