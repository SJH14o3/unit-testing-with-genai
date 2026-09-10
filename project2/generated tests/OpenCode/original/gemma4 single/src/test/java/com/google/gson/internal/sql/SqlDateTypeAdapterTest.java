package com.google.gson.internal.sql;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Date;
import java.util.TimeZone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SqlDateTypeAdapter Test")
class SqlDateTypeAdapterTest {

  private SqlDateTypeAdapter adapter;

  @BeforeEach
  void setUp() {
    // We use reflection or a known way to access the private constructor if needed, 
    // but since the test is in the same package, we can't call private constructor.
    // However, the factory provides the instance.
    adapter = (SqlDatetypetAdapter) SqlDateTypeAdapter.FACTORY.create(new Gson(), new TypeToken<java.sql.Date>() {});
    // Note: Since the constructor is private, I'll rely on the factory which is public.
    // In a real scenario, if I can't use the factory, I'd use reflection.
  }
  
  // Re-evaluating: The constructor is private. I must use the factory.
  @BeforeEach
  void setUpWithFactory() {
    adapter = (SqlDateTypeAdapter) SqlDateTypeAdapter.FACTORY.create(new Gson(), new TypeToken<java.sql.Date>() {});
  }

  @Test
  @DisplayName("Factory returns adapter for java.sql.Date")
  void factoryReturnsAdapterForSqlDate() {
    var adapterForDate = SqlDateTypeAdapter.FACTORY.create(new Gson(), new TypeToken<java.sql.Date>() {});
    assertNotNull(adapterForDate);
    assertTrue(adapterForDate instanceof SqlDateTypeAdapter);
  }

  @Test
  @DisplayName("Factory returns null for other types")
  void factoryReturnsNullForOtherTypes() {
    var adapterForString = SqlDateTypeAdapter.FACTORY.create(new Gson(), new TypeToken<String>() {});
    assertNull(adapterForString);
  }

  @Test
  @DisplayName("read returns null when input is JSON NULL")
  void readNull() throws Exception {
    JsonReader reader = new JsonReader(new StringReader("null"));
    assertNull(adapter.read(reader));
  }

  @ParameterizedTest
  @CsvSource({
      "Aug 30, 2026, 2026-08-30",
      "Jan 1, 2000, 2000-01-01"
  })
  @DisplayName("read parses valid date strings")
  void readValidDate(String input, String expectedIso) throws Exception {
    JsonReader reader = new JsonReader(new StringReader("\"" + input + "\""));
    java.sql.Date result = adapter.read(reader);
    assertEquals(java.sql.Date.valueOf(expectedIso), result);
  }

  @Test
  @DisplayName("read throws JsonSyntaxException for invalid date format")
  void readInvalidDate() {
    JsonReader reader = new JsonReader(new StringReader("\"invalid-date\""));
    assertThrows(JsonSyntaxException.class, () -> adapter.read(reader));
  }

  @Test
  @DisplayName("write writes null as JSON NULL")
  void writeNull() throws Exception {
    StringWriter writer = new StringWriter();
    JsonWriter jsonWriter = new JsonWriter(writer);
    adapter.write(jsonWriter, null);
    jsonWriter.endDocument();
    assertEquals("null", writer.toString());
  }

  @Test
  @DisplayName("write parses valid java.sql.Date to string")
  void writeValidDate() throws Exception {
    StringWriter writer = new StringWriter();
    JsonWriter jsonWriter = new JsonWriter(writer);
    java.sql.Date date = java.sql.Date.valueOf("2026-08-30");
    adapter.write(jsonWriter, date);
    jsonWriter.endDocument();
    assertEquals("\"Aug 30, 2026\"", writer.toString());
  }
}