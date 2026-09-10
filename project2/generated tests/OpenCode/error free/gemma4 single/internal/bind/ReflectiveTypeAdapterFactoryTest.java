package com.google.gson.internal.bind;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.ReflectionAccessFilter;
import com.google.gson.ReflectionAccessFilter.FilterResult;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;

@DisplayName("ReflectiveTypeAdapterFactory Test")
class ReflectiveTypeAdapterFactoryTest {

    private ConstructorConstructor constructorConstructor;
    private FieldNamingStrategy fieldNamingPolicy;
    private Excluder excluder;
    private List<ReflectionAccessFilter> reflectionFilters;
    private ReflectiveTypeAdapterFactory factory;
    private Gson gson;

    static class TestClass {
        String name;
        int value;
        static final String STATIC_FIELD = "static";

        public TestClass() {}
    }

    static class AnnotatedClass {
        @com.google.gson.annotations.SerializedName("converted_name")
        String name;
        @com.google.gson.annotations.SerializedName(value = "alternate", alternate = {"alt1", "alt2"})
        String alternateName;

        public AnnotatedClass() {}
    }

    static class InterfaceTest {
        String value;
    }

    static class DuplicateFieldClass {
        String field1;
        @com.google.gson.annotations.SerializedName("field1")
        String field2;

        public DuplicateFieldClass() {}
    }

    @BeforeEach
    void setUp() {
        constructorConstructor = new ConstructorConstructor(Collections.emptyMap(), false, Collections.emptyList());
        fieldNamingPolicy = new FieldNamingStrategy() {
            @Override
            public String translateName(Field f) {
                return f.getName();
            }
            @Override
            public List<String> alternateNames(Field f) {
                return Collections.emptyList();
            }
        };
        excluder = new Excluder();
        reflectionFilters = Collections.emptyList();

        factory = new ReflectiveTypeAdapterFactory(
                constructorConstructor,
                fieldNamingPolicy,
                excluder,
                null,
                reflectionFilters
        );

        gson = new Gson();
    }

    @Test
    @DisplayName("Should return null for primitive types (not Object.class)")
    void testPrimitiveTypes() {
        TypeToken<Integer> typeToken = TypeToken.get(Integer.class);
        assertNull(factory.create(gson, typeToken));
    }

    @Test
    @DisplayName("Should handle SerializedName annotation")
    void testSerializedName() {
        TypeToken<AnnotatedClass> typeToken = TypeToken.get(AnnotatedClass.class);
        String json = gson.toJson(new AnnotatedClass());
        assertTrue(json.contains("\"converted_name\""));
    }

    @Test
    @DisplayName("Should handle duplicate field names and throw exception")
    void testDuplicateFields() {
        TypeToken<DuplicateFieldClass> typeToken = TypeToken.get(DuplicateFieldClass.class);
        assertThrows(IllegalArgumentException.class, () -> {
            factory.create(gson, typeToken);
        });
    }

    @Test
    @DisplayName("Should handle interfaces by returning empty FieldsData (no adapters)")
    void testInterface() {
        TypeToken<InterfaceTest> typeToken = TypeToken.get(InterfaceTest.class);
        TypeAdapter<InterfaceTest> adapter = factory.create(gson, typeToken);
        assertNotNull(adapter);
    }
}
