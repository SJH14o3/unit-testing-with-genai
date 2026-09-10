package com.google.gson;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

class TypeAdapterTest {

    private static class TestTypeAdapter extends TypeAdapter<String> {
        @Override
        public void write(JsonWriter out, String value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value);
            }
        }

        @Override
        public String read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return in.nextString();
        }
    }

    @Test
    void testToJsonWriter() throws IOException {
        TypeAdapter<String> adapter = new TestTypeAdapter();
        StringWriter writer = new StringWriter();
        adapter.toJson(writer, "hello");
        assertEquals("\"hello\"", writer.toString());
    }

    @Test
    void testToJsonString() {
        TypeAdapter<String> adapter = new TestTypeAdapter();
        assertEquals("\"hello\"", adapter.toJson("hello"));
    }

    @Test
    void testToJsonTree() {
        TypeAdapter<String> adapter = new TestTypeAdapter();
        JsonElement element = adapter.toJsonTree("hello");
        assertEquals("\"hello\"", element.toString());
    }

    @Test
    void testFromJsonReader() throws IOException {
        TypeAdapter<String> adapter = new TestTypeAdapter();
        JsonReader reader = new JsonReader(new StringReader("\"hello\""));
        assertEquals("hello", adapter.fromJson(reader));
    }

    @Test
    void testFromJsonString() throws IOException {
        TypeAdapter<String> adapter = new TestTypeAdapter();
        assertEquals("hello", adapter.fromJson("\"hello\""));
    }

    @Test
    void testFromJsonTree() {
        TypeAdapter<String> adapter = new TestTypeAdapter();
        JsonElement element = JsonParser.parseString("\"hello\"");
        assertEquals("hello", adapter.fromJsonTree(element));
    }

    @Test
    void testNullSafeNew() {
        TypeAdapter<String> adapter = new TestTypeAdapter();
        TypeAdapter<String> nullSafe = adapter.nullSafe();
        assertNotSame(adapter, nullSafe);
        assertTrue(nullSafe.getClass().getName().contains("NullSafeTypeAdapter"));
    }

    @Test
    void testNullSafeExisting() {
        TypeAdapter<String> adapter = new TestTypeAdapter().nullSafe();
        TypeAdapter<String> nullSafe = adapter.nullSafe();
        assertSame(adapter, nullSafe);
    }

    @Test
    void testNullSafeWriteNull() throws IOException {
        TypeAdapter<String> adapter = new TestTypeAdapter().nullSafe();
        StringWriter writer = new StringWriter();
        adapter.write(new JsonWriter(writer), null);
        assertEquals("null", writer.toString());
    }

    @Test
    void testNullSafeWriteNotNull() throws IOException {
        TypeAdapter<String> adapter = new TestTypeAdapter().nullSafe();
        StringWriter writer = new StringWriter();
        adapter.write(new JsonWriter(writer), "hello");
        assertEquals("\"hello\"", writer.toString());
    }

    @Test
    void testNullSafeReadNull() throws IOException {
        TypeAdapter<String> adapter = new TestTypeAdapter().nullSafe();
        JsonReader reader = new JsonReader(new StringReader("null"));
        assertNull(adapter.read(reader));
    }

    @Test
    void testNullSafeReadNotNull() throws IOException {
        TypeAdapter<String> adapter = new TestTypeAdapter().nullSafe();
        JsonReader reader = new JsonReader(new StringReader("\"hello\""));
        assertEquals("hello", adapter.read(reader));
    }

    @Test
    void testNullSafeToString() {
        TypeAdapter<String> adapter = new TestTypeAdapter();
        TypeAdapter<String> nullSafe = adapter.nullSafe();
        assertTrue(nullSafe.toString().contains("NullSafeTypeAdapter"));
        assertTrue(nullSafe.toString().contains(adapter.toString()));
    }
}