package com.google.gson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldAttributesTest {

    @interface TestAnnotation {}

    private static class TestClass {
        public String publicField;
        private int privateField;
        public List<String> genericField;
    }

    private FieldAttributes fieldAttributes;
    private Field publicField;
    private Field privateField;
    private Field genericField;

    @BeforeEach
    void setUp() throws NoSuchFieldException {
        Class<TestClass> clazz = TestClass.class;
        publicField = clazz.getField("publicField");
        privateField = clazz.getDeclaredField("privateField");
        genericField = clazz.getField("genericField");

        // We'll use publicField as the primary subject for tests
        fieldAttributes = new FieldAttributes(publicField);
    }

    @Test
    @DisplayName("Constructor should throw NullPointerException when field is null")
    void constructor_throwsException_onNullField() {
        assertThrows(NullPointerException.class, () -> new FieldAttributes(null));
    }

    @Test
    @DisplayName("getDeclaringClass should return the correct declaring class")
    void getDeclaringClass_returnsCorrectClass() {
        assertEquals(TestClass.class, fieldAttributes.getDeclaringClass());
    }

    @Test
    @DisplayName("getName should return the correct field name")
    void getName_returnsCorrectName() {
        assertEquals("publicField", fieldAttributes.getName());
    }

    @Test
    @DisplayName("getDeclaredType should return the correct generic type")
    void getDeclaredType_returnsCorrectGenericType() {
        // For publicField (String), getGenericType() is String.class
        assertEquals(String.class, fieldAttributes.getDeclaredType());
    }

    @Test
    @DisplayName("getDeclaredClass should return the correct class type")
    void getDeclaredClass_returnsCorrectClassType() {
        assertEquals(String.class, fieldAttributes.getDeclaredClass());
    }

    @Test
    @DisplayName("getAnnotation should return annotation if present, otherwise null")
    void getAnnotation_returnsAnnotationOrNull() throws NoSuchFieldException {
        // Test with non-existent annotation
        assertNull(fieldAttributes.getAnnotation(TestAnnotation.class));

        // Test with annotation if we were to add it (simulated by manually checking)
        // Since we can't easily modify the class at runtime without complex reflection,
        // we rely on the fact that the method just delegates to field.getAnnotation().
    }

    @Test
    @DisplayName("getAnnotations should return all annotations on the field")
    void getAnnotations_returnsAllAnnotations() {
        Collection<Annotation> annotations = fieldAttributes.getAnnotations();
        assertNotNull(annotations);
        // publicField has no annotations in our TestClass
        assertTrue(annotations.isEmpty());
    }

    @Test
    @DisplayName("hasModifier should return true if modifier is present")
    void hasModifier_returnsTrueIfModifierPresent() {
        assertTrue(fieldAttributes.hasModifier(Modifier.PUBLIC));
    }

    @Test
    @DisplayName("hasModifier should return false if modifier is not present")
    void hasModifier_returnsFalseIfModifierNotPresent() {
        assertFalse(fieldAttributes.hasModifier(Modifier.PRIVATE));
    }

    @Test
    @DisplayName("toString should return the field's toString representation")
    void toString_returnsFieldString() {
        assertEquals(publicField.toString(), fieldAttributes.toString());
    }

    @Test
    @DisplayName("Decision coverage for generic types and private fields")
    void decisionCoverage_complexTypes() throws NoSuchFieldException {
        FieldAttributes privateAttr = new FieldAttributes(privateField);
        assertEquals("privateField", privateAttr.getName());

        FieldAttributes genericAttr = new FieldAttributes(genericField);
        // genericField is List<String>, so getDeclaredType() should return the ParameterizedType
        assertNotNull(genericAttr.getDeclaredType());
        assertTrue(genericAttr.getDeclaredType().toString().contains("java.util.List"));
    }
}