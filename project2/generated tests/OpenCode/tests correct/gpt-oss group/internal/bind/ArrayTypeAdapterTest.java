package com.google.gson.internal.bind;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonReader;
import java.io.StringReader;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link ArrayTypeAdapter#read} and {@link ArrayTypeAdapter#write} for primitive
 * and object arrays, as well as null handling.
 */
class ArrayTypeAdapterTest<E> {

    private ArrayTypeAdapter<E> createAdapter(Class<E> componentType) {
        Gson gson = new Gson();
        // The real constructor uses a TypeAdapterRuntimeTypeWrapper; we mimic the same
        TypeAdapterRuntimeTypeWrapper<E> wrapper =
                new TypeAdapterRuntimeTypeWrapper<>(gson,
                        gson.getAdapter(componentType),
                        componentType);
        return new ArrayTypeAdapter<>(gson, wrapper, componentType);
    }

    @Test
    void writePrimitiveArray() throws Exception {
        var adapter = createAdapter((Class<E>) int.class);
        var writer = new StringWriter();
        adapter.write(new JsonWriter(writer), new int[]{1, 2, 3});
        assertEquals("[1,2,3]", writer.toString());
    }

    @Test
    void writeObjectArray() throws Exception {
        var adapter = createAdapter((Class<E>) String.class);
        var writer = new StringWriter();
        adapter.write(new JsonWriter(writer), new String[]{"a", "b", "c"});
        assertEquals("[\"a\",\"b\",\"c\"]", writer.toString());
    }

    @Test
    void readPrimitiveArray() throws Exception {
        var adapter = createAdapter((Class<E>) int.class);
        JsonReader reader = new JsonReader(new StringReader("[4,5,6]"));
        var result = (int[]) adapter.read(reader);
        assertArrayEquals(new int[]{4,5,6}, result);
    }

    @Test
    void readNull() throws Exception {
        var adapter = createAdapter((Class<E>) String.class);
        JsonReader reader = new JsonReader(new StringReader("null"));
        var result = adapter.read(reader);
        assertNull(result);
    }
}