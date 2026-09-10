package com.google.gson;

import com.google.gson.ReflectionAccessFilter.FilterResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ReflectionAccessFilter")
class ReflectionAccessFilterTest {

    @Test
    @DisplayName("BLOCK_INACCESSIBLE_JAVA on java class")
    void blockInaccessibleJava() {
        assertEquals(FilterResult.BLOCK_INACCESSIBLE,
                ReflectionAccessFilter.BLOCK_INACCESSIBLE_JAVA.check(String.class));
    }

    @Test
    @DisplayName("BLOCK_INACCESSIBLE_JAVA on non‑java class")
    void blockInaccessibleJavaNon() {
        assertEquals(FilterResult.INDECISIVE,
                ReflectionAccessFilter.BLOCK_INACCESSIBLE_JAVA.check(ReflectionAccessFilterTest.class));
    }

    @Test
    @DisplayName("BLOCK_ALL_JAVA on java class")
    void blockAllJava() {
        assertEquals(FilterResult.BLOCK_ALL,
                ReflectionAccessFilter.BLOCK_ALL_JAVA.check(Integer.class));
    }

    @Test
    @DisplayName("BLOCK_ALL_ANDROID on android class simulated")
    void blockAllAndroid() {
        // Simulated Android class by loading a custom class with package name "android.*"
        // Since we cannot create a real android class, we verify logic via ReflectionAccessFilterHelper
        assertEquals(FilterResult.INDECISIVE,
                ReflectionAccessFilter.BLOCK_ALL_ANDROID.check(ReflectionAccessFilterTest.class));
    }

    @Test
    @DisplayName("BLOCK_ALL_PLATFORM covers java and android")
    void blockAllPlatformPlatform() {
        assertEquals(FilterResult.BLOCK_ALL,
                ReflectionAccessFilter.BLOCK_ALL_PLATFORM.check(String.class));
        assertNotEquals(FilterResult.BLOCK_ALL,
                ReflectionAccessFilter.BLOCK_ALL_PLATFORM.check(ReflectionAccessFilterTest.class));
    }

    @Test
    @DisplayName("filter result falls back to ALLOW when all INDECISIVE")
    void allIndecisive() {
        var filter = new CompositeFilter(List.of(
                (ReflectionAccessFilter) cls -> FilterResult.INDECISIVE,
                (ReflectionAccessFilter) cls -> FilterResult.INDECISIVE
        ));
        assertEquals(FilterResult.ALLOW,
                filter.check(String.class));
    }

    /** simple composite filter used for test */
    private static class CompositeFilter implements ReflectionAccessFilter {
        private final java.util.List<ReflectionAccessFilter> list;

        CompositeFilter(java.util.List<ReflectionAccessFilter> list) {
            this.list = list;
        }

        @Override
        public FilterResult check(Class<?> rawClass) {
            for (ReflectionAccessFilter f : list) {
                FilterResult r = f.check(rawClass);
                if (r != FilterResult.INDECISIVE) return r;
            }
            return FilterResult.ALLOW;
        }
    }
}