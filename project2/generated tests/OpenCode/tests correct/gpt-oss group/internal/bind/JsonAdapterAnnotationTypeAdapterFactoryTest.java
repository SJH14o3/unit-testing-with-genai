package com.google.gson.internal.bind;

import com.google.gson.*;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.*;
import org.junit.jupiter.api.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JsonAdapterAnnotationTypeAdapterFactoryTest {

    static class TestClass {
        @com.google.gson.annotations.JsonAdapter(MyAdapter.class)
        int value;
    }

    static class MyAdapter extends TypeAdapter<Integer> {
        @Override
        public void write(JsonWriter out, Integer v) throws IOException { out.value(v); }
        @Override
        public Integer read(JsonReader in) throws IOException { return in.nextInt(); }
    }

    @Test
    void factoryProvidesAdapter() {
        ConstructorConstructor cc = new ConstructorConstructor(Collections.emptyMap(), false, Collections.emptyList());
        JsonAdapterAnnotationTypeAdapterFactory factory = new JsonAdapterAnnotationTypeAdapterFactory(cc);
        Gson gson = new Gson();
        TypeAdapter<?> adapter = factory.create(gson, TypeToken.get(TestClass.class));
        assertNull(adapter);
    }
}
