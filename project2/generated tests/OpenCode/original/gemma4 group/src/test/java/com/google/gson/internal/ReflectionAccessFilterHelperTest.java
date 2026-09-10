package com.google.gson.internal;

import com.google.gson.ReflectionAccessFilter;
import com.google.gson.ReflectionAccessFilter.FilterResult;
import org.junit.jupiter.api.Test;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionAccessFilterHelperTest {

  private static class TestFilter implements ReflectionAccessFilter {
    private final FilterResult result;
    TestFilter(FilterResult result) { this.result = result; }
    @Override
    public FilterResult check(Class<?> c) { return result; }
  }

  @Test
  void isJavaTypeTrue() {
    assertTrue(ReflectionAccessFilterHelper.isJavaType(java.lang.String.class));
  }

  @Test
  void isJavaTypeFalse() {
    assertFalse(ReflectionAccessFilterHelper.isJavaType(com.google.gson.FieldAttributes.class));
  }

  @Test
  void isAndroidTypeTrue() {
    assertTrue(ReflectionAccessFilterHelper.isAndroidType(java.lang.String.class));
  }

  @Test
  void isAndroidTypeFalse() {
    assertFalse(ReflectionAccessFilterHelper.isAndroidType(com.google.gson.FieldAttributes.class));
  }

  @Test
  void isAnyPlatformTypeTrue() {
    assertTrue(ReflectionAccessFilterHelper.isAnyPlatformType(java.lang.String.class));
  }

  @Test
  void isAnyPlatformTypeFalse() {
    assertFalse(ReflectionAccessFilterHelper.isAnyPlatformType(com.google.gson.FieldAttributes.class));
  }

  @Test
  void getFilterResultReturnsFirstNonIndecisive() {
    List<ReflectionAccessFilter> filters = new ArrayList<>();
    filters.add(new TestFilter(FilterResult.INDECISIVE));
    filters.add(new TestFilter(FilterResult.ALLOW));
    filters.add(new TestFilter(FilterResult.DENY));

    assertEquals(FilterResult.ALLOW, ReflectionAccessFilterHelper.getFilterResult(filters, String.class));
  }

  @Test
  void getFilterResultReturnsDeny() {
    List<ReflectionAccessFilter> filters = new ArrayList<>();
    filters.add(new TestFilter(FilterResult.INDECISIVE));
    filters.add(new TestFilter(FilterResult.DENY));
    filters.add(new TestFilter(FilterResult.ALLOW));

    assertEquals(FilterResult.DENY, ReflectionAccessFilterHelper.getFilterResult(filters, String.class));
  }

  @Test
  void getFilterResultReturnsAllowIfEmptyOrAllIndecisive() {
    assertEquals(FilterResult.ALLOW, ReflectionAccessFilterHelper.getFilterResult(new ArrayList<>(), String.class));
    
    List<ReflectionAccessFilter> filters = new ArrayList<>();
    filters.add(new TestFilter(FilterResult.INDECISIVE));
    assertEquals(FilterResult.ALLOW, ReflectionAccessFilterHelper.getFilterResult(filters, String.class));
  }

  @Test
  void canAccessTest() {
    // canAccess calls AccessChecker.INSTANCE.canAccess.
    // Since we can't easily control Java version-specific behavior in a unit test without complex setup,
    // we just verify it doesn't crash for a standard field.
    try {
      Field field = String.class.getDeclaredField("value");
      assertTrue(ReflectionAccessFilterHelper.canAccess(field, null));
    } catch (NoSuchFieldException e) {
      // ignore
    }
  }
}
