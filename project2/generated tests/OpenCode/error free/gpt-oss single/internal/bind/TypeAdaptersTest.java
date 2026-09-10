package com.google.gson.internal.bind;

import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonToken;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonIOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.BitSet;

public class TypeAdaptersTest {

    @Test
    @DisplayName("BitSet adapter reads valid values")
    void testBitSetReadValid() throws Exception {
        String json = "[0,1,true,false,1]"; // indices 0-4
        BitSet expected = new BitSet();
        expected.set(1);
        expected.set(2);
        expected.set(4);
        JsonReader reader = new JsonReader(new StringReader(json));
        BitSet result = TypeAdapters.BIT_SET.read(reader);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("BitSet adapter throws on invalid integer")
    void testBitSetReadInvalidInt() {
        String json = "[2]"; // not 0 or 1
        JsonReader reader = new JsonReader(new StringReader(json));
        assertThrows(JsonSyntaxException.class, () -> TypeAdapters.BIT_SET.read(reader));
    }

    @Test
    @DisplayName("BitSet adapter throws on invalid type")
    void testBitSetReadInvalidType() {
        String json = "[null]"; // invalid type
        JsonReader reader = new JsonReader(new StringReader(json));
        assertThrows(JsonSyntaxException.class, () -> TypeAdapters.BIT_SET.read(reader));
    }

    @Test
    @DisplayName("BitSet adapter writes correctly")
    void testBitSetWrite() throws Exception {
        BitSet bitset = new BitSet();
        bitset.set(0);
        bitset.set(3);
        StringWriter writerString = new StringWriter();
        JsonWriter writer = new JsonWriter(writerString);
        TypeAdapters.BIT_SET.write(writer, bitset);
        writer.flush();
        assertEquals("[1,0,0,1]", writerString.toString());
    }

    @Test
    @DisplayName("Boolean adapter handles null, string, and boolean values")
    void testBooleanAdapter() throws Exception {
        // null
        JsonReader nullReader = new JsonReader(new StringReader("null"));
        assertNull(TypeAdapters.BOOLEAN.read(nullReader));
        // string true
        JsonReader stringTrue = new JsonReader(new StringReader("\"true\""));
        assertTrue(TypeAdapters.BOOLEAN.read(stringTrue));
        // string false
        JsonReader stringFalse = new JsonReader(new StringReader("\"false\""));
        assertFalse(TypeAdapters.BOOLEAN.read(stringFalse));
        // boolean true
        JsonReader boolTrue = new JsonReader(new StringReader("true"));
        assertTrue(TypeAdapters.BOOLEAN.read(boolTrue));
        // boolean false
        JsonReader boolFalse = new JsonReader(new StringReader("false"));
        assertFalse(TypeAdapters.BOOLEAN.read(boolFalse));
    }

    @Test
    @DisplayName("Byte adapter respects bounds and throws on overflow")
    void testByteAdapter() throws IOException {
        // null
        JsonReader nullReader = new JsonReader(new StringReader("null"));
        assertNull(TypeAdapters.BYTE.read(nullReader));
        // valid byte 0
        JsonReader zeroReader = new JsonReader(new StringReader("0"));
        assertEquals((byte)0, TypeAdapters.BYTE.read(zeroReader));
        // valid byte 255
        JsonReader maxReader = new JsonReader(new StringReader("255"));
        assertEquals((byte)255, TypeAdapters.BYTE.read(maxReader));
        // overflow >255
        JsonReader overflowReader = new JsonReader(new StringReader("256"));
        assertThrows(JsonSyntaxException.class, () -> TypeAdapters.BYTE.read(overflowReader));
        // underflow <Byte.MIN_VALUE
        JsonReader underflowReader = new JsonReader(new StringReader("-129"));
        assertThrows(JsonSyntaxException.class, () -> TypeAdapters.BYTE.read(underflowReader));
    }
}
