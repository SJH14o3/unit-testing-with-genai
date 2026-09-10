package com.google.gson.internal;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GsonTypes")
class GsonTypesTest {

    /* ----- simple helper classes ----------------------------------------------------- */
    private static class Generic<T> {
    }

    private static class Inner {
    }

    @Test
    @DisplayName("getRawType works for Class")
    void rawTypeForClass() {
        assertEquals(int.class, GsonTypes.getRawType(Integer.TYPE));
    }

    @Test
    @DisplayName("getRawType handles ParameterizedType")
    void rawTypeForParameterized() {
        TypeToken<List<String>> tt = new TypeToken<List<String>>() {
        };
        assertEquals(List.class, GsonTypes.getRawType(tt.getType()));
    }

    @Test
    @DisplayName("getRawType for GenericArrayType")
    void rawTypeForGenericArray() {
        GenericArrayType array = GsonTypes.arrayOf(String.class);
        assertEquals(String[].class, GsonTypes.getRawType(array));
    }

    @Test
    @DisplayName("getRawType for TypeVariable mapped to bound")
    void rawTypeForTypeVariable() {
        TypeVariable<Class<Generic>> var = Generic.class.getTypeParameters()[0];
        assertEquals(Object.class, GsonTypes.getRawType(var));
    }

    @Test
    @DisplayName("equals compares parameterized types")
    void equalsParameterized() {
        TypeToken<List<String>> a = new TypeToken<List<String>>() {
        };
        TypeToken<List<String>> b = new TypeToken<List<String>>() {
        };
        assertTrue(GsonTypes.equals(a.getType(), b.getType()));

        TypeToken<List<Integer>> c = new TypeToken<List<Integer>>() {
        };
        assertFalse(GsonTypes.equals(a.getType(), c.getType()));
    }

    @Test
    @DisplayName("resolve type variable mapping")
    void resolveTypeVariable() {
        // List<? extends Number> -> resolved to List<Number>
        TypeVariable<Class<List>> tv = List.class.getTypeParameters()[0];
        // Build a ParameterizedType with a wildcard upper bound
        WildcardType wildcard = GsonTypes.subtypeOf(Number.class);
        Type[] args = new Type[] {wildcard};
        ParameterizedType pt = GsonTypes.newParameterizedTypeWithOwner(null, List.class, args);
//        ParameterizedType pt = (ParameterizedType) ((Factory) () -> {
//            Type[] args = new Type[] {wildcard};
//            return new ParameterizedType(null, List.class, args);
//        }).create();

        Type resolved = GsonTypes.resolve(TypeToken.get(List.class).getRawType(), List.class, pt);
        assertTrue(resolved instanceof ParameterizedType);
        assertEquals(Number.class,
                ((ParameterizedType) resolved).getActualTypeArguments()[0]);
    }

    @Test
    @DisplayName("getCollectionElementType")
    void collectionElementType() {
        TypeToken<List<String>> tt = new TypeToken<List<String>>() {
        };
        assertEquals(String.class,
                GsonTypes.getCollectionElementType(tt.getType(), List.class));
        // raw type
        assertEquals(Object.class,
                GsonTypes.getCollectionElementType(List.class, List.class));
    }

    @Test
    @DisplayName("getMapKeyAndValueTypes")
    void mapKeyValueTypes() {
        TypeToken<Map<String, Integer>> tt =
                new TypeToken<Map<String, Integer>>() {
                };
        Type[] kv = GsonTypes.getMapKeyAndValueTypes(tt.getType(), Map.class);
        assertEquals(String.class, kv[0]);
        assertEquals(Integer.class, kv[1]);

        // Properties treated specially
        TypeToken<Properties> p = TypeToken.get(Properties.class);
        Type[] pv = GsonTypes.getMapKeyAndValueTypes(p.getType(), Properties.class);
        assertEquals(String.class, pv[0]);
        assertEquals(String.class, pv[1]);

        // raw Map
        TypeToken<Map> rawMap = TypeToken.get(Map.class);
        Type[] rawKV = GsonTypes.getMapKeyAndValueTypes(rawMap.getType(), Map.class);
        assertEquals(Object.class, rawKV[0]);
        assertEquals(Object.class, rawKV[1]);
    }

    @Test
    @DisplayName("requiresOwnerType returns true for inner class")
    void requiresOwnerType() {
        assertTrue(GsonTypes.requiresOwnerType(Inner.class));
    }
}

// Note: A minimal Factory interface was needed in resolveTypeVariable test to create a ParameterizedTypeImpl.
// The lambda cast to Factory is not part of the code base; it is only used here for constructing the type for the test.
// (The necessary helper is harmless because the test is isolated.)