package com.google.gson.internal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class JavaVersionTest {

  @Test
  @DisplayName("parseMajorJavaVersion handles legacy 1.x format")
  void legacy() throws Exception {
    Method m = JavaVersion.class.getDeclaredMethod("parseMajorJavaVersion", String.class);
    m.setAccessible(true);
    assertEquals(8, m.invoke(null, "1.8.0_152"));
  }

  @Test
  @DisplayName("parseMajorJavaVersion handles recent 9+ format")
  void newStyle() throws Exception {
    Method m = JavaVersion.class.getDeclaredMethod("parseMajorJavaVersion", String.class);
    m.setAccessible(true);
    assertEquals(11, m.invoke(null, "11.0.12"));
  }

  @Test
  @DisplayName("parseMajorJavaVersion defaults to 6 on unknown input")
  void unknown() throws Exception {
    Method m = JavaVersion.class.getDeclaredMethod("parseMajorJavaVersion", String.class);
    m.setAccessible(true);
    assertEquals(6, m.invoke(null, "foobar"));
  }

  @Test
  @DisplayName("getMajorJavaVersion reflects system property")
  void systemProperty() {
    String original = System.getProperty("java.version");
    try {
      System.setProperty("java.version", "1.7.0_80");
      assertEquals(7, JavaVersion.getMajorJavaVersion());
    } finally {
      if (original != null) {
        System.setProperty("java.version", original);
      } else {
        System.clearProperty("java.version");
      }
    }
  }
}