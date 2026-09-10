package com.google.gson.internal;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

class UnsafeAllocatorTest {
  @Test
  @DisplayName("INSTANCE is non‑null")
  void instanceNonNull() {
    assertNotNull(UnsafeAllocator.INSTANCE);
  }

  @Test
  @DisplayName("newInstance on interface throws AssertionError or UnsupportedOperationException")
  void newInstanceInterface() {
    assertThrows(RuntimeException.class, () -> UnsafeAllocator.INSTANCE.newInstance(Runnable.class));
  }
}
