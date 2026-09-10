package com.google.gson;

import org.junit.jupiter.api.Test;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class FieldAttributesTest {

  @Test
  void testFieldAttributes() throws NoSuchFieldException {
    Field field = TestClass.class.getDeclaredField("testField");
    FieldAttributes attributes = new FieldAttributes(field);

    assertEquals(TestClass.class, attributes.getDeclaringClass());
    assertEquals("testField", attributes.getName());
    assertEquals(String.class, attributes.getDeclaredClass());
    assertEquals(String.class, attributes.getDeclaredType());
    assertTrue(attributes.hasModifier(Modifier.PRIVATE));
    
    Collection<Annotation> annotations = attributes.getAnnotations();
    assertNotNull(annotations);
  }

  @Test
  void testGetAnnotation() throws NoSuchFieldException {
    Field field = TestClass.class.getDeclaredField("annotatedField");
    FieldAttributes attributes = new FieldAttributes(field);

    assertNotNull(attributes.getAnnotation(TestAnnotation.class));
    assertNull(attributes.getAnnotation(Deprecated.class));
  }

  @Test
  void testToString() throws NoSuchFieldException {
    Field field = TestClass.class.getDeclaredField("testField");
    FieldAttributes attributes = new FieldAttributes(field);
    
    // field.toString() usually contains the field name and class name
    assertTrue(attributes.toString().contains("testField"));
  }

  @Test
  void testConstructorThrowsExceptionOnNull() {
    assertThrows(NullPointerException.class, () -> new FieldAttributes(null));
  }

  private static class TestClass {
    private String testField;
    @TestAnnotation
    private String annotatedField;
  }

  @interface TestAnnotation {}
}
