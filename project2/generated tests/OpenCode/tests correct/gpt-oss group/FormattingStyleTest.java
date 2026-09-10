package com.google.gson;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormattingStyleTest {

    @Test
    @DisplayName("Default styles are immutable")
    void immutability() {
        FormattingStyle compact = FormattingStyle.COMPACT;
        FormattingStyle pretty = FormattingStyle.PRETTY;

        assertFalse(compact.usesSpaceAfterSeparators());
        assertEquals("", compact.getNewline());
        assertEquals("", compact.getIndent());

        FormattingStyle copy = compact.withNewline("\n").withIndent("  ").withSpaceAfterSeparators(true);
        assertEquals("\n", copy.getNewline());
        assertEquals("  ", copy.getIndent());
        assertTrue(copy.usesSpaceAfterSeparators());

        // original unchanged
        assertEquals("", compact.getNewline());
        assertEquals("", compact.getIndent());
        assertFalse(compact.usesSpaceAfterSeparators());
    }

    @Test
    @DisplayName("Constructor rejects invalid newline")
    void invalidNewlineThrows() {
        FormattingStyle dummy = FormattingStyle.PRETTY;
        assertThrows(
                IllegalArgumentException.class,
                () -> dummy.withNewline("gson"));
    }

    @Test
    @DisplayName("Constructor rejects invalid indent")
    void invalidIndentThrows() {
        FormattingStyle dummy = FormattingStyle.PRETTY;
        assertThrows(
                IllegalArgumentException.class,
                () -> dummy.withIndent("\n"));
    }
}