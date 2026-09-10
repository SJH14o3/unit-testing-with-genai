package com.google.gson.internal.sql;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SqlDateTypeAdapterTest {

  private final SqlDateTypeAdapter adapter = new SqlDateTypeAdapter();

  @Test
  @DisplayName("write() emits formatted date string")
  void testWriteNotNull() throws Exception {
    StringWriter out = new StringWriter();
    adapter.write(new JsonWriter(out), Date.valueOf("2021-12-31"));
    assertEquals("\"Dec 31, 2021\"",
        // JsonWriter escapes the string, so we match the quoted form
        out.toString().trim());
  }

  @Test
  @DisplayName("write() writes JSON null for null value")
  void testWriteNull() throws Exception {
    StringWriter out = new StringWriter();
    adapter.write(new JsonWriter(out), null);
    assertEquals("null", out.toString().trim());
  }

  @Test
  @DisplayName("read() constructs java.sql.Date from formatted string")
  void testReadValidString() throws Exception {
    JsonReader in = new JsonReader(new StringReader("\"Jan 5, 2022\""));
    Date result = adapter.read(in);
    assertEquals(Date.valueOf("2022-01-05"), result);
  }

  @Test
  @DisplayName("read() returns null for JSON null")
  void testReadJsonNull() throws Exception {
    JsonReader in = new JsonReader(new StringReader("null"));
    Date result = adapter.read(in);
    assertNull(result);
  }

  @Test
  @DisplayName("read() throws JsonSyntaxException on malformed date")
  void testReadInvalidString() {
    JsonReader in = new JsonReader(new StringReader("\"not a date\""));
    JsonSyntaxException thrown = assertThrows(JsonSyntaxException.class, () -> adapter.read(in));
    assertTrue(thrown.getMessage().contains("Failed parsing 'not a date'"));
  }
}