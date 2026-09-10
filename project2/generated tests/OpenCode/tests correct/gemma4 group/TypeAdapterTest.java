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

    private final TypeAdapter<String> adapter = new TestTypeAdapter();

    @Test
    void toJsonWithWriter() throws IOException {
        StringWriter writer = new StringWriter();
        adapter.toJson(writer, "hello");
        assertEquals("\"hello\"", writer.toString());
    }

    @Test
    void toJsonWithWriterNull() throws IOException {
        StringWriter writer = new StringWriter();
        adapter.toJson(writer, null);
        assertEquals("null", writer.toString());
    }

    @Test
    void toJsonString() {
        assertEquals("\"hello\"", adapter.toJson("hello"));
    }

    @Test
    void toJsonStringNull() {
        assertEquals("null", adapter.toJson((String) null));
    }

    @Test
    void toJsonTree() {
        JsonElement tree = adapter.toJsonTree("hello");
        assertTrue(tree.isJsonPrimitive());
        assertEquals("hello", tree.getAsString());
    }

    @Test
    void toJsonTreeNull() {
        JsonElement tree = adapter.toJsonTree((String) null);
        assertTrue(tree.isJsonNull());
    }

    @Test
    void fromJsonReader() throws IOException {
        String json = "\"hello\"";
        StringReader reader = new StringReader(json);
        assertEquals("hello", adapter.fromJson(reader));
    }

    @Test
    void fromJsonReaderNull() throws IOException {
        String json = "null";
        StringReader reader = new StringReader(json);
        assertNull(adapter.fromJson(reader));
    }

    @Test
    void fromJsonString() throws IOException {
        assertEquals("hello", adapter.fromJson("\"hello\""));
    }

    @Test
    void fromJsonStringNull() throws IOException {
        assertNull(adapter.fromJson("null"));
    }

    @Test
    void fromJsonTree() {
        JsonElement tree = JsonParser.parseString("\"hello\"");
        assertEquals("hello", adapter.fromJsonTree(tree));
    }

    @Test
    void fromJsonTreeNull() {
        assertNull(adapter.fromJsonTree(JsonNull.INSTANCE));
    }

    @Test
    void nullSafe() {
        TypeAdapter<String> nullSafeAdapter = adapter.nullSafe();
        assertNotSame(adapter, nullSafeAdapter);

        // Testing NullSafeTypeAdapter.write
        StringWriter writer = new StringWriter();
        try {
            nullSafeAdapter.write(new JsonWriter(writer), null);
            assertEquals("null", writer.toString());
        } catch (IOException e) {
            fail(e);
        }

        // Testing NullSafeTypeAdapter.read
        String json = "null";
        JsonReader reader = new JsonReader(new StringReader(json));
        try {
            assertNull(nullSafeAdapter.read(reader));
        } catch (IOException e) {
            fail(e);
        }
    }
}
