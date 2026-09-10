package com.google.gson;

import com.google.gson.reflect.FieldAttributes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FieldAttributes")
class FieldAttributesTest {

    @Retention(RetentionPolicy.RUNTIME)
    @interface MyAnn {
    }

    private static class Sample {
        @MyAnn
        private final String foo = "bar";

        public static int staticField = 42;
    }

    @Test
    @DisplayName("construction stores field")
    void construction() throws NoSuchFieldException {
        Field f = Sample.class.getDeclaredField("foo");
        FieldAttributes fa = new FieldAttributes(f);
        assertEquals(Sample.class, fa.getDeclaringClass());
        assertEquals("foo", fa.getName());
    }

    @Test
    @DisplayName("getDeclaredType and getDeclaredClass")
    void declaredType() throws NoSuchFieldException {
        Field f = Sample.class.getDeclaredField("foo");
        FieldAttributes fa = new FieldAttributes(f);
        assertEquals(String.class, fa.getDeclaredClass());
        assertEquals(String.class, fa.getDeclaredType());
    }

    @Test
    @DisplayName("annotation retrieval")
    void annotations() throws NoSuchFieldException {
        Field f = Sample.class.getDeclaredField("foo");
        FieldAttributes fa = new FieldAttributes(f);
        MyAnn ann = fa.getAnnotation(MyAnn.class);
        assertNotNull(ann);

        Collection<Annotation> annColl = fa.getAnnotations();
        assertTrue(annColl.iterator().hasNext());
    }

    @Test
    @DisplayName("modifier checks")
    void modifiers() throws NoSuchFieldException {
        Field f = Sample.class.getDeclaredField("staticField");
        FieldAttributes fa = new FieldAttributes(f);
        assertTrue(fa.hasModifier(Modifier.STATIC));
        assertFalse(fa.hasModifier(Modifier.PRIVATE));
    }

    @Test
    @DisplayName("toString delegates to field")
    void toStringTest() throws NoSuchFieldException {
        Field f = Sample.class.getDeclaredField("foo");
        FieldAttributes fa = new FieldAttributes(f);
        assertEquals(f.toString(), fa.toString());
    }
}