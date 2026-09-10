package com.google.gson.internal.bind;

import com.google.gson.*;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MapTypeAdapterFactoryTest {

    private ConstructorConstructor constructorConstructor;
    private TypeToken<Map<String, String>> tt;

    @BeforeEach
    void setUp() {
        Map<Type, InstanceCreator<?>> creators = new HashMap<>();
        Type type = new TypeToken<String>(){}.getType();
        creators.put(type, (t) -> "");
        constructorConstructor = new ConstructorConstructor(creators, false, Collections.emptyList());

        tt = (TypeToken<Map<String, String>>) TypeToken.getParameterized(Map.class, String.class, String.class);
    }

    @Test
    void factory_returnsNullForNonMapType() {
        Gson gson = new Gson();
        MapTypeAdapterFactory factory = new MapTypeAdapterFactory(constructorConstructor, false);
        assertNull(factory.create(gson, TypeToken.get(String.class)));
    }

    @Test
    void factory_returnsAdapterForMapType() {
        Gson gson = new Gson();
        MapTypeAdapterFactory factory = new MapTypeAdapterFactory(constructorConstructor, false);
        TypeAdapter<Map<String, String>> adapter = factory.create(gson, tt);
        assertNotNull(adapter);
    }

    @Test
    void adapter_read_handlesNull() throws IOException {
        Gson gson = new Gson();
        MapTypeAdapterFactory factory = new MapTypeAdapterFactory(constructorConstructor, false);
        TypeAdapter<Map<String, String>> adapter = factory.create(gson, tt);
        JsonReader reader = new JsonReader(new StringReader("null"));
        assertNull(adapter.read(reader));
    }

    @Test
    void adapter_read_handlesObjectFormat() throws IOException {
        Gson gson = new Gson();
        MapTypeAdapterFactory factory = new MapTypeAdapterFactory(constructorConstructor, false);
        TypeAdapter<Map<String, String>> adapter = factory.create(gson, tt);
        JsonReader reader = new JsonReader(new StringReader("{\"key1\":\"value1\",\"key2\":\"value2\"}"));
        Map<String, String> result = adapter.read(reader);
        assertEquals("value1", result.get("key1"));
        assertEquals("value2", result.get("key2"));
    }

    @Test
    void adapter_read_handlesArrayFormat() throws IOException {
        Gson gson = new Gson();
        MapTypeAdapterFactory factory = new MapTypeAdapterFactory(constructorConstructor, true);
        TypeAdapter<Map<String, String>> adapter = factory.create(gson, tt);
        JsonReader reader = new JsonReader(new StringReader("[[\"key1\",\"value1\"],[\"key2\",\"value2\"]]"));
        Map<String, String> result = adapter.read(reader);
        assertEquals("value1", result.get("key1"));
        assertEquals("value2", result.get("key2"));
    }

    @Test
    void adapter_read_throwsOnDuplicateKey() throws IOException {
        Gson gson = new Gson();
        MapTypeAdapterFactory factory = new MapTypeAdapterFactory(constructorConstructor, false);
        TypeAdapter<Map<String, String>> adapter = factory.create(gson, tt);
        JsonReader reader = new JsonReader(new StringReader("{\"key1\":\"value1\",\"key1\":\"value2\"}"));
        assertThrows(JsonSyntaxException.class, () -> adapter.read(reader));
    }

    @Test
    void adapter_write_handlesNull() throws IOException {
        Gson gson = new Gson();
        MapTypeAdapterFactory factory = new MapTypeAdapterFactory(constructorConstructor, false);
        TypeAdapter<Map<String, String>> adapter = factory.create(gson, tt);
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        adapter.write(jsonWriter, null);
        assertEquals("null", writer.toString());
    }

    @Test
    void adapter_write_handlesSimpleKeys() throws IOException {
        Gson gson = new Gson();
        MapTypeAdapterFactory factory = new MapTypeAdapterFactory(constructorConstructor, false);
        TypeAdapter<Map<String, String>> adapter = factory.create(gson, tt);
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        adapter.write(jsonWriter, map);
        assertEquals("{\"key1\":\"value1\"}", writer.toString());
    }
}
