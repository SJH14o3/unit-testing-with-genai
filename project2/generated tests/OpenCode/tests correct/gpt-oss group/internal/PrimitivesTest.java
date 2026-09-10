package com.google.gson.internal;

import java.lang.reflect.Type;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrimitivesTest {

  @Test
  @DisplayName("isPrimitive recognizes primitives")
  void primitiveCheck() {
    assertTrue(Primitives.isPrimitive(int.class));
    assertFalse(Primitives.isPrimitive(Integer.class));
  }

  @Test
  @DisplayName("isWrapperType recognizes wrappers")
  void wrapperCheck() {
    assertTrue(Primitives.isWrapperType(Integer.class));
    assertFalse(Primitives.isWrapperType(int.class));
  }

  @Nested
  @DisplayName("wrap returns wrapper for primitives")
  class WrapTest {
    @Test
    void intWrap() {
      assertEquals(Integer.class, Primitives.wrap(int.class));
    }
    @Test
    void primitiveWrap() {
      assertSame(String.class, Primitives.wrap(String.class));
    }
  }

  @Nested
  @DisplayName("unwrap returns primitive for wrappers")
  class UnwrapTest {
    @Test
    void intUnwrap() {
      assertEquals(int.class, Primitives.unwrap(Integer.class));
    }
    @Test
    void primitiveUnwrap() {
      assertSame(String.class, Primitives.unwrap(String.class));
    }
  }
}