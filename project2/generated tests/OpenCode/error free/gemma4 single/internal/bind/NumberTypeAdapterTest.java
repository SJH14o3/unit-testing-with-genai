package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.ToNumberPolicy;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumberTypeAdapterTest {

    private final Gson gson = new GsonBuilder()
            .setNumberToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
            .create();

    @Test
    @DisplayName("getFactory returns a factory for LAZILY_PARSED_NUMBER")
    void testGetFactory_LazilyParsedNumber() {
        assertNotNull(NumberTypeAdapter.getFactory(ToNumberPolicy.LAZILY_PARSED_NUMBER));
    }

    @Test
    @DisplayName("getFactory returns a factory for other strategies")
    void testGetFactory_OtherStrategy() {
        assertNotNull(NumberTypeAdapter.getFactory(ToNumberPolicy.LONG_OR_DOUBLE));
    }

    @Test
    @DisplayName("read returns null when token is NULL")
    void testRead_Null() throws IOException {
        TypeAdapter<Number> adapter = gson.getAdapter(Number.class);
        JsonReader reader = new JsonReader(new StringReader("null"));
        assertNull(adapter.read(reader));
    }

    @Test
    @DisplayName("read returns number when token is NUMBER")
    void testRead_Number() throws IOException {
        TypeAdapter<Number> adapter = gson.getAdapter(Number.class);
        JsonReader reader = new JsonReader(new StringReader("123"));
        assertEquals(123L, adapter.read(reader));
    }

    @Test
    @DisplayName("read returns number when token is STRING")
    void testRead_String() throws IOException {
        TypeAdapter<Number> adapter = gson.getAdapter(Number.class);
        JsonReader reader = new JsonReader(new StringReader("\"123\""));
        assertEquals(123L, adapter.read(reader));
    }

    @Test
    @DisplayName("read throws JsonSyntaxException when token is invalid")
    void testRead_InvalidToken() throws IOException {
        TypeAdapter<Number> adapter = gson.getAdapter(Number.class);
        JsonReader reader = new JsonReader(new StringReader("{\"key\":\"value\"}"));
        assertThrows(JsonSyntaxException.class, () -> adapter.read(reader));
    }

    @Test
    @DisplayName("write writes the number value correctly")
    void testWrite() throws IOException {
        TypeAdapter<Number> adapter = gson.getAdapter(Number.class);
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        adapter.write(jsonWriter, 456);
        jsonWriter.flush();
        assertEquals("456", writer.toString());
    }
}