package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class JavaTimeTypeAdaptersTest {

    @Test
    @DisplayName("Factory returns correct adapters for java.time types")
    void factoryReturnsCorrectAdapters() {
        TypeAdapterFactory factory = JavaTimeTypeAdapters.JAVA_TIME_FACTORY;
        Gson gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapterFactory(factory)
                .create();

        assertNotNull(gson.getAdapter(Duration.class));
        assertNotNull(gson.getAdapter(Instant.class));
        assertNotNull(gson.getAdapter(LocalDate.class));
        assertNotNull(gson.getAdapter(LocalTime.class));
        assertNotNull(gson.getAdapter(MonthDay.class));
        assertNotNull(gson.getAdapter(Period.class));
        assertNotNull(gson.getAdapter(Year.class));
        assertNotNull(gson.getAdapter(YearMonth.class));
        assertNotNull(gson.getAdapter(ZoneId.class));
        assertNotNull(gson.getAdapter(ZonedDateTime.class));
    }

    @Test
    @DisplayName("Serialization and deserialization of Duration")
    void durationTest() {
        Gson gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapterFactory(JavaTimeTypeAdapters.JAVA_TIME_FACTORY)
                .create();

        Duration duration = Duration.ofSeconds(10, 500);
        String json = "{\"seconds\":10,\"nanos\":500}";
        Duration result = gson.fromJson(json, Duration.class);
        assertEquals(duration, result);

        String writtenJson = gson.toJson(duration);
        assertTrue(writtenJson.contains("\"seconds\":10"));
        assertTrue(writtenJson.contains("\"nanos\":500"));
    }

    @Test
    @DisplayName("Serialization and deserialization of LocalDate")
    void localDateTest() {
        Gson gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapterFactory(JavaTimeTypeAdapters.JAVA_TIME_FACTORY)
                .create();

        LocalDate localDate = LocalDate.of(2025, 1, 1);
        String json = "{\"year\":2025,\"month\":1,\"day\":1}";
        LocalDate result = gson.fromJson(json, LocalDate.class);
        assertEquals(localDate, result);

        String writtenJson = gson.toJson(localDate);
        assertTrue(writtenJson.contains("\"year\":2025"));
        assertTrue(writtenJson.contains("\"month\":1"));
        assertTrue(writtenJson.contains("\"day\":1"));
    }
}
