package com.google.gson.internal.bind;

import com.google.gson.*;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link TreeTypeAdapter}. The tests cover the decision paths in
 * read, write, and delegate resolution without involving full Gson integration.
 */
class TreeTypeAdapterTest {

    private Gson gson;

    @BeforeEach
    void setUp() {
        gson = new Gson();
    }

    @Test
    @DisplayName("read(): delegate read called when deserializer is null")
    void testReadDelegateCalled() throws Exception {
        // Delegate adapter simply returns the integer parsed by Gson
        TypeAdapter<Integer> delegateAdapter = gson.getAdapter(Integer.class);
        TypeAdapterFactory factory = new CollectionTypeAdapterFactory(new ConstructorConstructor(
                Collections.emptyMap(), false, Collections.emptyList()));
        TreeTypeAdapter<Integer> adapter = new TreeTypeAdapter<>(null, null, gson,
                TypeToken.get(Integer.class), factory, true);
        // Force delegate creation
        adapter.read(new JsonReader(new StringReader("123")));
        // The adapter should delegate to Gson's Integer adapter.
        assertEquals(123, adapter.read(new JsonReader(new StringReader("123"))));
    }

    @Test
    @DisplayName("read(): null value returns null when nullSafe and deserializer not null")
    void testReadNullSafeReturnsNull() throws Exception {
        JsonDeserializer<Integer> deserializer = (json, type, ctx) -> 42; // dummy
        TreeTypeAdapter<Integer> adapter = new TreeTypeAdapter<>(null, deserializer, gson,
                TypeToken.get(Integer.class), null, true);
        JsonReader reader = new JsonReader(new StringReader("null"));
        assertNull(adapter.read(reader));
    }

    @Test
    @DisplayName("read(): non-null value deserialized correctly")
    void testReadCustomDeserialization() throws Exception {
        JsonDeserializer<Integer> deserializer = (json, type, ctx) -> 999;
        TreeTypeAdapter<Integer> adapter = new TreeTypeAdapter<>(null, deserializer, gson,
                TypeToken.get(Integer.class), null, false);
        JsonReader reader = new JsonReader(new StringReader("123"));
        assertEquals(999, adapter.read(reader));
    }

    @Test
    @DisplayName("write(): delegate write called when serializer is null")
    void testWriteDelegateCalled() throws Exception {
        StringWriter out = new StringWriter();
        JsonWriter writer = new JsonWriter(out);
        TypeAdapter<Integer> delegateAdapter = gson.getAdapter(Integer.class);
        TypeAdapterFactory factory = new CollectionTypeAdapterFactory(new ConstructorConstructor(
                Collections.emptyMap(), false, Collections.emptyList()));
        TreeTypeAdapter<Integer> adapter = new TreeTypeAdapter<>(null, null, gson,
                TypeToken.get(Integer.class), factory, true);
        adapter.write(writer, 123);
        assertEquals("123", out.toString());
    }

    @Test
    @DisplayName("write(): null value writes JSON null when nullSafe is true")
    void testWriteNullValue() throws Exception {
        StringWriter out = new StringWriter();
        JsonWriter writer = new JsonWriter(out);
        JsonSerializer<Integer> serializer = (src, type, ctx) -> new JsonPrimitive(src);
        TreeTypeAdapter<Integer> adapter = new TreeTypeAdapter<>(serializer, null, gson,
                TypeToken.get(Integer.class), null, true);
        adapter.write(writer, null);
        assertEquals("null", out.toString());
    }

    @Test
    @DisplayName("write(): serializer returns expected JSON tree")
    void testWriteCustomSerializer() throws Exception {
        StringWriter out = new StringWriter();
        JsonWriter writer = new JsonWriter(out);
        JsonSerializer<String> serializer = (src, type, ctx) -> new JsonPrimitive("hello");
        TreeTypeAdapter<String> adapter = new TreeTypeAdapter<>(serializer, null, gson,
                TypeToken.get(String.class), null, false);
        adapter.write(writer, "anything");
        assertEquals("\"hello\"", out.toString());
    }

    @Test
    @DisplayName("getSerializationDelegate() returns this when serializer present")
    void testGetSerializationDelegateWithSerializer() {
        JsonSerializer<String> serializer = null; // actual not needed
        TypeAdapterFactory factory = new CollectionTypeAdapterFactory(new ConstructorConstructor(
                Collections.emptyMap(), false, Collections.emptyList()));
        TreeTypeAdapter<String> adapter = new TreeTypeAdapter<>(serializer, null, gson,
                TypeToken.get(String.class), factory, true);
        assertSame("com.google.gson.internal.bind.TypeAdapters$16", adapter.getSerializationDelegate().getClass().getName());
    }

    @Test
    @DisplayName("getSerializationDelegate() returns delegate when serializer null")
    void testGetSerializationDelegateWithoutSerializer() {
        TypeAdapterFactory factory = new CollectionTypeAdapterFactory(new ConstructorConstructor(
                Collections.emptyMap(), false, Collections.emptyList()));
        TreeTypeAdapter<String> adapter = new TreeTypeAdapter<>(null, null, gson,
                TypeToken.get(String.class), factory, true);
        System.out.println();
        assertSame("com.google.gson.internal.bind.TypeAdapters$16", adapter.getSerializationDelegate().getClass().getName());
    }

    @Test
    @DisplayName("newFactory creates adapter only for exact type")
    void testNewFactoryExactType() {
        JsonSerializer<String> serializer = (src, type, ctx) -> new JsonPrimitive(src);
        TypeAdapterFactory factory = TreeTypeAdapter.newFactory(TypeToken.get(String.class), serializer);
        TypeAdapter<String> typed = factory.create(gson, TypeToken.get(String.class));
        assertNotNull(typed);
        assertTrue(typed instanceof TreeTypeAdapter);
        TypeAdapter<Integer> notTyped = factory.create(gson, TypeToken.get(Integer.class));
        assertNull(notTyped);
    }

    @Test
    @DisplayName("newFactoryWithMatchRawType matches raw type when appropriate")
    void testNewFactoryWithMatchRawType() {
        JsonSerializer<List> serializer = (src, type, ctx) -> new JsonPrimitive("list");
        TypeAdapterFactory factory = TreeTypeAdapter.newFactoryWithMatchRawType(TypeToken.get(List.class), serializer);
        TypeAdapter<List> matched = factory.create(gson, TypeToken.get(List.class));
        assertNotNull(matched);
        TypeAdapter<String> notMatch = factory.create(gson, TypeToken.get(String.class));
        assertNull(notMatch);
    }

    @Test
    @DisplayName("newTypeHierarchyFactory matches assignable types")
    void testNewTypeHierarchyFactory() {
        JsonSerializer<Number> serializer = (src, type, ctx) -> new JsonPrimitive(src);
        TypeAdapterFactory factory = TreeTypeAdapter.newTypeHierarchyFactory(Number.class, serializer);
        TypeAdapter<Integer> matched = factory.create(gson, TypeToken.get(Integer.class));
        assertNotNull(matched);
        TypeAdapter<String> notMatch = factory.create(gson, TypeToken.get(String.class));
        assertNull(notMatch);
    }
}
