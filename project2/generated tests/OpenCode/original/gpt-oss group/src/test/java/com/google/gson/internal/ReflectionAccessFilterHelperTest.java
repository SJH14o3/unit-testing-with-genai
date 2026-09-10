package com.google.gson.internal;

import com.google.gson.ReflectionAccessFilterHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ReflectionAccessFilterHelper")
class ReflectionAccessFilterHelperTest {

    @Test
    @DisplayName("isJavaType detects java package")
    void isJavaTypeJava() {
        assertTrue(ReflectionAccessFilterHelper.isJavaType(String.class));
    }

    @Test
    @DisplayName("isJavaType rejects non‑Java types")
    void isJavaTypeNonJava() {
        assertFalse(ReflectionAccessFilterHelper.isJavaType(ReflectionAccessFilterHelperTest.class));
    }

    @Test
    @DisplayName("isAndroidType falls back to java check")
    void isAndroidType() {
        assertFalse(ReflectionAccessFilterHelper.isAndroidType(ReflectionAccessFilterHelperTest.class));
    }

    @Test
    @DisplayName("isAnyPlatformType includes java and kotlin")
    void isAnyPlatformType() {
        assertTrue(ReflectionAccessFilterHelper.isAnyPlatformType(String.class));
        assertFalse(ReflectionAccessFilterHelper.isAnyPlatformType(ReflectionAccessFilterHelperTest.class));
    }

    @Test
    @DisplayName("getFilterResult uses first decisive filter")
    void getFilterResultDecisive() {
        // Custom filters that prefer BLOCK_ALL for String, otherwise INDECISIVE
        var filterAll = new com.google.gson.ReflectionAccessFilter() {
            @Override
            public com.google.gson.ReflectionAccessFilter.FilterResult check(Class<?> rawClass) {
                return rawClass == String.class
                        ? com.google.gson.ReflectionAccessFilter.FilterResult.BLOCK_ALL
                        : com.google.gson.ReflectionAccessFilter.FilterResult.INDECISIVE;
            }
        };

        var filters = new ArrayList<com.google.gson.ReflectionAccessFilter>();
        filters.add(filterAll);
        filters.add(com.google.gson.ReflectionAccessFilter.BLOCK_INACCESSIBLE_JAVA); // should be ignored

        assertEquals(com.google.gson.ReflectionAccessFilter.FilterResult.BLOCK_ALL,
                ReflectionAccessFilterHelper.getFilterResult(filters, String.class));
        assertEquals(com.google.gson.ReflectionAccessFilter.FilterResult.ALPHABET,
                // fall‑back to ALLOW on INDECISIVE only filter
                ReflectionAccessFilterHelper.getFilterResult(
                        List.of(() -> com.google.gson.ReflectionAccessFilter.FilterResult.INDECISIVE), Dummy.class));
    }

    /** Dummy class for type references */
    private static class Dummy {
    }
}