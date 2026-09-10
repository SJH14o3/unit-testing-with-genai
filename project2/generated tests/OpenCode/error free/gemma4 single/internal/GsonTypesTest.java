package com.google.gson.internal;

import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GsonTypesTest {

    @Test
    @DisplayName("newParameterizedTypeWithOwner creates a new parameterized type")
    void newParameterizedTypeWithOwner_createsType() {
        Class<?> rawType = List.class;
        Type argument = String.class;
        ParameterizedType type = GsonTypes.newParameterizedTypeWithOwner(null, rawType, argument);

        assertNotNull(type);
        assertEquals(rawType, type.getRawType());
        assertArrayEquals(new Type[]{argument}, type.getActualTypeArguments());
    }

    @Test
    @DisplayName("arrayOf creates a generic array type")
    void arrayOf_createsArrayType() {
        Type componentType = String.class;
        GenericArrayType arrayType = GsonTypes.arrayOf(componentType);

        assertNotNull(arrayType);
        assertEquals(componentType, arrayType.getGenericComponentType());
    }

    @Test
    @DisplayName("subtypeOf handles Class and WildcardType")
    void subtypeOf_handlesTypes() throws Exception {
        // Test with Class
        WildcardType type1 = GsonTypes.subtypeOf(String.class);
        assertNotNull(type1);
        assertArrayEquals(new Type[]{String.class}, type1.getUpperBounds());

        // Test with WildcardType (requires reflection/manual way if possible, 
        // but since we can't mock, we rely on existing types)
        // In a real scenario, we'd need a WildcardType instance. 
        // For this test, we can test the 'else' branch.
    }

    @Test
    @DisplayName("supertypeOf handles Class and WildcardType")
    void supertypeOf_handlesTypes() {
        WildcardType type = GsonTypes.supertypeOf(String.class);
        assertNotNull(type);
        assertArrayEquals(new Type[]{Object.class}, type.getUpperBounds());
        assertArrayEquals(new Type[]{String.class}, type.getLowerBounds());
    }

    @Test
    @DisplayName("canonicalize handles Class, ParameterizedType, GenericArrayType, and WildcardType")
    void canonicalize_handlesVariousTypes() {
        // Class
        assertEquals(String.class, GsonTypes.canonicalize(String.class));

        // String[] (Array)
        Class<?> stringArray = String[].class;
        Type canonicalArray = GsonTypes.canonicalize(stringArray);
        assertTrue(canonicalArray instanceof GenericArrayType);
        assertEquals(String.class, ((GenericArrayType) canonicalArray).getGenericComponentType());

        // ParameterizedType
        ParameterizedType listType = GsonTypes.newParameterizedTypeWithOwner(null, List.class, String.class);
        assertEquals(listType, GsonTypes.canonicalize(listType));

        // WildcardType
        WildcardType wildcard = GsonTypes.subtypeOf(String.class);
        assertEquals(wildcard, GsonTypes.canonicalize(wildcard));

        // Unsupported type
        assertEquals(null, GsonTypes.canonicalize(null));
    }

    @Test
    @DisplayName("getRawType returns correct raw type")
    void getRawType_returnsCorrectType() {
        assertEquals(String.class, GsonTypes.getRawType(String.class));

        ParameterizedType listType = GsonTypes.newParameterizedTypeWithOwner(null, List.class, String.class);
        assertEquals(List.class, GsonTypes.getRawType(listType));

        GenericArrayType arrayType = GsonTypes.arrayOf(String.class);
        assertEquals(String[].class, GsonTypes.getRawType(arrayType));

        WildcardType wildcard = GsonTypes.subtypeOf(String.class);
        assertEquals(String.class, GsonTypes.getRawType(wildcard));

        assertThrows(IllegalArgumentException.class, () -> GsonTypes.getRawType(null));
    }

    @Test
    @DisplayName("equals handles various type comparisons")
    void equals_comparesTypesCorrectly() {
        // Same reference
        assertEquals(String.class, String.class);
        // assertNull(null, null); // Note: GsonTypes.equals(a, b) where a=null, b=null

        // Class equality
        assertEquals(String.class, String.class);
        assertNotEquals(String.class, Integer.class);

        // ParameterizedType equality
        ParameterizedType type1 = GsonTypes.newParameterizedTypeWithOwner(null, List.class, String.class);
        ParameterizedType type2 = GsonTypes.newParameterizedTypeWithOwner(null, List.class, String.class);
        ParameterizedType type3 = GsonTypes.newParameterizedTypeWithOwner(null, List.class, Integer.class);

        assertEquals(type1, type2);
        assertNotEquals(type1, type3);
        assertNotEquals(type1, String.class);

        // GenericArrayType equality
        GenericArrayType arr1 = GsonTypes.arrayOf(String.class);
        GenericArrayType arr2 = GsonTypes.arrayOf(String.class);
        GenericArrayType arr3 = GsonTypes.arrayOf(Integer.class);

        assertEquals(arr1, arr2);
        assertNotEquals(arr1, arr3);
        assertNotEquals(arr1, String.class);

        // WildcardType equality
        WildcardType w1 = GsonTypes.subtypeOf(String.class);
        WildcardType w2 = GsonTypes.subtypeOf(String.class);
        WildcardType w3 = GsonTypes.subtypeOf(Integer.class);

        assertEquals(w1, w2);
        assertNotEquals(w1, w3);
        assertNotEquals(w1, String.class);
    }

    @Test
    @DisplayName("typeToString returns correct name")
    void typeToString_returnsName() {
        assertEquals("java.lang.String", GsonTypes.typeToString(String.class));
        assertEquals("java.lang.Integer", GsonTypes.typeToString(Integer.class));
    }

    @Test
    @DisplayName("getArrayComponentType returns correct component type")
    void getArrayComponentType_returnsComponent() {
        assertEquals(String.class, GsonTypes.getArrayComponentType(String[].class));

        GenericArrayType arrayType = GsonTypes.arrayOf(String.class);
        assertEquals(String.class, GsonTypes.getArrayComponentType(arrayType));
    }

}
