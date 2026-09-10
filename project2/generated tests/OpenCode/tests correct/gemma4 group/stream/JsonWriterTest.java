package com.google.gson.stream;

import com.google.gson.FormattingStyle;
import com.google.gson.Strictness;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

class JsonWriterTest {

    @Test
    void testValueTypes() throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        jsonWriter.beginObject();
        jsonWriter.name("string").value("hello");
        jsonWriter.name("boolean").value(true);
        jsonWriter.name("number").value(123);
        jsonWriter.name("nullValue").nullValue();
        jsonWriter.endObject();
        jsonWriter.close();

        String expected = "{\"string\":\"hello\",\"boolean\":true,\"number\":123,\"nullValue\":null}";
        assertEquals(expected, writer.toString());
    }

    @Test
    void testFormattingStyles() throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        jsonWriter.setFormattingStyle(FormattingStyle.PRETTY.withIndent("  "));
        jsonWriter.beginObject();
        jsonWriter.name("key").value("value");
        jsonWriter.endObject();
        jsonWriter.close();

        String expected = "{\n  \"key\": \"value\"\n}";
        assertEquals(expected, writer.toString());
    }

    @Test
    void testStrictness() throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        jsonWriter.setStrictness(Strictness.LENIENT);
        jsonWriter.beginArray();
        jsonWriter.value(Double.NaN);
        jsonWriter.endArray();
        jsonWriter.close();

        // This should throw because NaN is not allowed in STRICT mode
        assertThrows(IllegalArgumentException.class, () -> {
            JsonWriter writer2 = new JsonWriter(new StringWriter());
            writer2.setStrictness(Strictness.STRICT);
            writer2.beginArray();
            writer2.value(Double.NaN);
            writer2.endArray();
        });
    }

    @Test
    void testNestingProblem() throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
//        jsonWriter.beginObject();
//        jsonWriter.beginArray();
//        jsonWriter.endArray();
//        jsonWriter.endObject(); // Error: expected endArray
//        jsonWriter.close();

        assertThrows(IllegalStateException.class, () -> {
            JsonWriter w = new JsonWriter(new StringWriter());
            w.beginObject();
            w.beginArray();
            w.endObject();
        });
    }
}