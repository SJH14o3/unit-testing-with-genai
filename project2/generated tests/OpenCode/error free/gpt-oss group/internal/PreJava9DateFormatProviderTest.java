package com.google.gson.internal;

import static org.junit.jupiter.api.Assertions.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class PreJava9DateFormatProviderTest {

    @DisplayName("getUsDateTimeFormat patterns")
    @Nested
    class PatternTests {

        @Test
        void shortShortPattern() {

            DateFormat df =
                    PreJava9DateFormatProvider.getUsDateTimeFormat(DateFormat.SHORT, DateFormat.SHORT);
            SimpleDateFormat sdf = (SimpleDateFormat) df;
            assertEquals("M/d/yy h:mm a", sdf.toPattern());
        }

        @Test
        void mediumFullPattern() {
            DateFormat df =
                    PreJava9DateFormatProvider.getUsDateTimeFormat(DateFormat.MEDIUM, DateFormat.FULL);
            SimpleDateFormat sdf = (SimpleDateFormat) df;
            assertEquals("MMM d, yyyy h:mm:ss a z", sdf.toPattern());
        }

        @Test
        void longFullPattern() {
            DateFormat df =
                    PreJava9DateFormatProvider.getUsDateTimeFormat(DateFormat.LONG, DateFormat.FULL);
            SimpleDateFormat sdf = (SimpleDateFormat) df;
            assertEquals("MMMM d, yyyy h:mm:ss a z", sdf.toPattern());
        }
    }

    @DisplayName("Exception paths for unknown styles")
    @Nested
    class ExceptionTests {

        @Test
        void unknownDateStyle() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> PreJava9DateFormatProvider.getUsDateTimeFormat(999, DateFormat.SHORT));
        }

        @Test
        void unknownTimeStyle() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> PreJava9DateFormatProvider.getUsDateTimeFormat(DateFormat.SHORT, 999));
        }

        @Test
        void datePartInvalidStyle() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> PreJava9DateFormatProvider.getUsDateTimeFormat(999, DateFormat.SHORT));
        }

        @Test
        void timePartInvalidStyle() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> PreJava9DateFormatProvider.getUsDateTimeFormat(DateFormat.SHORT, 999));
        }
    }
}