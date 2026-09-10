package com.google.gson;

import com.google.gson.stream.MalformedJsonException;
import com.google.gson.stream.JsonReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonStreamParser Test")
class JsonStreamParserTest {

    @Test
    @DisplayName("next() returns JsonElement when elements are present")
    void nextReturnsElementWhenPresent() {
        String json = "{\"a\":1} [1,2,3] \"string\"";
        JsonStreamParser parser = new JsonStreamParser(json);

        assertTrue(parser.hasNext());
        assertEquals(1, parser.next().getAsJsonObject().get("a").getAsInt());
        assertTrue(parser.hasNext());
        assertEquals(3, parser.next().getAsJsonArray().size());
        assertTrue(parser.hasNext());
        assertEquals("string", parser.next().getAsString());
        assertFalse(parser.hasNext());
    }

    @Test
    @DisplayName("next() throws NoSuchElementException when no elements are available")
    void nextThrowsExceptionWhenEmpty() {
        JsonStreamParser parser = new JsonStreamParser("");
        assertFalse(parser.hasNext());
        assertThrows(NoSuchElementException.class, parser::next);
    }

    @Test
    @DisplayName("hasNext() returns false for empty input")
    void hasNextReturnsFalseForEmptyInput() {
        JsonStreamParser parser = new JsonStreamParser("");
        assertFalse(parser.hasNext());
    }

    @Test
    @DisplayName("remove() throws UnsupportedOperationException")
    void removeThrowsException() {
        JsonStreamParser parser = new JsonStreamParser("{}");
        assertThrows(UnsupportedOperationException.class, parser::remove);
    }

    @Test
    @DisplayName("hasNext() throws JsonSyntaxException when JSON is malformed")
    void hasNextThrowsJsonSyntaxExceptionOnMalformedJson() throws IOException {
        // We use a custom reader to force an IOException or MalformedJsonException during peek() if possible,
        // but since we can't mock, we use a malformed string that triggers MalformedJsonException.
        // Note: In JsonReader, certain malformed inputs trigger MalformedJsonException on peek().
        String malformedJson = "{ invalid }";
        JsonStreamParser parser = new JsonStreamParser(malformedJson);

        // The implementation of hasNext() catches MalformedJsonException and throws JsonSyntaxException.
        assertThrows(JsonSyntaxException.class, parser::hasNext);
    }

    /**
     * Helper class to facilitate testing of IOException in hasNext.
     * Since we cannot use mocks, we use a subclass or a specific input.
     * However, for decision coverage of the catch block (IOException),
     * we'd need a Reader that throws IOException.
     */
//    private static class JsonKeyStreamParser extends JsonStreamParser {
//        public JsonKeyStreamParser(String json) {
//            super(json);
//        }
//    }

    // To cover IOException, we need a custom Reader.
    @Test
    @DisplayName("hasNext() throws JsonIOException when IOException occurs")
    void hasNextThrowsJsonIOExceptionOnIOException() {
        class ThrowingReader extends StringReader {
            public ThrowingReader(String s) { super(s); }
            @Override
            public int read(char[] cbuf, int off, int len) throws IOException {
                throw new IOException("Simulated IOException");
            }
        }
        JsonStreamParser parser = new JsonStreamParser(new ThrowingReader("{\"a\":1}"));
        assertThrows(JsonIOException.class, parser::hasNext);
    }
}
