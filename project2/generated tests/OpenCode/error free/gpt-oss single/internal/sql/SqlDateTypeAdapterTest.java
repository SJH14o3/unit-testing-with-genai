package com.google.gson.internal.sql;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Unit tests for {@link SqlDateTypeAdapter}.  Coverage focuses on
 * the four execution branches in {@link SqlDateTypeAdapter#read(JsonReader)}
 * and {@link SqlDateTypeAdapter#write(JsonWriter, SqlDate)}.
 */
class SqlDateTypeAdapterTest {

    private TypeAdapter<java.sql.Date> adapter;
    private StringWriter writer;
    private StringReader reader;

    @BeforeEach
    void setUp() {
        adapter = SqlDateTypeAdapter.FACTORY.create(new Gson(), TypeToken.get(java.sql.Date.class));
        writer = new StringWriter();
    }

    /* ---------- read(JsonReader) ---------- */

    @Test
    @DisplayName("reads null token")
    void read_nullToken_returnsNull() throws Exception {
        JsonReader in = new JsonReader(new StringReader("null"));
        assertNull(adapter.read(in));
    }

    @Test
    @DisplayName("reads valid date string")
    void read_validDate_returnsSqlDate() throws Exception {
        String json = "\"Jan 1, 2020\"";
        JsonReader in = new JsonReader(new StringReader(json));
        java.sql.Date result = adapter.read(in);
        assertNotNull(result);

        // Convert back to util.Date to verify the parsed time
        Date util = new Date(result.getTime());
        assertEquals(2020, util.getYear() + 1900);   // year of util.Date
        assertEquals(0, util.getMonth());            // January
        assertEquals(1, util.getDate());             // 1st
    }

    @Test
    @DisplayName("parses with original tz restored after exception")
    void read_parseException_restoresOriginalTimeZone() throws Exception {
        // Force a parse exception by providing a bad  string
        String json = "\"invalid date\"";
        JsonReader in = new JsonReader(new StringReader(json));

        // Use reflection to confirm the time zone is modified during parsing
        Field formatField = SqlDateTypeAdapter.class
                .getDeclaredField("format");
        formatField.setAccessible(true);
        SimpleDateFormat fmt = (SimpleDateFormat) formatField.get(adapter);

        // Set a non‑default zone to detect restoration
        TimeZone original = fmt.getTimeZone();
        TimeZone nonDefault = TimeZone.getTimeZone("Europe/Paris");
        fmt.setTimeZone(nonDefault);

        JsonSyntaxException thrown = assertThrows(JsonSyntaxException.class, () -> adapter.read(in));
        assertTrue(thrown.getMessage().contains("Failed parsing"));

        // After exception, zone should be the original non-default zone
        assertEquals(nonDefault, fmt.getTimeZone());
    }

    /* ---------- write(JsonWriter, SqlDate) ---------- */

    @Test
    @DisplayName("writes null value")
    void write_nullValue_outputsNull() throws Exception {
        JsonWriter out = new JsonWriter(writer);
        adapter.write(out, null);
        out.flush();
        assertEquals("null", writer.toString());
    }

    @Test
    @DisplayName("writes non‑null SqlDate")
    void write_value_outputsFormattedString() throws Exception {
        // 2021‑12‑25  (December 25, 2021)
        java.sql.Date date = new java.sql.Date(121, java.util.Calendar.DECEMBER, 25);  // API: year-1900, month0‑based, day
        JsonWriter out = new JsonWriter(writer);
        adapter.write(out, date);
        out.flush();

        // Format expected string according to adapter's SimpleDateFormat ("MMM d, yyyy")
        assertEquals("\"Dec 25, 2021\"", writer.toString());
    }
}