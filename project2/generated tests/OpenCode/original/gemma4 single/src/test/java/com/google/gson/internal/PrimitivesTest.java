package com.google.gson.internal;

import java.lang.reflect.Type;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Primitives Test")
class PrimitivesTest {

    @Test
    @DisplayName("isPrimitive returns true for primitive classes")
    void isPrimitive_trueForPrimitives() {
        assertTrue(Primitives.isPrimitive(int.class));
        assertTrue(Primitives.isPrimitive(float.class));
        assertTrue(Primitives.isPrimitive(byte.class));
        assertTrue(Primitives.isPrimitive(double.class));
        assertTrue(Primitives.isPrimitive(long.class));
        assertTrue(Primitives.isPrimitive(char.class));
        assertTrue(Primitives.isPrimitive(boolean.class));
        assertTrue(Primitives.isPrimitive(short.class));
        assertTrue(Primitives.isPrimitive(void.class));
    }

    @Test
    @DisplayName("isPrimitive returns false for non-primitive classes")
    void isPrimitive_falseForNonPrimitives() {
        assertFalse(Primitives.isPrimitive(String.class));
        assertFalse(Primitives.isPrimitive(Integer.class));
        assertFalse(Primitives.isPrimitive(Object.class));
    }

    @Test
    @DisplayName("isPrimitive returns false for null or non-Class types")
    void isPrimitive_falseForNonClassTypes() {
        // Note: isPrimitive uses instanceof Class<?>
        // We can't easily pass a non-Class type that isn't null to Type without complex mocking or custom implementation,
        // but we can test null if the implementation allowed it (it doesn't check for null before instanceof).
        // In Java, (null instanceof Class<?>) is false.
        assertFalse(Primitives.isPrimitive(null));
    }

    @ParameterizedTest
    @MethodSource("provideWrapperTypes")
    @DisplayName("isWrapperType returns true for all nine wrapper types")
    void isWrapperType_trueForWrappers(Class<?> type) {
        assertTrue(Primitives.isWrapperType(type));
    }

    private static java.util.stream.Stream<Arguments> provideWrapperTypes() {
        return java.util.stream.Stream.of(
            Arguments.of(Integer.class),
            Arguments.of(Float.class),
            Arguments.of(Byte.class),
            Arguments.of(Double.class),
            Arguments.of(Long.class),
            Arguments.of(Character.class),
            Arguments.of(Boolean.class),
            Arguments.of(Short.class),
            Arguments.of(Void.class)
        );
    }

    @Test
    @DisplayName("isWrapperType returns false for non-wrapper types")
    void isWrapperType_falseForNonWrappers() {
        assertFalse(Primitives.isWrapperType(String.class));
        assertFalse(Primitives.isWrapperType(int.class)); // Primitive class is not a wrapper type
    }

    @ParameterizedTest
    @MethodSource("provideWrapCases")
    @DisplayName("wrap returns correct wrapper class for primitive types")
    void wrap_correctWrappers(Class<?> input, Class<?> expected) {
        assertEquals(expected, Primitives.wrap(input));
    }

    private static java.util.stream.Stream<Arguments> provideWrapCases() {
        return java.util.stream.Stream.of(
            Arguments.of(int.class, Integer.class),
            Arguments.of(float.class, Float.class),
            Arguments.of(byte.class, Byte.class),
            Arguments.of(double.class, Double.class),
            Arguments.of(long.class, Long.class),
            Arguments.of(char.class, Character.class),
            Arguments.of(boolean.class, Boolean.class),
            Arguments.of(short.class, Short.class),
            Arguments.of(void.class, Void.class),
            Arguments.of(String.class, String.class), // Non-primitive
            Arguments.of(Integer.class, Integer.class) // Already wrapped (idempotent)
        );
    }

    @ParameterizedTest
    @MethodSource("provideUnwrapCases")
    @DisplayName("unwrap returns correct primitive class for wrapper types")
    void unwrap_correctPrimitives(Class<?> input, Class<?> expected) {
        assertEquals(expected, Primitives.unwrap(input));
    }

    private static java.util.stream.Stream<Arguments> provideUnwrapCases() {
        return java.util.stream.Stream.of(
            Arguments.of(Integer.class, int.class),
            Arguments.of(Float.class, float.class),
            Arguments.of(Byte.class, byte.class),
            Arguments.of(Double.class, double.class),
            Arguments.of(Long.class, long.class),
            Arguments.of(Character.class, char.class),
            Arguments.of(Boolean.class, boolean.class),
            Arguments.of(Short.class, short.class),
            Arguments.of(Void.class, void.class),
            Arguments.of(String.class, String.class), // Non-wrapper
            Arguments.of(int.class, int.class) // Already primitive (idempotent)
        );
    }
}