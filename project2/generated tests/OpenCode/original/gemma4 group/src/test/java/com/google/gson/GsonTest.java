package com.google.gson;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.util.List;
import java.util.Arrays;

@DisplayName("Gson Test")
class GsonTest {

    private final Gson gson = new Gson();

    @Test
    @DisplayName("newBuilder should return a new GsonBuilder")
    void newBuilder_returnsGsonBuilder() {
        GsonBuilder builder = gson.newBuilder();
        assertNotNull(builder);
    }

    @Test
    @DisplayName("toJson should serialize string correctly")
    void toJson_string_serializesCorrectly() {
        assertEquals("\"hello\"", gson.toJson("hello"));
    }

    @Test
    @DisplayName("fromJson should deserialize string correctly")
    void fromJson_string_deserializesCorrectly() {
        assertEquals("hello", gson.fromJson("\"hello\"", String.class));
    }

    @Test
    @DisplayName("toJson should serialize integer correctly")
    void toJson_integer_serializesCorrectly() {
        assertEquals("123", gson.toJson(123));
    }

    @Test
    @DisplayName("fromJson should deserialize integer correctly")
    void fromJson_integer_deserializesCorrectly() {
        assertEquals(123, gson.fromJson("\"123\"", Integer.class));
    }

    @Test
    @DisplayName("serializeNulls should return false by default")
    void serializeNulls_defaultFalse() {
        assertFalse(gson.serializeNulls());
    }

    @Test
    @DisplayName("htmlSafe should return true by default")
    void htmlSafe_defaultTrue() {
        assertTrue(gson.htmlSafe());
    }

    @Test
    @DisplayName("toJsonTree should return JsonElement")
    void toJsonTree_returnsJsonElement() {
        assertNotNull(gson.toJsonTree("hello"));
    }

    @Test
    @DisplayName("fromJson with TypeToken should work")
    void fromJson_withTypeToken_works() {
        String json = "[\"a\",\"b\"]";
        Type type = new TypeToken<List<String>>(){}.getType();
        List<String> list = gson.fromJson(json, type);
        assertEquals(2, list.size());
        assertEquals("a", list.get(0));
    }

    @Test
    @DisplayName("newJsonWriter should return JsonWriter")
    void newJsonWriter_returnsJsonWriter() throws Exception {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = gson.newJsonWriter(writer);
        assertNotNull(jsonWriter);
    }

    @Test
    @DisplayName("newJsonReader should return JsonReader")
    void newJsonReader_returnsJsonReader() {
        JsonReader jsonReader = gson.newJsonReader(new StringReader("{}"));
        assertNotNull(jsonReader);
    }

    @Test
    @DisplayName("fromJson with Reader and Class should work")
    void fromJson_withReaderAndClass_works() throws Exception {
        String json = "{\"name\":\"test\"}";
        StringReader reader = new StringReader(json);
        TestClass result = gson.fromJson(reader, TestClass.class);
        assertNotNull(result);
        assertEquals("test", result.name);
    }

    @Test
    @DisplayName("toJson with Appendable should work")
    void toJson_withAppendable_works() throws Exception {
        StringWriter writer = new StringWriter();
        gson.toJson(123, writer);
        assertEquals("123", writer.toString());
    }

    @Test
    @DisplayName("toJson with Type and Appendable should work")
    void toJson_withTypeAndAppendable_works() throws Exception {
        StringWriter writer = new StringWriter();
        Type type = new TypeToken<List<Integer>>(){}.getType();
        gson.toJson(Arrays.asList(1, 2), type, writer);
        assertEquals("[1,2]", writer.toString());
    }

    @Test
    @DisplayName("fromJson with null should return null")
    void fromJson_withNull_returnsNull() {
        assertNull(gson.fromJson((String) null, String.class));
    }

    @Test
    @DisplayName("toJson with null should return JSON null")
    void toJson_withNull_returnsJsonNull() {
        assertEquals("null", gson.toJson(null));
    }

    @Test
    @DisplayName("fromJson(JsonReader, TypeToken) should work")
    void fromJson_withJsonReaderAndTypeToken_works() throws Exception {
        JsonReader reader = new JsonReader(new StringReader("[\"a\"]"));
        Type type = new TypeToken<List<String>>(){}.getType();
        List<String> list = gson.fromJson(reader, type);
        assertEquals(1, list.size());
        assertEquals("a", list.get(0));
    }

    @Test
    @DisplayName("toJson(JsonElement, JsonWriter) should work")
    void toJson_withJsonElementAndJsonWriter_works() throws Exception {
        StringWriter sw = new StringWriter();
        JsonWriter writer = gson.newJsonWriter(sw);
        JsonElement element = new JsonPrimitive("test");
        gson.toJson(element, writer);
        writer.close();
        assertEquals("\"test\"", sw.toString());
    }

    static class TestClass {
        String name;
    }
}
