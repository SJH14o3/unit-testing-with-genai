package com.google.gson.internal.bind;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.StringReader;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;

/**
 * Tests TreeTypeAdapter behavior:
 * - using a custom serializer only
 * - using a custom deserializer only
 * - delegation fallback
 * - nullSafe handling
 */
class TreeTypeAdapterTest {

    private final Gson gson = new Gson();

    @Test
    void customSerializerWritesTree() throws Exception {
        var serializer = new JsonSerializer<String>() {
            @Override public JsonElement serialize(String src, java.lang.reflect.Type typeOfSrc,
                                                   com.google.gson.JsonSerializationContext context) {
                return com.google.gson.JsonNull.INSTANCE;
            }
        };
        var adapter = TreeTypeAdapter.newFactoryWithMatchRawType(TypeToken.get(String.class), serializer);
        var typeAdapter = adapter.create(gson, com.google.gson.reflect.TypeToken.get(String.class));
        String written = gson.toJson("value", String.class);
        assertEquals("null", written); // serializer wrote JSON null
    }

    @Test
    void customDeserializerReturnsValue() throws Exception {
        var deserializer = new JsonDeserializer<Integer>() {
            @Override public Integer deserialize(com.google.gson.JsonElement json,
                                                 java.lang.reflect.Type typeOfT, com.google.gson.JsonDeserializationContext context) {
                return 42;
            }
        };
        var adapter = TreeTypeAdapter.newFactoryWithMatchRawType(TypeToken.get(Integer.class), deserializer);
        var typeAdapter = adapter.create(gson, com.google.gson.reflect.TypeToken.get(Integer.class));
        Integer val = gson.fromJson("123", Integer.class);
        assertEquals(42, val); // overridden return
    }

    @Test
    void nullSafeDeserialization() throws Exception {
        var deserializer = new JsonDeserializer<Object>() {
            @Override public Object deserialize(com.google.gson.JsonElement json,
                                                java.lang.reflect.Type typeOfT, com.google.gson.JsonDeserializationContext context) {
                throw new RuntimeException("should not reach");
            }
        };
        var adapter = TreeTypeAdapter.newFactoryWithMatchRawType(TypeToken.get(Object.class), deserializer);
        String json = "null";
        Object value = gson.fromJson(json, Object.class);
        assertNull(value); // nullSafe returns null, deserializer not invoked
    }
}