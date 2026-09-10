package com.google.gson.internal.sql;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Time;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SqlTimeTypeAdapterTest {

  private final SqlTimeTypeAdapter adapter = new SqlTimeTypeAdapter();

  @Test
  @DisplayName("write() formats Time as 'hh:mm:ss AM/PM'")
  void testWriteNotNull() throws Exception {
    StringWriter out = new StringWriter();
    adapter.write(new JsonWriter(out), Time.valueOf("13:45:30"));
    // The formatter uses 12‑hour clock with AM/PM. 13:45:30 becomes 01:45:30 PM.
    assertEquals("\"01:45:30 PM\"", out.toString().trim());
  }

  @Test
  @DisplayName("write() writes JSON null for null value")
  void testWriteNull() throws Exception {
    StringWriter out = new StringWriter();
    adapter.write(new JsonWriter(out), null);
    assertEquals("null", out.toString().trim());
  }

  @Test
  @DisplayName("read() parses formatted time string")
  void testReadValidString() throws Exception {
    JsonReader in = new JsonReader(new StringReader("\"02:15:30 PM\""));
    Time time = adapter.read(in);
    assertEquals(Time.valueOf("14:15:30"), time);
  }

  @Test
  @DisplayName("read() returns null for JSON null")
  void testReadNull() throws Exception {
    JsonReader in = new JsonReader(new StringReader("null"));
    Time time = adapter.read(in);
    assertNull(time);
  }

  @Test
  @DisplayName("read() throws JsonSyntaxException on malformed time")
  void testReadInvalidString() {
    JsonReader in = new JsonReader(new StringReader("\"oops\""));
    JsonSyntaxException thrown =
        assertThrows(JsonSyntaxException.class, () -> adapter.read(in));
    assertTrue(thrown.getMessage().contains("Failed parsing 'oops'"));
  }
}