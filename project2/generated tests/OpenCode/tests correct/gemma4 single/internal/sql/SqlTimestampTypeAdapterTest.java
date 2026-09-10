package com.google.gson.internal.sql;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class SqlTimestampTypeAdapterTest {

    private final Gson gson = new Gson();

    @Test
    void factory_ReturnsAdapterForTimestamp() {
        TypeAdapter<Timestamp> adapter = (TypeAdapter<Timestamp>) SqlTimestampTypeAdapter.FACTORY.create(
                gson, new TypeToken<Timestamp>() {});
        assertNotNull(adapter);
    }

    @Test
    void factory_ReturnsNullForOtherTypes() {
        TypeAdapter<String> adapter = SqlTimestampTypeAdapter.FACTORY.create(
                gson, new TypeToken<String>() {});
        assertNull(adapter);
    }

    @Test
    void read_ValidDate_ReturnsTimestamp() throws Exception {
        TypeAdapter<Timestamp> adapter = (TypeAdapter<Timestamp>) SqlTimestampTypeAdapter.FACTORY.create(
                gson, new TypeToken<Timestamp>() {});

        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        Date originalDate = new Date(2026, 8, 7);
        gson.getAdapter(Date.class).write(writer, originalDate);

        JsonReader reader = new JsonReader(new StringReader(sw.toString()));
        Timestamp result = adapter.read(reader);

        assertNotNull(result);
        assertEquals(originalDate.getTime(), result.getTime());
    }

    @Test
    void read_Null_ReturnsNull() throws Exception {
        TypeAdapter<Timestamp> adapter = (TypeAdapter<Timestamp>) SqlTimestampTypeAdapter.FACTORY.create(
                gson, new TypeToken<Timestamp>() {});

        JsonReader reader = new JsonReader(new StringReader("null"));
        Timestamp result = adapter.read(reader);

        assertNull(result);
    }

    @Test
    void write_ValidTimestamp_WritesCorrectJson() throws Exception {
        TypeAdapter<Timestamp> adapter = (TypeAdapter<Timestamp>) SqlTimestampTypeAdapter.FACTORY.create(
                gson, new TypeToken<Timestamp>() {});

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        timestamp.setTime(timestamp.getTime() - timestamp.getTime() % 1000);
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);

        adapter.write(writer, timestamp);

        String json = sw.toString();
        Date resultDate = gson.fromJson(json, Date.class);
        assertNotNull(resultDate);
        assertEquals(timestamp.getTime(), resultDate.getTime());
    }
}