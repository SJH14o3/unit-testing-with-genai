package com.google.gson;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.StringReader;
import com.google.gson.stream.JsonReader;

@DisplayName("JsonParser Tests")
class JsonParserTest {

    @Test
    @DisplayName("parseString works for valid JSON")
    void parseString_works() {
        String json = "{\"key\": \"value\"}";
        JsonElement element = JsonParser.parseString(json);
        assertTrue(element.isJsonObject());
        assertEquals("value", element.getAsJsonObject().get("key").getAsString());
    }

    @Test
    @DisplayName("parseString throws exception for invalid JSON")
    void parseString_throwsException() {
        String json = "{\"key\": \"value\""; // missing closing brace
        assertThrows(JsonSyntaxException.class, () -> JsonParser.parseString(json));
    }

    @Test
    @DisplayName("parseReader works for valid JSON")
    void parseReader_works() {
        String json = "[1, 2, 3]";
        JsonReader reader = new JsonReader(new StringReader(json));
        JsonElement element = JsonParser.parseReader(reader);
        assertTrue(element.isJsonArray());
        assertEquals(3, element.getAsJsonArray().size());
    }

    @Test
    @DisplayName("parseReader throws exception for invalid JSON")
    void parseReader_throwsException() {
        String json = "[1, 2, "; // incomplete array
        JsonReader reader = new JsonReader(new StringReader(json));
        assertThrows(JsonSyntaxException.class, () -> JsonParser.parseReader(reader));
    }

    @Test
    @DisplayName("parseReader throws exception for multiple top-level elements")
    void parseReader_multipleElements_throwsException() {
        String json = "{\"a\":1 {\"b\":2}";
        JsonReader reader = new JsonReader(new StringReader(json));
        assertThrows(JsonSyntaxException.class, () -> JsonParser.parseReader(reader));
    }

    @Test
    @DisplayName("parse(String) follows parseString")
    void parse_string_followsParseString() {
        JsonParser parser = new JsonParser();
        String json = "\"test\"";
        JsonElement element = parser.parse(json);
        assertTrue(element.isJsonPrimitive());
        assertEquals("test", element.getAsString());
    }

    @Test
    @DisplayName("parse(Reader) follows parseReader")
    void parse_reader_followsParseReader() {
        JsonParser parser = new JsonParser();
        String json = "true";
        JsonReader reader = new JsonReader(new StringReader(json));
        JsonElement element = parser.parse(reader);
        assertTrue(element.isJsonPrimitive());
        assertTrue(element.getAsBoolean());
    }
}
