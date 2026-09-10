package com.google.gson.internal;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.lang.reflect.Type;

/**
 * Unit tests for {@link com.google.gson.internal.Primitives}.
 */
@DisplayName("Primitives unit tests")
class PrimitivesTest {

    // ---------- isPrimitive ----------
    @Test
    @DisplayName("isPrimitive returns true for primitive classes")
    void testIsPrimitiveTrue() {
        assertTrue(com.google.gson.internal.Primitives.isPrimitive(int.class));
        assertTrue(com.google.gson.internal.Primitives.isPrimitive(double.class));
    }

    @Test
    @DisplayName("isPrimitive returns false for non-primitive classes")
    void testIsPrimitiveFalse() {
        assertFalse(com.google.gson.internal.Primitives.isPrimitive(Integer.class));
        assertFalse(com.google.gson.internal.Primitives.isPrimitive(String.class));
    }

    // ---------- isWrapperType ----------
    @Test
    @DisplayName("isWrapperType recognizes all wrapper types")
    void testIsWrapperTypeTrue() {
        assertTrue(com.google.gson.internal.Primitives.isWrapperType(Integer.class));
        assertTrue(com.google.gson.internal.Primitives.isWrapperType(Float.class));
        assertTrue(com.google.gson.internal.Primitives.isWrapperType(Byte.class));
        assertTrue(com.google.gson.internal.Primitives.isWrapperType(Double.class));
        assertTrue(com.google.gson.internal.Primitives.isWrapperType(Long.class));
        assertTrue(com.google.gson.internal.Primitives.isWrapperType(Character.class));
        assertTrue(com.google.gson.internal.Primitives.isWrapperType(Boolean.class));
        assertTrue(com.google.gson.internal.Primitives.isWrapperType(Short.class));
        assertTrue(com.google.gson.internal.Primitives.isWrapperType(Void.class));
    }

    @Test
    @DisplayName("isWrapperType rejects non-wrapper types")
    void testIsWrapperTypeFalse() {
        assertFalse(com.google.gson.internal.Primitives.isWrapperType(int.class));
        assertFalse(com.google.gson.internal.Primitives.isWrapperType(String.class));
    }

    // ---------- wrap ----------
    @ParameterizedTest
    @ValueSource(strings = {"int", "float", "byte", "double", "long", "char", "boolean", "short", "void"})
    @DisplayName("wrap returns correct wrapper for primitive types")
    void testWrapPrimitive(String primitiveName) {
        Class<?> primitive = switch (primitiveName) {
            case "int" -> int.class;
            case "float" -> float.class;
            case "byte" -> byte.class;
            case "double" -> double.class;
            case "long" -> long.class;
            case "char" -> char.class;
            case "boolean" -> boolean.class;
            case "short" -> short.class;
            case "void" -> void.class;
            default -> throw new IllegalArgumentException();
        };
        Class<?> expectedWrapper = switch (primitiveName) {
            case "int" -> Integer.class;
            case "float" -> Float.class;
            case "byte" -> Byte.class;
            case "double" -> Double.class;
            case "long" -> Long.class;
            case "char" -> Character.class;
            case "boolean" -> Boolean.class;
            case "short" -> Short.class;
            case "void" -> Void.class;
            default -> throw new IllegalArgumentException();
        };
        assertEquals(expectedWrapper, com.google.gson.internal.Primitives.wrap((Class) primitive));
    }

    @Test
    @DisplayName("wrap returns argument when not a primitive")
    void testWrapNonPrimitive() {
        assertSame(String.class, com.google.gson.internal.Primitives.wrap(String.class));
    }

    // ---------- unwrap ----------
    @ParameterizedTest
    @ValueSource(strings = {"Integer", "Float", "Byte", "Double", "Long", "Character", "Boolean", "Short", "Void"})
    @DisplayName("unwrap returns correct primitive for wrapper types")
    void testUnwrapWrapper(String wrapperName) {
        Class<?> wrapper = switch (wrapperName) {
            case "Integer" -> Integer.class;
            case "Float" -> Float.class;
            case "Byte" -> Byte.class;
            case "Double" -> Double.class;
            case "Long" -> Long.class;
            case "Character" -> Character.class;
            case "Boolean" -> Boolean.class;
            case "Short" -> Short.class;
            case "Void" -> Void.class;
            default -> throw new IllegalArgumentException();
        };
        Class<?> expectedPrimitive = switch (wrapperName) {
            case "Integer" -> int.class;
            case "Float" -> float.class;
            case "Byte" -> byte.class;
            case "Double" -> double.class;
            case "Long" -> long.class;
            case "Character" -> char.class;
            case "Boolean" -> boolean.class;
            case "Short" -> short.class;
            case "Void" -> void.class;
            default -> throw new IllegalArgumentException();
        };
        assertEquals(expectedPrimitive, com.google.gson.internal.Primitives.unwrap((Class) wrapper));
    }

    @Test
    @DisplayName("unwrap returns argument when not a wrapper")
    void testUnwrapNonWrapper() {
        assertSame(String.class, com.google.gson.internal.Primitives.unwrap(String.class));
    }
}
