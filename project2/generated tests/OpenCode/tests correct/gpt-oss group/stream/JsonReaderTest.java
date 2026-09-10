package com.google.gson.stream;

import static org.junit.jupiter.api.Assertions.*;

import java.io.StringReader;
import java.io.IOException;

import com.google.gson.Strictness;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class JsonReaderTest {

    private JsonReader createReader(String json) {
        return new JsonReader(new StringReader(json));
    }

    @Nested
    @DisplayName("primitive values")
    class PrimitiveValues {

        @Test
        @DisplayName("parses numbers as long and double")
        void numberParsing() throws IOException {
            JsonReader r = createReader("12345");
            assertEquals(JsonToken.NUMBER, r.peek());
            assertEquals(12345L, r.nextLong());
        }

        @Test
        @DisplayName("parses booleans case‑insensitively in lenient mode")
        void booleansInLenient() throws IOException {
            JsonReader r = new JsonReader(new StringReader("True"));
            r.setStrictness(Strictness.LENIENT);
            assertTrue(r.nextBoolean());
        }
    }

    @Test
    @DisplayName("rejects NaN/Infinity when not lenient")
    void strictnessRejectsSpecialNumbers() {
        JsonReader r = createReader("NaN");
        r.setStrictness(Strictness.LEGACY_STRICT);
        assertThrows(IOException.class, r::nextDouble);
    }

    @Test
    @DisplayName("handles nested arrays and objects correctly")
    void nestedStructure() throws IOException {
        String json = "{\"a\":{\"b\":[1,2]}}";
        JsonReader r = createReader(json);
        r.beginObject();
        assertEquals("a", r.nextName());
        r.beginObject();
        assertEquals("b", r.nextName());
        r.beginArray();
        assertEquals(1, r.nextInt());
        assertEquals(2, r.nextInt());
        r.endArray();
        r.endObject();
        r.endObject();
        // after the root object has ended, peek() should yield END_DOCUMENT
        assertEquals(JsonToken.END_DOCUMENT, r.peek());
    }

    @Test
    @DisplayName("throws on invalid JSON when strict")
    void invalidJson() throws IOException {
        JsonReader r = new JsonReader(new StringReader("{a:1}")); // unquoted name
        r.setStrictness(Strictness.STRICT);
        r.beginObject();
        assertThrows(IOException.class, r::nextName);
    }
}