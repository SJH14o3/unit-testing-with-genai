package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.ToNumberPolicy;
import com.google.gson.ToNumberStrategy;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

class NumberTypeAdapterTest {

    @Test
    @DisplayName("Factory returns adapter for Number types")
    void factoryReturnsAdapterForNumber() {
        TypeAdapterFactory factory = NumberTypeAdapter.getFactory(ToNumberPolicy.LAZILY_PARSED_NUMBER);
        Gson gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapterFactory(factory)
                .create();

        assertNotNull(gson.getAdapter(Number.class));
        assertNotNull(gson.getAdapter(String.class));
    }

    @Test
    @DisplayName("Serialization and deserialization of Number")
    void serializationDeserialization() throws Exception {
        Gson gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapterFactory(NumberTypeAdapter.getFactory(ToNumberPolicy.LAZILY_PARSED_NUMBER))
                .create();

        String json = "123.45";
        Number result = gson.fromJson(json, Number.class);
        assertNotNull(result);
        assertEquals(123.45, result.doubleValue());

        String writtenJson = gson.toJson(123.45);
        assertEquals("123.45", writtenJson);
    }

    @Test
    @DisplayName("Deserialization of null returns null")
    void deserializationNullReturnsNull() throws Exception {
        Gson gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapterFactory(NumberTypeAdapter.getFactory(ToNumberPolicy.LAZILY_PARSED_NUMBER))
                .create();

        String json = "null";
        Number result = gson.fromJson(json, Number.class);
        assertNull(result);
    }
}
