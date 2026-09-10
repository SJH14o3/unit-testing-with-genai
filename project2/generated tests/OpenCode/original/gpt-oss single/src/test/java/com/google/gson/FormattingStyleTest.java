import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link com.google.gson.FormattingStyle}.
 */
public class FormattingStyleTest {

    @Test
    @DisplayName("Compact style defaults")
    void testCompactDefaults() {
        var compact = com.google.gson.FormattingStyle.COMPACT;
        assertEquals("", compact.getNewline());
        assertEquals("", compact.getIndent());
        assertFalse(compact.usesSpaceAfterSeparators());
    }

    @Test
    @DisplayName("Pretty style defaults")
    void testPrettyDefaults() {
        var pretty = com.google.gson.FormattingStyle.PRETTY;
        assertEquals("\n", pretty.getNewline());
        assertEquals("  ", pretty.getIndent());
        assertTrue(pretty.usesSpaceAfterSeparators());
    }

    @Test
    @DisplayName("withNewline with valid value")
    void testWithNewlineValid() {
        var style = com.google.gson.FormattingStyle.COMPACT.withNewline("\r\n");
        assertEquals("\r\n", style.getNewline());
        // other values unchanged
        assertEquals("", style.getIndent());
        assertFalse(style.usesSpaceAfterSeparators());
    }

    @Test
    @DisplayName("withNewline with invalid value throws IAE")
    void testWithNewlineInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> com.google.gson.FormattingStyle.COMPACT.withNewline("abc"));
    }

    @Test
    @DisplayName("withIndent with valid value")
    void testWithIndentValid() {
        var style = com.google.gson.FormattingStyle.COMPACT.withIndent("\t  ");
        assertEquals("\t  ", style.getIndent());
        assertEquals("", style.getNewline());
        assertFalse(style.usesSpaceAfterSeparators());
    }

    @Test
    @DisplayName("withIndent with invalid value throws IAE")
    void testWithIndentInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> com.google.gson.FormattingStyle.COMPACT.withIndent("a"));
    }

    @Test
    @DisplayName("withSpaceAfterSeparators toggle")
    void testWithSpaceAfterSeparators() {
        var style = com.google.gson.FormattingStyle.COMPACT.withSpaceAfterSeparators(true);
        assertTrue(style.usesSpaceAfterSeparators());
        assertEquals("", style.getNewline());
        assertEquals("", style.getIndent());
    }

    @Test
    @DisplayName("Chaining creates distinct instances")
    void testChaining() {
        var style = com.google.gson.FormattingStyle.COMPACT
                .withNewline("\n")
                .withIndent("  ")
                .withSpaceAfterSeparators(true);
        assertEquals("\n", style.getNewline());
        assertEquals("  ", style.getIndent());
        assertTrue(style.usesSpaceAfterSeparators());
    }
}
