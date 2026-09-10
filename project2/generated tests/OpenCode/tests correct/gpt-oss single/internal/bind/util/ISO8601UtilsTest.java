package com.google.gson.internal.bind.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ISO8601Utils parse method tests")
class ISO8601UtilsTest {

    @Test
    void parseDateOnlyWithoutTime() throws ParseException {
        String iso = "2023-07-15";
        java.util.Date date = ISO8601Utils.parse(iso, new ParsePosition(0));
        Calendar cal = Calendar.getInstance();
        cal.setLenient(false);
        cal.setTime(date);
        assertEquals(2023, cal.get(Calendar.YEAR));
        assertEquals(6, cal.get(Calendar.MONTH)); // JUL in 0‑based
        assertEquals(15, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.SECOND));
    }

    @Test
    void parseDateTimeWithMillisAndUtc() throws ParseException {
        String iso = "2023-07-15T12:34:56.789Z";
        java.util.Date date = ISO8601Utils.parse(iso, new ParsePosition(0));
        GregorianCalendar expected = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        expected.setLenient(false);
        expected.set(2023, Calendar.JULY, 15, 12, 34, 56);
        expected.set(Calendar.MILLISECOND, 789);
        assertEquals(expected.getTimeInMillis(), date.getTime());
    }

    @Test
    void parseLeapSecondTruncates() throws ParseException {
        String iso = "2023-07-15T12:34:60Z"; // seconds 60 → truncated to 59
        java.util.Date date = ISO8601Utils.parse(iso, new ParsePosition(0));
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setLenient(false);
        cal.setTime(date);
        assertEquals(59, cal.get(Calendar.SECOND));
    }

    @Test
    void parseWithPositiveTimezoneOffset() throws ParseException {
        String iso = "2023-07-15T12:00:00+02:00";
        java.util.Date date = ISO8601Utils.parse(iso, new ParsePosition(0));
        GregorianCalendar expected = new GregorianCalendar(TimeZone.getTimeZone("GMT+02:00"));
        expected.setLenient(false);
        expected.set(2023, Calendar.JULY, 15, 12, 0, 0);
        expected.set(Calendar.MILLISECOND, 0);
        assertEquals(expected.getTimeInMillis(), date.getTime());
    }

    @Test
    void parseWithNegativeTimezoneOffsetNoMinutes() throws ParseException {
        String iso = "2023-07-15T12:00:00-0100"; // missing colon should be treated as -01:00
        java.util.Date date = ISO8601Utils.parse(iso, new ParsePosition(0));
        GregorianCalendar expected = new GregorianCalendar(TimeZone.getTimeZone("GMT-01:00"));
        expected.setLenient(false);
        expected.set(2023, Calendar.JULY, 15, 12, 0, 0);
        expected.set(Calendar.MILLISECOND, 0);
        assertEquals(expected.getTimeInMillis(), date.getTime());
    }

    @Test
    void parseMissingTimezoneThrows() {
        String iso = "2023-07-15T12:00:00"; // no timezone indicator
        ParsePosition pos = new ParsePosition(0);
        ParseException thrown = assertThrows(ParseException.class, () -> ISO8601Utils.parse(iso, pos));
        assertTrue(thrown.getMessage().contains("No time zone indicator"));
    }
}
