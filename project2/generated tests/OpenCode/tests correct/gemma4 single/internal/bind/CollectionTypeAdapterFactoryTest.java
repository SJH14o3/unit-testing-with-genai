package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.reflect.TypeToken;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.ObjectConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CollectionTypeAdapterFactoryTest {

    private ConstructorConstructor constructorConstructor;
    private TypeToken<List<String>> tt;

    @BeforeEach
    void setUp() {
        Map<Type, InstanceCreator<?>> creators = new HashMap<>();
        Type type = new TypeToken<String>(){}.getType();
        creators.put(type, (t) -> "");
        constructorConstructor = new ConstructorConstructor(creators, false, Collections.emptyList());

        tt = (TypeToken<List<String>>) TypeToken.getParameterized(List.class, String.class);
    }

    @Test
    void testCreate_NonCollection_ReturnsNull() {
        CollectionTypeAdapterFactory factory = new CollectionTypeAdapterFactory(constructorConstructor);
        Gson gson = new GsonBuilder().create();

        TypeToken<String> typeToken = TypeToken.get(String.class);
        assertNull(factory.create(gson, typeToken));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testAdapter_Read_NullToken() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new CollectionTypeAdapterFactory(constructorConstructor))
                .create();

        String json = "null";
        List<String> result = gson.fromJson(new StringReader(json), tt);
        assertNull(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testAdapter_Read_EmptyArray() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new CollectionTypeAdapterFactory(constructorConstructor))
                .create();

        String json = "[]";
        List<String> result = gson.fromJson(new StringReader(json), tt);
        assertTrue(result.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testAdapter_Read_PopulatedArray() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new CollectionTypeAdapterFactory(constructorConstructor))
                .create();

        String json = "[\"a\",\"b\"]";
        List<String> result = gson.fromJson(new StringReader(json), tt);
        assertEquals(List.of("a", "b"), result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testAdapter_Write_NullCollection() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new CollectionTypeAdapterFactory(constructorConstructor))
                .create();

        StringWriter sw = new StringWriter();
        gson.toJson(null, tt.getType(), sw);
        assertEquals("null", sw.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testAdapter_Write_PopulatedCollection() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new CollectionTypeAdapterFactory(constructorConstructor))
                .create();

        StringWriter sw = new StringWriter();
        gson.toJson(List.of("x", "y"), tt.getType(), sw);
        assertEquals("[\"x\",\"y\"]", sw.toString());
    }
}