package com.google.gson;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.StringWriter;
import java.io.StringReader;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.Test;

/**
 * Tests the public API of {@link TypeAdapter} via a concrete {@code StringTypeAdapter}.
 */
class TypeAdapterTest {

    /** Simple concrete adapter that treats strings literally. */
    private static final class StringAdapter extends TypeAdapter<String> {
        @Override
        public void write(JsonWriter out, String value) throws IOException {
            out.value(value);
        }
        @Override
        public String read(JsonReader in) throws IOException {
            return in.nextString();
        }
    }

    @Test
    void toJsonWritesCorrectly() throws Exception {
        var adapter = new StringAdapter();
        var writer = new StringWriter();
        adapter.toJson(writer, "hello");
        assertEquals("\"hello\"", writer.toString());
    }

    @Test
    void fromJsonParsesValue() throws Exception {
        var adapter = new StringAdapter();
        var result = adapter.fromJson("\"world\"");
        assertEquals("world", result);
    }

    @Test
    void toJsonTreeWrapsNull() throws Exception {
        var adapter = new StringAdapter();
        var element = adapter.toJsonTree((String) null);
        assertTrue(element.isJsonNull());
    }

    @Test
    void nullSafeAdapterDelegates() throws Exception {
        var real = new StringAdapter();
        var wrapper = real.nullSafe();
        var json = wrapper.toJsonTree(null);
        assertTrue(json.isJsonNull());
    }
}