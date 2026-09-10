package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

class ArrayTypeAdapterTest {

    @Test
    void factory_returnsNullForNonArrayType() {
        Gson gson = new Gson();
        TypeAdapterFactory factory = ArrayTypeAdapter.FACTORY;
        assertNull(factory.create(gson, TypeToken.get(String.class)));
    }

    @Test
    void factory_returnsArrayTypeAdapterForArrayType() {
        Gson gson = new Gson();
        TypeAdapterFactory factory = ArrayTypeAdapter.FACTORY;
        TypeAdapter<String[]> adapter = factory.create(gson, TypeToken.get(String[].class));
        assertNotNull(adapter);
    }

    @Test
    void read_handlesNull() throws IOException {
        Gson gson = new Gson();
        // Using the constructor directly as it's in the same package
        TypeAdapter<Object> adapter = new ArrayTypeAdapter<>(gson, gson.getAdapter(String.class), String.class);
        JsonReader reader = new JsonReader(new StringReader("null"));
        assertNull(adapter.read(reader));
    }

    @Test
    void read_handlesPrimitiveArray() throws IOException {
        Gson gson = new Gson();
        TypeAdapter<Object> adapter = new ArrayTypeAdapter<>(gson, gson.getAdapter(int.class), int.class);
        JsonReader reader = new JsonReader(new StringReader("[1, 2, 3]"));
        int[] result = (int[]) adapter.read(reader);
        assertArrayEquals(new int[]{1, 2, 3}, result);
    }

    @Test
    void read_handlesObjectArray() throws IOException {
        Gson gson = new Gson();
        TypeAdapter<Object> adapter = new ArrayTypeAdapter<>(gson, gson.getAdapter(String.class), String.class);
        JsonReader reader = new JsonReader(new StringReader("[\"a\", \"b\"]"));
        String[] result = (String[]) adapter.read(reader);
        assertArrayEquals(new String[]{"a", "b"}, result);
    }

    @Test
    void write_handlesNull() throws IOException {
        Gson gson = new Gson();
        TypeAdapter<Object> adapter = new ArrayTypeAdapter<>(gson, gson.getAdapter(String.class), String.class);
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);
        adapter.write(writer, null);
        assertEquals("null", stringWriter.toString());
    }

    @Test
    void write_handlesArray() throws IOException {
        Gson gson = new Gson();
        TypeAdapter<Object> adapter = new ArrayTypeAdapter<>(gson, gson.getAdapter(String.class), String.class);
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);
        adapter.write(writer, new String[]{"a", "b"});
        assertEquals("[\"a\",\"b\"]", stringWriter.toString());
    }
}
