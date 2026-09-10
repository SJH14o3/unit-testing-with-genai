package com.google.gson.internal.bind;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class JsonTreeWriterTest {

    @Test
    @DisplayName("builds a simple array element")
    void buildArray() throws IOException {
        JsonTreeWriter w = new JsonTreeWriter();
        w.beginArray();
        w.value(1);
        w.value(2);
        w.endArray();
        w.close();
        JsonElement e = w.get();
        assertEquals("[1,2]", e.toString());
    }

    @Test
    @DisplayName("handles name/value pairs correctly")
    void objectWithNames() throws IOException {
        JsonTreeWriter w = new JsonTreeWriter();
        w.beginObject();
        w.name("foo");
        w.value("bar");
        w.endObject();
        w.close();
        assertEquals("{\"foo\":\"bar\"}", w.get().toString());
    }

    @Test
    @DisplayName("throws if endArray is called in wrong context")
    void illegalEndArray() {
        JsonTreeWriter w = new JsonTreeWriter();
        assertThrows(IllegalStateException.class, w::endArray);
    }

    @Test
    @DisplayName("supports nested structures")
    void nestedStructure() throws IOException {
        JsonTreeWriter w = new JsonTreeWriter();
        w.beginObject();
        w.name("arr");
        w.beginArray();
        w.value(1);
        w.value(2);
        w.endArray();
        w.endObject();
        w.close();
        assertEquals("{\"arr\":[1,2]}", w.get().toString());
    }
}