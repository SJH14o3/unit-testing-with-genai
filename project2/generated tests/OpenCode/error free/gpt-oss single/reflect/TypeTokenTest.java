package com.google.gson.reflect;

import com.google.gson.internal.GsonTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TypeTokenTest {

    /* ------------------------------------------------------------------- */
    /* 1. Construction & raw type resolution                              */
    /* ------------------------------------------------------------------- */

    @Test
    @DisplayName("Default constructor captures generic type correctly")
    void defaultConstructorCapturesType() {
        TypeToken<java.util.List<String>> token =
                new TypeToken<java.util.List<String>>() {};
        assertEquals(java.util.List.class, token.getRawType());
        assertTrue(GsonTypes.getRawType(token.getType()).equals(java.util.List.class));
    }

    /* ------------------------------------------------------------------- */
    /* 2. getParameterized() - success path                               */
    /* ------------------------------------------------------------------- */

    @Test
    @DisplayName("getParameterized creates parameterized type for Map")
    void getParameterized_success() {
        TypeToken<?> mapToken =
                TypeToken.getParameterized(Map.class, String.class, Integer.class);
        Type type = mapToken.getType();
        // The canonical type is a ParameterizedType with raw type Map
        assertEquals(Map.class, GsonTypes.getRawType(type));
    }

    /* ------------------------------------------------------------------- */
    /* 3. getParameterized() – error handling                            */
    /* ------------------------------------------------------------------- */

    @Test
    @DisplayName("getParameterized throws when rawType is not a Class")
    void getParameterized_nonClassRawType() {
        Type t = new SimpleTypeParameterElement(null, null, null); // placeholder
        assertThrows(
                IllegalArgumentException.class,
                () -> TypeToken.getParameterized(t, String.class));
    }

    @Test
    @DisplayName("getParameterized throws when argument count mismatched")
    void getParameterized_wrongArgCount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> TypeToken.getParameterized(Map.class, String.class));
    }

    @Test
    @DisplayName("getParameterized delegates to get(Class) for non‑generic class")
    void getParameterized_nonGenericClass() {
        TypeToken<?> token = TypeToken.getParameterized(String.class);
        assertEquals(String.class, token.getRawType());
    }

    @Test
    @DisplayName("getParameterized throws when owner type is required")
    void getParameterized_ownerTypeRequired() {
        // Outer$Inner is a non‑static inner class and therefore requires an owner type.
        class Outer {
            class Inner {}
        }
        assertThrows(
                IllegalArgumentException.class,
                () -> TypeToken.getParameterized(Outer.Inner.class));
    }

    @Test
    @DisplayName("getParameterized throws when type argument does not satisfy bounds")
    void getParameterized_boundsViolation() {
        interface Bound<T extends Number> {}
        assertThrows(
                IllegalArgumentException.class,
                () -> TypeToken.getParameterized(Bound.class, String.class));
    }

    /* ------------------------------------------------------------------- */
    /* 4. getArray()                                                      */
    /* ------------------------------------------------------------------- */

    @Test
    @DisplayName("getArray creates array type token and returns correct raw type")
    void getArray_success() {
        TypeToken<?> arrayToken = TypeToken.getArray(Integer.class);
        Type arrayType = arrayToken.getType();
        assertTrue(arrayType instanceof java.lang.reflect.GenericArrayType);
        assertEquals(Integer.class, GsonTypes.getRawType(arrayType));
        assertEquals(Integer[].class, arrayToken.getRawType());
    }

    /* ------------------------------------------------------------------- */
    /* 5. isAssignableFrom() – various scenarios                         */
    /* ------------------------------------------------------------------- */

    @Test
    @DisplayName("isAssignableFrom works for the same raw type")
    void assignableSameRawType() {
        TypeToken<String> stringToken = TypeToken.get(String.class);
        TypeToken<Object> objectToken = TypeToken.get(Object.class);
        assertFalse(stringToken.isAssignableFrom(objectToken));
        assertTrue(objectToken.isAssignableFrom(stringToken));
    }

    @Test
    @DisplayName("isAssignableFrom handles parameterized types with same parameters")
    void assignableParameterizedSame() {
        TypeToken<?> listString1 =
                TypeToken.getParameterized(List.class, String.class);
        TypeToken<?> listString2 =
                TypeToken.getParameterized(List.class, String.class);
        assertTrue(listString1.isAssignableFrom(listString2));
        assertTrue(listString2.isAssignableFrom(listString1));
    }

    @Test
    @DisplayName("isAssignableFrom handles wildcard upper bounds")
    void assignableWildcard() {
        TypeToken<?> superListToken = TypeToken.getParameterized(List.class, Object.class);
        TypeToken<?> stringListToken = TypeToken.getParameterized(List.class, String.class);
        assertTrue(superListToken.isAssignableFrom(stringListToken));
    }

    @Test
    @DisplayName("isAssignableFrom handles generic array types")
    void assignableGenericArray() {
        TypeToken<?> stringArrayToken = TypeToken.getArray(String.class);
        TypeToken<?> objectArrayToken = TypeToken.getArray(Object.class);
        assertTrue(objectArrayToken.isAssignableFrom(stringArrayToken));
    }

    /* ------------------------------------------------------------------- */
    /* 6. equals() / hashCode() / toString() ------------------------------ */
    /* ------------------------------------------------------------------- */

    @Test
    @DisplayName("equals and hashCode are consistent across equivalent tokens")
    void equalsAndHashCode() {
        TypeToken<?> token1 =
                TypeToken.getParameterized(List.class, String.class);
        TypeToken<?> token2 =
                TypeToken.getParameterized(List.class, String.class);
        assertEquals(token1, token2);
        assertEquals(token1.hashCode(), token2.hashCode());
    }

    @Test
    @DisplayName("toString delegates to GsonTypes.typeToString")
    void toStringDelegate() {
        TypeToken<Integer> token = TypeToken.get(Integer.class);
        assertEquals("int", token.toString()); // Gson renders primitives as type names
    }

    private class SimpleTypeParameterElement implements Type {
        public SimpleTypeParameterElement(Object o, Object o1, Object o2) {
        }
    }
}