package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeToken;
import comcom.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MapTypeAdapterFactoryTest {

    // Mocking ConstructorConstructor and ObjectConstructor is hard without libraries.
    // Since we are in the same package, we can try to use a simple implementation.
    private static class SimpleConstructorConstructor implements com.google.gson.internal.ConstructorConstructor {
        @Override
        @SuppressWarnings("unchecked")
        public <T> com.google.gson.internal.ObjectConstructor<T> get(TypeToken<T> type, boolean unsafe) {
            return (typeToken) -> new HashMap<>();
        }
    }

    @Test
    void factory_returnsNullForNonMapType() {
        Gson gson = new Gson();
        MapTypeAdapterFactory factory = new MapTypeAdapterFactory(new SimpleConstructorConstructor(), false);
        assertNull(factory.create(gson, TypeToken.get(String.class)));
    }

    @Test
    void factory_returnsAdapterForMapType() {
        Gson gson = new Gson();
        MapTypeAdapterFactory factory = new MapTypeAdapterFactory(new SimpleConstructorConstructor(), false);
        TypeAdapter<Map<String, String>> adapter = factory.create(gson, TypeToken.get(new TypeToken<Map<String, String>>() {}));
        assertNotNull(adapter);
    }

    @Test
    void adapter_read_handlesNull() throws IOException {
        Gson gson = new Gson();
        MapTypeAdapterFactory factory = new MapType0AdapterFactory(new SimpleConstructorConstructor(), false);
        TypeAdapter<Map<String, String>> adapter = factory.create(gson, TypeToken.get(new TypeToken<Map<String, String>>() {}));
        JsonReader reader = new JsonReader(new StringReader("null"));
        assertNull(adapter.read(reader));
    }

    @Test
    void adapter_read_handlesObjectFormat() throws IOException {
        Gson gson = new Gson();
        MapTypeAdapterFactory factory = new MapTypeAdapterFactory(new SimpleConstructorConstructor(), false);
        TypeAdapter<Map<String, String>> adapter = factory.create(gson, TypeToken.get(new TypeToken<Map<String, String>>() {}));
        JsonReader reader = new JsonReader(new StringReader("{\"key1\":\"value1\",\"key2\":\"value2\"}"));
        Map<String, String> result = adapter.read(reader);
        assertEquals("value1", result.get("key1"));
        assertEquals("value2", result.get("key2"));
    }

    @Test
    void adapter_read_handlesArrayFormat() throws IOException {
        Gson gson = new Gson();
        MapTypeAdapterFactory factory = new MapTypeAdapterFactory(new SimpleConstructorConstructor(), true);
        TypeAdapter<Map<String, String>> adapter = factory.create(gson, TypeToken.get(new TypeToken<Map<String, String>>() {}));
        JsonReader reader = new JsonReader(new StringReader("[[\"key1\",\"value1\"],[\"key2\",\"value2\"]]"));
        Map<String, String> result = adapter.read(reader);
        assertEquals("value1", result.get("key1"));
        assertEquals("value2", result.get("key2"));
    }

    @Test
    void adapter_read_throwsOnDuplicateKey() throws IOException {
        Gson gson = new Gson();
        MapTypeAdapterFactory factory = new MapTypeAdapterFactory(new SimpleConstructorConstructor(), false);
        TypeAdapter<Map<String, String>> adapter = factory.create(gson, TypeToken.get(new TypeToken<Map<String, String>>() {}));
        JsonReader reader = new JsonReader(new StringReader("{\"key1\":\"value1\",\"key1\":\"value2\"}"));
        assertThrows(JsonSyntaxException.class, () -> adapter.read(reader));
    }

    @Test
    void adapter_write_handlesNull() throws IOException {
        Gson gson = new Gson();
        MapTypeAdapterFactory factory = new MapTypeAdapterFactory(new SimpleConstructorConstructor(), false);
        TypeAdapter<Map<String, String>> adapter = factory.create(gson, TypeToken.get(new TypeToken<Map<String, String>>() {}));
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        adapter.write(jsonWriter, null);
        assertEquals("null", writer.toString());
    }

    @Test
    void adapter_write_handlesSimpleKeys() throws IOException {
        Gson gson = new Gson();
        MapTypeAdapterFactory factory = new MapTypeAdapterFactory(new SimpleConstructorConstructor(), false);
        TypeAdapter<Map<String, String>> adapter = factory.create(gson, TypeToken.get(new TypeToken<Map<String, String>>() {}));
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        adapter.write(jsonWriter, map);
        assertEquals("{\"key1\":\"value1\"}", writer.toString());
    }
}
