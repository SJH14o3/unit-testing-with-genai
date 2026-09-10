package com.google.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.Strictness;
import com.google.gson.JsonIOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class JsonParserTest {

    @Test
    @DisplayName("parseString parses valid JSON string into JsonObject")
    void parseString_parsesValidJson() {
        JsonElement element = JsonParser.parseString("{\"a\":1}");
        assertTrue(element.isJsonObject());
        assertEquals(1, element.getAsJsonObject().get("a").getAsInt());
    }

    @Test
    @DisplayName("parseString throws JsonSyntaxException for malformed JSON")
    void parseString_invalidJson_throwsException() {
        assertThrows(JsonSyntaxException.class, () -> JsonParser.parseString("{\"a\":"));
    }

    @Test
    @DisplayName("parseReader parses valid Reader into JsonObject")
    void parseReader_parsesValidReader() throws JsonIOException, JsonSyntaxException {
        Reader reader = new StringReader("{\"b\":2}");
        JsonElement element = JsonParser.parseReader(reader);
        assertTrue(element.isJsonObject());
        assertEquals(2, element.getAsJsonObject().get("b").getAsInt());
    }

    @Test
    @DisplayName("parseReader throws JsonSyntaxException when data remains after first element")
    void parseReader_multipleTopLevelElements_throwsException() {
        Reader reader = new StringReader("{}{}");
        JsonSyntaxException ex = assertThrows(JsonSyntaxException.class, () -> JsonParser.parseReader(reader));
        assertEquals("Did not consume the entire document.", ex.getMessage());
    }

    @Test
    @DisplayName("parseReader throws JsonIOException when Reader throws IOException")
    void parseReader_readerThrowsIOException_throwsException() {
        Reader badReader = new Reader() {
            @Override public int read(char[] cbuf, int off, int len) throws IOException { throw new IOException("boom"); }
            @Override public void close() throws IOException {}
        };
        assertThrows(JsonIOException.class, () -> JsonParser.parseReader(badReader));
    }

    @Test
    @DisplayName("parseReader(JsonReader) restores original strictness after parsing")
    void parseReader_restoreStrictness() throws JsonIOException, JsonSyntaxException {
        JsonReader reader = new JsonReader(new StringReader("{\"c\":3}"));
        reader.setStrictness(Strictness.LEGACY_STRICT);
        Strictness before = reader.getStrictness();
        JsonElement element = JsonParser.parseReader(reader);
        Strictness after = reader.getStrictness();
        assertEquals(before, after);
        assertTrue(element.isJsonObject());
        assertEquals(3, element.getAsJsonObject().get("c").getAsInt());
    }

//    @Test
//    @DisplayName("parseReader(JsonReader) handles StackOverflowError and OutOfMemoryError by wrapping")
//    void parseReader_errorsWrapped() {
//        JsonReader reader = new JsonReader(new StringReader("{\"d\":4}")) {
//            @Override public JsonSyntaxException parse() {
//                throw new StackOverflowError("stack overflow");
//            }
//        };
//        // Since we cannot override Streams.parse easily, this is a conceptual test; in practice this scenario
//        // would be rare. The test ensures that such errors are wrapped in JsonParseException.
//        // As we cannot trigger it directly, we will skip this runtime check.
//    }
}
