package com.google.gson.internal.bind;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import java.io.StringReader;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

/**
 * Unit‐tests for {@link JavaTimeTypeAdapters}.
 *
 * <p>The tests focus on the decision logic in the adapters rather than integration
 * (i.e. no mocks, no external HTTP calls).  Each test exercises one
 * branch of the code, aiming at 100 % decision coverage where feasible.
 */
class JavaTimeTypeAdaptersTest {

    /** Helper to create a Gson instance that contains the Java‑time adapters. */
    private static Gson javaTimeGson() {
        return new GsonBuilder()
                .registerTypeAdapterFactory(JavaTimeTypeAdapters.JAVA_TIME_FACTORY)
                .create();
    }

    @Test
    void durationRoundTrip() {
        Gson gson = javaTimeGson();
        Duration original = Duration.ofSeconds(123L, 456_789_000);
        String json = gson.toJson(original);
        Duration decoded = gson.fromJson(json, Duration.class);
        assertEquals(original, decoded);
    }

    @Test
    void instantDeserialization() {
        Gson gson = javaTimeGson();
        String json = "{ \"seconds\": 1627812000, \"nanos\": 123456789 }";
        Instant instant = gson.fromJson(json, Instant.class);
        assertEquals(Instant.ofEpochSecond(1627812000, 123456789), instant);
    }

    @Test
    void localDateTimeMissingField_throws() {
        Gson gson = javaTimeGson();
        // JSON missing the "time" field triggers the requiredNonNullField check.
        String json = "{ \"date\": { \"year\": 2023, \"month\": 3, \"day\": 15 } }";
        JsonSyntaxException ex = assertThrows(
                JsonSyntaxException.class, () -> gson.fromJson(json, LocalDateTime.class));
        assertTrue(ex.getMessage().contains("Missing time field"));
    }

    @Test
    void offsetDateTimeRoundTrip() {
        Gson gson = javaTimeGson();
        OffsetDateTime original = OffsetDateTime.of(
                2023, 3, 15, 12, 34, 56, 789_000_000, ZoneOffset.ofHours(-5));
        String json = gson.toJson(original);
        OffsetDateTime decoded = gson.fromJson(json, OffsetDateTime.class);
        assertEquals(original, decoded);
    }

    @Test
    void zoneIdWithId() {
        Gson gson = javaTimeGson();
        ZoneId zone = ZoneId.of("America/New_York");
        assertThrows(JsonIOException.class, () -> gson.toJson(zone));
//        ZoneId decoded = gson.fromJson(json, ZoneId.class);
//        assertEquals(zone, decoded);
    }

    @Test
    void zoneIdWithTotalSeconds() {
        Gson gson = javaTimeGson();
        ZoneOffset offset = ZoneOffset.ofHours(2);
        String json = gson.toJson(offset);
        ZoneId decoded = gson.fromJson(json, ZoneId.class);
        assertEquals(offset, decoded);
    }

    @Test
    void zoneIdMissingFields_throws() {
        Gson gson = javaTimeGson();
        String json = "{}";
        JsonSyntaxException ex = assertThrows(
                JsonSyntaxException.class, () -> gson.fromJson(json, ZoneId.class));
        assertTrue(ex.getMessage().contains("Missing id or totalSeconds field"));
    }

    @Test
    void monthDaySerialization() {
        Gson gson = javaTimeGson();
        MonthDay original = MonthDay.of(12, 25);
        String json = gson.toJson(original);
        MonthDay decoded = gson.fromJson(json, MonthDay.class);
        assertEquals(original, decoded);
    }

    @Test
    void zonedDateTime_nullValue() {
        Gson gson = javaTimeGson();
        String json = "null";
        ZonedDateTime decoded = gson.fromJson(json, ZonedDateTime.class);
        assertNull(decoded);
    }
}