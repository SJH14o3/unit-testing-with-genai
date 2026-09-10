package com.google.gson.internal.bind;

import com.google.gson.*;
import com.google.gson.internal.bind.ObjectTypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ObjectTypeAdapterTest {

    private final Gson gson = new GsonBuilder().create();

    @Test
    @DisplayName("getFactory returns DOUBLE_FACTORY when strategy is DOUBLE")
    void testGetFactoryDouble() {
        TypeAdapterFactory factory = ObjectTypeAdapter.getFactory(ToNumberPolicy.DOUBLE);
        assertSame(factory, ObjectTypeAdapter.getFactory(ToNumberPolicy.DOUBLE));
    }

    @Test
    @DisplayName("getFactory creates new factory for other strategies")
    void testGetFactoryOther() {
        TypeAdapterFactory factory1 = ObjectTypeAdapter.getFactory(ToNumberPolicy.LONG_OR_DOUBLE);
        TypeAdapterFactory factory2 = ObjectTypeAdapter.getFactory(ToNumberPolicy.LONG_OR_DOUBLE);
        assertNotSame(factory1, factory2);
        // factory should produce adapters for Object type only
        TypeAdapter<?> adapter = factory1.create(gson, TypeToken.get(Object.class));
        assertTrue(adapter instanceof ObjectTypeAdapter);
        // Should return null for non-Object type
        assertNull(factory1.create(gson, TypeToken.get(String.class)));
    }

    @Test
    @DisplayName("read decodes array, elements and nested structures")
    void testReadArrayAndNested() throws Exception {
        String json = "[1, {\"b\": null, \"c\": [true, \"str\"]}]";
        JsonReader reader = new JsonReader(new StringReader(json));
        Object result = gson.getAdapter(Object.class).read(reader);
        assertTrue(result instanceof List);
        List<?> outer = (List<?>) result;
        assertEquals(2, outer.size());
        assertEquals(1.0, outer.get(0)); // toNumberStrategy DOUBLE

        Map<?, ?> innerMap = (Map<?, ?>) outer.get(1);
        assertEquals(2, innerMap.size());
        assertNull(innerMap.get("b"));
        ArrayList al = new ArrayList<>();
        al.add(true);
        al.add("str");
        assertEquals(al, innerMap.get("c"));
        // ensure inner list
        List<?> innerList = (List<?>) innerMap.get("c");
        assertEquals(2, innerList.size());
        assertEquals(true, innerList.get(0));
        assertEquals("str", innerList.get(1));
    }

    @Test
    @DisplayName("read decodes object with primitive members")
    void testReadObjectPrimitives() throws Exception {
        String json = "{\"s\":\"hello\",\"n\":42,\"b\":true,\"p\":null}";
        Object obj = gson.getAdapter(Object.class).read(new JsonReader(new StringReader(json)));
        assertTrue(obj instanceof Map);
        Map<?, ?> map = (Map<?, ?>) obj;
        assertEquals("hello", map.get("s"));
        assertEquals(42.0, map.get("n"));
        assertEquals(true, map.get("b"));
        assertNull(map.get("p"));
    }

    @Test
    @DisplayName("write null serialises to JSON null")
    void testWriteNull() throws Exception {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        gson.getAdapter(Object.class).write(jsonWriter, null);
        assertEquals("null", writer.toString());
    }

    @Test
    @DisplayName("write handles empty Object as ObjectTypeAdapter by writing empty object")
    void testWriteEmptyObject() throws Exception {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        gson.getAdapter(Object.class).write(jsonWriter, new Object());
        assertEquals("{}", writer.toString());
    }

    @Test
    @DisplayName("write delegates for non-ObjectTypeAdapter types")
    void testWriteDelegate() throws Exception {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        gson.getAdapter(Object.class).write(jsonWriter, Collections.singletonMap("k", 10));
        assertEquals("{\"k\":10}", writer.toString());
    }
}
