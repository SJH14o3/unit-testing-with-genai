package com.google.gson.internal;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.internal.GsonTypes;
import org.junit.jupiter.api.Test;

import java.lang.reflect.*;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class GsonTypesTest {

    // Helper inner class to test requiresOwnerType logic
    private class NonStaticInner {
    }

    /**
     * Tests that newParameterizedTypeWithOwner throws when ownerType is null but required.
     */
    @Test
    void newParameterizedTypeWithOwner_requiresOwnerType() {
        Class<?> raw = NonStaticInner.class; // non-static inner class requires owner
        assertTrue(GsonTypes.requiresOwnerType(raw));
        Exception e = assertThrows(IllegalArgumentException.class, () ->
                GsonTypes.newParameterizedTypeWithOwner(null, raw));
        assertEquals("Must specify owner type for " + raw, e.getMessage());
    }

    /**
     * Tests arrayOf returns a GenericArrayType with correct component type.
     */
    @Test
    void arrayOf_returnsCorrectType() {
        Type arrayType = GsonTypes.arrayOf(String.class);
        assertTrue(arrayType instanceof java.lang.reflect.GenericArrayType);
        assertEquals(String.class, ((GenericArrayType) arrayType).getGenericComponentType());
    }

    /**
     * Tests subtypeOf and supertypeOf handling of WildcardType bounds.
     */
    @Test
    void wildcardBounds() {
        Type w = GsonTypes.subtypeOf(String.class);
        assertTrue(w instanceof WildcardType);
        assertArrayEquals(new Type[]{String.class}, ((WildcardType) w).getUpperBounds());
        assertArrayEquals(new Type[]{}, ((WildcardType) w).getLowerBounds());

        Type s = GsonTypes.supertypeOf(Integer.class);
        assertTrue(s instanceof WildcardType);
        assertArrayEquals(new Type[]{Object.class}, ((WildcardType) s).getUpperBounds());
        assertArrayEquals(new Type[]{Integer.class}, ((WildcardType) s).getLowerBounds());
    }

    /**
     * Tests canonicalize paths for different type kinds.
     */
    @Test
    void canonicalizeBranches() {
        // Class array
        Type array = String[].class;
        Type canon = GsonTypes.canonicalize(array);
        assertTrue(canon instanceof java.lang.reflect.GenericArrayType);
        assertEquals(String.class, ((GenericArrayType) canon).getGenericComponentType());

        // Parameterized type
        ParameterizedType pt = (ParameterizedType) GsonTypes.newParameterizedTypeWithOwner(null, Set.class, String.class);
        Type canonPt = GsonTypes.canonicalize(pt);
        assertTrue(canonPt instanceof ParameterizedType);
        assertEquals(pt.getRawType(), ((ParameterizedType) canonPt).getRawType());

        // Wildcard type
        WildcardType w = GsonTypes.subtypeOf(String.class);
        Type canonW = GsonTypes.canonicalize(w);
        assertTrue(canonW instanceof WildcardType);
        assertEquals(w.getUpperBounds()[0], ((WildcardType) canonW).getUpperBounds()[0]);

        // GenericArrayType
        GenericArrayType ga = (GenericArrayType) GsonTypes.arrayOf(String.class);
        Type canonGa = GsonTypes.canonicalize(ga);
        assertTrue(canonGa instanceof GenericArrayType);
        assertEquals(ga.getGenericComponentType(), ((GenericArrayType) canonGa).getGenericComponentType());
    }

    /**
     * Tests getRawType for different branches.
     */
    @Test
    void getRawTypeBranches() {
        // Class
        assertEquals(String.class, GsonTypes.getRawType(String.class));

        // ParameterizedType
        ParameterizedType pt = (ParameterizedType) GsonTypes.newParameterizedTypeWithOwner(null, Map.class, String.class, Integer.class);
        assertEquals(Map.class, GsonTypes.getRawType(pt));

        // GenericArrayType
        GenericArrayType ga = (GenericArrayType) GsonTypes.arrayOf(Double[].class);
        assertNotEquals(Double[].class, GsonTypes.getRawType(ga));

        // TypeVariable - use outer generic type
        class Generic<T> {}
        TypeVariable<?> var = Generic.class.getTypeParameters()[0];
        assertEquals(Object.class, GsonTypes.getRawType(var));

        // WildcardType
        WildcardType w = GsonTypes.subtypeOf(Float.class);
        assertEquals(Float.class, GsonTypes.getRawType(w));
    }

    /**
     * Tests equals method across various type kinds.
     */
    @Test
    void equalsBranches() {
        assertTrue(GsonTypes.equals(String.class, String.class));
        assertFalse(GsonTypes.equals(String.class, Integer.class));

        ParameterizedType pt1 = (ParameterizedType) GsonTypes.newParameterizedTypeWithOwner(null, Map.class, String.class, Integer.class);
        ParameterizedType pt2 = (ParameterizedType) GsonTypes.newParameterizedTypeWithOwner(null, Map.class, String.class, Integer.class);
        assertTrue(GsonTypes.equals(pt1, pt2));

        GenericArrayType ga1 = (GenericArrayType) GsonTypes.arrayOf(String.class);
        GenericArrayType ga2 = (GenericArrayType) GsonTypes.arrayOf(String.class);
        assertTrue(GsonTypes.equals(ga1, ga2));

        WildcardType wt1 = GsonTypes.subtypeOf(String.class);
        WildcardType wt2 = GsonTypes.subtypeOf(String.class);
        assertTrue(GsonTypes.equals(wt1, wt2));

        // Different bounds
        WildcardType wt3 = GsonTypes.subtypeOf(Integer.class);
        assertFalse(GsonTypes.equals(wt1, wt3));
    }

    /**
     * Tests getMapKeyAndValueTypes special case for Properties.
     */
    @Test
    void mapKeyAndValueTypesWithProperties() {
        Type[] types = GsonTypes.getMapKeyAndValueTypes(Properties.class, Properties.class);
        assertEquals(String.class, types[0]);
        assertEquals(String.class, types[1]);
    }

    /**
     * Tests resolve logic for a type variable.
     */
    @Test
    void resolveTypeVariable() {
        class Base<T> {}
        class Derived extends Base<Integer> {}
        TypeVariable<?> var = Base.class.getTypeParameters()[0];
        Type resolved = GsonTypes.resolve(Derived.class, Derived.class, var);
        assertEquals(Integer.class, resolved);
    }
}
