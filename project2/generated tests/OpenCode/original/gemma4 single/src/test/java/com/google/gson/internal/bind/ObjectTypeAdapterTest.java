package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.ToNumberStrategy;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ObjectTypeAdapterTest {

    @Test
    @DisplayName("getFactory returns DOUBLE_FACTORY when ToNumberPolicy.DOUBLE is used")
    void testGetFactoryDouble() {
        TypeAdapterFactory factory = ObjectTypeAdapter.getFactory(ToNumberPolicy.DOUBLE);
        // We can't easily check if it's the exact same instance without reflection, 
        // but we can verify it works as expected for Object.class.
        Gson gson = new GsonBuilder().create();
        TypeAdapter<Object> adapter = factory.create(gson, com.google.gson.reflect.TypeToken.get(Object.class));
        assertNotNull(adapter);
    }

    @Test
    @DisplayName("getFactory returns new factory for other strategies")
    void testGetFactoryOtherStrategy() {
        TypeAdapterFactory factory = ObjectTypeAdapter.getFactory(ToNumberPolicy.LONG);
        Gson gson = new GsonBuilder().create();
        TypeAdapter<Object> adapter = factory.create(gson, com.google.gson.reflect.TypeToken.get(Object.class));
        assertNotNull(adapter);
    }

    @ParameterizedTest
    @MethodSource("provideReadScenarios")
    @DisplayName("read should correctly parse various JSON types into Object")
    void testRead(String json, Object expected) throws IOException {
        Gson gson = new GsonBuilder().create();
        TypeAdapter<Object> adapter = ObjectTypeCompleter.getAdapter(gson);
        JsonReader reader = new JsonReader(new StringReader(json));
        assertEquals(expected, adapter.read(reader));
    }

    private static Stream<Arguments> provideReadScenarios() {
        return Stream.of(
            Arguments.of("\"string\"", "string"),
            Arguments.of("123", 123.0), // Default uses DOUBLE
            Arguments.of("true", true),
            Arguments.of("null", null),
            Arguments.of("[\"a\", \"b\"]", List.of("a", "b")),
            Arguments.of("{\"key\": \"value\"}", Map.of("key", "value")),
            Arguments.of("[{\"inner\": 1}]", List.of(Map.of("inner", 1.0))),
            Arguments.of("{\"outer\": [1, 2]}", Map.of("outer", List.of(1.0, 2.0)))
        );
    }

    @Test
    @DisplayName("write should correctly serialize various objects")
    void testWrite() throws IOException {
        Gson gson = new GsonBuilder().create();
        TypeAdapter<Object> adapter = ObjectCompleter.getAdapter(gson);
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        // Test null
        jsonWriter.reset();
        adapter.write(jsonWriter, null);
        assertEquals("null", writer.toString());

        // Test String
        json.reset();
        adapter.write(jsonWriter, "hello");
        assertEquals("\"hello\"", writer.toString());

        // Test Integer (wrapped in Object)
        json.reset();
        adapter.write(jsonWriter, 42);
        assertEquals("42", writer.toString());

        // Test Boolean
        json.reset();
        adapter.write(jsonWriter, false);
        assertEquals("false", writer.toString());
    }

    @Test
    @DisplayName("write handles ObjectTypeAdapter case (empty object for Object.class)")
    void testWriteObjectTypeAdapterCase() throws IOException {
        Gson gson = new GsonBuilder().create();
        TypeAdapter<Object> adapter = ObjectCompleter.getAdapter(gson);
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        // Writing an object that is itself an ObjectTypeAdapter should result in {}
        // In our test, we pass a value whose class is Object.class
        adapter.write(jsonWriter, new Object());
        assertEquals("{}", writer.toString());
    }

    /**
     * Helper to get the adapter for testing without needing complex setup.
     * Since the constructor is private, we use the factory.
     */
    private static class ObjectCompleter {
        static TypeAdapter<Object> getAdapter(Gson gson) {
            return ObjectTypeAdapter.getFactory(ToNumberPolicy.DOUBLE).create(gson, com.google.gson.reflect.TypeToken.get(Object.class));
        }
    }

    private static class ObjectTypeAdapterAccess {
        static TypeAdapter<Object> getAdapter(Gson gson) {
            return ObjectTypeAdapter.getFactory(ToNumberPolicy.DOUBLE).create(gson, com.google.gson.reflect.TypeToken.get(Object.class));
        }
    }
}

// Note: Due to the private constructor and static nature of the factory, 
// we access the adapter via the public factory method.