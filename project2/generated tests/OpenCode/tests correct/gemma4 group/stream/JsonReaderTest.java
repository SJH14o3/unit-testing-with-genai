package com.google.gson.stream;

import com.google.gson.Strictness;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class JsonReaderTest {

    @Test
    void testReadingVariousTokens() throws IOException {
        String json = "{\"string\":\"val\",\"number\":123,\"boolean\":true,\"null\":null}";
        JsonReader reader = new JsonReader(new StringReader(json));

        reader.beginObject();
        assertEquals("string", reader.nextName());
        assertEquals("val", reader.nextString());
        assertEquals("number", reader.nextName());
        assertEquals(123, reader.nextLong());
        assertEquals("boolean", reader.nextName());
        assertTrue(reader.nextBoolean());
        assertEquals("null", reader.nextName());
        reader.nextNull();
        reader.endObject();
        assertFalse(reader.hasNext());
    }

    @Test
    void testLenientReading() throws IOException {
        String json = "[1; 2; 3]"; // Using ';' instead of ','
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.setStrictness(Strictness.LENIENT);

        reader.beginArray();
        assertEquals(1, reader.nextLong());
        assertEquals(2, reader.nextLong());
        assertEquals(3, reader.nextLong());
        reader.endArray();
        reader.close();
    }

    @Test
    void testMalformedJson() {
        String json = "{\"key\": \"unclosed value";
        JsonReader reader = new JsonReader(new StringReader(json));

        assertThrows(IOException.class, () -> {
            reader.beginObject();
            reader.nextName();
            reader.nextString();
        });
    }
}