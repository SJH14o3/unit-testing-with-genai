package com.google.gson.internal.bind;

import com.google.gson.*;
import com.google.gson.stream.*;
import org.junit.jupiter.api.*;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

class JsonElementTypeAdapterTest {

    @Test
    void readAndWriteSimple() throws IOException {
        JsonElementTypeAdapter adapter = JsonElementTypeAdapter.ADAPTER;
        JsonElement element = adapter.read(new JsonReader(new StringReader("{\"a\":1}")));
        assertTrue(element.getAsJsonObject().has("a"));
        StringWriter sw = new StringWriter();
        adapter.write(new JsonWriter(sw), element);
        assertEquals("{\"a\":1}", sw.toString());
    }
}
