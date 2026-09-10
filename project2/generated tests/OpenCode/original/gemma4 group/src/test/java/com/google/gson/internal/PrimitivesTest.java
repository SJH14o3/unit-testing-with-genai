package com.google.gson.internal;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class PrimitivesTest {

  @Test
  @DisplayName("Test isPrimitive")
  void testIsPrimitive() {
    assertTrue(Primitives.isPrimitive(int.class));
    assertTrue(Primitives.isPrimitive(boolean.class));
    assertFalse(Primitives.isPrimitive(String.class));
  }

  @Test
  @DisplayName("Test isWrapperType")
  void testIsWrapperType() {
    assertTrue(Primitives.isWrapperType(Integer.class));
    assertTrue(Primitives.isWrapperType(Double.class));
    assertTrue(Primitives.isWrapperType(Void.class));
    assertFalse(Primitives.isWrapperType(String.class));
  }

  @Test
  @DisplayName("Test wrap")
  void testWrap() {
    assertEquals(Integer.class, Primitives.wrap(int.class));
    assertEquals(Double.class, Primitives.wrap(double.class));
    assertEquals(String.class, Primitives.wrap(String.class));
  }

  @Test
  @DisplayName("Test unwrap")
  void testUnwrap() {
    assertEquals(int.class, Primitives.unwrap(Integer.class));
    assertEquals(double.class, Primitives.unwrap(Double.class));
    assertEquals(String.class, Primitives.unwrap(String.class));
  }
}
