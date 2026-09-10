package com.google.gson.internal.bind;

import com.google.gson.*;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.stream.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;

class NumberTypeAdapterTest {

    @Test
    void readNumber_fromJson() throws IOException {
        TypeAdapter<Number> adapter = new Gson().getAdapter(Number.class);
        String json = "123";
        JsonReader reader = new JsonReader(new StringReader(json));
        Number n = adapter.read(reader);
        assertEquals(123, n.intValue());
    }

    @Test
    void writeNumber_toJson() throws IOException {
        TypeAdapter<Number> adapter = new Gson().getAdapter(Number.class);
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        adapter.write(writer, 12.34);
        assertEquals("12.34", sw.toString());
    }
}
