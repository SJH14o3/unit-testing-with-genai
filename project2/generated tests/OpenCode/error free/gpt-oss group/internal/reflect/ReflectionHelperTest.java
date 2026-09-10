package com.google.gson.internal.reflect;

import com.google.gson.JsonIOException;
import com.google.gson.internal.ReflectionAccessFilterHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ReflectionHelper")
class ReflectionHelperTest {

    @Retention(RetentionPolicy.RUNTIME)
    @interface DummyAnn {
    }

    private static class TestClass {
        @DummyAnn
        private final int field = 0;

        @DummyAnn
        public void foo(int a, String b) {
        }

        public TestClass() {
        }

        public TestClass(String s) {
        }
    }

    @Test
    @DisplayName("getAccessibleObjectDescription formats field")
    void fieldDescription() throws NoSuchFieldException {
        Field f = TestClass.class.getDeclaredField("field");
        String desc = ReflectionHelper.getAccessibleObjectDescription(f, false);
        assertTrue(desc.startsWith("field '"));
        assertTrue(desc.contains(TestClass.class.getName() + "#field"));
    }

    @Test
    @DisplayName("getAccessibleObjectDescription formats method")
    void methodDescription() throws NoSuchMethodException {
        Method m = TestClass.class.getMethod("foo", int.class, String.class);
        String desc = ReflectionHelper.getAccessibleObjectDescription(m, false);
        assertTrue(desc.startsWith("method '"));
        assertTrue(desc.contains(TestClass.class.getName() + "#foo"));
    }

    @Test
    @DisplayName("getAccessibleObjectDescription formats constructor")
    void ctorDescription() throws NoSuchMethodException {
        Constructor<TestClass> ctor = TestClass.class.getConstructor(String.class);
        String desc = ReflectionHelper.getAccessibleObjectDescription(ctor, false);
        assertTrue(desc.startsWith("constructor '"));
        assertTrue(desc.contains(TestClass.class.getName()));
    }

    @Test
    @DisplayName("makeAccessible succeeds for a private field")
    void makeAccessibleSuccess() throws Exception {
        Field f = TestClass.class.getDeclaredField("field");
        // default accessible flag is false
        assertFalse(f.canAccess(null));
        ReflectionHelper.makeAccessible(f);
        assertTrue(f.canAccess(null));
    }

    @Test
    @DisplayName("tryMakeAccessible returns null on success")
    void tryMakeAccessibleSuccess() throws Exception {
        Constructor<TestClass> ctor = TestClass.class.getDeclaredConstructor();
        String msg = ReflectionHelper.tryMakeAccessible(ctor);
        assertNull(msg);
    }

    @Test
    @DisplayName("tryMakeAccessible returns failure message for inaccessible ctor")
    void tryMakeAccessibleFailure() throws Exception {
        Constructor<TestClass> ctor = TestClass.class.getDeclaredConstructor(String.class);
        // Make it non‑public to force failure
        ctor.setAccessible(false);
        String msg = ReflectionHelper.tryMakeAccessible(ctor);
        assertNotNull(msg);
        assertTrue(msg.contains("Failed making constructor"));
    }
}