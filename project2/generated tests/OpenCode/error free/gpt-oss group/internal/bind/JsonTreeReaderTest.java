package com.google.gson.internal.bind;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class JsonTreeReaderTest {

    private JsonTreeReader createReader(JsonElement element) {
        return new JsonTreeReader(element);
    }

    @Test
    @DisplayName("reads a simple element tree")
    void readSimpleStructure() throws IOException {
        JsonElement e = JsonParser.parseString("[1, {\"x\": 2}]");
        JsonTreeReader r = createReader(e);
        r.beginArray();

        assertEquals(1, r.nextInt());
        r.beginObject();
        assertEquals("x", r.nextName());
        assertEquals(2, r.nextInt());
        r.endObject();
        r.endArray();
        assertEquals(JsonToken.END_DOCUMENT, r.peek());
    }

    @Test
    @DisplayName("promotes name to a value when requested")
    void promoteName() throws IOException {
        JsonElement e = JsonParser.parseString("{\"foo\":\"bar\"}");
        JsonTreeReader r = createReader(e);
        r.beginObject();
        String key = r.nextName();
        assertEquals("foo", key);
        r.promoteNameToValue();
        assertEquals("foo", r.nextString());
        assertEquals("bar", r.nextString());
        r.endObject();
    }

    @Test
    @DisplayName("throws on unsupported JsonElement subtype")
    void unsupportedElement() {
        // Create a custom subclass
        JsonElement custom = new NonSupportedJson(); // anonymous subclass
        JsonTreeReader r = createReader(custom);
        assertThrows(MalformedJsonException.class, r::nextInt);
    }

    private class NonSupportedJson extends JsonElement {

        @Override
        public JsonElement deepCopy() {
            return null;
        }
    }
}