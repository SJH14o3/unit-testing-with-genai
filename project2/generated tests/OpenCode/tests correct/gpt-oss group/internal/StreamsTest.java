package com.google.gson.internal;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StreamsTest {
  @Test
  @DisplayName("parse(empty) returns JsonNull")
  void parseEmpty() throws IOException {
    JsonReader reader = new JsonReader(new StringReader(""));
    JsonElement el = Streams.parse(reader);
    assertEquals(JsonNull.INSTANCE, el);
  }

  @Test
  @DisplayName("parse(invalid) throws JsonSyntaxException")
  void parseInvalid() {
    JsonReader reader = new JsonReader(new StringReader("{unclosed"));
    assertThrows(JsonSyntaxException.class, () -> Streams.parse(reader));
  }

  @Test
  @DisplayName("parse(number) returns JsonPrimitive with numeric value")
  void parseNumber() throws IOException {
    JsonReader reader = new JsonReader(new StringReader("123"));
    JsonElement el = Streams.parse(reader);
    assertTrue(el.isJsonPrimitive());
    assertEquals(123, el.getAsInt());
  }

  @Test
  @DisplayName("write(JsonElement) produces valid JSON string")
  void writeElement() throws IOException {
    com.google.gson.JsonObject obj = new com.google.gson.JsonObject();
    obj.addProperty("x", 10);
    StringWriter sw = new StringWriter();
    JsonWriter writer = new JsonWriter(sw);
    Streams.write(obj, writer);
    writer.flush();
    assertEquals("{\"x\":10}", sw.toString());
  }

  @Test
  @DisplayName("writerForAppendable accepts Writer and Appendable")
  void writerForAppendable() throws IOException {
    // Writer case
    StringWriter sw = new StringWriter();
    java.io.Writer w1 = Streams.writerForAppendable(sw);
    assertSame(sw, w1);

    // Appendable case
    StringBuilder sb = new StringBuilder();
    java.io.Writer w2 = Streams.writerForAppendable(sb);
    assertNotSame(sb, w2);
    JsonWriter jw = new JsonWriter(w2);
    jw.beginObject();
    jw.name("a").value(1);
    jw.endObject();
    jw.flush();
    assertEquals("{\"a\":1}", sb.toString());
  }
}
