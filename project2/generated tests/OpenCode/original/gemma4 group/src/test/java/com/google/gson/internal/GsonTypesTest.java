package com.google.gson.internal;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class GsonTypesTest {

  @Test
  void testGetRawType() {
    assertEquals(String.class, GsonTypes.getRawType(String.class));
    assertEquals(List.class, GsonTypes.getRawType(new com.google.gson.reflect.TypeToken<List<String>>() {}.getType()));
  }

  @Test
  void testEquals() {
    Type type1 = String.class;
    Type type2 = String.class;
    Type type3 = Integer.class;
    
    assertTrue(GsonTypes.equals(type1, type2));
    assertFalse(GsonTypes.equals(type1, type3));
    
    Type param1 = new com.google.gson.reflect.TypeToken<List<String>>() {}.getType();
    Type param2 = new com.google.gson.reflect.TypeToken<List<String>>() {}.getType();
    Type param3 = new com.google.gson.reflect.TypeToken<List<Integer>>() {}.getType();
    
    assertTrue(GsonTypes.equals(param1, param2));
    assertFalse(GsonTypes.equals(param1, param3));
  }

  @Test
  void testTypeToString() {
    assertEquals("java.lang.String", GsonTypes.typeToString(String.class));
  }

  @Test
  void testGetArrayComponentType() {
    assertEquals(String.class, GsonTypes.getArrayComponentType(String[].class));
  }

  @Test
  void testGetMapKeyAndValueTypes() {
    Type[] types = GsonTypes.getMapKeyAndValueTypes(Properties.class, Properties.class);
    assertEquals(String.class, types[0]);
    assertEquals(String.class, types[1]);
  }
}
