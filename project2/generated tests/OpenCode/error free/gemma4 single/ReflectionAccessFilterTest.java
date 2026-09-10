package com.google.gson;

import com.google.gson.ReflectionAccessFilter.FilterResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ReflectionAccessFilter Test")
class ReflectionAccessFilterTest {

    @ParameterizedTest(name="Filter {0} should return {1} for class {2}")
    @MethodSource("provideFilterTestData")
    @DisplayName("Verify all static filters against various class types")
    void verifyFilters(ReflectionAccessFilter filter,
                       FilterResult expectedResult,
                       Class<?> testClass) {
        assertEquals(expectedResult, filter.check(testClass));
    }

    private static Stream<Arguments> provideFilterTestData() {
        return Stream.of(
                // BLOCK_INACCESSIBLE_JAVA tests
                Arguments.of(ReflectionAccessFilter.BLOCK_INACCESSIBLE_JAVA, FilterResult.BLOCK_INACCESSIBLE, String.class),
                Arguments.of(ReflectionAccessFilter.BLOCK_INACCESSIBLE_JAVA, FilterResult.BLOCK_INACCESSIBLE, javax.crypto.KeyGenerator.class),
                Arguments.of(ReflectionAccessFilter.BLOCK_INACCESSIBLE_JAVA, FilterResult.INDECISIVE, Integer.class),
                Arguments.of(ReflectionAccessFilter.BLOCK_INACCESSIBLE_JAVA, FilterResult.INDECISIVE, Object.class),

                // BLOCK_ALL_JAVA tests
                Arguments.of(ReflectionAccessFilter.BLOCK_ALL_JAVA, FilterResult.BLOCK_ALL, String.class),
                Arguments.of(ReflectionAccessFilter.BLOCK_ALL_JAVA, FilterResult.BLOCK_ALL, java.util.List.class),
                Arguments.of(ReflectionAccessFilter.BLOCK_ALL_JAVA, FilterResult.INDECISIVE, Object.class),

                // BLOCK_ALL_ANDROID tests
                // Note: testing android.* or androidx.* requires environment setup, 
                // but we can check if the logic handles it if the class name was android.foo.Bar.
                // Since we cannot easily create android.foo.Bar classes without dependencies, 
                // we rely on the provided logic checking for prefixes.
                // However, we can test the 'INDECISIVE' path for non-android types.
                Arguments.of(ReflectionAccessFilter.BLOCK_ALL_ANDROID, FilterResult.INDECISIVE, String.class),

                // BLOCK_ALL_PLATFORM tests
                // We'll use a class that might trigger the 'else' (INDECISIVE) branch.
                Arguments.of(ReflectionAccessFilter.BLOCK_ALL_PLATFORM, FilterResult.INDECISIVE, Object.class)
        );
    }

    @Test
    @DisplayName("Verify toString implementations")
    void verifyToString() {
        assertEquals("ReflectionAccessFilter#BLOCK_INACCESSIBLE_JAVA",
                ReflectionAccessFilter.BLOCK_INACCESSIBLE_JAVA.toString());
        assertEquals("ReflectionAccessFilter#BLOCK_ALL_JAVA",
                ReflectionAccessFilter.BLOCK_ALL_JAVA.toString());
        assertEquals("ReflectionAccessFilter#BLOCK_ALL_ANDROID",
                ReflectionAccessFilter.BLOCK_ALL_ANDROID.toString());
        assertEquals("ReflectionAccessFilter#BLOCK_ALL_PLATFORM",
                ReflectionAccessFilter.BLOCK_ALL_PLATFORM.toString());
    }
}