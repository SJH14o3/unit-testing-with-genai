package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.ObjectConstructor;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CollectionTypeAdapterFactoryTest {

    private static class TestConstructorConstructor extends ConstructorConstructor {
        private final ObjectConstructor<?> constructor;

        TestConstructorConstructor(ObjectConstructor<?> constructor) {
            this.constructor = constructor;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> ObjectConstructor<T> get(TypeToken<T> typeToken, boolean allowUnsafe) {
            return (ObjectConstructor<T>) constructor;
        }
    }

    private static class TestObjectConstructor<T> implements ObjectConstructor<T> {
        private final T instance;

        TestObjectConstructor(T instance) {
            this.instance = instance;
        }

        @Override
        public T construct() {
            return instance;
        }
    }

    @Test
    void testCreate_NonCollection_ReturnsNull() {
        ObjectConstructor<List<String>> constructor = new TestObjectConstructor<>(new ArrayList<>());
        ConstructorConstructor constructorConstructor = new TestConstructorConstructor(constructor);
        CollectionTypeAdapterFactory factory = new CollectionTypeAdapterFactory(constructorConstructor);
        Gson gson = new GsonBuilder().create();
        
        TypeToken<String> typeToken = TypeToken.get(String.class);
        assertNull(factory.create(gson, typeToken));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testAdapter_Read_NullToken() {
        ObjectConstructor<List<String>> constructor = new TestObjectConstructor<>(new ArrayList<>());
        ConstructorConstructor constructorConstructor = new TestConstructorConstructor(constructor);
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new CollectionTypeAdapterFactory(constructorConstructor))
                .create();

        TypeToken<List<String>> typeToken = TypeToken.get(new TypeToken<List<String>>() {}.getType());

        String json = "null";
        List<String> result = gson.fromJson(new StringReader(json), typeToken.getType());
        assertNull(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testAdapter_Read_EmptyArray() {
        ObjectConstructor<List<String>> constructor = new TestObjectConstructor<>(new ArrayList<>());
        ConstructorConstructor constructorConstructor = new TestConstructorConstructor(constructor);
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new CollectionTypeAdapterFactory(constructorConstructor))
                .create();

        TypeToken<List<String>> typeToken = TypeToken.get(new TypeToken<List<String>>() {}.getType());

        String json = "[]";
        List<String> result = gson.fromJson(new StringReader(json), typeToken.getType());
        assertTrue(result.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testAdapter_Read_PopulatedArray() {
        Object
        Constructor<List<String>> constructor = new TestObjectConstructor<>(new ArrayList<>());
        ConstructorConstructor constructorConstructor = new TestConstructorConstructor(constructor);
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new CollectionTypeAdapterFactory(constructorConstructor))
                .create();

        TypeToken<List<String>> typeToken = TypeToken.get(new TypeToken<List<String>>() {}.getType());

        String json = "[\"a\",\"b\"]";
        List<String> result = gson.fromJson(new StringReader(json), typeToken.getType());
        assertEquals(List.of("a", "b"), result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testAdapter_Write_NullCollection() {
        ObjectConstructor<List<String>> constructor = new TestObjectConstructor<>(new ArrayList<>());
        ConstructorConstructor constructorConstructor = new TestConstructorConstructor(constructor);
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new CollectionTypeAdapterFactory(constructorConstructor))
                .create();

        TypeToken<List<String>> typeToken = TypeToken.get(new TypeToken<List<String>>() {}.getType());

        StringWriter sw = new StringWriter();
        gson.toJson(null, typeToken, sw);
        assertEquals("null", sw.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testAdapter_Write_PopulatedCollection() {
        ObjectConstructor<List<String>> constructor = new TestObjectConstructor<>(new ArrayList<>());
        ConstructorConstructor constructorConstructor = new TestConstructorConstructor(constructor);
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new CollectionTypeAdapterFactory(constructorConstructor))
                .create();

        TypeToken<List<String>> typeToken = TypeToken.get(new TypeToken<List<String>>() {}.getType());

        StringWriter sw = new StringWriter();
        gson.toJson(List.of("x", "y"), typeToken, sw);
        assertEquals("[\"x\",\"y\"]", sw.toString());
    }
}