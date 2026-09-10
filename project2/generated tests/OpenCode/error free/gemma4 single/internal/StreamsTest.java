package com.google.gson.internal;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.bind.JsonElementTypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class StreamsTest {

    @Test
    @DisplayName("parse(JsonReader) - Empty document returns JsonNull")
    void parse_emptyDocument_returnsJsonNull() throws Exception {
        JsonReader reader = new JsonReader(new StringReader(""));
        JsonElement element = Streams.parse(reader);
        assertTrue(element.isJsonNull());
    }

    @Test
    @DisplayName("parse(JsonReader) - Valid JSON string")
    void parse_validJson_returnsJsonElement() throws Exception {
        JsonReader reader = new JsonReader(new StringReader("\"hello\""));
        JsonElement element = Streams.parse(reader);
        assertEquals("hello", element.getAsString());
    }

    @Test
    @DisplayName("parse(JsonReader) - Throws JsonSyntaxException on MalformedJsonException")
    void parse_malformedJson_throwsJsonSyntaxException() {
        JsonReader reader = new JsonReader(new StringReader("{ \"unclosed\" "));
        assertThrows(JsonSyntaxException.class, () -> Streams.parse(reader));
    }

    @Test
    @DisplayName("parse(JsonReader) - Throws JsonSyntaxException on EOFException when not empty")
    void parse_prematureEOF_throwsJsonSyntaxException() {
        JsonReader reader = new JsonReader(new StringReader("[ "));
        assertThrows(JsonSyntaxException.class, () -> Streams.parse(reader));
    }

    @Test
    @DisplayName("write(JsonElement, JsonWriter) - Writes JsonNull")
    void write_jsonNull_writesNull() throws IOException {
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        Streams.write(JsonNull.INSTANCE, writer);
        writer.flush();
        assertEquals("null", sw.toString());
    }

    @Test
    @DisplayName("write(JsonElement, JsonWriter) - Writes JsonPrimitive (String)")
    void write_jsonPrimitiveString_writesString() throws IOException {
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        Streams.write(new com.google.gson.JsonPrimitive("hello"), writer);
        writer.flush();
        assertEquals("\"hello\"", sw.toString());
    }

    @Test
    @DisplayName("writerForAppendable - Returns Writer if input is Writer")
    void writerForAppendable_returnsWriterIfInputIsWriter() {
        StringWriter sw = new StringWriter();
        var result = Streams.writerForAppendable(sw);
        assertEquals(sw, result);
    }

    @Test
    @DisplayName("writerForAppendable - Returns AppendableWriter if input is Appendable")
    void writerForAppendable_returnsAppendableWriterIfInputIsAppendable() throws IOException {
        StringBuilder sb = new StringBuilder();
        var result = Streams.writerForAppendable(sb);
        assertNotEquals(sb, result);
        // Test functionality
        result.append("test");
        assertEquals("test", sb.toString());
    }

    @Test
    @DisplayName("AppendableWriter - Closeable behavior")
    void appendableWriter_close_callsCloseOnCloseable() throws IOException {
        class MockCloseable implements Closeable, Appendable {
            boolean closed = false;
            @Override public Appendable append(CharSequence csq) { return this; }
            @Override public Appendable append(CharSequence csq, int start, int end) { return this; }
            @Override public Appendable append(char c) { return this; }
            @Override public void close() { closed = true; }
        }
        MockCloseable mock = new MockCloseable();
        var writer = Streams.writerForAppendable(mock);
        writer.close();
        assertTrue(mock.closed);
    }

    @Test
    @DisplayName("AppendableWriter - Flushable behavior")
    void appendableWriter_flush_callsFlushOnFlushable() throws IOException {
        class MockFlushable implements java.io.Flushable, Appendable {
            boolean flushed = false;
            @Override public Appendable append(CharSequence csq) { return this; }
            @Override public Appendable append(CharSequence csq, int start, int end) { return this; }
            @Override public Appendable append(char c) { return this; }
            @Override public void flush() { flushed = true; }
        }
        MockFlushable mock = new MockFlushable();
        var writer = Streams.writerForAppendable(mock);
        writer.flush();
        assertTrue(mock.flushed);
    }
}
