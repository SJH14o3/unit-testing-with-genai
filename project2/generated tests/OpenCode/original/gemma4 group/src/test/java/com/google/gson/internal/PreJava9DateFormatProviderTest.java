package com.google.gson;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.text.DateFormat;
import java.util.Locale;

@DisplayName("PreJava9DateFormatProvider Test")
class PreJava9DateFormatProviderTest {

    @Test
    @DisplayName("getUsDateTimeFormat with valid styles should return correct DateFormat")
    void getUsDateTimeFormat_validStyles_returnsCorrectFormat() {
        DateFormat dateFormat = PreJava9DateFormatFomatProvider.getUsDateTimeFormat(DateFormat.SHORT, DateFormat.SHORT);
        assertNotNull(dateFormat);
    }

    @Test
    @DisplayName("getUsDateTimeFormat with different styles should return valid DateFormat")
    void getUsDateTimeFormat_differentStyles_returnsValidFormat() {
        assertNotNull(PreJava9DateFormatProvider.getUsDateTimeFormat(DateFormat.MEDIUM, DateFormat.MEDIUM));
        assertNotNull(PreJava9DateFormatProvider.getUsDateTimeFormat(DateFormat.LONG, DateFormat.LONG));
        assertNotNull(PreJava9DateFormatProvider.getUsDateTimeFormat(DateFormat.FULL, DateFormat.FULL));
    }

    @Test
    @DisplayName("getUsDateTimeFormat with invalid dateStyle should throw IllegalArgumentException")
    void getUsDateTimeFormat_invalidDateStyle_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> 
            PreJava9DateFormatProvider.getUsDateTimeFormat(-1, DateFormat.SHORT));
    }

    @Test
    @DisplayName("getUsDateTimeFormat with invalid timeStyle should throw IllegalArgumentException")
    void getUsDateTimeFormat_invalidTimeStyle_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> 
            PreJava9DateFormatProvider.getUsDateTimeFormat(DateFormat.SHORT, -1));
    }

    @Test
    @DisplayName("getUsDateTimeFormat with extreme invalid style should throw IllegalArgumentException")
    void getUsDateTimeFormat_extremeInvalidStyle_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> 
            PreJava9DateFormatProvider.getUsDateTimeFormat(10, 10));
    }
}
