package com.google.gson.internal.reflect;

import com.google.gson.JsonIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReflectionHelperTest {

    private static class TestClass {
        private String name;
        private static final int STATIC_VAL = 42;
        public TestClass(String name) {
            this.name = name;
        }
        public void testMethod(int param) {}
    }

    @Test
    @DisplayName("isStatic should return true for static fields and classes")
    void testIsStatic() {
        assertTrue(ReflectionHelper.isStatic(TestClass.class));
        try {
            Field field = TestClass.class.getDeclaredField("STATIC_VAL");
            assertTrue(Modifier.isStatic(field.getModifiers()));
        } catch (NoSuchFieldException e) {
            fail(e);
        }
    }

    @Test
    @DisplayName("fieldToString should return formatted field name")
    void testFieldToString() throws NoSuchFieldException {
        Field field = TestClass.class.getDeclaredField("name");
        assertEquals("com.google.gson.internal.reflect.ReflectionHelperTest$TestClass#name",
                ReflectionHelper.fieldToString(field));
    }

    @Test
    @DisplayName("constructorToString should return formatted constructor signature")
    void testConstructorToString() throws NoSuchMethodException {
        Constructor<TestClass> constructor = TestClass.class.getConstructor(String.class);
        assertEquals("com.google.gson.internal.reflect.ReflectionHelperTest$TestClass(String)",
                ReflectionHelper.constructorToString(constructor));
    }

    @Test
    @DisplayName("getAccessibleObjectDescription should handle Field, Method, and Constructor")
    void testGetAccessibleObjectDescription() throws NoSuchFieldException, NoSuchMethodException {
        Field field = TestClass.class.getDeclaredField("name");
        Method method = TestClass.class.getMethod("testMethod", int.class);
        Constructor<TestClass> constructor = TestClass.class.getConstructor(String.class);

        assertEquals("field 'com.google.gson.internal.reflect.ReflectionHelperTest$TestClass#name'",
                ReflectionHelper.getAccessibleObjectDescription(field, false));

        assertEquals("method 'com.google.gson.internal.reflect.ReflectionHelperTest$TestClass#testMethod(int)'",
                ReflectionHelper.getAccessibleObjectDescription(method, false));

        assertEquals("constructor 'com.google.gson.internal.reflect.ReflectionHelperTest$TestClass(String)'",
                ReflectionHelper.getAccessibleObjectDescription(constructor, false));

        // Test uppercaseFirstLetter
        assertEquals("Field 'com.google.gson.internal.reflect.ReflectionHelperTest$TestClass#name'",
                ReflectionHelper.getAccessibleObjectDescription(field, true));
    }

    @Test
    @DisplayName("makeAccessible should make object accessible")
    void testMakeAccessible() throws NoSuchFieldException {
        Field field = TestClass.class.getDeclaredField("name");
        field.setAccessible(false);
        ReflectionHelper.makeAccessible(field);
        assertTrue(field.isAccessible());
    }

    @Test
    @DisplayName("tryMakeAccessible should return null on success")
    void testTryMakeAccessibleSuccess() throws NoSuchMethodException {
        Constructor<TestClass> constructor = TestClass.class.getConstructor(String.class);
        assertNull(ReflectionHelper.tryMakeAccessible(constructor));
    }

    @Test
    @DisplayName("createExceptionForUnexpectedIllegalAccess should throw RuntimeException")
    void testCreateExceptionForUnexpectedIllegalAccess() {
        IllegalAccessException cause = new IllegalAccessException("test cause");
        try {
            ReflectionHelper.createExceptionForUnexpectedIllegalAccess(cause);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Unexpected IllegalAccessException occurred"));
            assertEquals(cause, e.getCause());
        }
    }
}
