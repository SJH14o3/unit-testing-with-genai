package com.google.gson.internal.bind.util;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ISO8601UtilsTest {

    @Test
    @DisplayName("format(Date) should use UTC and no millis")
    void formatDefault() {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"), java.util.Locale.US);
        cal.set(2023, Calendar.OCTOBER, 27, 10, 30, 45);
        cal.set(Calendar.MILLISECOND, 0);
        Date date = cal.getTime();
        assertEquals("2023-10-27T10:30:45Z", ISO8601Utils.format(date));
    }

    @Test
    @DisplayName("format(Date, boolean) with millis true")
    void formatWithMillis() {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"), java.util.Locale.US);
        cal.set(2023, Calendar.OCTOBER, 27, 10, 30, 45);
        cal.set(Calendar.MILLISECOND, 123);
        Date date = cal.getTime();
        assertEquals("2023-10-27T10:30:45.123Z", ISO8601Utils.format(date, true));
    }

    @Test
    @DisplayName("format(Date, boolean, TimeZone) with positive offset")
    void formatPositiveOffset() {
        TimeZone tz = TimeZone.getTimeZone("GMT+05:30");
        Calendar cal = new GregorianCalendar(tz, java.util.Locale.US);
        cal.set(2023, Calendar.OCTOBER, 27, 10, 30, 45);
        cal.set(Calendar.MILLISECOND, 0);
        Date date = cal.getTime();
        assertEquals("2023-10-27T10:30:45+05:30", ISO8601Utils.format(date, false, tz));
    }

    @Test
    @DisplayName("format(Date, boolean, TimeZone) with negative offset")
    void formatNegativeOffset() {
        TimeZone tz = TimeZone.getTimeZone("GMT-04:00");
        Calendar cal = new GregorianCalendar(tz, java.util.Locale.US);
        cal.set(2023, Calendar.OCTOBER, 27, 10, 30, 45);
        cal.set(Calendar.MILLISECOND, 0);
        Date date = cal.getTime();
        assertEquals("2023-10-27T10:30:45-04:00", ISO8601Utils.format(date, false, tz));
    }

    @ParameterizedTest
    @CsvSource({
            "2023-10-27, 2023-10-27",
            "2023-10-27T10:30:45Z, 2023-10-27T10:30:45Z",
            "2023-10-27T10:30:45+05:30, 2023-10-27T10:30:45+05:30",
            "2023-10-27T10:30:45-04:00, 2023-10-27T10:30:45-04:00",
            "2023-10-27T10:30:45.123Z, 2023-10-27T10:30:45.123Z",
            "2023-10-27T10:30:45.12Z, 2023-10-27T10:30:45.120Z",
            "2023-10-27T10:30:45.1Z, 2023-10-27T10:30:45.100Z"
    })
    @DisplayName("parse() should correctly parse various ISO8601 formats")
    void parseFormats(String input, String expected) throws ParseException {
        ParsePosition pos = new ParsePosition(0);
        Date date = ISO8601Utils.parse(input, pos);

        assertNotNull(date);
        assertEquals(input.length(), pos.getIndex());
    }

    @Test
    @DisplayName("parse() should handle seconds truncation (leap seconds)")
    void parseLeapSeconds() throws ParseException {
        ParsePosition pos = new ParsePosition(0);
        // 61 seconds should be truncated to 5rad 59
        Date date = ISO8601Utils.parse("2023-10-27T10:30:61Z", pos);
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"), java.util.Locale.US);
        cal.set(2023, Calendar.OCTOBER, 27, 10, 30, 59);
        cal.setLenient(false);
        assertEquals(cal.getTime().toString(), date.toString());
    }

    @Test
    @DisplayName("parse() should throw exception for invalid timezone indicator")
    void parseInvalidTimezoneIndicator() {
        ParsePosition pos = new ParsePosition(0);
        assertThrows(ParseException.class, () -> ISO8601Utils.parse("2023-10-27T10:30:45X", pos));
    }

    @Test
    @DisplayName("parse() should throw exception for missing timezone indicator")
    void parseMissingTimezoneIndicator() {
        ParsePosition pos = new ParsePosition(0);
        assertThrows(ParseException.class, () -> ISO8601Utils.parse("2023-10-27T10:30:45", pos));
    }

    @Test
    @DisplayName("parse() handles +00:00 and +0000 as UTC")
    void parseZeroOffset() throws ParseException {
        ParsePosition pos1 = new ParsePosition(0);
        Date date1 = ISO8601Utils.parse("2023-10-27T10:30:45+00:00", pos1);

        ParsePosition pos2 = new ParsePosition(0);
        Date date2 = ISO8601Utils.parse("2023-10-27T10:30:45+0000", pos2);

        assertEquals(date1, date2);
    }

    @Test
    @DisplayName("parse() should handle simple date format (no time component)")
    void parseSimpleDate() throws ParseException {
        ParsePosition pos = new ParsePosition(0);
        Date date = ISO8601Utils.parse("2023-10-27", pos);
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"), java.util.Locale.US);
        cal.set(2023, Calendar.OCTOBER, 26, 20, 30, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.setLenient(false);
        assertEquals(cal.getTime().toString(), date.toString());
    }

    @Test
    @DisplayName("parse() handles different millisecond digit counts")
    void parseMillisDigits() throws ParseException {
        ParsePosition pos = new ParsePosition(0);
        Date date = ISO8601Utils.parse("2023-10-27T10:30:45.1Z", pos);
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"), java.util.Locale.US);
        cal.set(2023, Calendar.OCTOBER, 27, 10, 30, 45);
        cal.set(Calendar.MILLISECOND, 100);
        cal.setLenient(false);
        assertEquals(cal.getTime(), date);
    }
}
