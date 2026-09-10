package com.google.gson.internal.bind;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.junit.jupiter.api.Assertions.*;

class JsonTreeReaderTest {

    @Test
    void testReadFromJsonElement() throws IOException {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", "test");
        obj.add("arr", new JsonArray());
        obj.add("nullVal", JsonNull.INSTANCE);

        JsonTreeReader reader = new JsonTreeReader(obj);

        reader.beginObject();
        assertEquals("name", reader.nextName());
        assertEquals("test", reader.nextString());
        assertEquals("arr", reader.nextName());
        reader.beginArray();
        reader.endArray();
        assertEquals("nullVal", reader.nextName());
        reader.nextNull();
        reader.endObject();
    }

    @Test
    void testReadFromArrayElement() throws IOException {
        JsonArray arr = new JsonArray();
        arr.add(1);
        arr.add(2);

        JsonTreeReader reader = new JsonTreeReader(arr);

        reader.beginArray();
        assertEquals(1, reader.nextLong());
        assertEquals(2, reader.nextLong());
        reader.endArray();
    }
}