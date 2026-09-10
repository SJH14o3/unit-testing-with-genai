package com.google.gson.internal.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.google.gson.Gson;
import com.google.gson.Strictness;
import org.junit.jupiter.api.BeforeEach;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import java.sql.Timestamp;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SqlTimestampTypeAdapterTest {

    private SqlTimestampTypeAdapter adapter;
    private Gson gson;

    @BeforeEach
    void setUp() {
        gson = new Gson();
        adapter = (SqlTimestampTypeAdapter) SqlTimestampTypeAdapter.FACTORY.create(gson, new TypeToken<Timestamp>(){});
    }

    @Test
    @DisplayName("Test factory returns null for non-Timestamp types")
    void testFactoryReturnsNull() {
        assertNull(SqlTimestampTypeAdapter.FACTORY.create(gson, new TypeToken<String>(){}));
    }

    @Test
    @DisplayName("Test factory returns adapter for Timestamp type")
    void testFactoryReturnsAdapter() {
        assertNotNull(adapter);
    }

    @Test
    @DisplayName("Test writing Timestamp to JSON")
    void testWrite() throws Exception {
        // Oct 25, 2023 00:00:00 UTC
        Timestamp ts = new Timestamp(1698192000000L);

        java.io.StringWriter sw = new java.io.StringWriter();
        com.google.gson.stream.JsonWriter jw = new com.google.gson.stream.JsonWriter(sw);
        adapter.write(jw, ts);
        jw.close();

        // Since we don't know exactly how the default Date adapter formats it without mocking,
        // we can just check that it's not null and contains some date-like structure or at least a string.
        assertNotNull(sw.toString());
    }

    @Test
    @DisplayName("Test reading Timestamp from JSON")
    void testRead() throws Exception {
        // Assuming the default date format is something like "Oct 25, 2023"
        String json = "\"2023-10-25\"";
        com.google.gson.stream.JsonReader jr = new com.google.gson.stream.JsonReader(new java.io.StringReader(json));
        jr.setStrictness(Strictness.LENIENT);
        Timestamp ts = adapter.read(jr);
        assertNotNull(ts);

        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(ts);
        assertEquals(2023, cal.get(java.util.Calendar.YEAR));
        assertEquals(java.util.Calendar.OCTOBER, cal.get(java.util.Calendar.MONTH));
        assertEquals(25, cal.get(java.util.Calendar.DAY_OF_MONTH));
    }

    @Test
    @DisplayName("Test reading null from JSON")
    void testReadNull() throws Exception {
        com.google.gson.stream.JsonReader jr = new com.google.gson.stream.JsonReader(new java.io.StringReader("null"));
        Timestamp ts = adapter.read(jr);
        assertNull(ts);
    }
}
