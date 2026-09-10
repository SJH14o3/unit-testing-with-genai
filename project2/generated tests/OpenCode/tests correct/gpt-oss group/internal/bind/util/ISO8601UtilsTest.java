package com.google.gson.internal.bind.util;

import static org.junit.jupiter.api.Assertions.*;
import static java.time.ZoneOffset.UTC;

import java.text.ParsePosition;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;
import java.text.ParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ISO8601UtilsTest {

    /** Utility to compare two dates at the minute granularity. */
    private static void assertDatesEqual(Date expected, Date actual) {
        assertEquals(expected.getTime() / 60000, actual.getTime() / 60000);
    }

    @Test
    @DisplayName("parse simple date")
    void parseDateOnly() throws Exception {
        Date d = ISO8601Utils.parse("2023-08-28", new ParsePosition(0));
        assertEquals(2023, d.toInstant().atZone(UTC).getYear());
    }

    @Test
    @DisplayName("parse date with millisecond precision")
    void parseWithMillis() throws Exception {
        Date d = ISO8601Utils.parse(
                "2023-08-28T12:34:56.789Z", new ParsePosition(0));
        assertEquals(12, d.toInstant().atZone(UTC).getHour());
        assertEquals(34, d.toInstant().atZone(UTC).getMinute());
        assertEquals(56, d.toInstant().atZone(UTC).getSecond());
        assertEquals(789, d.toInstant().atZone(UTC).getNano() / 1_000_000);
    }

    @Test
    @DisplayName("parse date with offset")
    void parseOffset() throws Exception {
        Date d = ISO8601Utils.parse(
                "2023-08-28T12:34:56+02:00", new ParsePosition(0));
        // Should be 10:34 UTC
        Instant utc = d.toInstant();
        assertEquals(10, utc.atZone(UTC).getHour());
    }

    @Test
    @DisplayName("format defaults to UTC without milliseconds")
    void formatDefault() {
        Date now = Date.from(Instant.parse("2021-05-01T15:30:20Z"));
        String txt = ISO8601Utils.format(now);
        assertTrue(txt.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z"));
    }

    @Test
    @DisplayName("format with milliseconds")
    void formatMillis() {
        Date now = Date.from(Instant.parse("2021-05-01T15:30:20.123Z"));
        String txt = ISO8601Utils.format(now, true, TimeZone.getTimeZone("UTC"));
        assertTrue(txt.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z"));
    }

    @Test
    @DisplayName("format with non‑UTC time zone")
    void formatWithTimeZone() {
        TimeZone tz = TimeZone.getTimeZone("GMT+02:00");
        Date d = Date.from(Instant.parse("2021-05-01T13:30:00Z")); // 13:30 UTC
        String txt = ISO8601Utils.format(d, false, tz);
        assertTrue(txt.endsWith("+02:00"));
        assertTrue(txt.contains("15:30:00"));
    }

    @Test
    @DisplayName("invalid format throws ParseException")
    void parseInvalid() {
        assertThrows(ParseException.class,
                () -> ISO8601Utils.parse("not-a-date", new ParsePosition(0)));
    }
}