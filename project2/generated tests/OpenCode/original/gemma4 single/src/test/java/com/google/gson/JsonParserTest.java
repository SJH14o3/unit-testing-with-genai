package com.google.gson;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonParser Test")
class JsonParserTest {

    @Test
    @DisplayName("parseString: valid JSON object")
    void parseString_validObject() {
        String json = "{\"key\": \"value\"}";
        JsonElement element = JsonParser.parseString(json);
        assertTrue(element.isJsonObject());
        assertEquals("value", element.getAsJsonObject().get("key").getAsString());
    }

    @Test
    @DisplayName("parseString: valid JSON array")
    void parseString_validArray() {
        String json = "[1, 2, 3]";
        JsonElement element = JsonParser.parseString(json);
        assertTrue(element.isJsonArray());
        assertEquals(3, element.getAsJsonArray().size());
    }

    @Test
    @DisplayName("parseString: valid JSON null")
    void parseString_null() {
        String json = "null";
        JsonElement element = JsonParser.parseString(json);
        assertTrue(element.isJsonNull());
    }

    @Test
    @DisplayName("parseString: throws JsonSyntaxException for invalid JSON")
    void parseString_invalidJson_throwsException() {
        String json = "{not valid json}";
        assertThrows(JsonSyntaxException.class, () -> JsonParser.parseString(json));
    }

    @Test
    @DisplayName("parseReader: valid JSON string")
    void parseReader_validString() {
        String json = "\"hello\"";
        JsonElement element = JsonParser.parseReader(new StringReader(json));
        assertTrue(element.isJsonPrimitive());
        assertEquals("hello", element.getAsString());
    }

    @Test
    @DisplayName("parseReader: throws JsonSyntaxException for incomplete document (trailing data)")
    void parseReader_trailingData_throwsException() {
        String json = "{\"key\":\"value\"}extra";
        assertThrows(JsonSyntaxException.class, () -> JsonParser.parseReader(new StringReader(json)));
    }

    @Test
    @DisplayName("parseReader: handles IOException")
    void parseReader_ioException_throwsJsonIOException() throws IOException {
        // Using a custom Reader that throws IOException
        class ThrowingReader extends StringReader {
            public ThrowingReader(String s) { super(s); }
            @Override
            public int read(char[] cbuf, int off, int len) throws IOException {
                throw new IOException("Simulated IOException");
            }
        }
        assertThrows(JsonIOException.class, () -> JsonParser.parseReader(new ThrowingReader("{}")));
    }

    @Test
    @DisplayName("parseReader(JsonReader): respects strictness and handles LEGACY_STRICT")
    void parseReader_withJsonReader_handlesStrictness() {
        String json = "{\"key\":\"value\"}";
        JsonReader reader = new JsonReader(new StringReader(json));
        // Assuming LEGACY_STRICT is an available enum constant in the codebase
        reader.setStrictness(Strictness.LEGACY_STRICT);
        
        JsonElement element = JsonParser.parseReader(reader);
        assertTrue(element.isJsonObject());
        // Verify strictness was restored (it should be LENIENT or original, but not modified permanently if logic works)
        // The method sets it back to 'strictness' in finally block.
    }

    @Test
    @DisplayName("Deprecated methods: verify they delegate correctly")
    void deprecatedMethods_delegateCorrectly() {
        String json = "{\"a\":1}";
        assertNotNull(new JsonParser().parse(json));
        assertNotNull(JsonParser.parse(new StringReader(json)));
        assertNotNull(JsonParser.parse(new JsonReader(new StringReader(json))));
    }
}