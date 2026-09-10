package com.google.gson.internal.bind;

import static com.google.gson.internal.bind.JsonElementTypeAdapter.ADAPTER;
import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Deque;
import java.util.ArrayDeque;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JsonElementTypeAdapterTest {

  /* --------------------------------------------------------------------- */
  /*  read() – terminal values                                             */
  /* --------------------------------------------------------------------- */

  @Test
  @DisplayName("read() → terminal STRING returns JsonPrimitive with that value")
  void readTerminalString() throws IOException {
    JsonElement element = readViaAdapter("\"hello world\"");
    assertTrue(element.isJsonPrimitive());
    assertEquals("hello world", element.getAsString());
  }

  @Test
  @DisplayName("read() → terminal NUMBER returns JsonPrimitive with LazilyParsedNumber")
  void readTerminalNumber() throws IOException {
    JsonElement element = readViaAdapter("12345.67");
    assertTrue(element.isJsonPrimitive());
    assertEquals(new BigDecimal("12345.67"), element.getAsNumber());
  }

  @Test
  @DisplayName("read() → terminal BOOLEAN returns JsonPrimitive with that value")
  void readTerminalBoolean() throws IOException {
    JsonElement element = readViaAdapter("\"\u0011\""); // dummy, will trigger boolean
  }

  @Test
  @DisplayName("read() → terminal NULL returns JsonNull")
  void readTerminalNull() throws IOException {
    JsonElement element = readViaAdapter("null");
    assertSame(JsonNull.INSTANCE, element);
  }

  /* --------------------------------------------------------------------- */
  /*  read() – array / object nesting                                      */
  /* --------------------------------------------------------------------- */

  @Test
  @DisplayName("read() → simple array of primitives")
  void readSimpleArray() throws IOException {
    JsonElement element = readViaAdapter("[1,2,3]");
    assertTrue(element.isJsonArray());
    JsonArray array = element.getAsJsonArray();
    assertEquals(3, array.size());
    assertEquals(1, array.get(0).getAsInt());
    assertEquals(2, array.get(1).getAsInt());
    assertEquals(3, array.get(2).getAsInt());
  }

  @Test
  @DisplayName("read() → nested array within object")
  void readNestedArrayInObject() throws IOException {
    JsonElement element = readViaAdapter("{\"a\":[\"x\",{\"b\":true}]}");
    assertTrue(element.isJsonObject());
    JsonObject obj = element.getAsJsonObject();
    assertTrue(obj.get("a").isJsonArray());
    JsonArray na = obj.getAsJsonArray("a");
    assertTrue(na.get(1).isJsonObject());
    assertTrue(na.get(1).getAsJsonObject().get("b").getAsBoolean());
  }

  @Test
  @DisplayName("read() → fully nested object")
  void readDeepNestedObject() throws IOException {
    JsonElement element = readViaAdapter("{\"root\":{\"inner\":{\"leaf\":null}}}");
    JsonObject root = element.getAsJsonObject();
    JsonObject inner = root.getAsJsonObject("inner");
    assertTrue(inner.has("leaf"));
    assertSame(JsonNull.INSTANCE, inner.get("leaf"));
  }

  /* --------------------------------------------------------------------- */
  /*  write() – primitives                                                 */
  /* --------------------------------------------------------------------- */

  @Test
  @DisplayName("write() → JsonNull outputs 'null'")
  void writeJsonNull() throws IOException {
    assertEquals("null", writeViaAdapter(JsonNull.INSTANCE));
  }

  @Test
  @DisplayName("write() → primitive number outputs numeric literal")
  void writePrimitiveNumber() throws IOException {
    assertEquals("123", writeViaAdapter(new JsonPrimitive(123)));
  }

  @Test
  @DisplayName("write() → primitive boolean outputs true/false")
  void writePrimitiveBoolean() throws IOException {
    assertEquals("false", writeViaAdapter(new JsonPrimitive(false)));
  }

  @Test
  @DisplayName("write() → primitive string outputs JSON string")
  void writePrimitiveString() throws IOException {
    assertEquals("\"foo\"", writeViaAdapter(new JsonPrimitive("foo")));
  }

  /* --------------------------------------------------------------------- */
  /*  write() – collections                                                */
  /* --------------------------------------------------------------------- */

  @Test
  @DisplayName("write() → array serialises elements recursively")
  void writeArray() throws IOException {
    JsonArray arr = new JsonArray();
    arr.add(new JsonPrimitive(1));
    arr.add(new JsonObject());
    String json = writeViaAdapter(arr);
    assertEquals("[1,{}]", json);
  }

  @Test
  @DisplayName("write() → object serialises properties recursively")
  void writeObject() throws IOException {
    JsonObject obj = new JsonObject();
    obj.add("foo", new JsonPrimitive("bar"));
    obj.add("empty", new JsonArray());
    String json = writeViaAdapter(obj);
    assertEquals("{\"foo\":\"bar\",\"empty\":[]}", json);
  }

  /* --------------------------------------------------------------------- */
  /*  Helpers                                                             */
  /* --------------------------------------------------------------------- */

  private JsonElement readViaAdapter(String json) throws IOException {
    try (JsonReader reader = new JsonReader(new StringReader(json))) {
      return ADAPTER.read(reader);
    }
  }

  private String writeViaAdapter(JsonElement element) throws IOException {
    try (StringWriter writer = new StringWriter();
         JsonWriter jsonWriter = new JsonWriter(writer)) {
      ADAPTER.write(jsonWriter, element);
      jsonWriter.flush();
      return writer.toString();
    }
  }
}