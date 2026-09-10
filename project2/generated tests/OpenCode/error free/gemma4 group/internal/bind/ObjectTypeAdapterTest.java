package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.ToNumberPolicy;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ObjectTypeAdapterTest {

    private final Gson gson = new Gson();

    @Test
    void factory_returnsAdapterForObjectClass() {
        TypeAdapterFactory factory = ObjectTypeAdapter.getFactory(ToNumberPolicy.DOUBLE);
        TypeAdapter<Object> adapter = factory.create(gson, TypeToken.get(Object.class));
        assertNotNull(adapter);
    }

    @Test
    void factory_returnsNullForOtherClass() {
        TypeAdapterFactory factory = ObjectTypeAdapter.getFactory(ToNumberPolicy.DOUBLE);
        TypeAdapter<String> adapter = factory.create(gson, TypeToken.get(String.class));
        assertNull(adapter);
    }

    @Test
    void read_handlesString() throws IOException {
        TypeAdapter<Object> adapter = ObjectTypeAdapter.getFactory(ToNumberPolicy.DOUBLE).create(gson, TypeToken.get(Object.class));
        JsonReader reader = new JsonReader(new StringReader("\"test\""));
        assertEquals("test", adapter.read(reader));
    }

    @Test
    void read_handlesNumber() throws IOException {
        TypeAdapter<Object> adapter = ObjectTypeAdapter.getFactory(ToNumberPolicy.DOUBLE).create(gson, TypeToken.get(Object.class));
        JsonReader reader = new JsonReader(new StringReader("123.45"));
        assertEquals(123.45, (Double) adapter.read(reader), 0.001);
    }

    @Test
    void read_handlesBoolean() throws IOException {
        TypeAdapter<Object> adapter = ObjectTypeAdapter.getFactory(ToNumberPolicy.DOUBLE).create(gson, TypeToken.get(Object.class));
        JsonReader reader = new JsonReader(new StringReader("true"));
        assertEquals(true, adapter.read(reader));
    }

    @Test
    void read_handlesNull() throws IOException {
        TypeAdapter<Object> adapter = ObjectTypeAdapter.getFactory(ToNumberPolicy.DOUBLE).create(gson, TypeToken.get(Object.class));
        JsonReader reader = new JsonReader(new StringReader("null"));
        assertNull(adapter.read(reader));
    }

    @Test
    void read_handlesArray() throws IOException {
        TypeAdapter<Object> adapter = ObjectTypeAdapter.getFactory(ToNumberPolicy.DOUBLE).create(gson, TypeToken.get(Object.class));
        JsonReader reader = new JsonReader(new StringReader("[\"a\", \"b\"]"));
        Object result = adapter.read(reader);
        assertTrue(result instanceof List);
        assertEquals(List.of("a", "b"), result);
    }

    @Test
    void read_handlesObject() throws IOException {
        TypeAdapter<Object> adapter = ObjectTypeAdapter.getFactory(ToNumberPolicy.DOUBLE).create(gson, TypeToken.get(Object.class));
        JsonReader reader = new JsonReader(new StringReader("{\"key\":\"value\"}"));
        Object result = adapter.read(reader);
        assertTrue(result instanceof Map);
        assertEquals("value", ((Map<?, ?>) result).get("key"));
    }
}
