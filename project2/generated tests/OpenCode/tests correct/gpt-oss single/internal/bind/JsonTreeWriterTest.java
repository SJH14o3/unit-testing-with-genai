package com.google.gson.internal.bind;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JsonTreeWriter.
 * Focuses on decision coverage for methods that modify the internal stack and
 * produce JsonElements.
 */
public class JsonTreeWriterTest {

    @Test
    @DisplayName("get() returns initial JsonNull and fails when stack not empty")
    void testGetInitialAndError() throws Exception {
        JsonTreeWriter writer = new JsonTreeWriter();
        assertEquals(JsonNull.INSTANCE, writer.get());

        // Begin an array without closing it and expect get() to throw
        writer.beginArray();
        assertThrows(IllegalStateException.class, writer::get);
    }

    @Test
    @DisplayName("beginArray() and endArray() build an empty array correctly")
    void testArrayCreation() throws Exception {
        JsonTreeWriter writer = new JsonTreeWriter();
        writer.beginArray();
        writer.endArray();
        JsonElement element = writer.get();
        assertTrue(element instanceof JsonArray);
        assertEquals(0, ((JsonArray) element).size());
    }

    @Test
    @DisplayName("endArray() throws when stack is empty or pending a name")
    void testEndArrayErrors() throws Exception {
        JsonTreeWriter writer = new JsonTreeWriter();
        assertThrows(IllegalStateException.class, writer::endArray);

        writer.beginObject();
        writer.name("a");
        writer.value(1);
        assertThrows(IllegalStateException.class, writer::endArray);
    }

    @Test
    @DisplayName("beginObject() and endObject() build an empty object correctly")
    void testObjectCreation() throws Exception {
        JsonTreeWriter writer = new JsonTreeWriter();
        writer.beginObject();
        writer.endObject();
        JsonElement element = writer.get();
        assertTrue(element instanceof JsonObject);
        assertEquals(0, ((JsonObject) element).size());
    }

    @Test
    @DisplayName("name() enforces position and type constraints")
    void testNameConstraints() throws Exception {
        JsonTreeWriter writer = new JsonTreeWriter();
        writer.beginObject();
        writer.name("key");
        assertThrows(IllegalStateException.class, () -> writer.name("another"));

        // name outside object
        writer.beginArray();
        assertThrows(IllegalStateException.class, () -> writer.name("out"));
    }

    @Test
    @DisplayName("value(String) processes null and non‑null")
    void testValueString() throws Exception {
        JsonTreeWriter writer = new JsonTreeWriter();
        writer.beginObject();
        writer.name("foo").value((String) null);
        writer.endObject();
        JsonElement element = writer.get();
        assertEquals(JsonNull.INSTANCE, ((JsonObject) element).get("foo"));
    }

    @Test
    @DisplayName("value(boolean) and value(Boolean) correctly handle null")
    void testValueBoolean() throws Exception {
        JsonTreeWriter writer = new JsonTreeWriter();
        writer.beginObject();
        writer.name("flag").value(true);
        writer.name("maybe").value((Boolean) null);
        writer.endObject();
        JsonObject obj = (JsonObject) writer.get();
        assertTrue(((JsonPrimitive) obj.get("flag")).getAsBoolean());
        assertEquals(JsonNull.INSTANCE, obj.get("maybe"));
    }

    @Test
    @DisplayName("value(float) rejects NaN and Infinity unless lenient")
    void testValueFloat() throws Exception {
        JsonTreeWriter writer = new JsonTreeWriter();
        assertThrows(IllegalArgumentException.class, () -> writer.value(Float.NaN));
        assertThrows(IllegalArgumentException.class, () -> writer.value(Float.POSITIVE_INFINITY));

        writer.setLenient(true);
        writer.beginArray();
        writer.value(Float.NaN);
        writer.value(Float.POSITIVE_INFINITY);
        writer.endArray();
        JsonArray arr = (JsonArray) writer.get();
        assertEquals(2, arr.size());
    }

    @Test
    @DisplayName("value(double) rejects NaN and Infinity unless lenient")
    void testValueDouble() throws Exception {
        JsonTreeWriter writer = new JsonTreeWriter();
        assertThrows(IllegalArgumentException.class, () -> writer.value(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> writer.value(Double.POSITIVE_INFINITY));

        writer.setLenient(true);
        writer.beginArray();
        writer.value(Double.NaN);
        writer.value(Double.POSITIVE_INFINITY);
        writer.endArray();
        JsonArray arr = (JsonArray) writer.get();
        assertEquals(2, arr.size());
    }

    @Test
    @DisplayName("value(Number) validates NaN/Infinite according to lenient flag")
    void testValueNumber() throws Exception {
        JsonTreeWriter writer = new JsonTreeWriter();
        assertThrows(IllegalArgumentException.class, () -> writer.value(Double.NaN));

        writer.setLenient(true);
        writer.beginArray();
        writer.value(Double.NaN);
        writer.endArray();
        JsonArray arr = (JsonArray) writer.get();
        assertEquals(1, arr.size());
    }

    @Test
    @DisplayName("nullValue() puts JsonNull appropriately")
    void testNullValue() throws Exception {
        JsonTreeWriter writer = new JsonTreeWriter();
        writer.beginObject();
        writer.name("nothing");
        writer.nullValue();
        writer.endObject();
        JsonObject obj = (JsonObject) writer.get();
        assertEquals(JsonNull.INSTANCE, obj.get("nothing"));
    }

    @Test
    @DisplayName("close() throws IOException if stack not empty")
    void testCloseError() throws Exception {
        JsonTreeWriter writer = new JsonTreeWriter();
        writer.beginArray();
        assertThrows(IOException.class, writer::close);
    }

    @Test
    @DisplayName("Product is immutable after close() called")
    void testClosedProductImmutability() throws Exception {
        JsonTreeWriter writer = new JsonTreeWriter();
        writer.beginObject();
        writer.name("x").value(1);
        writer.endObject();
        JsonElement beforeClose = writer.get();
        // any further operations should fail
        writer.close();
        assertThrows(IllegalStateException.class, () -> writer.value("test"));
        assertThrows(IllegalStateException.class, writer::get);
    }
}
