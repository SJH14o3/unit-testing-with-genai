package com.google.gson;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Gson}.
 *
 * <p>These tests aim to provide high decision coverage for critical
 * public methods in {@link Gson}, focusing on constructor logic,
 * {@link Gson#getAdapter(TypeToken)}, {@link Gson#toJson(Object)},
 * and {@link Gson#fromJson(String, Class)} / {@link Gson#fromJson(String, TypeToken)}.
 *
 * <p>No external mocks or integration with other classes are used; all
 * interactions are pure Java objects.
 */
@DisplayName("Gson class unit tests")
class GsonTest {

    // -----------------------------------------------------------------------
    // 1. Constructor test
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("default constructor should create a fully initialized Gson instance")
    void defaultConstructor_initializesFields() {
        Gson gson = new Gson();
        // Basic sanity checks – the instance should not be null,
        // and certain boolean flags should have their default values.
        assertNotNull(gson);
        assertFalse(gson.serializeNulls());
        assertTrue(gson.htmlSafe());
        // The default instance should configure non‑executable JSON prefix to be used.
        // Since Gson builds the writer lazily, we just ensure a writer can be created without error.
        assertDoesNotThrow(() -> gson.newJsonWriter(new java.io.StringWriter()));
    }

    // -----------------------------------------------------------------------
    // 2. getAdapter tests
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("getAdapter with null should throw NullPointerException")
    void getAdapter_nullThrows() {
        Gson gson = new Gson();
        assertThrows(NullPointerException.class, () -> gson.getAdapter((TypeToken<?>) null));
    }

    @Test
    @DisplayName("getAdapter for a simple type should return a non‑null adapter")
    void getAdapter_simpleType() {
        Gson gson = new Gson();
        TypeToken<Integer> integerToken = TypeToken.get(Integer.class);
        assertNotNull(gson.getAdapter(integerToken));
    }

    // A tiny type containing @JsonAdapter annotation to test the
    // special branch in getDelegateAdapter that skips the JSON‑adapter factory.
    private static class Foo {
        @com.google.gson.annotations.JsonAdapter(FooTypeAdapter.class)
        String value;
    }

    private static class FooTypeAdapter extends com.google.gson.TypeAdapter<Foo> {
        @Override public void write(com.google.gson.stream.JsonWriter out, Foo value) {
            try { out.value(value.value != null ? value.value : null); } catch (java.io.IOException ignored) {}
        }
        @Override public Foo read(com.google.gson.stream.JsonReader in) {
            return new Foo();
        }
    }

//    @Test
//    @DisplayName("getAdapter respects @JsonAdapter and returns the custom adapter")
//    void getAdapter_customAnnotation() {
//        Gson gson = new Gson();
//        TypeToken<Foo> fooToken = TypeToken.get(Foo.class);
//        var adapter = gson.getDelegateAdapter(gson.jsonAdapterFactory, fooToken);
//        assertNotNull(adapter);
//        assertTrue(adapter instanceof FooTypeAdapter);
//    }

    // -----------------------------------------------------------------------
    // 3. toJson tests
    // -----------------------------------------------------------------------
    private static class Person {
        String name;
        Integer age;

        Person(String name, Integer age) { this.name = name; this.age = age; }
    }

    @Test
    @DisplayName("toJson null should return the string \"null\"")
    void toJson_null() {
        Gson gson = new Gson();
        assertEquals("null", gson.toJson(null));
    }

    @Test
    @DisplayName("toJson non‑null simple object serializes fields correctly")
    void toJson_person() {
        Gson gson = new Gson();
        Person person = new Person("Alice", 30);
        String json = gson.toJson(person);
        // The order of fields in JSON is not guaranteed, but both should be there.
        assertTrue(json.contains("\"name\":\"Alice\""));
        assertTrue(json.contains("\"age\":30"));
    }

    @Test
    @DisplayName("toJson with serializeNulls flag includes null fields")
    void toJson_nullFieldsIncluded() {
        Gson gson = new GsonBuilder().serializeNulls().create();
        Person person = new Person(null, null);
        String json = gson.toJson(person);
        // For null name, JSON should contain "name":null
        assertTrue(json.contains("\"name\":null"));
        assertTrue(json.contains("\"age\":null"));
    }

    // -----------------------------------------------------------------------
    // 4. fromJson tests
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("fromJson with null string returns null")
    void fromJson_nullString() {
        Gson gson = new Gson();
        Integer result = gson.fromJson((String) null, Integer.class);
        assertNull(result);
    }

    @Test
    @DisplayName("fromJson with empty string returns null")
    void fromJson_emptyString() {
        Gson gson = new Gson();
        Integer result = gson.fromJson("", Integer.class);
        assertNull(result);
    }

    @Test
    @DisplayName("fromJson with whitespace returns null")
    void fromJson_whitespace() {
        Gson gson = new Gson();
        Integer result = gson.fromJson("   ", Integer.class);
        // According to Gson's implementation, whitespace-only content is treated as empty
        assertNull(result);
    }

    @Test
    @DisplayName("fromJson generic type (List<Person>) returns the correct list")
    void fromJson_genericList() {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Person>>() {}.getType();
        String json = "[{\"name\":\"Bob\",\"age\":25},{\"name\":\"Carol\",\"age\":28}]";
        List<Person> people = gson.fromJson(json, listType);
        assertNotNull(people);
        assertEquals(2, people.size());
        assertEquals("Bob", people.get(0).name);
        assertEquals(25, people.get(0).age);
    }

    @Test
    @DisplayName("fromJson with malformed JSON throws JsonSyntaxException")
    void fromJson_malformed() {
        Gson gson = new Gson();
        String malformed = "{\"name\":\"Alice\", \"age\":}";
        assertThrows(com.google.gson.JsonSyntaxException.class, () ->
                gson.fromJson(malformed, Person.class));
    }

    // -----------------------------------------------------------------------
    // 5. Edge cases for getAdapter caching
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("getAdapter should cache adapters after first creation")
    void getAdapter_cacheBehavior() {
        Gson gson = new Gson();
        // First retrieval
        var adapter1 = gson.getAdapter(TypeToken.get(Integer.class));
        // Second retrieval should return the same instance (adapter caching)
        var adapter2 = gson.getAdapter(TypeToken.get(Integer.class));
        assertSame(adapter1, adapter2);
    }
}