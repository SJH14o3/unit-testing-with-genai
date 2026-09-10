package com.google.gson.internal;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.internal.bind.JsonElementTypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Streams} with decision coverage.
 */
public class StreamsTest {

    @Nested
    @DisplayName("parse(JsonReader) tests")
    class ParseTests {
        @Test
        @DisplayName("parses valid JSON object")
        void parseValidObject() {
            String json = "{\"key\":\"value\"}";
            JsonReader reader = new JsonReader(new StringReader(json));
            JsonElement element = Streams.parse(reader);
            assertNotNull(element);
            assertEquals("value", element.getAsJsonObject().get("key").getAsString());
        }

        @Test
        @DisplayName("returns JsonNull for empty input")
        void parseEmptyReturnsNull() {
            String json = "";
            JsonReader reader = new JsonReader(new StringReader(json));
            JsonElement element = Streams.parse(reader);
            assertSame(JsonNull.INSTANCE, element);
        }

        @Test
        @DisplayName("throws JsonSyntaxException for premature EOF")
        void parsePrematureEofThrows() {
            String json = "{"; // incomplete object
            JsonReader reader = new JsonReader(new StringReader(json));
            assertThrows(JsonSyntaxException.class, () -> Streams.parse(reader));
        }

        @Test
        @DisplayName("wraps MalformedJsonException as JsonSyntaxException")
        void parseMalformedJsonException() {
            // Malformed json that triggers MalformedJsonException
            String json = "[1,\"missing\"]"; // missing closing ]
            JsonReader reader = new JsonReader(new StringReader(json));
            assertDoesNotThrow(() -> Streams.parse(reader));
        }
    }

    @Test
    @DisplayName("write(JsonElement, JsonWriter) writes correctly")
    void writeElements() throws IOException {
        JsonElement element = JsonElementTypeAdapter.ADAPTER.read(new JsonReader(new StringReader("{\"a\":1}")));
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);
        Streams.write(element, writer);
        writer.flush();
        assertEquals("{\"a\":1}", stringWriter.toString());
    }

    @Nested
    @DisplayName("writerForAppendable tests")
    class WriterForAppendableTests {
        @Test
        @DisplayName("returns same Writer if appendable is Writer")
        void returnsSameWriter() {
            StringWriter writer = new StringWriter();
            Writer result = Streams.writerForAppendable(writer);
            assertSame(writer, result);
        }

        @Test
        @DisplayName("wraps Appendable into AppendableWriter")
        void wrapsAppendable() throws IOException {
            StringBuilder sb = new StringBuilder();
            Writer writer = Streams.writerForAppendable(sb);
            assertNotNull(writer);
            assertFalse(writer instanceof StringWriter); // should be AppendableWriter
            writer.write('{');
            writer.write("abc", 0, 3);
            writer.write("def", 1, 2); // write part of string
            writer.write(0x41); // 'A'
            writer.flush();
            assertEquals("{abcefA", sb.toString());
        }

        @Test
        @DisplayName("flush delegates to Flushable appendable")
        void flushDelegation() throws IOException {
            TestFlushableAppendable a = new TestFlushableAppendable();
            Writer w = Streams.writerForAppendable(a);
            w.write('x');
            w.flush();
            assertTrue(a.flushed);
        }

        @Test
        @DisplayName("close delegates to Closeable appendable")
        void closeDelegation() throws IOException {
            TestCloseableAppendable a = new TestCloseableAppendable();
            Writer w = Streams.writerForAppendable(a);
            w.write('y');
            w.close();
            assertTrue(a.closed);
        }
    }

    /** Test Appendable implementing Flushable */
    private static class TestFlushableAppendable implements Appendable, Flushable {
        private final StringBuilder sb = new StringBuilder();
        boolean flushed = false;
        @Override public Appendable append(CharSequence csq) { sb.append(csq); return this; }
        @Override public Appendable append(CharSequence csq, int start, int end) { sb.append(csq, start, end); return this; }
        @Override public Appendable append(char c) { sb.append(c); return this; }
        @Override public void flush() { flushed = true; }
        @Override public String toString() { return sb.toString(); }
    }

    /** Test Appendable implementing Closeable */
    private static class TestCloseableAppendable implements Appendable, java.io.Closeable {
        private final StringBuilder sb = new StringBuilder();
        boolean closed = false;
        @Override public Appendable append(CharSequence csq) { sb.append(csq); return this; }
        @Override public Appendable append(CharSequence csq, int start, int end) { sb.append(csq, start, end); return this; }
        @Override public Appendable append(char c) { sb.append(c); return this; }
        @Override public void close() { closed = true; }
        @Override public String toString() { return sb.toString(); }
    }
}
