package com.google.gson.internal.bind;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

class JsonElementTypeAdapterTest {

    @Test
    @DisplayName("Serialization and deserialization of JsonPrimitive (String)")
    void primitiveStringTest() throws Exception {
        JsonElementTypeAdapter adapter = JsonElementTypeAdapter.ADAPTER;
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        
        JsonPrimitive primitive = new JsonPrimitive("test");
        adapter.write(writer, primitive);
        assertEquals("\"test\"", sw.toString());

        JsonReader reader = new JsonReader(new StringReader("\"test\""));
        JsonElement result = adapter.read(reader);
        assertEquals(primitive, result);
    }

    @Test
    @DisplayName("Serialization and deserialization of JsonArray")
    void arrayTest() throws Exception {
        JsonElementTypeAdapter adapter = JsonElementTypeAdapter.ADAPTER;
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        
        JsonArray array = new JsonArray();
        array.add("one");
        array.add(2);
        
        adapter.write(writer, array);
        assertEquals("[\"one\",2]", sw.toString());

        JsonReader reader = new JsonReader(new StringReader("[\"one\",2]"));
        JsonElement result = adapter.read(reader);
        assertEquals(array, result);
    }

    @Test
    @DisplayName("Serialization and deserialization of JsonObject")
    void objectTest() throws Exception {
        JsonElementTypeAdapter adapter = JsonElementTypeAdapter.ADAPTER;
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        
        JsonObject object = new JsonObject();
        object.addProperty("key", "value");
        
        adapter.write(writer, object);
        assertEquals("{\"key\":\"value\"}", sw.toString());

        JsonReader reader = new JsonReader(new StringReader("{\"key\":\"value\"}"));
        JsonElement result = adapter.read(reader);
        assertEquals(object, result);
    }

    @Test
    @DisplayName("Serialization and deserialization of JsonNull")
    void nullTest() throws Exception {
        JsonElementTypeAdapter adapter = JsonElementTypeAdapter.ADAPTER;
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        
        adapter.write(writer, JsonNull.INSTANCE);
        assertEquals("null", sw.toString());

        JsonReader reader = new JsonReader(new StringReader("null"));
        JsonElement result = adapter.read(reader);
        assertTrue(result.isJsonNull());
    }
}
