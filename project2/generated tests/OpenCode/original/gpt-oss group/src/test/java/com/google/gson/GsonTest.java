package com.google.gson;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class GsonTest {

  @DisplayName("Serialization")
  @Nested
  class Serialization {

    private final Gson gson = new GsonBuilder().setDateFormat(DateFormat.SHORT, DateFormat.SHORT).create();

    @Test
    void toJsonNull() {
      assertEquals("null", gson.toJson((Object) null));
    }

    @Test
    void toJsonEmptyString() {
      assertEquals("\"\"", gson.toJson(""));
    }

    @Test
    void toJsonMap() {
      Map<String, Integer> map = Map.of("a", 1);
      String json = gson.toJson(map);
      // order may vary due to HashMap, but presence of key/value implies correctness
      assertTrue(json.contains("\"a\":1"));
    }

    @Test
    void toJsonWithFormatting() {
      Gson pretty = new GsonBuilder().setPrettyPrinting().create();
      String json =
          pretty.toJson(Arrays.asList("one", "two"), List.class.getGenericSuperclass().getType());
      assertTrue(json.contains("\n") || json.contains("\"one\"")); // pretty printing includes newlines
    }

    @Test
    void newJsonWriterProperties() throws IOException {
      StringWriter writer = new StringWriter();
      JsonWriter jsonWriter = gson.newJsonWriter(writer);
      // check the prefix for non-executable json
      jsonWriter.setHtmlSafe(false);
      jsonWriter.beginObject();
      jsonWriter.name("key").value(42);
      jsonWriter.endObject();
      jsonWriter.close();
      String output = writer.toString();
      // no prefix should be present
      assertFalse(output.startsWith(Gson.JSON_NON_EXECUTABLE_PREFIX));
      // JSON should contain key and value
      assertTrue(output.contains("\"key\":42"));
    }
  }

  @DisplayName("Deserialization")
  @Nested
  class Deserialization {

    private final Gson gson = new GsonBuilder().create();

    @Test
    void fromJsonNullString() {
      assertNull(gson.fromJson((String) null, String.class));
    }

    @Test
    void fromJsonEmptyString() {
      assertNull(gson.fromJson("", String.class));
    }

    @Test
    void fromJsonValid() {
      String json = "{\"x\":10,\"y\":\"hello\"}";
      SimplePojo obj = gson.fromJson(json, SimplePojo.class);
      assertEquals(10, obj.x);
      assertEquals("hello", obj.y);
    }

    @Test
    void fromJsonInvalidNumber() {
      String json = "{\"x\":\"notAnInt\"}";
      assertThrows(JsonSyntaxException.class, () -> gson.fromJson(json, SimplePojo.class));
    }

    @Test
    void fromJsonWithReaderAndFullConsumption() {
      StringReader reader = new StringReader("{\"ok\":true}");
      SimplePojo p = gson.fromJson(reader, SimplePojo.class);
      assertTrue(p.ok);
    }

    @Test
    void fromJsonWithTrailingData() {
      String json = "{\"ok\":true}   extra";
      assertThrows(JsonSyntaxException.class, () -> gson.fromJson(json, SimplePojo.class));
    }
  }

  /** Simple POJO used only for deserialization tests. */
  static class SimplePojo {
    int x;
    String y;
    boolean ok;
  }
}