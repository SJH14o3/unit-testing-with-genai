package com.google.gson.internal.bind;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import java.io.StringReader;
import java.io.StringWriter;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JavaTimeTypeAdaptersTest {

  private Gson gson;

  @BeforeEach
  void setUp() {
    gson = new Gson();
  }

  @Test
  @DisplayName("Test Duration adapter")
  void testDuration() {
    Duration duration = Duration.ofSeconds(10, 500);
    String json = "{\"seconds\":10,\"nanos\":500}";
    TypeAdapter<Duration> adapter = getAdapter(Duration.class);
    
    String written = writeAdapter(adapter, duration);
    assertEquals(json, written);
    
    Duration read = readAdapter(adapter, json);
    assertEquals(duration, read);
  }

  @Test
  @DisplayName("Test Instant adapter")
  void testInstant() {
    Instant instant = Instant.ofEpochSecond(123456, 789);
    String json = "{\"seconds\":123456,\"nanos\":789}";
    TypeAdapter<Instant> adapter = getAdapter(Instant.class);
    
    String written = writeAdapter(adapter, instant);
    assertEquals(json, written);
    
    Instant read = readAdapter(adapter, json);
    assertEquals(instant, read);
  }

  @Test
  @DisplayName("Test LocalDate adapter")
  void testLocalDate() {
    LocalDate date = LocalDate.of(2023, 10, 27);
    String json = "{\"year\":2023,\"month\":10,\"day\":27}";
    TypeAdapter<LocalDate> adapter = getAdapter(LocalDate.class);
    
    String written = writeAdapter(adapter, date);
    assertEquals(json, written);
    
    LocalDate read = readAdapter(adapter, json);
    assertEquals(date, read);
  }

  @Test
  @DisplayName("Test LocalTime adapter")
  void testLocalTime() {
    LocalTime time = LocalTime.of(12, 34, 56, 789);
    String json = "{\"hour\":12,\"minute\":34,\"second\":56,\"nano\":789}";
    TypeAdapter<LocalTime> adapter = getAdapter(LocalTime.class);
    
    String written = writeAdapter(adapter, time);
    assertEquals(json, written);
    
    LocalKeyTime timeRead = (LocalKeyTime) null; // placeholder to avoid confusion
    LocalTime read = readAdapter(adapter, json);
    assertEquals(time, read);
  }

  @Test
  @DisplayName("Test LocalDateTime adapter")
  void testLocalDateTime() {
    LocalDateTime dateTime = LocalDateTime.of(2023, 10, 27, 12, 34, 56);
    TypeAdapter<LocalDateTime> adapter = getAdapter(LocalDateTime.class);
    
    String written = writeAdapter(adapter, dateTime);
    LocalDateTime read = readAdapter(adapter, written);
    assertEquals(dateTime, read);
  }

  @Test
  @DisplayName("Test MonthDay adapter")
  void testMonthDay() {
    MonthDay monthDay = MonthDay.of(10, 27);
    String json = "{\"month\":10,\"day\":27}";
    TypeAdapter<MonthDay> adapter = getAdapter(MonthDay.class);
    
    String written = writeAdapter(adapter, monthDay);
    assertEquals(json, written);
    
    MonthDay read = readAdapter(adapter, json);
    assertEquals(monthDay, read);
  }

  @Test
  @DisplayName("Test OffsetDateTime adapter")
  void testOffsetDateTime() {
    OffsetDateTime odt = OffsetDateTime.of(2023, 10, 27, 12, 34, 56, 0, ZoneOffset.ofHours(2));
    TypeAdapter<OffsetDateTime> adapter = getAdapter(OffsetDateTime.class);
    
    OffsetDateTime read = readAdapter(adapter, writeAdapter(adapter, odt));
    assertEquals(odt, read);
  }

  @Test
  @DisplayName("Test OffsetTime adapter")
  void testOffsetTime() {
    OffsetTime ot = OffsetTime.of(12, 34, 56, 0, ZoneOffset.ofHours(2));
    TypeAdapter<OffsetTime> adapter = getAdapter(OffsetTime.class);
    
    OffsetTime read = readAdapter(adapter, writeAdapter(adapter, ot));
    assertEquals(ot, read);
  }

  @Test
  @DisplayName("Test Period adapter")
  void testPeriod() {
    Period period = Period.of(1, 2, 3);
    String json = "{\"years\":1,\"months\":2,\"days\":3}";
    TypeAdapter<Period> adapter = getAdapter(Period.class);
    
    String written = writeAdapter(adapter, period);
    assertEquals(json, written);
    
    Period read = readAdapter(adapter, json);
    assertEquals(period, read);
  }

  @Test
  @DisplayName("Test Year adapter")
  void testYear() {
    Year year = Year.of(2023);
    String json = "{\"year\":2023}";
    TypeAdapter<Year> adapter = getAdapter(Year.class);
    
    String written = writeAdapter(adapter, year);
    assertEquals(json, written);
    
    Year read = readAdapter(adapter, json);
    assertEquals(year, read);
  }

  @Test
  @DisplayName("Test YearMonth adapter")
  void testYearMonth() {
    YearMonth ym = YearMonth.of(2023, 10);
    String json = "{\"year\":2023,\"month\":10}";
    TypeAdapter<YearMonth> adapter = getAdapter(YearMonth.class);
    
    String written = writeAdapter(adapter, ym);
    assertEquals(json, written);
    
    YearMonth read = readAdapter(adapter, json);
    assertEquals(ym, read);
  }

  @Test
  @DisplayName("Test ZoneId adapter (ZoneId)")
  void testZoneIdId() {
    ZoneId zoneId = ZoneId.of("UTC");
    String json = "{\"id\":\"UTC\"}";
    TypeAdapter<ZoneId> adapter = getAdapter(ZoneId.class);
    
    String written = writeAdapter(adapter, zoneId);
    assertEquals(json, written);
    
    ZoneId read = readAdapter(adapter, json);
    assertEquals(zoneId, read);
  }

  @Test
  @DisplayName("Test ZoneId adapter (ZoneOffset)")
  void testZoneIdOffset() {
    ZoneId zoneId = ZoneOffset.ofHours(2);
    String json = "{\"totalSeconds\":7200}";
    TypeAdapter<ZoneId> adapter = getAdapter(ZoneOffset.class);
    
    String written = writeAdapter(adapter, zoneId);
    assertEquals(json, written);
    
    ZoneId read = readAdapter(adapter, json);
    assertEquals(zoneId, read);
  }

  @Test
  @DisplayName("Test ZoneId exception for missing fields")
  void testZoneIdException() {
    String json = "{\"wrong\":\"field\"}";
    TypeAdapter<ZoneId> adapter = getAdapter(ZoneId.class);
    
    assertThrows(JsonSyntaxException.class, () -> readAdapter(adapter, json));
  }

  @Test
  @DisplayName("Test ZonedDateTime adapter")
  void testZonedDateTime() {
    ZonedDateTime zdt = ZonedDateTime.of(2023, 10, 27, 12, 34, 56, 0, ZoneId.of("UTC"));
    TypeAdapter<ZTokens> adapter = (TypeAdapter<ZTokens>) (TypeAdapter<?>) getAdapter(ZonedDateTime.class);
    
    ZonedDateTime read = (ZonedDateTime) readAdapter((TypeAdapter)adapter, writeAdapter(adapter, zdt));
    assertEquals(zdt, read);
  }

  @Test
  @DisplayName("Test factory returns null for non-java.time types")
  void testFactoryNonJavaTime() {
    TypeToken<String> typeToken = new TypeToken<String>() {};
    TypeAdapter<?> adapter = JavaTimeTypeAdapters.JAVA_TIME_FACTORY.create(gson, typeToken);
    assertNull(adapter);
  }

  @Test
  @DisplayName("Test factory returns adapter for java.time types")
  void testFactoryWorksForJavaTime() {
    TypeToken<Duration> typeToken = new TypeToken<Duration>() {};
    TypeAdapter<?> adapter = JavaTimeTypeAdapters.JAVA_TIME_FACTORY.create(gson, typeToken);
    assertNotNull(adapter);
  }

  private <T> TypeAdapter<T> getAdapter(Class<T> clazz) {
    return JavaTimeTypeAdapters.JAVA_TIME_FACTORY.create(gson, TypeToken.get(clazz));
  }

  private <T> String writeAdapter(TypeAdapter<T> adapter, T value) {
    StringWriter writer = new StringWriter();
    try {
      adapter.write(new com.google.gson.stream.JsonWriter(writer), value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return writer.toString();
  }

  private <T> T readAdapter(TypeAdapter<T> adapter, String json) {
    com.google.gson.stream.JsonReader reader = new com.google.gson.stream.JsonReader(new StringReader(json));
    try {
      return adapter.read(reader);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // Dummy class to satisfy compiler for the weird cast in ZonedDateTime test
  private static class ZTokens {}
}
EOF
