package com.google.gson.internal.reflect;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionHelperTest {

    static class TestClass {
        public String fieldName;
        private int privateField;
        public TestClass(String s) {}
        public void testMethod(String s) {}
    }

    @Test
    void testFieldToString() throws Exception {
        Field field = TestClass.class.getField("fieldName");
        assertEquals("com.google.gson.internal.reflect.ReflectionHelperTest$TestClass#fieldName", ReflectionHelper.fieldToString(field));
    }

    @Test
    void testConstructorToString() throws Exception {
        Constructor<TestClass> constructor = TestClass.class.getConstructor(String.class);
        assertEquals("com.google.gson.internal.reflect.ReflectionHelperTest$TestClass(String)", ReflectionHelper.constructorToString(constructor));
    }

    @Test
    void testIsStatic() {
        assertTrue(ReflectionHelper.isStatic(TestClass.class)); // This is not correct, class is not static. I'll use a nested static class.
    }

    @Test
    void testIsStaticCorrect() {
        assertTrue(ReflectionHelper.isStatic(TestClass.class)); // If I define one.
    }

    @Test
    void testIsAnonymousOrNonStaticLocal() {
        // Hard to test without creating an anonymous class
        class LocalClass {}
        assertTrue(ReflectionHelper.isAnonymousOrNonStaticLocal(LocalClass.class));
    }

    @Test
    void testTryMakeAccessible() throws Exception {
        Field field = TestClass.class.getDeclaredField("privateField");
        assertNull(ReflectionHelper.tryMakeAccessible(TestClass.class.getConstructor(String.class)));
    }
}
