package com.google.gson;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link FieldAttributes}.  
 * <p>
 * These tests cover constructor validation and all delegate methods providing decision‑coverage:
 * <ul>
 *   <li>Null‑check in constructor</li>
 *   <li>Field‑level queries (declaring class, name, type, class)</li>
 *   <li>Annotation retrieval (present vs absent)</li>
 *   <li>Annotation collection</li>
 *   <li>Modifier checks (true/false)</li>
 *   <li>{@link Object#toString} delegation</li>
 * </ul>
 */
class FieldAttributesTest {

    /* ---- Test helpers ----------------------------------------------------- */

    @Retention(RetentionPolicy.RUNTIME)
    @interface MyAnn {
        String value() default "";
    }

    /** Simple class with a public annotated field */
    static class AnnotatedClass {
        @MyAnn("foo")
        public int annotatedField;
    }

    /** Simple class with a generic field */
    static class GenericClass {
        public java.util.List<String> genericList;
    }

    /* ---- Constructor ----------------------------------------------------- */

    @Test
    @DisplayName("Constructor throws NPE when null is passed")
    void constructorNullThrows() {
        assertThrows(NullPointerException.class, () -> new FieldAttributes(null));
    }

    /* ---- Delegated field information -------------------------------------- */

    @Test
    @DisplayName("getDeclaringClass returns the defining class")
    void testGetDeclaringClass() throws NoSuchFieldException {
        Field f = AnnotatedClass.class.getDeclaredField("annotatedField");
        FieldAttributes attr = new FieldAttributes(f);
        assertEquals(AnnotatedClass.class, attr.getDeclaringClass());
    }

    @Test
    @DisplayName("getName returns the field name")
    void testGetName() throws NoSuchFieldException {
        Field f = AnnotatedClass.class.getDeclaredField("annotatedField");
        FieldAttributes attr = new FieldAttributes(f);
        assertEquals("annotatedField", attr.getName());
    }

    @Test
    @DisplayName("getDeclaredType returns the generic type")
    void testGetDeclaredType() throws NoSuchFieldException {
        Field f = GenericClass.class.getDeclaredField("genericList");
        FieldAttributes attr = new FieldAttributes(f);
        Type declaredType = attr.getDeclaredType();
        // Type is ParameterizedType for List<String>
        assertTrue(declaredType instanceof java.lang.reflect.ParameterizedType);
        java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) declaredType;
        assertEquals(String.class, pt.getActualTypeArguments()[0]);
    }

    @Test
    @DisplayName("getDeclaredClass returns the raw class of the field")
    void testGetDeclaredClass() throws NoSuchFieldException {
        Field f = AnnotatedClass.class.getDeclaredField("annotatedField");
        FieldAttributes attr = new FieldAttributes(f);
        assertEquals(int.class, attr.getDeclaredClass());
    }

    /* ---- Annotation handling -------------------------------------------- */

    @Test
    @DisplayName("getAnnotation returns annotation instance when present")
    void testGetAnnotationPresent() throws NoSuchFieldException {
        Field f = AnnotatedClass.class.getDeclaredField("annotatedField");
        FieldAttributes attr = new FieldAttributes(f);
        MyAnn ann = attr.getAnnotation(MyAnn.class);
        assertNotNull(ann);
        assertEquals("foo", ann.value());
    }

    @Test
    @DisplayName("getAnnotation returns null when annotation absent")
    void testGetAnnotationAbsent() throws NoSuchFieldException {
        Field f = AnnotatedClass.class.getDeclaredField("annotatedField");
        FieldAttributes attr = new FieldAttributes(f);
        // Override is not present on the field
        Override override = attr.getAnnotation(Override.class);
        assertNull(override);
    }

    @Test
    @DisplayName("getAnnotations returns all annotations on the field")
    void testGetAnnotations() throws NoSuchFieldException {
        Field f = AnnotatedClass.class.getDeclaredField("annotatedField");
        FieldAttributes attr = new FieldAttributes(f);
        Set<Annotation> anns = attr.getAnnotations();
        assertNotNull(anns);
        assertTrue(anns.stream().anyMatch(a -> a.annotationType() == MyAnn.class));
    }

    /* ---- Modifier checks ------------------------------------------------- */

    @Test
    @DisplayName("hasModifier returns true for existing modifier")
    void testHasModifierTrue() throws NoSuchFieldException {
        Field f = AnnotatedClass.class.getDeclaredField("annotatedField");
        FieldAttributes attr = new FieldAttributes(f);
        assertTrue(attr.hasModifier(Modifier.PUBLIC));
    }

    @Test
    @DisplayName("hasModifier returns false for non‑present modifier")
    void testHasModifierFalse() throws NoSuchFieldException {
        Field f = AnnotatedClass.class.getDeclaredField("annotatedField");
        FieldAttributes attr = new FieldAttributes(f);
        assertFalse(attr.hasModifier(Modifier.PRIVATE));
    }

    /* ---- toString -------------------------------------------------------- */

    @Test
    @DisplayName("toString delegates to the underlying field")
    void testToString() throws NoSuchFieldException {
        Field f = AnnotatedClass.class.getDeclaredField("annotatedField");
        FieldAttributes attr = new FieldAttributes(f);
        assertEquals(f.toString(), attr.toString());
    }
}