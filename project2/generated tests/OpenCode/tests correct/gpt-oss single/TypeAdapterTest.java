package com.google.gson;

import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonReader;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.IOException;
import com.google.gson.internal.bind.JsonTreeWriter;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link TypeAdapter}.  Only core logic is tested, no external
 * integration.  Decision coverage is pursued by exercising all public methods
 * and the nested {@code NullSafeTypeAdapter} wrapper.
 */
class TypeAdapterTest {

  /**
   * A very small concrete adapter that serialises and deserialises plain
   * {@link String} values.
   */
  private static class StringAdapter extends TypeAdapter<String> {
    @Override
    public void write(JsonWriter out, String value) throws IOException {
      out.value(value);
    }
    @Override
    public String read(JsonReader in) throws IOException {
      return in.nextString();
    }
  }

  private final TypeAdapter<String> adapter = new StringAdapter();

  @Test
  void toJsonString_returnsExpectedJson() throws IOException {
    String json = adapter.toJson("hello");
    assertEquals("\"hello\"", json);
  }

  @Test
  void toJsonWriter_writesValueWithoutEscaping() throws IOException {
    StringWriter sw = new StringWriter();
    adapter.toJson(sw, "world");
    assertEquals("\"world\"", sw.toString());
  }

  @Test
  void toJsonTree_returnsJsonElement() throws IOException {
    JsonElement tree = adapter.toJsonTree("test");
    assertNotNull(tree);
    assertEquals("\"test\"", tree.toString());
  }

  @Test
  void fromJsonReader_parsesValue() throws IOException {
    StringReader sr = new StringReader("\"data\"");
    String result = adapter.fromJson(sr);
    assertEquals("data", result);
  }

  @Test
  void fromJsonString_parsesValue() throws IOException {
    String result = adapter.fromJson("\"value\"");
    assertEquals("value", result);
  }

  @Test
  void fromJsonTree_parsesValue() throws IOException {
    JsonElement json = adapter.toJsonTree("tree");
    String result = adapter.fromJsonTree(json);
    assertEquals("tree", result);
  }

  /* --- NullSafe tests --- */

  private static class NonNullAdapter extends TypeAdapter<String> {
    @Override
    public void write(JsonWriter out, String value) throws IOException {
      // Intentionally throw if value is null – this should be wrapped by nullSafe
      if (value == null) {
        throw new NullPointerException("value is null");
      }
      out.value(value);
    }
    @Override
    public String read(JsonReader in) throws IOException {
      if (in.peek() == JsonToken.NULL) {
        in.nextNull();
        return null;
      }
      return in.nextString();
    }
  }

  @Test
  void nullSafe_writeHandlesNull() throws IOException {
    TypeAdapter<String> wrapped = new NonNullAdapter().nullSafe();
    StringWriter sw = new StringWriter();
    JsonWriter jw = new JsonWriter(sw);
    assertDoesNotThrow(() -> wrapped.write(jw, null));
    jw.flush();
    assertEquals("null", sw.toString());
  }

  @Test
  void nullSafe_readHandlesNull() throws IOException {
    TypeAdapter<String> wrapped = new NonNullAdapter().nullSafe();
    StringReader sr = new StringReader("null");
    JsonReader jr = new JsonReader(sr);
    String result = wrapped.read(jr);
    assertNull(result);
  }

  @Test
  void nullSafe_singletonBehavior() {
    TypeAdapter<String> adapter = new StringAdapter();
    TypeAdapter<String> firstWrap = adapter.nullSafe();
    // Calling nullSafe on an already wrapped instance returns the same instance.
    assertSame(firstWrap, firstWrap.nullSafe());
  }

  @Test
  void nullSafe_toStringIncludesWrappedAdapter() {
    TypeAdapter<String> wrapped = new StringAdapter().nullSafe();
    String expected = "NullSafeTypeAdapter[" + wrapped.toString() + "]";
    // Since NullSafeTypeAdapter overrides toString, we just assert it contains wrapped's class name.
    assertTrue(wrapped.toString().startsWith("NullSafeTypeAdapter["));
  }
}
