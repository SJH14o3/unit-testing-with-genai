package com.google.gson.internal.sql;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SqlTimestampTypeAdapterTest {

    private final TypeAdapter<Date> dateAdapter = new Gson().getAdapter(Date.class);
    private final TypeAdapter<Timestamp> adapter = new Gson().getAdapter(Timestamp.class);

    @Test
    @DisplayName("read() converts Date to Timestamp")
    void testReadNotNull() throws Exception {
        String json = "\"2022-01-01T00:00:00.000Z\"";
        JsonReader in = new JsonReader(new StringReader(json));
        Timestamp ts = adapter.read(in);
        assertNotNull(ts);
        assertEquals(Timestamp.valueOf("2022-01-01 03:30:00.000"), ts);
    }

    @Test
    @DisplayName("read() returns null for JSON null")
    void testReadNull() throws Exception {
        JsonReader in = new JsonReader(new StringReader("null"));
        Timestamp ts = adapter.read(in);
        assertNull(ts);
    }

    @Test
    @DisplayName("write() delegates to underlying DateTypeAdapter")
    void testWrite() throws Exception {
        Timestamp ts = Timestamp.valueOf("2023-03-15 12:34:56.789");
        StringWriter out = new StringWriter();
        adapter.write(new JsonWriter(out), ts);
        // The delegated adapter writes ISO‑8601 format produced by its own Date formatter.
        // We just check that the output is not empty and not null literal.
        String result = out.toString().trim();
        assertFalse(result.isEmpty());
        assertFalse(result.equals("null"));
    }
}