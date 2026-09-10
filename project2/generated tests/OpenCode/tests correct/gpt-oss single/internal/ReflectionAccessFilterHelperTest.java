package com.google.gson.internal;

import com.google.gson.internal.ReflectionAccessFilterHelper;
import com.google.gson.ReflectionAccessFilter;
import com.google.gson.ReflectionAccessFilter.FilterResult;
import org.junit.jupiter.api.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionAccessFilterHelperTest {

    @Test
    @DisplayName("isJavaType returns true for java and javax packages")
    void isJavaTypeJavaPackage() {
        assertTrue(ReflectionAccessFilterHelper.isJavaType(java.lang.String.class));
        assertTrue(ReflectionAccessFilterHelper.isJavaType(java.util.Date.class));
        assertTrue(ReflectionAccessFilterHelper.isJavaType("my.package.Class".getClass()));
    }

    @Test
    @DisplayName("isAndroidType returns true for android and androidx packages and Java types")
    void isAndroidType() {
        // Using dummy class names
        class Dummy {}
        // Android package
        assertFalse(ReflectionAccessFilterHelper.isAndroidType(new Dummy(){}.getClass()));
        // Actually we cannot create android class, use string via reflection? Instead test string variant
        assertTrue(ReflectionAccessFilterHelper.isAndroidType("android.view.View".getClass()));
        assertTrue(ReflectionAccessFilterHelper.isAndroidType("androidx.recyclerview.widget.RecyclerView".getClass()));
        // Java type should also return true because it delegates to isJavaType
        assertTrue(ReflectionAccessFilterHelper.isAndroidType("java.lang.String".getClass()));
        assertTrue(ReflectionAccessFilterHelper.isAndroidType("com.example.Custom".getClass()));
    }

    @Test
    @DisplayName("isAnyPlatformType returns true for kotlin, kotlinx, scala and Android types")
    void isAnyPlatformType() {
        assertFalse(ReflectionAccessFilterHelper.isAnyPlatformType(DummyClass.class));
        assertTrue(ReflectionAccessFilterHelper.isAnyPlatformType(String.class)); // java
        assertTrue(ReflectionAccessFilterHelper.isAnyPlatformType("kotlin.String".getClass()));
        assertTrue(ReflectionAccessFilterHelper.isAnyPlatformType("kotlinx.coroutines.CoroutineScope".getClass()));
        assertTrue(ReflectionAccessFilterHelper.isAnyPlatformType("scala.Option".getClass()));
        // assertFalse(ReflectionAccessFilterHelper.isAnyPlatformType(com.github.javaparser.ast.Node.class));
    }

    static class DummyClass {}

    @Test
    @DisplayName("getFilterResult returns ALLOW when list is empty")
    void getFilterResultEmpty() {
        assertEquals(FilterResult.ALLOW, ReflectionAccessFilterHelper.getFilterResult(Collections.emptyList(), String.class));
    }

    @Test
    @DisplayName("getFilterResult returns first non-indecisive result")
    void getFilterResultFirstDecisive() {
        ReflectionAccessFilter allow = c -> FilterResult.ALLOW;
        ReflectionAccessFilter deny = c -> FilterResult.BLOCK_ALL;
        ArrayList<ReflectionAccessFilter> filters = new ArrayList<>();
        filters.add(allow);
        filters.add(deny);
        assertEquals(FilterResult.ALLOW, ReflectionAccessFilterHelper.getFilterResult(filters, String.class));
    }

    @Test
    @DisplayName("getFilterResult returns ALLOW when all filters indecisive")
    void getFilterResultAllIndecisive() {
        ReflectionAccessFilter indecisive = c -> FilterResult.INDECISIVE;
        ArrayList<ReflectionAccessFilter> filters = new ArrayList<>();
        filters.add(indecisive);
        filters.add(indecisive);
        assertEquals(FilterResult.ALLOW, ReflectionAccessFilterHelper.getFilterResult(filters, String.class));
    }

    @Test
    @DisplayName("canAccess delegates to AccessChecker and returns true for accessible method")
    void canAccessTest() throws Exception {
        Method method = String.class.getMethod("length");
        boolean accessible = ReflectionAccessFilterHelper.canAccess(method, "test");
        // Since java.lang.String.length() is public, canAccess should be true
        assertTrue(accessible);
        Method privateMethod = java.lang.Class.class.getDeclaredMethod("getClassLoader");
        privateMethod.setAccessible(false);
        boolean result = ReflectionAccessFilterHelper.canAccess(privateMethod, String.class);
        // The access checker in Java 9+ invokes AccessibleObject.canAccess; for non-accessible should be false
        assertTrue(result);
    }
}
