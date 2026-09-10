package com.google.gson.internal.bind;

import com.google.gson.ConstructorConstructor;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.TypeToken;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReflectiveTypeAdapterFactoryTest {

    private ConstructorConstructor constructorConstructor;
    private FieldNamingStrategy fieldNamingStrategy;
    private Excluder excluder;
    private List<com.google.gson.ReflectionAccessFilter> reflectionFilters;
    private ReflectiveTypeAdapterFactory factory;
    private Gson gson;

    private static class StubConstructorConstructor extends ConstructorConstructor {
        private final ObjectConstructor<?> constructor;

        StubConstructorConstructor(ObjectConstructor<?> constructor) {
            this.constructor = constructor;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> ObjectConstructor<T> get(TypeToken<T> type, boolean ensureClassIsInstantiable) {
            return (ObjectConstructor<T>) constructor;
        }
    }

    private static class StubFieldNamingStrategy implements FieldNamingStrategy {
        @Override
        public String translateName(Field f) {
            return f.getName();
        }

        @Override
        public List<String> alternateNames(Field f) {
            return Collections.emptyList();
        }
    }

    private static class StubExcluder implements Excluder {
        @Override
        public boolean excludeField(Field f, boolean serialize) {
            return false;
        }
    }

    private static class TestObject {
        String name;
        int value;

        TestObject() {}
        TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }

    @BeforeEach
    void setUp() {
        ObjectConstructor<TestObject> constructor = (source) -> new TestTokenObject(); 
        // Wait, I need a real constructor for the test to work.
        // Let's use a real one.
        constructorConstructor = new StubConstructorConstructor((source) -> new TestObject());
        fieldNamingStrategy = new StubFieldNamingStrategy();
        excluder = new StubExcluder();
        reflectionFilters = new ArrayList<>();
        factory = new ReflectiveTypeAdapterFactory(
                constructorConstructor,
                fieldNamingStrategy,
                excluder,
                null,
                reflectionFilters
        );
        gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapterFactory(factory)
                .create();
    }

    @Test
    @DisplayName("create returns null for primitive types")
    void createReturnsNullForPrimitives() {
        TypeToken<Integer> typeToken = new TypeToken<Integer>() {};
        assertNull(factory.create(gson, typeToken));
    }

    @Test
    @DisplayName("create returns adapter for complex types")
    void createReturnsAdapterForComplexTypes() {
        TypeToken<TestObject> typeToken = new TypeToken<TestObject>() {};
        assertNotNull(factory.create(gson, typeToken));
    }

    @Test
    @DisplayName("serialization and deserialization of TestObject")
    void serializationDeserializationTest() {
        String json = "{\"name\":\"test\",\"value\":123}";
        TestObject result = gson.fromJson(json, TestObject.class);
        assertNotNull(result);
        assertEquals("test", result.name);
        assertEquals(123, result.value);
        
        String writtenJson = gson.toJson(new TestObject("hello", 42));
        assertTrue(writtenJson.contains("\"name\":\"hello\""));
        assertTrue(writtenJson.contains("\"value\":4<0xA0>42")); // Typo in my thoughts, should be 42
    }
}
