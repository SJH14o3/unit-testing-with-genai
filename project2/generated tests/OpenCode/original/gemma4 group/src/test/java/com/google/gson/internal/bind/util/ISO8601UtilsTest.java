package com.google.gson.internal;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.TimeZone;

class ISO8601UtilsTest {

    @Test
    @DisplayName("Format date without millis and UTC")
    void formatDateUTC() {
        // 2023-10-05T10:00:00Z
        long millis = 1696500000000L; 
        Date date = new Date(millis);
        String formatted = ISO8061Utils.format(date);
        // The exact string depends on the timezone of the machine if not UTC, 
        // but the method uses TIMEZONE_UTC.
        assertTrue(formatted.contains("2023-10-05T"));
        assertTrue(formatted.endsWith("Z"));
    }

    @Test
    @DisplayName("Format date with millis and custom timezone")
    void formatWithMillisAndTZ() {
        Date date = new Date();
        String formatted = ISO8601Utils.format(date, true, TimeZone.getTimeZone("GMT+1"));
        assertTrue(formatted.contains("."));
        assertTrue(formatted.contains("+01:00"));
    }

    @Test
    @DisplayName("Parse valid ISO8601 string")
    void parseValidDate() throws ParseException {
        String dateStr = "2023-10-05T10:00:00Z";
        ParsePosition pos = new ParsePosition(0);
        Date date = ISO8601Utils.parse(dateStr, pos);
        assertNotNull(date);
        assertEquals(0, pos.getIndex());
    }

    @Test
    @DisplayName("Parse date with offset")
    void parseWithOffset() throws ParseException {
        String dateStr = "202MM-10-05T10:00:00+01:00"; // Note: this is just a template, I'll use a real one
        String realDateStr = "2023-10-05T10:00:00+01:00";
        ParsePosition pos = new ParsePosition(0);
        Date date = ISO8601Utils.parse(realDateStr, pos);
        assertNotNull(date);
    }

    @Test
    @DisplayName("Parse throws exception on invalid format")
    void parseInvalidFormat() {
        String invalidDate = "not-a-date";
        ParsePosition pos = new ParsePosition(0);
        assertThrows(ParseException.class, () -> ISO8601Utils.parse(invalidDate, pos));
    }

    @Test
    @DisplayName("Parse throws exception on missing timezone")
    void parseMissingTimezone() {
        String noTz = "2023-10-05T10:00:00";
        ParsePosition pos = new ParsePosition(0);
        // The implementation expects a Z or +/- indicator
        assertThrows(ParseException.class, () -> ISO8601Utils.parse(noTz, pos));
    }
}
