package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import java.io.StringReader;
import java.io.StringWriter;

@DisplayName("ArrayTypeAdapter Unit Tests")
class ArrayTypeAdapterTest {
    private Gson gson;

    @BeforeEach
    void setUp() {
        gson = new Gson();
    }

    @Test
    @DisplayName("read null input returns null")
    void readNullReturnsNull() throws Exception {
        ArrayTypeAdapter<Integer> adapter = new ArrayTypeAdapter<>(gson, gson.getAdapter(TypeToken.get(Integer.class)), Integer.class);
        JsonReader reader = new JsonReader(new StringReader("null"));
        Object result = adapter.read(reader);
        Assertions.assertNull(result);
    }

    @Test
    @DisplayName("read empty object array returns empty array")
    void readEmptyObjectArray() throws Exception {
        ArrayTypeAdapter<String> adapter = new ArrayTypeAdapter<>(gson, gson.getAdapter(TypeToken.get(String.class)), String.class);
        JsonReader reader = new JsonReader(new StringReader("[]"));
        Object result = adapter.read(reader);
        Assertions.assertTrue(result.getClass().isArray());
        Assertions.assertEquals(0, java.lang.reflect.Array.getLength(result));
    }

    @Test
    @DisplayName("read empty primitive array returns empty array")
    void readEmptyPrimitiveArray() throws Exception {
        ArrayTypeAdapter<Integer> adapter = new ArrayTypeAdapter<>(gson, gson.getAdapter(TypeToken.get(Integer.class)), int.class);
        JsonReader reader = new JsonReader(new StringReader("[]"));
        Object result = adapter.read(reader);
        Assertions.assertTrue(result.getClass().isArray());
        Assertions.assertEquals(0, java.lang.reflect.Array.getLength(result));
    }

    @Test
    @DisplayName("read integer array converts JSON array to int[]")
    void readIntArray() throws Exception {
        ArrayTypeAdapter<Integer> adapter = new ArrayTypeAdapter<>(gson, gson.getAdapter(TypeToken.get(Integer.class)), int.class);
        JsonReader reader = new JsonReader(new StringReader("[1, 2, 3]"));
        Object result = adapter.read(reader);
        Assertions.assertTrue(result.getClass().isArray());
        int[] arr = (int[]) result;
        Assertions.assertArrayEquals(new int[]{1, 2, 3}, arr);
    }

    @Test
    @DisplayName("write null writes JSON null")
    void writeNullWritesNull() throws Exception {
        ArrayTypeAdapter<String> adapter = new ArrayTypeAdapter<>(gson, gson.getAdapter(TypeToken.get(String.class)), String.class);
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        adapter.write(jsonWriter, null);
        Assertions.assertEquals("null", writer.toString());
    }

    @Test
    @DisplayName("write primitive array writes JSON array")
    void writeIntArray() throws Exception {
        ArrayTypeAdapter<Integer> adapter = new ArrayTypeAdapter<>(gson, gson.getAdapter(TypeToken.get(Integer.class)), int.class);
        int[] arr = {4, 5, 6};
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        adapter.write(jsonWriter, arr);
        Assertions.assertEquals("[4,5,6]", writer.toString());
    }

    @Test
    @DisplayName("write object array writes JSON array")
    void writeObjectArray() throws Exception {
        ArrayTypeAdapter<String> adapter = new ArrayTypeAdapter<>(gson, gson.getAdapter(TypeToken.get(String.class)), String.class);
        String[] arr = {"a", "b"};
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        adapter.write(jsonWriter, arr);
        Assertions.assertEquals("[\"a\",\"b\"]", writer.toString());
    }
}
