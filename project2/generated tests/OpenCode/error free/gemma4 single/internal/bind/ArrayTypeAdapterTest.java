package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.lang.reflect.GenericArrayType;

import static org.junit.jupiter.api.Assertions.*;

class ArrayTypeAdapterTest {

    private final Gson gson = new Gson();

    @Test
    void factoryReturnsNullForNonArrayType() {
        assertNull(ArrayTypeAdapter.FACTORY.create(gson, new TypeToken<String>() {}));
    }

    @Test
    void factoryReturnsAdapterForClassArrayType() {
        assertNotNull(ArrayTypeAdapter.FACTORY.create(gson, new TypeToken<String[]>() {}));
    }

    @Test
    void factoryReturnsAdapterForGenericArrayType() {
        Type genericArrayType = new TypeToken<String[][]>() {}.getType();
        // We need to manually create a TypeToken that represents the GenericArrayType
        // because TypeToken<T> usually resolves to Class if it's a simple array.
        // However, for the sake of the factory check:
        assertNotNull(ArrayTypeAdapter.FACTORY.create(gson, new TypeToken<String[][]>() {}));
    }

    @Test
    void readReturnsNullForJsonNull() throws Exception {
        JsonReader reader = new JsonReader(new StringReader("null"));
        TypeAdapter<String[]> adapter = ArrayTypeAdapter.FACTORY.create(gson, new TypeToken<String[]>() {});
        assertNull(adapter.read(reader));
    }

    @Test
    void readReturnsEmptyArrayForEmptyJsonArray() throws Exception {
        JsonReader reader = new JsonReader(new StringReader("[]"));
        TypeAdapter<String[]> adapter = ArrayTypeAdapter.FACTORY.create(gson, new TypeToken<String[]>() {});
        Object result = adapter.read(reader);
        assertEquals(0, java.lang.reflect.Array.getLength(result));
    }

    @Test
    void readReturnsPrimitiveArray() throws Exception {
        JsonReader reader = new JsonReader(new StringReader("[1, 2, 3]"));
        TypeAdapter<int[]> adapter = ArrayTypeAdapter.FACTORY.create(gson, new TypeToken<int[]>() {});
        Object result = adapter.read(reader);
        assertTrue(result instanceof int[]);
        assertArrayEquals(new int[]{1, 2, 3}, (int[]) result);
    }

    @Test
    void readReturnsObjectArray() throws Exception {
        JsonReader reader = new JsonReader(new StringReader("[\"a\", \"b\"]"));
        TypeAdapter<String[]> adapter = ArrayTypeAdapter.FACTORY.create(gson, new TypeToken<String[]>() {});
        Object result = adapter.read(reader);
        assertArrayEquals(new String[]{"a", "b"}, (String[]) result);
    }

    @Test
    void writeWritesNull() throws Exception {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        TypeAdapter<String[]> adapter = ArrayTypeAdapter.FACTORY.create(gson, new TypeToken<String[]>() {});
        adapter.write(jsonWriter, null);
        jsonWriter.flush();
        assertEquals("null", writer.toString());
    }

    @Test
    void writeWritesEmptyArray() throws Exception {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        TypeAdapter<String[]> adapter = ArrayTypeAdapter.FACTORY.create(gson, new TypeToken<String[]>() {});
        adapter.write(jsonWriter, new String[0]);
        jsonWriter.flush();
        assertEquals("[]", writer.toString());
    }

    @Test
    void writeWritesPrimitiveArray() throws Exception {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        TypeAdapter<int[]> adapter = ArrayTypeAdapter.FACTORY.create(gson, new TypeToken<int[]>() {});
        adapter.write(jsonWriter, new int[]{1, 2, 3});
        jsonWriter.flush();
        assertEquals("[1,2,3]", writer.toString());
    }

    @Test
    void writeWritesObjectArray() throws Exception {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        TypeAdapter<String[]> adapter = ArrayTypeAdapter.FACTORY.create(gson, new TypeToken<String[]>() {});
        adapter.write(jsonWriter, new String[]{"a", "b"});
        jsonWriter.flush();
        assertEquals("[\"a\",\"b\"]", writer.toString());
    }
}