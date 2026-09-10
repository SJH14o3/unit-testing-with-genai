package com.google.gson;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("FormattingStyle Test")
class FormattingStyleTest {

    @Test
    @DisplayName("COMPACT style has correct defaults")
    void compactStyle_hasCorrectDefaults() {
        assertEquals("", FormattingStyle.COMPACT.getNewline());
        assertEquals("", FormattingStyle.COMPACT.getIndent());
        assertEquals(false, FormattingStyle.COMPACT.usesSpaceAfterSeparators());
    }

    @Test
    @DisplayName("PRETTY style has correct defaults")
    void prettyStyle_hasCorrectDefaults() {
        assertEquals("\n", FormattingStyle.PRETTY.getNewline());
        assertEquals("  ", FormattingStyle.PRETTY.getIndent());
        assertEquals(true, FormattingStyle.PRETTY.usesSpaceAfterSeparators());
    }

    @ParameterizedTest
    @CsvSource({
            "'\n', '  ', true",
            "'\r', '\t', false",
            "'\r\n', ' ', true"
    })
    @DisplayName("withNewline, withIndent, and withSpaceAfterSeparators create new instances with correct values")
    void configurationMethods_updateValues(String newline, String indent, boolean spaceAfterSeparators) {
        FormattingStyle base = FormattingStyle.COMPACT;
        FormattingStyle updated = base.withNewline(newline)
                .withIndent(indent)
                .withSpaceAfterSeparators(spaceAfterSeparators);

        assertEquals(newline, updated.getNewline());
        assertEquals(indent, updated.getIndent());
        assertEquals(spaceAfterSeparators, updated.usesSpaceAfterSeparators());
    }

    @Test
    @DisplayName("Constructor throws NullPointerException if newline is null")
    void constructor_throwsException_onNullNewline() {
        assertThrows(NullPointerException.class, () -> FormattingStyle.COMPACT.withNewline(null));
    }

    @Test
    @DisplayName("Constructor throws NullPointerException if indent is null")
    void constructor_throwsException_onNullIndent() {
        assertThrows(NullPointerException.class, () -> FormattingStyle.COMPACT.withIndent(null));
        // Note: NullPointerException is actually thrown by Objects.requireNonNull
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "  \n", "  \r", "foo"})
    @DisplayName("Constructor throws IllegalArgumentException if newline contains invalid characters")
    void constructor_throwsException_onInvalidNewline(String invalidNewline) {
        assertThrows(IllegalArgumentException.class, () -> FormattingStyle.COMPACT.withNewline(invalidNewline));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "  \n", "  \r", "foo"})
    @DisplayName("Constructor throws IllegalArgumentException if indent contains invalid characters")
    void constructor_throwsException_onInvalidIndent(String invalidIndent) {
        assertThrows(IllegalArgumentException.class, () -> FormattingStyle.COMPACT.withIndent(invalidIndent));
    }
}
