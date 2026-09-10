package com.google.gson;

import android.test.AndroidDummy;
import kotlin.foo.KotlinDummy;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ReflectionAccessFilter} static filter instances.
 *
 * <p>Each test covers the true/false branches of {@link
 * ReflectionAccessFilterHelper#isJavaType(Class)} and the corresponding helper methods used by the
 * filter implementations.
 */
class ReflectionAccessFilterTest {

  /* --------------------------------------------------------------------- *
   * Tests for BLOCK_INACCESSIBLE_JAVA
   * --------------------------------------------------------------------- */

  @Test
  @DisplayName("BLOCK_INACCESSIBLE_JAVA: java type => BLOCK_INACCESSIBLE")
  void blockInaccessibleJava_javaClass() {
    var result = ReflectionAccessFilter.BLOCK_INACCESSIBLE_JAVA.check(String.class);
    assertEquals(ReflectionAccessFilter.FilterResult.BLOCK_INACCESSIBLE, result);
  }

  @Test
  @DisplayName("BLOCK_INACCESSIBLE_JAVA: non‑java type => INDECISIVE")
  void blockInaccessibleJava_nonJavaClass() {
    var result = ReflectionAccessFilter.BLOCK_INACCESSIBLE_JAVA.check(NonJavaClass.class);
    assertEquals(ReflectionAccessFilter.FilterResult.INDECISIVE, result);
  }

  /* --------------------------------------------------------------------- *
   * Tests for BLOCK_ALL_JAVA
   * --------------------------------------------------------------------- */

  @Test
  @DisplayName("BLOCK_ALL_JAVA: java type => BLOCK_ALL")
  void blockAllJava_javaClass() {
    var result = ReflectionAccessFilter.BLOCK_ALL_JAVA.check(Integer.class);
    assertEquals(ReflectionAccessFilter.FilterResult.BLOCK_ALL, result);
  }

  @Test
  @DisplayName("BLOCK_ALL_JAVA: non‑java type => INDECISIVE")
  void blockAllJava_nonJavaClass() {
    var result = ReflectionAccessFilter.BLOCK_ALL_JAVA.check(NonJavaClass.class);
    assertEquals(ReflectionAccessFilter.FilterResult.INDECISIVE, result);
  }

  /* --------------------------------------------------------------------- *
   * Tests for BLOCK_ALL_ANDROID
   * --------------------------------------------------------------------- */

  @Test
  @DisplayName("BLOCK_ALL_ANDROID: android type => BLOCK_ALL")
  void blockAllAndroid_androidClass() {
    var result = ReflectionAccessFilter.BLOCK_ALL_ANDROID.check(AndroidDummy.class);
    assertEquals(ReflectionAccessFilter.FilterResult.BLOCK_ALL, result);
  }

  @Test
  @DisplayName("BLOCK_ALL_ANDROID: non‑android type => INDECISIVE")
  void blockAllAndroid_nonAndroidClass() {
    var result = ReflectionAccessFilter.BLOCK_ALL_ANDROID.check(NonJavaClass.class);
    assertEquals(ReflectionAccessFilter.FilterResult.INDECISIVE, result);
  }

  /* --------------------------------------------------------------------- *
   * Tests for BLOCK_ALL_PLATFORM
   * --------------------------------------------------------------------- */

  @Test
  @DisplayName("BLOCK_ALL_PLATFORM: platform type (android) => BLOCK_ALL")
  void blockAllPlatform_android() {
    var result = ReflectionAccessFilter.BLOCK_ALL_PLATFORM.check(AndroidDummy.class);
    assertEquals(ReflectionAccessFilter.FilterResult.BLOCK_ALL, result);
  }

  @Test
  @DisplayName("BLOCK_ALL_PLATFORM: platform type (kotlin) => BLOCK_ALL")
  void blockAllPlatform_kotlin() {
    var result = ReflectionAccessFilter.BLOCK_ALL_PLATFORM.check(KotlinDummy.class);
    assertEquals(ReflectionAccessFilter.FilterResult.BLOCK_ALL, result);
  }

  @Test
  @DisplayName("BLOCK_ALL_PLATFORM: non‑platform type => INDECISIVE")
  void blockAllPlatform_nonPlatform() {
    var result = ReflectionAccessFilter.BLOCK_ALL_PLATFORM.check(NonJavaClass.class);
    assertEquals(ReflectionAccessFilter.FilterResult.INDECISIVE, result);
  }

  /* --------------------------------------------------------------------- *
   * Utility classes
   * --------------------------------------------------------------------- */

  /** A simple non‑Java class used to exercise the INDECISIVE branch. */
  static final class NonJavaClass {}

  // The following dummy classes reside in special packages to trigger the helper
  // methods that detect Android or Kotlin types.

  /* These classes are defined in separate source files due to package
   * constraints. Look under src/test/java for their definitions. */
}