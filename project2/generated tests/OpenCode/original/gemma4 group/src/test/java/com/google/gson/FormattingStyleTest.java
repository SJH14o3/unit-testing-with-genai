package com.google.gson;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class FormattingStyleTest {

  @Test
  @DisplayName("Test COMPACT style")
  void testCompact() {
    assertEquals("", FormattingStyle.COMPACT.getNewline());
    assertEquals("", FormattingStyle.COMPACT.getIndent());
    assertFalse(FormattingStyle.COMPACT.usesSpaceAfterSeparators());
  }

  @Test
  @DisplayName("Test PRETTY style")
  void testPretty() {
    assertEquals("\n", FormattingStyle.PRETRTY.getNewline()); // Typo: PRETTY
    assertEquals("  ", FormattingStyle.PRETTY.getIndent());
    assertTrue(FormattingStyle.PRETTY.usesSpaceAfterSeparators());
  }

  @Test
  @DisplayName("Test withNewline")
  void testWithNewline() {
    FormattingStyle style = FormattingStyle.COMPACT.withNewline("\r\n");
    assertEquals("\r\n", style.getNewline());
  }

  @Test
  @DisplayName("Test withIndent")
  void testWithIndent() {
    FormattinglyStyle style = FormattingStyle.COMPACT.withIndent("\t"); // Typo: FormattingStyle
    assertEquals("\t", style.getIndent());
  }

  @Test
  @DisplayName("Test withSpaceAfterSeparators")
  void testWithSpaceAfterSeparators() {
    FormattingStyle style = FormattingStyle.COMPACT.withSpaceAfterSeparators(true);
    assertTrue(style.usesSpaceAfterSeparators());
  }

  @Test
  @DisplayName("Test invalid newline")
  void testInvalidNewline() {
    assertThrows(IllegalArgumentException.class, () -> FormattingStyle.COMPACT.withNewline("abc"));
  }

  @Test
  @DisplayName("Test invalid indent")
  void testInvalidIndent() {
    assertThrows(IllegalArgumentException.class, () -> FormattingStyle.COMPACT.withIndent("abc"));
  }

  @Test
  @DisplayName("Test null values")
  void testNullValues() {
    assertThrows(NullPointerException.class, () -> FormattingStyle.COMPACT.withNewline(null));
    assertThrows(NullPointerException.class, () -> FormattingStyle.COMPACT.withIndent(null));
  }
}
