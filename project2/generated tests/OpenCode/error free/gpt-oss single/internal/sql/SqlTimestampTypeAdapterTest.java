package com.google.gson.internal.sql;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SqlTimestampTypeAdapter unit tests")
class SqlTimestampTypeAdapterTest {

    /* Helper that returns a Gson instance wired with the factory under test. */
    private Gson gsonFactoryInstance() {
        return new GsonBuilder()
                .registerTypeAdapterFactory(SqlTimestampTypeAdapter.FACTORY)
                .create();
    }

    @Test
    @DisplayName("read() returns Timestamp when JSON contains a number")
    void read_nonNullNumber() throws IOException {
        Gson gson = gsonFactoryInstance();
        TypeAdapter<Timestamp> adapter = gson.getAdapter(Timestamp.class);
        String json = "1625155200000"; // 2021‑07‑01T00:00:00Z
        Timestamp result = adapter.read(new com.google.gson.stream.JsonReader(new StringReader(json)));

        assertNotNull(result);
        assertEquals(1625155200000L, result.getTime());
    }

    @Test
    @DisplayName("read() returns null when JSON is null")
    void read_null() throws IOException {
        Gson gson = gsonFactoryInstance();
        TypeAdapter<Timestamp> adapter = gson.getAdapter(Timestamp.class);
        String json = "null";
        Timestamp result = adapter.read(new com.google.gson.stream.JsonReader(new StringReader(json)));

        assertNull(result);
    }

    @Test
    @DisplayName("write() serialises a Timestamp to its epoch milliseconds")
    void write_roundTrip() throws IOException {
        Gson gson = gsonFactoryInstance();
        TypeAdapter<Timestamp> adapter = gson.getAdapter(Timestamp.class);
        Timestamp value = new Timestamp(1625155200000L);

        StringWriter stringWriter = new StringWriter();
        com.google.gson.stream.JsonWriter jsonWriter =
                new com.google.gson.stream.JsonWriter(stringWriter);
        adapter.write(jsonWriter, value);
        jsonWriter.flush();

        String jsonOutput = stringWriter.toString();
        assertEquals("1625155200000", jsonOutput);

        // Read back to ensure round‑trip round‑trip works
        Timestamp roundTrip =
                adapter.read(new com.google.gson.stream.JsonReader(new StringReader(jsonOutput)));
        assertEquals(value, roundTrip);
    }

    @Test
    @DisplayName("Factory returns adapter for Timestamp.class and null otherwise")
    void factoryBehaviour() {
        Gson gson = gsonFactoryInstance();
        TypeToken<Timestamp> timestampToken = TypeToken.get(Timestamp.class);
        TypeToken<String> stringToken = TypeToken.get(String.class);

        // Verify factory path that creates an adapter
        assertNotNull(
                SqlTimestampTypeAdapter.FACTORY.create(gson, timestampToken),
                "Factory should provide adapter for Timestamp");

        // Verify factory path that returns null for non‑Timestamp
        assertNull(
                SqlTimestampTypeAdapter.FACTORY.create(gson, stringToken),
                "Factory should return null for non‑Timestamp types");
    }
}