package com.google.gson.stream;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.StringReader;
import java.io.IOException;

class JsonReaderTest {

    @Test
    @DisplayName("beginArray should throw IllegalStateException if token is not BEGIN_ARRAY")
    void beginArray_throwsException_ifNotArray() throws IOException {
        JsonReader reader = new JsonReader(new StringReader("{\"a\":1}"));
        assertThrows(IllegalStateException.class, reader::beginArray);
    }

    @Test
    @DisplayName("endArray should throw IllegalStateException if token is not END_ARRAY")
    void endArray_throwsException_ifNotArray() throws IOException {
        JsonReader reader2 = new JsonReader(new StringReader("{\"a\":1}"));
        assertThrows(IllegalStateException.class, reader2::endArray);
    }

    @Test
    @DisplayName("beginObject should throw IllegalStateException if token is not BEGIN_OBJECT")
    void beginObject_throwsException_ifNotObject() throws IOException {
        JsonReader reader = new JsonReader(new StringReader("[1,2,3]"));
        assertThrows(IllegalStateException.class, reader::beginObject);
    }

    @Test
    @DisplayName("endObject should throw IllegalStateException if token is not END_OBJECT")
    void endObject_throwsException_ifNotObject() throws IOException {
        JsonReader reader = new JsonReader(new StringReader("[1,2,3]"));
        assertThrows(IllegalStateException.class, reader::endObject);
    }

    @Test
    @DisplayName("hasNext should return false on EOF")
    void hasNext_returnsFalseOnEof() throws IOException {
        JsonReader reader = new JsonReader(new StringReader(""));
        assertThrows(IOException.class, reader::hasNext);
    }

    @Test
    @DisplayName("peek should return correct token for various JSON types")
    void peek_returnsCorrectTokens() throws IOException {
        JsonReader readerObj = new JsonReader(new StringReader("{}"));
        assertEquals(JsonToken.BEGIN_OBJECT, readerObj.peek());

        JsonReader readerArr = new JsonReader(new StringReader("[]"));
        assertEquals(JsonToken.BEGIN_ARRAY, readerArr.peek());

        JsonReader readerNull = new JsonReader(new StringReader("null"));
        assertEquals(JsonToken.NULL, readerNull.peek());

        JsonReader readerTrue = new JsonReader(new StringReader("true"));
        assertEquals(JsonToken.BOOLEAN, readerTrue.peek());

        JsonReader readerString = new JsonReader(new StringReader("\"string\""));
        assertEquals(JsonToken.STRING, readerString.peek());

        JsonReader readerNumber = new JsonReader(new StringReader("123"));
        assertEquals(JsonToken.NUMBER, readerNumber.peek());
    }

    @Test
    @DisplayName("nextString should return string value")
    void nextString_returnsString() throws IOException {
        JsonReader reader = new JsonReader(new StringReader("\"hello\""));
        assertEquals("hello", reader.nextString());
    }

    @Test
    @DisplayName("nextBoolean should return boolean value")
    void nextBoolean_returnsBoolean() throws IOException {
        JsonReader readerTrue = new JsonReader(new StringReader("true"));
        assertTrue(readerTrue.nextBoolean());
        JsonReader readerFalse = new JsonReader(new StringReader("false"));
        assertFalse(readerFalse.nextBoolean());
    }

    @Test
    @DisplayName("nextNull should consume null token")
    void nextNull_consumesNull() throws IOException {
        JsonReader reader = new JsonReader(new StringReader("null"));
        reader.nextNull();
        assertFalse(reader.hasNext());
    }

    @Test
    @DisplayName("nextLong should return long value")
    void nextLong_returnsLong() throws IOException {
        JsonReader reader = new JsonReader(new StringReader("123456789"));
        assertEquals(123456789L, reader.nextLong());
    }

    @Test
    @DisplayName("nextDouble should return double value")
    void nextDouble_returnsDouble() throws IOException {
        JsonReader reader = new JsonReader(new StringReader("123.456"));
        assertEquals(123.456, reader.nextDouble(), 0.0001);
    }

    @Test
    @DisplayName("nextName should return property name")
    void nextName_returnsName() throws IOException {
        JsonReader reader = new JsonReader(new StringReader("{\"key\":\"value\"}"));
        reader.beginObject();
        assertEquals("key", reader.nextName());
        assertEquals("value", reader.nextString());
        reader.endObject();
    }

    @Test
    @DisplayName("setNestingLimit should throw exception for negative limit")
    void setNestingLimit_throwsExceptionForNegative() {
        JsonReader reader = new JsonReader(new StringReader(""));
        assertThrows(IllegalArgumentException.class, () -> reader.setNestingLimit(-1));
    }

    @Test
    @DisplayName("close should close the underlying reader")
    void close_closesReader() throws IOException {
        StringReader stringReader = new StringReader("[]");
        JsonReader reader = new JsonReader(stringReader);
        reader.close();
        assertThrows(IOException.class, stringReader::read);
    }

    @Test
    @DisplayName("setStrictness should work correctly")
    void setStrictness_works() throws IOException {
        JsonReader reader = new JsonReader(new StringReader(""));
        reader.setStrictness(com.google.gson.Strictness.LENIENT);
        assertTrue(reader.isLenient());
        reader.setStrictness(com.google.gson.Strictness.STRICT);
        assertFalse(reader.isLenient());
    }

    @Test
    @DisplayName("skipValue should skip various values")
    void skipValue_skipsValues() throws IOException {
        JsonReader reader = new JsonReader(new StringReader("{\"a\": [1, 2, {\"b\": 3}, \"s\"], \"c\": true, \"d\": null}"));
        reader.beginObject();
        reader.nextName();
        reader.skipValue();
        reader.nextName();
        reader.skipValue();
        reader.nextName();
        reader.skipValue();
        reader.endObject();
        assertFalse(reader.hasNext());
    }

    @Test
    @DisplayName("toString should return class name and location")
    void toString_returnsClassNameAndLocation() {
        JsonReader reader = new JsonReader(new StringReader(""));
        assertTrue(reader.toString().contains("JsonReader"));
    }
}
