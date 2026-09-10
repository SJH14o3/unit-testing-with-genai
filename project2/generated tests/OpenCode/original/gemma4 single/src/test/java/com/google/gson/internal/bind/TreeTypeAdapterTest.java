package com.google.gson.internal.bind;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.parser.JsonParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class TreeTypeAdapterTest {

    private Gson gson;
    private TypeToken<String> stringTypeToken;

    @BeforeEach
    void setUp() {
        // Use a real Gson instance for the context and for testing factory logic
        gson = new Gson();
        stringTypeToken = new TypeToken<String>() {};
    }

    @Test
    @DisplayName("read: when deserializer is not null, handles non-null value")
    void read_deserializerPresent_returnsDeserializedValue() throws IOException {
        JsonDeserializer<String> deserializer = (json, type, context) -> JsonParser.parseString("\"decoded\"");
        TreeTypeAdapter<String> adapter = new TreeTypeAdapter<>(null, deserializer, gson, stringTypeToken, null, true);

        JsonReader reader = new JsonReader(new StringReader("\"input\""));
        String result = adapter.read(reader);

        assertEquals("decoded", result);
    }

    @Test
    @DisplayName("read: when deserializer is not null, handles null value with nullSafe=true")
    void read_deserializerPresent_nullSafeTrue_returnsNullOnJsonNull() throws IOException {
        JsonDeserializer<String> deserializer = (json, type, context) -> "should not be called";
        TreeTypeAdapter<String> adapter = new TreeTypeAdapter<>(null, deserializer, gson, stringTypeToken, null, true);

        JsonReader reader = new JsonReader(new StringReader("null"));
        String result = adapter.read(reader);

        assertNull(result);
    }

    @Test
    @DisplayName("read: when deserializer is not null, handles null value with nullSafe=false")
    void read_deserializerPresent_nullSafeFalse_callsDeserializer() throws IOException {
        JsonDeserializer<String> deserializer = (json, type, context) -> null;
        TreeTypeAdapter<String> adapter = new TreeTypeAdapter<>(null, deserializer, gson, stringTypeToken, null, false);

        JsonReader reader = new JsonReader(new StringReader("null"));
        String result = adapter.read(reader);

        assertNull(result);
    }

    @Test
    @DisplayName("write: when serializer is not null, handles non-null value")
    void write_serializerPresent_writesSerializedValue() throws IOException {
        JsonSerializer<String> serializer = (src, type, context) -> JsonPrimitive("serialized");
        TreeType
        TreeTypeAdapter<String> adapter = new TreeTypeAdapter<>(serializer, null, gson, stringTypeToken, null, true);

        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        adapter.write(jsonWriter, "input");

        assertEquals("\"serialized\"", writer.toString());
    }

    @Test
    @DisplayName("write: when serializer is not null, handles null value with nullSafe=true")
    void write_serializerPresent_nullSafeTrue_writesNullValue() throws IOException {
        JsonSerializer<String> serializer = (src, type, context) -> JsonPrimitive("should not be called");
        TreeTypeAdapter<String> adapter = new TreeTypeAdapter<>(serializer, null, gson, stringTypeToken, null, true);

        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        adapter.write(jsonWriter, null);

        assertEquals("null", writer.toString());
    }

    @Test
    @DisplayName("write: when serializer is not null, handles null value with nullSafe=false")
    void write_serializerPresent_nullSafeFalse_callsSerializer() throws IOException {
        JsonSerializer<String> serializer = (src, type, context) -> JsonPrimitive("null_as_string");
        TreeTypeAdapter<String> adapter = new TreeTypeAdapter<>(serializer, null, gson, stringTypeToken, null, false);

        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        adapter.write(jsonWriter, null);

        assertEquals("\"null_as_string\"", writer.toString());
    }

    @Test
    @DisplayName("getSerializationDelegate: returns this when serializer is present")
    void getSerializationDelegate_serializerPresent_returnsThis() {
        JsonSerializer<String> serializer = (src, type, context) -> JsonPrimitive("val");
        TreeTypeAdapter<String> adapter = new TreeTypeParsingAdapter<>(serializer, null, gson, stringTypeToken, null, true);
        
        assertEquals(adapter, adapter.getSerializationDelegate());
    }

    @Test
    @DisplayName("SingleTypeFactory: creates adapter when exact type matches")
    void factory_exactTypeMatches_returnsAdapter() {
        JsonSerializer<String> serializer = (src, type, context) -> JsonPrimitive("val");
        TypeAdapterFactory factory = TreeTypeAdapter.newFactory(stringTypeToken, serializer);

        TypeAdapter<String> adapter = factory.create(gson, stringTypeToken);

        assertNotNull(adapter);
        assertTrue(adapter instanceof TreeTypeAdapter);
    }

    @Test
    @DisplayName("SingleTypeFactory: does not create adapter when exact type does not match")
    void factory_exactTypeDoesNotMatch_returnsNull() {
        JsonSerializer<String> serializer = (src, type, context) -> JsonPrimitive("val");
        TypeAdapterFactory factory = TreeTypeAdapter.newFactory(new TypeToken<Integer>() {}, serializer);

        TypeAdapter<String> adapter = factory.create(gson, stringTypeToken);

        assertNull(adapter);
    }

    @Test
    @DisplayName("SingleTypeFactory: handles hierarchy type match")
    void factory_hierarchyTypeMatches_returnsAdapter() {
        JsonSerializer<Object> serializer = (src, type, context) -> JsonPrimitive("val");
        TypeAdapterFactory factory = TreeTypeAdapter.newTypeHierarchyFactory(Object.class, serializer);

        TypeAdapter<String> adapter = factory.create(gson, stringTypeToken);

        assertNotNull(adapter);
    }

    @Test
    @DisplayName("SingleTypeFactory: constructor throws exception if no serializer or deserializer")
    void factoryConstructor_noSerializerOrDeserializer_throwsException() {
        Object invalidAdapter = new Object();
        assertThrows(IllegalArgumentException.class, () -> {
            TreeTypeAdapter.newFactory(stringTypeToken, invalidAdapter);
        });
    }

    // Helper class to access protected constructor for testing
    private static class TreeTypeParsingAdapter<T> extends TreeTypeAdapter<T> {
        TreeTypeParsingAdapter(JsonSerializer<T> s, JsonDeserializer<T> d, Gson g, TypeToken<T> t, TypeAdapterFactory f, boolean ns) {
            super(s, d, g, t, f, ns);
        }
    }
}