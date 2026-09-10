package com.google.gson.internal;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.IOException;

class StreamsTest {

    @Test
    @DisplayName("Parse empty JSON returns JsonNull")
    void parseEmptyJson() throws Exception {
        JsonReader reader = new JsonReader(new StringReader(""));
        JsonElement element = Streams.parse(reader);
        assertTrue(element == JsonNull.INSTANCE);
    }

    @Test
    @DisplayName("Parse valid JSON object")
    void parseValidJson() throws Exception {
        JsonReader reader = new JsonElementReader("{\"foo\":\"bar\"}");
        JsonElement element = Streams.parse(reader);
        assertNotNull(element);
        assertTrue(element.isJsonObject());
    }

    @Test
    @DisplayName("Parse invalid JSON throws JsonSyntaxException")
    void parseInvalidJson() {
        JsonReader reader = new JsonReader(new StringReader("{\"foo\":\"bar\"")); // missing closing brace
        assertThrows(JsonSyntaxException.class, () -> Streams.parse(reader));
    }

    @Test
    @DisplayName("Write JSON element to writer")
    void writeElement() throws IOException {
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        JsonElement element = JsonNull.INSTANCE; // Can't easily create JsonObject without Gson, but we use primitives
        // Since we are testing Streams.write, let's use a simple string as a JsonElement if possible, 
        // or manually build a JsonObject if we had access. 
        // Since we are in the internal package, we might be able to use other classes.
        // For now, testing the capability of the method to not throw.
        Streams.write(JsonNull.INSTANCE, writer);
        assertEquals("null", sw.toString());
    }

    // Helper to wrap reader for test
    private static class JsonElementReader extends JsonReader {
        JsonElementReader(String json) {
            super(new StringReader(json));
        }
        // We need to bypass the limitation of not having Gson dependency in a pure unit test
        // but here we are in the codebase, so we use the actual JsonElementTypeAdapter.
    }
}
