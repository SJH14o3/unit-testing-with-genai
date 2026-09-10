package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;
import java.lang.reflect.Field;

class ReflectiveTypeAdapterFactoryTest {

    @Test
    void testCreate_withPrimitiveClass_returnsNull() {
        Gson gson = new Gson();
        TypeToken<String> typeToken = TypeToken.get(String.class);
        ConstructorConstructor constructorConstructor = new ConstructorConstructor(Collections.emptyMap(), false, Collections.emptyList());
        var factory = new com.google.gson.internal.bind.ReflectiveTypeAdapterFactory(
                constructorConstructor,
                com.google.gson.FieldNamingPolicy.IDENTITY,
                com.google.gson.internal.Excluder.DEFAULT,
                new com.google.gson.internal.bind.JsonAdapterAnnotationTypeAdapterFactory(constructorConstructor),
                List.of());
        assertNull(factory.create(gson, typeToken));
    }

    @Test
    void testGetFieldNames_singleName() throws Exception {
        class Dummy { String field; }
        Field f = Dummy.class.getDeclaredField("field");
        var factory = new com.google.gson.internal.bind.ReflectiveTypeAdapterFactory(
                null, null, null, null, List.of());
        List<String> names = (List<String>) factory.getClass()
                .getDeclaredMethod("getFieldNames", Field.class)
                .invoke(factory, f);
        assertEquals(1, names.size());
        assertEquals("field", names.get(0));
    }
}
