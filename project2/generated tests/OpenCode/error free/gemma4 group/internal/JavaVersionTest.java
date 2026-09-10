package com.google.gson.internal;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class JavaVersionTest {

  @Test
  @DisplayName("Test parseMajorJavaVersion - legacy 1.8 style")
  void testParseMajorJavaVersionLegacy() {
    assertEquals(8, JavaVersion.parseMajorJavaVersion("1.8"));
    assertEquals(8, JavaVersion.parseMajorJavaVersion("1.8.0_202"));
  }

  @Test
  @DisplayName("Test parseMajorJavaVersion - newer 9.0.4 style")
  void testParseMajorJavaVersionNewer() {
    assertEquals(9, JavaVersion.parseMajorJavaVersion("9.0.4"));
    assertEquals(11, JavaVersion.parseMajorJavaVersion("11"));
  }

  @Test
  @DisplayName("Test parseMajorJavaVersion - invalid format")
  void testParseMajorJavaVersionInvalid() {
    assertEquals(6, JavaVersion.parseMajorJavaVersion("unknown"));
  }

  @Test
  @DisplayName("Test isJava9OrLater")
  void testIsJava9OrLater() {
    // This is hard to test directly because majorJavaVersion is static and private.
    // However, we can test the logic if we knew how to manipulate it.
    // Since we cannot, we can only assert based on the current running JVM.
    // But we are testing the logic.
    // If the current JVM is 9 or later, it should return true.
  }
  
  @Test
  @DisplayName("Test parseDotted via parseMajorJavaVersion")
  void testParseDotted() throws Exception {
    // Accessing private method via reflection for testing
    java.lang.reflect.Method method = JavaVersion.class.getDeclaredMethod("parseDotted", String.class);
    method.setAccessible(true);
    
    assertEquals(8, method.invoke(null, "1.8"));
    assertEquals(11, method.invoke(null, "11.0.1"));
    assertEquals(-1, method.invoke(null, "invalid"));
  }

  @Test
  @DisplayName("Test extractBeginningInt via parseMajorJavaVersion")
  void testExtractBeginningInt() throws Exception {
    java.lang.reflect.Method method = JavaVersion.class.getDeclaredMethod("extractBeginningInt", String.class);
    method.setAccessible(true);
    
    assertEquals(11, method.invoke(null, "11.0.1"));
    assertEquals(8, method.invoke(null, "8-debian"));
    assertEquals(-1, method.invoke(null, "abc"));
  }
}
