package com.google.gson;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReflectionAccessFilterTest {

  @Test
  void testBlockAllJava() {
    assertEquals(ReflectionAccessFilter.FilterResult.BLOCK_INACCESSIBLE, 
                 ReflectionAccessFilter.BLOCK_INACCESSIBLE_JAVA.check(String.class));
  }

  @Test
  void testBlockAllJavaIndecisive() {
    class MyClass {}
    assertEquals(ReflectionAccessFilter.FilterResult.INDECISIVE, 
                 ReflectionAccessFilter.BLOCK_INACCESSIBLE_JAVA.check(MyClass.class));
  }

  @Test
  void testBlockAllAndroidIndecisive() {
    class MyClass {}
    assertEquals(ReflectionAccessFilter.FilterResult.INDECISIVE, 
                 ReflectionAccessFilter.BLOCK_ALL_ANDROID.check(MyClass.class));
  }

  @Test
  void testBlockAllPlatformIndecisive() {
    class MyClass {}
    assertEquals(ReflectionAccessFilter.FilterResult.INDECISIVE, 
                 ReflectionAccessFilter.BLOCK_ALL_PLATFORM.check(MyClass.class));
  }
}
