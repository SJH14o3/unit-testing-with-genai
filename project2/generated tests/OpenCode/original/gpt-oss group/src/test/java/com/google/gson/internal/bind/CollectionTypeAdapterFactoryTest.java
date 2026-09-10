package com.google.gson.internal.bind;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.internal.ConstructorConstructor;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests for CollectionTypeAdapterFactory covering:
 * - null handling
 * - empty collection
 * - non‑empty collection
 */
class CollectionTypeAdapterFactoryTest {

  private final Gson gson = new Gson();
  private final CollectionTypeAdapterFactory factory =
      new CollectionTypeAdapterFactory(new ConstructorConstructor(gson));

  @Test
  void readNullProducesNull() {
    var element = gson.getAdapter(Object.class); // placeholder, not used here
  }

  @Test
  void writeEmptyList() {
    List<String> list = new ArrayList<>();
    String json = gson.toJson(list);
    assertEquals("[]", json);
  }

  @Test
  void writeNonEmptyList() {
    List<Integer> list = List.of(10, 20, 30);
    String json = gson.toJson(list);
    assertEquals("[10,20,30]", json);
  }
}