package com.google.gson.internal;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NonNullElementWrapperListTest {

  @Test
  @DisplayName("constructor rejects null delegate")
  void constructorNullDelegate() {
    assertThrows(NullPointerException.class,
        () -> new NonNullElementWrapperList<>(null));
  }

  @Test
  @DisplayName("add permits non‑null but rejects null")
  void add() {
    ArrayList<String> base = new ArrayList<>();
    NonNullElementWrapperList<String> list = new NonNullElementWrapperList<>(base);
    list.add(0, "a");
    assertEquals(1, list.size());
    assertThrows(NullPointerException.class, () -> list.add(1, null));
  }

  @Test
  @DisplayName("set throws when value is null")
  void setNull() {
    ArrayList<String> base = new ArrayList<>();
    List<String> list = new NonNullElementWrapperList<>(base);
    list.add("x");
    assertThrows(NullPointerException.class, () -> ((NonNullElementWrapperList<String>) list).set(0, null));
  }

  @Test
  @DisplayName("remove by index works")
  void removeIndex() {
    ArrayList<String> base = new ArrayList<>();
    NonNullElementWrapperList<String> list = new NonNullElementWrapperList<>(base);
    list.add("one");
    list.add("two");
    assertEquals("one", list.remove(0));
    assertEquals(1, list.size());
  }

  @Test
  @DisplayName("remove by object delegates")
  void removeObject() {
    ArrayList<String> base = new ArrayList<>();
    NonNullElementWrapperList<String> list = new NonNullElementWrapperList<>(base);
    list.add("alpha");
    list.add("beta");
    assertTrue(list.remove("alpha"));
    assertFalse(list.remove("gamma"));
  }

  @Test
  @DisplayName("contains, indexOf, and iterator behave as expected")
  void listOperations() {
    ArrayList<Integer> base = new ArrayList<>();
    NonNullElementWrapperList<Integer> list = new NonNullElementWrapperList<>(base);
    list.add(10);
    list.add(20);
    list.add(30);

    assertTrue(list.contains(20));
    assertEquals(1, list.indexOf(20));
    assertEquals(2, list.lastIndexOf(30));

    assertTrue(list.iterator().hasNext());
    assertEquals(10, list.iterator().next());
  }

  @Test
  @DisplayName("clear empties underlying collection")
  void clear() {
    ArrayList<Integer> base = new ArrayList<>();
    NonNullElementWrapperList<Integer> list = new NonNullElementWrapperList<>(base);
    list.add(1);
    list.clear();
    assertEquals(0, list.size());
    assertTrue(base.isEmpty());
  }
}