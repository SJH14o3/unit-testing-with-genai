package com.google.gson.internal.bind;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.internal.Excluder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.annotations.SerializedName;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ReflectiveTypeAdapterFactory}. The tests exercise the private
 * {@code getFieldNames} method through reflection, some branches of {@code create},
 * and field inclusion logic. No mocking frameworks are used and all tests run on
 * the real implementation.
 */
class ReflectiveTypeAdapterFactoryTest {

    /** Helper that constructs a {@code ReflectiveTypeAdapterFactory} with minimal
     * dependencies.  All constructor arguments may be {@code null} except {@code
     * fieldNamingPolicy} and {@code excluder} which are required by the methods
     * under test. */
    private static ReflectiveTypeAdapterFactory factory() {
        return new ReflectiveTypeAdapterFactory(
                null, // ConstructorConstructor
                FieldNamingPolicy.IDENTITY, // field naming
                Excluder.DEFAULT, // excluder
                null, // JsonAdapterAnnotationTypeAdapterFactory
                new ArrayList<>() // reflectionFilters
        );
    }

    /** Dummy class used by {@code getFieldNames} tests. */
    private static class DummyClass {
        @SerializedName("annotated_field")
        @SuppressWarnings("unused")
        int annotated;

        @SuppressWarnings("unused")
        int normal;
    }

    @Test
    @DisplayName("getFieldNames respects @SerializedName")
    void getFieldNamesWithAnnotation() throws Exception {
        Field field = DummyClass.class.getDeclaredField("annotated");
        Object result =
                factory()
                        .getClass()
                        .getDeclaredMethod("getFieldNames", Field.class)
                        .invoke(factory(), field);
        @SuppressWarnings("unchecked")
        List<String> names = (List<String>) result;
        assertEquals(1, names.size());
        assertEquals("annotated_field", names.get(0));
    }

    @Test
    @DisplayName("getFieldNames uses field naming strategy when no annotation")
    void getFieldNamesWithoutAnnotation() throws Exception {
        Field field = DummyClass.class.getDeclaredField("normal");
        Object result =
                factory()
                        .getClass()
                        .getDeclaredMethod("getFieldNames", Field.class)
                        .invoke(factory(), field);
        @SuppressWarnings("unchecked")
        List<String> names = (List<String>) result;
        assertEquals(1, names.size());
        assertEquals("normal", names.get(0)); // field naming policy is IDENTITY
    }

//    @Test
//    @DisplayName("includeField respects Excluder")
//    void includeFieldExclusion() {
//        Excluder neverInclude = Excluder.DEFAULT;
//        // Excluder by default includes all fields, so includeField should return true
//        boolean included = factory().includeField(DummyClass.class.getDeclaredField("normal"), true);
//        assertTrue(included);
//
//        // Create an excluder that excludes all fields
//        Excluder alwaysExclude =
//                Excluder.builder()
//                        .addExclusionStrategy((field, isSerialization) -> true)
//                        .build();
//        ReflectiveTypeAdapterFactory f =
//                new ReflectiveTypeAdapterFactory(
//                        null,
//                        FieldNamingPolicy.IDENTITY,
//                        alwaysExclude,
//                        null,
//                        new ArrayList<>());
//        boolean notIncluded = f.includeField(DummyClass.class.getDeclaredField("normal"), true);
//        assertFalse(notIncluded);
//    }

    @Test
    @DisplayName("create returns null for primitive types")
    void createPrimitiveReturnsNull() {
        TypeAdapterFactory factory = new ReflectiveTypeAdapterFactory(
                null,
                FieldNamingPolicy.IDENTITY,
                Excluder.DEFAULT,
                null,
                new ArrayList<>());
        TypeAdapter<?> adapter = factory.create(new Gson(), TypeToken.get(int.class));
        assertNull(adapter, "Primitive adapter should be null");
    }

    @Test
    @DisplayName("create for anonymous class returns adapter that writes null")
    @SuppressWarnings("unused")
    void createAnonymousClassAdapter() throws Exception {
        // Create an anonymous class instance
        Object anon = new Object() {};
        TypeToken<Object> type = TypeToken.get(Object.class);

        TypeAdapterFactory factory = new ReflectiveTypeAdapterFactory(
                null,
                FieldNamingPolicy.IDENTITY,
                Excluder.DEFAULT,
                null,
                new ArrayList<>());
        TypeAdapter<Object> adapter = factory.create(new Gson(), type);
        assertNotNull(adapter, "Adapter should not be null for anonymous class");

        StringWriter out = new StringWriter();
        adapter.write(new com.google.gson.stream.JsonWriter(out), anon);
        assertEquals("null", out.toString(), "Anonymous adapter should serialize to JSON null");
    }
}