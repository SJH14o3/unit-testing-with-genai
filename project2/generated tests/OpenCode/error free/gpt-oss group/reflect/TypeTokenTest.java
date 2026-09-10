package com.google.gson.reflect;

import com.google.gson.internal.GsonTypes;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TypeToken")
class TypeTokenTest {

    @Test
    @DisplayName("TypeToken of a parameterized type")
    void typeTokenParameterized() {
        TypeToken<List<String>> token = new TypeToken<List<String>>() {
        };
        assertEquals(List.class, token.getRawType());
        assertTrue(token.getType() instanceof ParameterizedType);

        // hashCode uses hashCode of the internal Type
        assertEquals(token.getType().hashCode(), token.hashCode());
        assertEquals(token, token);
    }

    @Test
    @DisplayName("TypeToken of an array type")
    void typeTokenArray() {
        TypeToken<int[]> token = new TypeToken<int[]>() {
        };
        assertEquals(int[].class, token.getRawType());
        assertEquals(int.class, ((Class<?>) token.getType()).getComponentType());
    }

    @Test
    @DisplayName("getParameterized returns correct type")
    void getParameterizedCorrect() {
        TypeToken<?> token = TypeToken.getParameterized(List.class, String.class);
        assertEquals(List.class, token.getRawType());
        assertTrue(token.getType() instanceof ParameterizedType);
        ParameterizedType pt = (ParameterizedType) token.getType();
        assertEquals(String.class, pt.getActualTypeArguments()[0]);
    }

    @Test
    @DisplayName("getParameterized with wrong #args throws")
    void getParameterizedWrongArgCount() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> TypeToken.getParameterized(List.class, String.class, Integer.class));
        assertTrue(e.getMessage().contains("requires 1 type arguments"));
    }

    @Test
    @DisplayName("getParameterized on non‑generic class")
    void getParameterizedNonGeneric() {
        TypeToken<?> token = TypeToken.getParameterized(Integer.class);
        assertEquals(Integer.class, token.getRawType());
    }

    @Test
    @DisplayName("capturing type variable is forbidden")
    void captureTypeVariableForbidden() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> new TypeToken<List<?>>() {
                });
        assertTrue(e.getMessage().contains("must not contain a type variable"));
    }

    @Test
    @DisplayName("equals and hashCode honor underlying Type")
    void equalsAndHashCode() {
        TypeToken<List<String>> a = new TypeToken<List<String>>() {
        };
        TypeToken<List<String>> b = new TypeToken<List<String>>() {
        };
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        TypeToken<List<Integer>> c = new TypeToken<List<Integer>>() {
        };
        assertNotEquals(a, c);
    }

    @Test
    @DisplayName("toString delegates to GsonTypes#typeToString")
    void toStringRepresentation() {
        TypeToken<List<String>> token = new TypeToken<List<String>>() {
        };
        assertEquals(GsonTypes.typeToString(token.getType()), token.toString());
    }
}