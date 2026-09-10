package com.google.gson.internal.bind;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonToken;
import com.google.gson.internal.bind.JsonTreeReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link JsonTreeReader} covering decision points.
 */
class JsonTreeReaderTest {
    JsonTreeReader reader;

    @BeforeEach
    void setUp() {
        // default reader will be reinitialized in each test that needs it
    }

    @Test
    @DisplayName("peek detects JsonObject, JsonArray, JsonPrimitive, JsonNull, and custom element")
    void peekBranches() throws IOException {
        JsonObject obj = new JsonObject();
        JsonTreeReader readerObj = new JsonTreeReader(obj);
        assertEquals(JsonToken.BEGIN_OBJECT, readerObj.peek());

        JsonTreeReader readerOut = new JsonTreeReader(obj);
        readerOut.beginObject();
        // after beginning object, expecting NAME
        assertNotNull(readerOut.peek());

        JsonTreeReader readerArray = new JsonTreeReader(new JsonArray());
        assertEquals(JsonToken.BEGIN_ARRAY, readerArray.peek());

        JsonPrimitive strPrim = new JsonPrimitive("hello");
        JsonTreeReader readerStr = new JsonTreeReader(strPrim);
        assertEquals(JsonToken.STRING, readerStr.peek());

        JsonPrimitive boolPrim = new JsonPrimitive(true);
        JsonTreeReader readerBool = new JsonTreeReader(boolPrim);
        assertEquals(JsonToken.BOOLEAN, readerBool.peek());

        JsonPrimitive numPrim = new JsonPrimitive(42);
        JsonTreeReader readerNum = new JsonTreeReader(numPrim);
        assertEquals(JsonToken.NUMBER, readerNum.peek());

        JsonTreeReader readerNull = new JsonTreeReader(JsonNull.INSTANCE);
        assertEquals(JsonToken.NULL, readerNull.peek());

        // Test custom unsupported subclass
        class CustomElement extends JsonElement {

            @Override
            public JsonElement deepCopy() {
                return null;
            }
        }
        JsonTreeReader readerCustom = new JsonTreeReader(new CustomElement());
        IOException thrown = assertThrows(IOException.class, readerCustom::peek);
        assertTrue(thrown.getMessage().contains("is not supported"));
    }

    @Test
    @DisplayName("nextString handles STRING and NUMBER tokens")
    void nextStringNumber() throws IOException {
        JsonPrimitive strPrim = new JsonPrimitive("abc");
        JsonTreeReader reader = new JsonTreeReader(strPrim);
        assertEquals("abc", reader.nextString());
        assertEquals(JsonToken.END_DOCUMENT, reader.peek());

        JsonPrimitive numPrim = new JsonPrimitive(123);
        JsonTreeReader readerNum = new JsonTreeReader(numPrim);
        assertEquals("123", readerNum.nextString());
        assertEquals(JsonToken.END_DOCUMENT, readerNum.peek());
    }

    @Test
    @DisplayName("nextBoolean reads BOOLEAN token")
    void nextBooleanTest() throws IOException {
        JsonTreeReader reader = new JsonTreeReader(new JsonPrimitive(true));
        assertTrue(reader.nextBoolean());
    }

    @Test
    @DisplayName("nextNull reads NULL token")
    void nextNullTest() throws IOException {
        JsonTreeReader reader = new JsonTreeReader(JsonNull.INSTANCE);
        reader.nextNull(); // should not throw
    }

    @Test
    @DisplayName("nextDouble accepts NUMBER and STRING, rejects others")
    void nextDoubleTest() throws IOException {
        JsonTreeReader rNum = new JsonTreeReader(new JsonPrimitive(1.23));
        assertEquals(1.23, rNum.nextDouble());

        JsonTreeReader rStr = new JsonTreeReader(new JsonPrimitive("4.56"));
        assertEquals(4.56, rStr.nextDouble());

        JsonTreeReader rWrong = new JsonTreeReader(new JsonPrimitive("no-number"));
        assertThrows(NumberFormatException.class, rWrong::nextDouble);
    }

    @Test
    @DisplayName("nextLong accepts NUMBER and STRING with ascii, rejects others")
    void nextLongTest() throws IOException {
        JsonTreeReader rNum = new JsonTreeReader(new JsonPrimitive(987L));
        assertEquals(987L, rNum.nextLong());

        JsonTreeReader rStr = new JsonTreeReader(new JsonPrimitive("42"));
        assertEquals(42L, rStr.nextLong());

        JsonTreeReader rNonAscii = new JsonTreeReader(new JsonPrimitive("café"));
        assertThrows(IOException.class, rNonAscii::nextLong);
    }

    @Test
    @DisplayName("nextInt accepts NUMBER and STRING with ascii, rejects others")
    void nextIntTest() throws IOException {
        JsonTreeReader rNum = new JsonTreeReader(new JsonPrimitive(1234));
        assertEquals(1234, rNum.nextInt());

        JsonTreeReader rStr = new JsonTreeReader(new JsonPrimitive("567"));
        assertEquals(567, rStr.nextInt());

        JsonTreeReader rNonAscii = new JsonTreeReader(new JsonPrimitive("é"));
        assertThrows(IOException.class, rNonAscii::nextInt);
    }

    @Test
    @DisplayName("skipValue handles NAME, END_ARRAY, END_OBJECT, default cases")
    void skipValueTest() throws IOException {
        JsonObject obj = new JsonObject();
        obj.addProperty("x", 1);
        JsonTreeReader r = new JsonTreeReader(obj);
        r.beginObject();
        // skip the name and its value
        r.skipValue();
        r.skipValue();
        assertEquals(JsonToken.END_OBJECT, r.peek());
        // after skipping we are at END_OBJECT; now skip it
        r.skipValue();
        assertEquals(JsonToken.END_DOCUMENT, r.peek());

        JsonArray arr = new JsonArray();
        arr.add(1);
        JsonTreeReader rArr = new JsonTreeReader(arr);
        rArr.beginArray();
        rArr.skipValue(); // skip the element
        assertEquals(JsonToken.END_ARRAY, rArr.peek());
        rArr.skipValue();
        assertEquals(JsonToken.END_DOCUMENT, rArr.peek());
    }

    @Test
    @DisplayName("promoteNameToValue pushes key as JsonPrimitive")
    void promoteNameToValueTest() throws IOException {
        JsonObject obj = new JsonObject();
        obj.addProperty("key", "val");
        JsonTreeReader r = new JsonTreeReader(obj);
        r.beginObject();
        r.promoteNameToValue();
        // now top should be key string primitive, then value primitive
        assertEquals("key", r.nextString());
        assertEquals("val", r.nextString());
    }

    @Test
    @DisplayName("getPath reflects current stack location")
    void getPathTest() throws IOException {
        JsonObject root = new JsonObject();
        JsonObject child = new JsonObject();
        child.addProperty("leaf", 10);
        root.add("child", child);
        JsonTreeReader r = new JsonTreeReader(root);
        r.beginObject(); // root object
        assertEquals("$.", r.getPath());
        r.nextName(); // should set name for child
        r.beginObject(); // child object
        assertEquals("$.child.", r.getPath());
        r.nextName(); // leaf
        assertEquals("$.child.leaf", r.getPath());
        r.nextInt(); // read leaf value
        assertEquals("$.child.leaf", r.getPath());
    }

    @Test
    @DisplayName("isAllAscii correctly detects non‑ASCII characters")
    void isAllAsciiTest() {
        assertTrue(JsonTreeReader.isAllAscii("abc"));
        assertFalse(JsonTreeReader.isAllAscii("cafè"));
    }
}
