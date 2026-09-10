package com.google.gson.internal.sql;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import java.sql.Time;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlTimeTypeAdapterTest {

    private SqlTimeTypeAdapter adapter;
    private Gson gson;

    @BeforeEach
    void setUp() {
        gson = new Gson();
        adapter = (SqlTimeTypeAdapter) SqlTimeTypeAdapter.FACTORY.create(gson, new TypeToken<Time>(){});
    }

    @Test
    @DisplayName("Test factory returns null for non-Time types")
    void testFactoryReturnsNull() {
        assertNull(SqlTimeTypeAdapter.FACTORY.create(gson, new TypeToken<String>(){}));
    }

    @Test
    @DisplayName("Test factory returns adapter for Time type")
    void testFactoryReturnsAdapter() {
        assertNotNull(adapter);
    }

    @Test
    @DisplayName("Test writing Time to JSON")
    void testWrite() throws Exception {
        // 10:30:00 AM
        Time time = Time.valueOf("10:30:00");

        java.io.StringWriter sw = new java.io.StringWriter();
        com.google.gson.stream.JsonWriter jw = new com.google.gson.stream.JsonWriter(sw);
        adapter.write(jw, time);
        jw.close();

        // Format is "hh:mm:ss a" (e.g., "10:30:00 AM")
        // Note: actual output depends on locale/timezone, but we expect something containing 10:30:00
        assertNotNull(sw.toString());
        assertTrue(sw.toString().contains("10:30:00"));
    }

    @Test
    @DisplayName("Test reading Time from JSON")
    void testRead() throws Exception {
        String json = "\"10:30:00 AM\"";
        com.google.gson.stream.JsonReader jr = new com.google.gson.stream.JsonReader(new java.io.StringReader(json));
        Time time = adapter.read(jr);
        assertNotNull(time);

        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(time);
        assertEquals(10, cal.get(java.util.Calendar.HOUR));
        assertEquals(30, cal.get(java.util.Calendar.MINUTE));
        assertEquals(0, cal.get(java.util.Calendar.SECOND));
    }

    @Test
    @DisplayName("Test reading null from JSON")
    void testReadNull() throws Exception {
        com.google.gson.stream.JsonReader jr = new com.google.gson.stream.JsonReader(new java.io.StringReader("null"));
        Time time = adapter.read(jr);
        assertNull(time);
    }

    @Test
    @DisplayName("Test reading invalid format throws JsonSyntaxException")
    void testReadInvalidFormat() throws Exception {
        com.google.gson.stream.JsonReader jr = new com.google.gson.stream.JsonReader(new java.io.StringReader("\"invalid-time\""));
        com.google.gson.JsonSyntaxException ex = org.junit.jupiter.api.Assertions.assertThrows(com.google.gson.JsonSyntaxException.class, () -> adapter.read(jr));
        assertNotNull(ex);
    }
}
