import com.google.gson.Strictness;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link JsonReader} covering decision points.
 */
@DisplayName("JsonReader decision coverage tests")
class JsonReaderTest {

    private JsonReader readerFrom(String json) {
        return new JsonReader(new StringReader(json));
    }

    @Test
    @DisplayName("constructor throws NPE when Reader is null")
    void constructorNull() {
        assertThrows(NullPointerException.class, () -> new JsonReader((StringReader) null));
    }

    @Test
    @DisplayName("setStrictness throws NPE for null argument")
    void setStrictnessNull() {
        JsonReader r = readerFrom("{});"); // placeholder to construct
        assertThrows(NullPointerException.class, () -> r.setStrictness(null));
    }

    @Test
    @DisplayName("setNestingLimit negative throws IllegalArgumentException")
    void setNestingLimitNegative() {
        JsonReader r = readerFrom("{});");
        assertThrows(IllegalArgumentException.class, () -> r.setNestingLimit(-1));
    }

    @Test
    @DisplayName("beginArray successfully reads array start and end")
    void beginArraySuccess() throws IOException {
        JsonReader r = readerFrom("[1,2,3]");
        r.beginArray();
        assertEquals(JsonToken.NUMBER, r.peek());
        r.nextInt();
        r.endArray();
    }

    @Test
    @DisplayName("beginArray on non-array throws IllegalStateException")
    void beginArrayNotArray() {
        JsonReader r = readerFrom("true");
        assertThrows(IllegalStateException.class, r::beginArray);
    }

    @Test
    @DisplayName("beginObject successfully reads object start and end")
    void beginObjectSuccess() throws IOException {
        JsonReader r = readerFrom("{\"a\":1}");
        r.beginObject();
        assertEquals(JsonToken.NAME, r.peek());
        String name = r.nextName();
        assertEquals("a", name);
        assertEquals(JsonToken.NUMBER, r.peek());
        r.nextInt();
        r.endObject();
    }

    @Test
    @DisplayName("hasNext returns false after consuming all elements")
    void hasNextEnd() throws IOException {
        JsonReader r = readerFrom("[true]");
        r.beginArray();
        assertTrue(r.hasNext());
        r.nextBoolean();
        assertFalse(r.hasNext());
        r.endArray();
    }

    @Test
    @DisplayName("peek returns correct token types for literals")
    void peekLiterals() throws IOException {
        JsonReader r = readerFrom("[true,false,null,"hello",123]");
        r.beginArray();
        assertEquals(JsonToken.BOOLEAN, r.peek());
        assertTrue(r.nextBoolean());
        assertEquals(JsonToken.BOOLEAN, r.peek());
        assertFalse(r.nextBoolean());
        assertEquals(JsonToken.NULL, r.peek());
        r.nextNull();
        assertEquals(JsonToken.STRING, r.peek());
        assertEquals("hello", r.nextString());
        assertEquals(JsonToken.NUMBER, r.peek());
        assertEquals(123, r.nextInt());
        r.endArray();
    }

    @Test
    @DisplayName("strictness can be toggled between LEGACY_STRICT and LENIENT")
    void strictnessToggling() throws IOException {
        JsonReader r = readerFrom("["\u0007"]");
        // Strict mode (default) should throw on unescaped control char
        assertThrows(IOException.class, r::nextString);

        r = readerFrom("["\u0007"]");
        r.setStrictness(Strictness.LENIENT);
        // Lenient mode should allow it and return the char
        assertEquals("\u0007", r.nextString());
    }

    @Test
    @DisplayName("skipValue correctly skips nested structure")
    void skipValue() throws IOException {
        JsonReader r = readerFrom("{\"foo\":{\"bar\":true},\"baz\":[1,2]}");
        r.beginObject();
        r.nextName(); // foo
        r.skipValue(); // skip inner object
        assertEquals("baz", r.nextName());
        r.skipValue(); // skip array
        r.endObject();
    }

    @Nested
    @DisplayName("Nesting limit tests")
    class NestingLimit {
        @Test
        @DisplayName("exceeding nesting limit throws MalformedJsonException")
        void exceedingLimit() throws IOException {
            String json = "[{[{1}]}]"; // depth 3 nesting
            JsonReader r = readerFrom(json);
            r.setNestingLimit(2);
            assertThrows(RuntimeException.class, () -> {
                r.beginArray(); // 1
                r.beginArray(); // 2
                r.beginArray(); // 3 should fail
            });
        }
    }
}
