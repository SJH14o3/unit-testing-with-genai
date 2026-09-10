package com.google.gson;

import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LongSerializationPolicyTest {

  @Test
  @DisplayName("DEFAULT serializes to number or JsonNull")
  void defaultPolicy() {
    assertSame(JsonNull.INSTANCE, LongSerializationPolicy.DEFAULT.serialize(null));
    JsonPrimitive expected = new JsonPrimitive(123L);
    assertEquals(expected, LongSerializationPolicy.DEFAULT.serialize(123L));
  }

  @Test
  @DisplayName("STRING serializes to string or JsonNull")
  void stringPolicy() {
    assertSame(JsonNull.INSTANCE, LongSerializationPolicy.STRING.serialize(null));
    JsonPrimitive expected = new JsonPrimitive("123");
    assertEquals(expected, LongSerializationPolicy.STRING.serialize(123L));
  }
}