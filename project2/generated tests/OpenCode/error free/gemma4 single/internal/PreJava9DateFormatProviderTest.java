package com.google.gson.internal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

class PreJava9DateFormatProviderTest {

    @ParameterizedTest
    @DisplayName("Test valid DateFormat style combinations")
    @CsvSource({
            "0, 0, 'M/d/yy h:mm a'",           // SHORT, SHORT
            "2, 2, 'MMM d, yyyy h:mm:ss a'",   // MEDIUM, MEDIUM
            "3, 3, 'MMMM d, yyyy h:mm:ss a z'", // LONG, LONG
            "4, 4, 'EEEE, MMMM d, yyyy h:mm:ss a z'", // FULL, FULL
            "0, 2, 'M/d/yy h:mm:ss a'",       // SHORT, MEDIUM
            "2, 0, 'MMM d, yyyy h:mm a'",     // MEDIUM, SHORT
            "3, 0, 'MMMM d, yyyy h:mm a'",    // LONG, SHORT
            "4, 2, 'EEEE, MMMM d, yyyy h:mm:ss a'" // FULL, MEDIUM
    })
    void testValidStyles(int dateStyle, int timeStyle, String expectedPattern) {
        DateFormat df = PreJava9DateFormatProvider.getUsDateTimeFormat(dateStyle, timeStyle);

        assertNotNull(df);
        assertTrue(df instanceof SimpleDateFormat);

        SimpleDateFormat sdf = (SimpleDateFormat) df;
        assertEquals(expectedPattern, sdf.toPattern());
        // assertEquals(Locale.US, sdf.getLocale());
    }

    @Test
    @DisplayName("Test invalid dateStyle throws exception")
    void testInvalidDateStyle() {
        assertThrows(IllegalArgumentException.class, () ->
                PreJava9DateFormatProvider.getUsDateTimeFormat(-1, 0));
    }

    @Test
    @DisplayName("Test invalid timeStyle throws exception")
    void testInvalidTimeStyle() {
        assertThrows(IllegalArgumentException.class, () ->
                PreJava9DateFormatProvider.getUsDateTimeFormat(0, -1));
    }
}
