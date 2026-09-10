package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

class TypeAdapterRuntimeTypeWrapperTest {

    @Test
    void read_delegatesToDelegate() throws IOException {
        Gson gson = new Gson();
        TypeAdapter<String> delegate = new TypeAdapter<String>() {
            @Override
            public void write(JsonWriter out, String value) throws IOException {
                out.value(value);
            }

            @Override
            public String read(JsonReader in) throws IOException {
                return in.nextString();
            }
        };
        TypeAdapter<String> wrapper = new TypeAdapterRuntimeTypeWrapper<>(gson, delegate, String.class);
        JsonReader reader = new JsonReader(new StringReader("\"hello\""));
        assertEquals("hello", wrapper.read(reader));
    }

    @Test
    void write_delegatesToDelegate() throws IOException {
        Gson gson = new Gson();
        TypeAdapter<String> delegate = new TypeAdapter<String>() {
            @Override
            public void write(JsonWriter out, String value) throws IOException {
                out.value(value);
            }

            @Override
            public String read(JsonReader in) throws IOException {
                return in.nextString();
            }
        };
        TypeAdapter<String> wrapper = new TypeAdapterRuntimeTypeWrapper<>(gson, delegate, String.class);
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        wrapper.write(jsonWriter, "hello");
        assertEquals("\"hello\"", stringWriter.toString());
    }
}
