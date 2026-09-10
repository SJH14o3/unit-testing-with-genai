package com.google.gson;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.StringReader;
import java.util.Iterator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JsonStreamParserTest {

  @Test
  @DisplayName("parses multiple JSON elements in a single string")
  void parseMultipleElements() {
    String input = "[1,2] {\"a\":3} 4";
    JsonStreamParser p = new JsonStreamParser(input);
    Iterator<JsonElement> it = p;
    assertTrue(it.hasNext());
    assertEquals("[1,2]", it.next().toString());
    assertTrue(it.hasNext());
    assertEquals("{\"a\":3}", it.next().toString());
    assertTrue(it.hasNext());
    assertEquals("4", it.next().toString());
    assertFalse(it.hasNext());
  }

  @Test
  @DisplayName("throws NoSuchElementException when no more elements")
  void noElements() {
    JsonStreamParser p = new JsonStreamParser("{\"ok\":true}");
    assertTrue(p.hasNext());
    p.next();
    assertFalse(p.hasNext());
    assertThrows(NoSuchElementException.class, p::next);
  }

  @Test
  @DisplayName("is thread‑safe when synchronized")
  void threadSafe() throws Exception {
    // basic sanity: two threads reading from the same parser won't corrupt state
    String json = "1 2 3";
    JsonStreamParser p = new JsonStreamParser(json);
    Thread t1 = new Thread(() -> {
      while (p.hasNext()) p.next();
    });
    Thread t2 = new Thread(() -> {
      while (p.hasNext()) p.next();
    });
    t1.start();
    t2.start();
    t1.join();
    t2.join();
    assertFalse(p.hasNext());
  }
}