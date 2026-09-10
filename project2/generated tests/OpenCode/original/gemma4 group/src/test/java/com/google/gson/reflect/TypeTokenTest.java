package com.google.gson.reflect;

import com.google.gson.internal.GsonTypes;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TypeTokenTest {

  @Test
  void testGetRawType() {
    TypeToken<String> token = new TypeToken<String>() {};
    assertEquals(String.class, token.getRawType());
  }

  @Test
  void testGetType() {
    TypeToken<List<String>> token = new TypeToken<List<String>>() {};
    assertEquals(List.class, token.getType().getRawType());
  }

  @Test
  void testEquals() {
    TypeToken<String> token1 = new TypeToken<String>() {};
    TypeToken<String> token2 = new TypeToken<String>() {};
    TypeToken<Integer> token3 = new TypeToken<Integer>() {};

    assertEquals(token1, token2);
    assertNotEquals(token1, token3);
  }

  @Test
  void testHashCode() {
    TypeToken<String> token1 = new TypeToken<String>() {};
    TypeToken<String> token2 = new TypeToken<String>() {};
    assertEquals(token1.hashCode(), token2.hashCode());
  }

  @Test
  void testGetParameterized() {
    Type token = GsonTypes.newParameterizedTypeWithOwner(null, Map.class, String.class, Integer.class);
    TypeToken<?> tokenResult = TypeToken.getParameterized(Map.class, String.class, Integer.class);
    assertEquals(token, tokenResult.getType());
  }

  @Test
  void testGetArray() {
    Type token = TypeToken.getArray(String.class);
    assertTrue(token.toString().contains("String[]"));
  }

  @Test
  void testGet() {
    TypeToken<String> token = TypeToken.get(String.class);
    assertEquals(String.class, token.getRawType());
  }
}
