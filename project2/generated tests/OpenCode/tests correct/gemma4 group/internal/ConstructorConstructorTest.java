package com.google.gson.internal;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

class ConstructorConstructorTest {

  static class TestClass {
    public TestClass() {}
  }

  static abstract class AbstractClass {}
  interface TestInterface {}

  @Test
  void testCheckInstantiable() {
    assertNull(ConstructorConstructor.checkInstantiable(TestClass.class));
    assertNotNull(ConstructorConstructor.checkInstantiable(TestInterface.class));
    assertNotNull(ConstructorConstructor.checkInstantiable(AbstractClass.class));
  }

  @Test
  void testGetInstanceCreatorConstructor() {
    Map<Type, InstanceCreator<?>> creators = new HashMap<>();
    InstanceCreator<TestClass> creator = type -> new TestClass();
    creators.put(TestClass.class, creator);

    ConstructorConstructor constructor = new ConstructorConstructor(creators, false, Collections.emptyList());
    TypeToken<TestClass> token = new TypeToken<TestClass>() {};
    
    Object instance = constructor.get(token).construct();
    assertNotNull(instance);
    assertTrue(instance instanceof TestClass);
  }

  @Test
  void testGetDefaultConstructor() {
    ConstructorConstructor constructor = new ConstructorConstructor(new HashMap<>(), false, Collections.emptyList());
    TypeToken<TestClass> token = new TypeToken<TestClass>() {};
    
    Object instance = constructor.get(token).construct();
    assertNotNull(instance);
    assertTrue(instance instanceof TestClass);
  }

  @Test
  void testNewCollectionConstructorArrayList() {
    ConstructorConstructor constructor = new ConstructorConstructor(new HashMap<>(), false, Collections.emptyList());
    TypeToken<ArrayList<String>> token = new TypeToken<ArrayList<String>>() {};
    
    Object instance = constructor.get(token).construct();
    assertTrue(instance instanceof ArrayList);
  }

  @Test
  void testNewMapConstructorLinkedHashMap() {
    ConstructorConstructor constructor = new ConstructorConstructor(new HashMap<>(), false, Collections.emptyList());
    TypeToken<LinkedHashMap<String, String>> token = new TypeToken<LinkedHashMap<String, String>>() {};
    
    Object instance = constructor.get(token).construct();
    assertTrue(instance instanceof LinkedHashMap);
  }
}
