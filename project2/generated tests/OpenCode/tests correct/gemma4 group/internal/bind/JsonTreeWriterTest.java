package com.google.gson.internal.bind;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class JsonTreeWriterTest {

    @Test
    void testWriteToJsonElement() throws IOException {
        JsonTreeWriter writer = new JsonTreeWriter();
        writer.beginObject();
        writer.name("primitive").value("hello");
        writer.name("array").beginArray();
        writer.value(1);
        writer.value(2);
        writer.endArray();
        writer.endObject();

        JsonElement result = writer.get();
        writer.close();
        assertTrue(result.isJsonObject());
        JsonObject obj = result.getAsJsonObject();
        assertEquals("hello", obj.get("primitive").getAsString());
        assertEquals(2, obj.getAsJsonArray("array").size());
    }
}