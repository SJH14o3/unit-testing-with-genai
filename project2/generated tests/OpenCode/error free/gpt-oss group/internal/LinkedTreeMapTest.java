package com.google.gson.internal;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import java.util.Iterator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LinkedTreeMapTest {

  @Test
  @DisplayName("put, get and size")
  void putGetSize() {
    LinkedTreeMap<String, Integer> map = new LinkedTreeMap<>();
    assertEquals(0, map.size());

    assertNull(map.put("a", 1));
    assertEquals(1, map.size());
    assertEquals(1, map.get("a"));
  }

  @Test
  @DisplayName("duplicate key updates value and returns old value")
  void duplicateKey() {
    LinkedTreeMap<String, Integer> map = new LinkedTreeMap<>();
    assertNull(map.put("x", 10));
    assertEquals(10, map.put("x", 20));
    assertEquals(20, map.get("x"));
    assertEquals(1, map.size());
  }

  @Test
  @DisplayName("containsKey and remove")
  void containsAndRemove() {
    LinkedTreeMap<String, String> map = new LinkedTreeMap<>();
    map.put("k1", "v1");
    map.put("k2", "v2");
    assertTrue(map.containsKey("k1"));
    assertEquals(2, map.size());

    assertEquals("v1", map.remove("k1"));
    assertFalse(map.containsKey("k1"));
    assertEquals(1, map.size());
    assertNull(map.remove("nonexistent"));
  }

  @Test
  @DisplayName("insertion order preserved in entrySet")
  void insertionOrderEntrySet() {
    LinkedTreeMap<Integer, String> map = new LinkedTreeMap<>();
    map.put(1, "one");
    map.put(3, "three");
    map.put(2, "two");

    Iterator<LinkedTreeMap.Entry<Integer, String>> it =
        map.entrySet().iterator();
    assertEquals(1, it.next().getKey());
    assertEquals(3, it.next().getKey());
    assertEquals(2, it.next().getKey());
  }

  @Test
  @DisplayName("insertion order preserved in keySet")
  void insertionOrderKeySet() {
    LinkedTreeMap<String, Integer> map = new LinkedTreeMap<>();
    map.put("first", 1);
    map.put("second", 2);
    map.put("third", 3);

    Iterator<String> it = map.keySet().iterator();
    assertEquals("first", it.next());
    assertEquals("second", it.next());
    assertEquals("third", it.next());
  }

  @Test
  @DisplayName("null key throws NullPointerException")
  void nullKey() {
    LinkedTreeMap<String, String> map = new LinkedTreeMap<>();
    assertThrows(NullPointerException.class, () -> map.put(null, "val"));
  }

  @Test
  @DisplayName("null value not allowed when constructor flag is false")
  void nullValueNotAllowed() {
    LinkedTreeMap<String, Integer> map = new LinkedTreeMap<>();
    assertThrows(NullPointerException.class, () -> map.put("key", null));
  }

  @Test
  @DisplayName("null value allowed when allowNullValues is true")
  void nullValueAllowed() {
    LinkedTreeMap<String, String> map = new LinkedTreeMap<>(true);
    assertNull(map.put("key", null));
    assertTrue(map.containsKey("key"));
    assertNull(map.get("key"));
  }

  @Test
  @DisplayName("clear empties the map")
  void clear() {
    LinkedTreeMap<String, Integer> map = new LinkedTreeMap<>();
    map.put("a", 1);
    map.put("b", 2);
    map.clear();
    assertEquals(0, map.size());
    assertFalse(map.containsKey("a"));
  }
}