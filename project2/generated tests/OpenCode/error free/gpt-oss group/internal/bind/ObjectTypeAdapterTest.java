package com.google.gson.internal.bind;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;

import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Covers the key decision branches of {@link ObjectTypeAdapter}:
 * - null handling
 * - array/list deserialization
 * - map/deserialization
 */
class ObjectTypeAdapterTest {

    private final Gson gson = new Gson();
    private final ObjectTypeAdapter adapter = (ObjectTypeAdapter)
            ObjectTypeAdapter.getFactory(ToNumberPolicy.DOUBLE).create(gson, TypeToken.get(Object.class));

    @Test
    void readNullReturnsNull() throws Exception {
        var reader = new JsonReader(new StringReader("null"));
        var result = adapter.read(reader);
        assertNull(result);
    }

    @Test
    void readArrayDeserializesToList() throws Exception {
        var reader = new JsonReader(new StringReader("[1,2,3]"));
        @SuppressWarnings("unchecked")
        var list = (List<Object>) adapter.read(reader);
        assertEquals(Arrays.asList(1.0, 2.0, 3.0), list);
    }

    @Test
    void readObjectDeserializesToMap() throws Exception {
        var reader = new JsonReader(new StringReader("{\"a\":1,\"b\":true}"));
        @SuppressWarnings("unchecked")
        var map = (LinkedHashMap<String, Object>) adapter.read(reader);
        assertEquals(2, map.size());
        assertEquals(1.0, map.get("a"));
        assertEquals(true, map.get("b"));
    }

    @Test
    void writeNullWritesNullValue() throws Exception {
        var writer = new StringWriter();
        adapter.write(new JsonWriter(writer), null);
        assertEquals("null", writer.toString());
    }

    @Test
    void writePrimitiveWritesValue() throws Exception {
        var writer = new StringWriter();
        adapter.write(new JsonWriter(writer), "xyz");
        assertEquals("\"xyz\"", writer.toString());
    }
}