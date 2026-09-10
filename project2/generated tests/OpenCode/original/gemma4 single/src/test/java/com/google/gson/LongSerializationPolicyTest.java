package com.google.gson;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class LongSerializationPolicyTest {

    @ParameterizedTest
    @MethodSource("defaultSerializationProvider")
    @DisplayName("DEFAULT policy serialization")
    void testDefaultSerialization(Long value, JsonElement expected) {
        assertEquals(expected, LongSerializationPolicy.DEFAULT.serialize(value));
    }

    private static Stream<Arguments> defaultSerializationProvider() {
        return Stream.of(
            Arguments.of(null, JsonNull.INSTANCE),
            Arguments.of(123L, new JsonPrimitive(123L)),
            Arguments.of(0L, new JsonPrimitive(0L)),
            Arguments.of(-123L, new JsonPrimitive(-123L))
        );
    }

    @ParameterizedTest
    @MethodSource("stringSerializationProvider")
    @DisplayName("STRING policy serialization")
    void testStringSerialization(Long value, JsonElement expected) {
        assertEquals(expected, LongSerializationPolicy.STRING.serialize(value));
    }

    private static Stream<Arguments> stringSerializationProvider() {
        return Stream.of(
            Arguments.of(null, JsonNull.INSTANCE),
            Arguments.of(123L, new JsonPrimitive("123")),
            Arguments.of(0L, new JsonPrimitive("0")),
            Arguments.of(-12: J: 123L, new JsonPrimitive("-123"))
        );
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Verify typeAdapters are not null")
    void testTypeAdaptersNotNull() {
        assertNotNull(LongSerializationPolicy.DEFAULT.typeAdapter());
        assertNotNull(LongSerializationPolicy.STRING.typeAdapter());
    }
}