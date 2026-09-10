package com.google.gson.internal.sql;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Time;
import java.util.TimeZone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class SqlTimeTypeAdapterTest {

    private TypeAdapter<Time> adapter;

    @BeforeEach
    void setUp() {
        adapter = new Gson().getAdapter(Time.class);
    }

    @Test
    @DisplayName("FACTORY.create returns adapter for Time.class")
    void factoryCreate_returnsAdapterForTimeClass() {
        var typeToken = TypeToken.get(Time.class);
        var result = SqlTimeTypeAdapter.FACTORY.create(new Gson(), typeToken);
        assertNotNull(result);
        assertTrue(result instanceof SqlTimeTypeAdapter);
    }

    @Test
    @DisplayName("FACTORY.create returns null for other classes")
    void factoryCreate_returnsNullForOtherClasses() {
        var typeToken = TypeToken.get(String.class);
        var result = SqlTimeTypeAdapter.FACTORY.create(new Gson(), typeToken);
        assertNull(result);
    }

    @Test
    @DisplayName("read returns null when input is JSON NULL")
    void read_returnsNullForJsonNull() throws Exception {
        JsonReader reader = new JsonReader(new StringReader("null"));
        assertNull(adapter.read(reader));
    }

    @ParameterizedTest
    @CsvSource({
            "10:30:45 AM, 10:30:45",
            "11:59:59 PM, 23:59:59",
            "12:00:00 AM, 00:00:00"
    })
    @DisplayName("read parses valid time strings correctly")
    void read_parsesValidTimeStrings(String input, String expected) throws Exception {
        JsonReader reader = new JsonReader(new StringReader("\"" + input + "\""));
        Time result = adapter.read(reader);
        assertNotNull(result);
        assertEquals(Time.valueOf(expected), result);
    }

    @Test
    @DisplayName("read throws JsonSyntaxException for invalid time strings")
    void read_throwsExceptionForInvalidFormat() {
        JsonReader reader = new JsonReader(new StringReader("\"invalid-time\""));
        assertThrows(JsonSyntaxException.class, () -> adapter.read(reader));
    }

    @Test
    @DisplayName("write writes null as JSON NULL")
    void write_writesNullAsJsonNull() throws Exception {
        StringWriter writerString = new StringWriter();
        JsonWriter writer = new JsonWriter(writerString);
        adapter.write(writer, null);
        writer.flush();
        assertEquals("null", writerString.toString());
    }

    @Test
    @DisplayName("write writes valid Time as formatted string")
    void write_writesFormattedTime() throws Exception {
        StringWriter writerString = new StringWriter();
        JsonWriter writer = new JsonWriter(writerString);
        Time time = Time.valueOf("14:30:00"); // 02:30:00 PM
        adapter.write(writer, time);
        writer.flush();
        assertEquals("\"02:30:00 PM\"", writerString.toString());
    }
}