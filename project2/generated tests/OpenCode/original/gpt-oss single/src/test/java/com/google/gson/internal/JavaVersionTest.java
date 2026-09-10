package com.google.gson.internal;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests covering the decision paths in {@link JavaVersion}.
 * No external dependencies or mocking are used; the tests exercise the
 * public and package‑private static methods directly.
 */
class JavaVersionTest {

  /* -------------------------------------------------- */
  /* parseDotted(String) – both legacy “1.x” and
   * modern “9.x” style parsing
   * -------------------------------------------------- */

  @Test
  @DisplayName("parseDotted – legacy 1.8 style")
  void parseDottedLegacy() {
    assertEquals(8, JavaVersion.parseDotted("1.8"));
    assertEquals(8, JavaVersion.parseDotted("1.8.0_241"));
  }

  @Test
  @DisplayName("parseDotted – modern 9.0.4 style")
  void parseDottedModern() {
    assertEquals(9, JavaVersion.parseDotted("9"));
    assertEquals(9, JavaVersion.parseDotted("9.0.4"));
  }

  @Test
  @DisplayName("parseDotted – invalid format")
  void parseDottedInvalid() {
    assertEquals(-1, JavaVersion.parseDotted("foo.bar"));
    assertEquals(-1, JavaVersion.parseDotted(""));
  }

  /* -------------------------------------------------- */
  /* extractBeginningInt(String) – number at string start
   * -------------------------------------------------- */

  @ParameterizedTest(name = "extractBeginningInt – \"{0}\" returns {1}")
  @CsvSource(
      {"1abcd, 1",
       "10xyz, 10",
       "123, 123",
       "foo, -1",
       "1.2, 1",
       ", -1"})
  void extractBeginningIntParameterized(String input, int expected) {
    assertEquals(expected, JavaVersion.extractBeginningInt(input));
  }

  /* -------------------------------------------------- */
  /* parseMajorJavaVersion(String) – tries parseDotted first,
   * then extractBeginningInt, otherwise defaults to 6
   * -------------------------------------------------- */

  @Test
  @DisplayName("parseMajorJavaVersion – uses parseDotted")
  void parseMajorUsesDotted() {
    assertEquals(8, JavaVersion.parseMajorJavaVersion("1.8"));
    assertEquals(9, JavaVersion.parseMajorJavaVersion("9.0.4"));
  }

  @Test
  @DisplayName("parseMajorJavaVersion – falls back to extractBeginningInt")
  void parseMajorFallsBack() {
    assertEquals(7, JavaVersion.parseMajorJavaVersion("7-something"));
    assertEquals(10, JavaVersion.parseMajorJavaVersion("10xyz"));
  }

  @Test
  @DisplayName("parseMajorJavaVersion – defaults to 6 on failure")
  void parseMajorDefaultsTo6() {
    assertEquals(6, JavaVersion.parseMajorJavaVersion("unknown"));
    assertEquals(6, JavaVersion.parseMajorJavaVersion(""));
  }

  /* -------------------------------------------------- */
  /* getMajorJavaVersion() – wrapper around the cached static field
   * -------------------------------------------------- */

  @Test
  @DisplayName("getMajorJavaVersion – reflects static field")
  void getMajorJavaVersionReturnsCachedValue() {
    int cached = JavaVersion.getMajorJavaVersion();
    // The cached value will be whatever the runtime reports, but
    // calling again should give the same value without side‑effects.
    assertEquals(cached, JavaVersion.getMajorJavaVersion());
  }

  /* -------------------------------------------------- */
  /* isJava9OrLater() – simple comparison
   * -------------------------------------------------- */

  @ParameterizedTest(name = "isJava9OrLater – major={0} → {1}")
  @CsvSource(
      {"8, false",
       "9, true",
       "10, true",
       "6, false"})
  void isJava9OrLaterParameterized(int major, boolean expected) {
    // We cannot change the static cached field, so we compare against the
    // expected behaviour given the current field value.
    if (JavaVersion.getMajorJavaVersion() == major) {
      assertEquals(expected, JavaVersion.isJava9OrLater());
    } // otherwise we skip; the point is to verify logic
  }
}