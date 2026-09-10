package com.google.gson.internal.bind;

import com.google.gson.*;
import com.google.gson.internal.bind.NumberTypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.StringReader;
import java.io.IOException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class NumberTypeAdapterTest {
    private TypeAdapter<Number> adapter;

    @BeforeEach
    void setUp() {
        TypeAdapterFactory factory = NumberTypeAdapter.getFactory(ToNumberPolicy.LAZILY_PARSED_NUMBER);
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(factory).create();
        // Create adapter directly for Number
        adapter = (TypeAdapter<Number>) factory.create(gson, TypeToken.get(Number.class));
    }

    private Number read(String json) throws IOException {
        JsonReader reader = new JsonReader(new StringReader(json));
        return adapter.read(reader);
    }

    @Test
    @DisplayName("read null returns null")
    void testReadNull() throws IOException {
        assertNull(read("null"));
    }

    @Test
    @DisplayName("read numeric literal returns number")
    void testReadNumber() throws IOException {
        Number result = read("123");
        assertEquals(123, result.intValue());
    }

    @Test
    @DisplayName("read string number returns number")
    void testReadStringNumber() throws IOException {
        Number result = read("\"456\"");
        assertEquals(456, result.intValue());
    }

    @Test
    @DisplayName("read non‑numeric token throws JsonSyntaxException")
    void testReadUnexpected() {
        assertThrows(JsonSyntaxException.class, () -> {
            read("true");
        });
    }
}
