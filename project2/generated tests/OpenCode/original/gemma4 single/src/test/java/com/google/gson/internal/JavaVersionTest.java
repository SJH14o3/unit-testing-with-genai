package com.google.gson.internal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link JavaVersion}.
 * Focuses on achieving decision coverage for the package-private parseMajorJavaVersion method.
 */
class JavaVersionTest {

  @ParameterizedTest(name = "javaVersion {0} should yield major version {1}")
  @CsvSource({
      "1.8, 8",           // Case: parseDotted works, firstVer is 1, parts.length > 1
      "1.8.0_51, 8",      // Case: parseDotted works, firstVer is 1, parts.length > 1
      "9.0.4, 9",         // Case: parseDotted works, firstVer is not 1
      "11, 11",           // Case: parseDotted works, parts.length is 1
      "11.0.1, 11",       // Case: parseDotted works, firstVer is not 1
      "8-something, 8",   // Case: parseDotted works (parts[0]=8), firstVer is not 1
      "1.abc, 1",         // Case: parseDotted fails (NFE), extractBeginningInt succeeds
      "abc, 6",           // Case: parseDotted fails (NFE), extractBeginningInt fails (NFE/empty)
      "invalid, 6"        // Case: parseDotted fails (NFE), extractBeginningInt fails (NFE/empty)
  })
  @DisplayName("parseMajorJavaVersion covers various Java version string formats and decision branches")
  void parseMajorJavaVersion_coversVariousFormats(String javaVersion, int expected) {
    assertEquals(expected, JavaVersion.parseMajorJavaVersion(javaVersion));
  }

  @Test
  @DisplayName("parseMajorJavaVersion handles empty string by returning default 6")
  void parseMajorJavaVersion_handlesEmptyString() {
    assertEquals(6, JavaVersion.parseMajorJavaVersion(""));
  }
}