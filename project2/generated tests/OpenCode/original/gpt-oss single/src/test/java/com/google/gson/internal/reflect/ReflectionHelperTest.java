package com.google.gson.internal.reflect;

import com.google.gson.JsonIOException;
import java.lang.reflect.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReflectionHelperTest {

  /** A trivial record that we can use to exercise record‑related code. */
  private record SampleRecord(String name, int value) {}

  //------------------------------------------------------------------
  // makeAccessible
  //------------------------------------------------------------------
  @Test
  void testMakeAccessibleSuccess() throws Exception {
    Field field = SampleRecord.class.getDeclaredField("name");
    assertThrows(IllegalAccessException.class, field::setAccessible);
    // makeAccessible should not throw
    ReflectionHelper.makeAccessible(field);
    // After making accessible, setAccessible(true) should succeed
    field.setAccessible(false); // undo for repeatability
    ReflectionHelper.makeAccessible(field);
  }

  @Test
  void testMakeAccessibleFailure() {
    // Use a dummy object that can't become accessible – use a method from java.lang.String
    Method m = String.class.getDeclaredMethod("substring", int.class, int.class);
    // setAccessible(false) not supported; making it accessible throws
    assertThrows(JsonIOException.class, () -> ReflectionHelper.makeAccessible(m));
  }

  //------------------------------------------------------------------
  // getAccessibleObjectDescription
  //------------------------------------------------------------------
  @Test
  void descriptionField() throws Exception {
    Field f = SampleRecord.class.getDeclaredField("value");
    String desc = ReflectionHelper.getAccessibleObjectDescription(f, false);
    assertTrue(desc.contains("field 'com.google.gson.internal.reflect.SampleRecord#value'"));
  }

  @Test
  void descriptionMethod() throws Exception {
    Method m = SampleRecord.class.getMethod("name");
    String desc = ReflectionHelper.getAccessibleObjectDescription(m, false);
    assertTrue(desc.contains(
        "method 'com.google.gson.internal.reflect.SampleRecord#name()'"));
  }

  @Test
  void descriptionConstructor() {
    Constructor<? extends SampleRecord> ctor =
        SampleRecord.class.getDeclaredConstructors()[0];
    String desc =
        ReflectionHelper.getAccessibleObjectDescription(ctor, false);
    assertTrue(desc.contains("constructor 'com.google.gson.internal.reflect.SampleRecord(String,int)'"));
  }

  @Test
  void descriptionUnknown() {
    Object obj = new Object();
    String desc = ReflectionHelper.getAccessibleObjectDescription(obj, false);
    assertTrue(desc.startsWith("<unknown AccessibleObject>"));
  }

  @Test
  void descriptionUppercase() throws Exception {
    Field f = SampleRecord.class.getDeclaredField("name");
    String desc = ReflectionHelper.getAccessibleObjectDescription(f, true);
    assertTrue(Character.isUpperCase(desc.charAt(0)));
  }

  //------------------------------------------------------------------
  // isStatic
  //------------------------------------------------------------------
  @Test
  void testIsStatic() {
    assertTrue(Modifier.isStatic(SampleRecord.class.getModifiers()));
    // instance class – non-static
    class Inner {}
    assertFalse(ReflectionHelper.isStatic(Inner.class));
  }

  //------------------------------------------------------------------
  // isAnonymousOrNonStaticLocal
  //------------------------------------------------------------------
  @Test
  void testIsAnonymousOrNonStaticLocal() {
    // anonymous class
    class Dummy {}
    assertTrue(ReflectionHelper.isAnonymousOrNonStaticLocal(new Dummy().getClass()));
    // local non‑static class
    class Local {}
    assertTrue(ReflectionHelper.isAnonymousOrNonStaticLocal(Local.class));
    // static nested class
    class StaticNested {}
    assertFalse(ReflectionHelper.isAnonymousOrNonStaticLocal(StaticNested.class));
  }

  //------------------------------------------------------------------
  // tryMakeAccessible
  //------------------------------------------------------------------
  @Test
  void testTryMakeAccessibleSuccess() throws Exception {
    Constructor<?> ctor = SampleRecord.class.getDeclaredConstructors()[0];
    assertNull(ReflectionHelper.tryMakeAccessible(ctor));
  }

  @Test
  void testTryMakeAccessibleFailure() {
    Constructor<?> ctor = String.class.getDeclaredConstructor(String.class);
    String msg = ReflectionHelper.tryMakeAccessible(ctor);
    assertTrue(msg.contains("Failed making constructor"));
    assertTrue(msg.contains("to module com.google.gson") || !msg.contains("to module"));
  }

  //------------------------------------------------------------------
  // record helper methods
  //------------------------------------------------------------------
  @Test
  void testRecordDetection() {
    assertTrue(ReflectionHelper.isRecord(SampleRecord.class));
    assertFalse(ReflectionHelper.isRecord(String.class));
  }

  @Test
  void testGetRecordComponentNames() {
    String[] names = ReflectionHelper.getRecordComponentNames(SampleRecord.class);
    assertArrayEquals(new String[]{"name", "value"}, names);
  }

  @Test
  void testGetAccessor() throws Exception {
    Field f = SampleRecord.class.getDeclaredField("name");
    Method accessor = ReflectionHelper.getAccessor(SampleRecord.class, f);
    assertNotNull(accessor);
    assertEquals("name", accessor.getName());
  }

  @Test
  void testCanonicalRecordConstructor() {
    Constructor<SampleRecord> ctor =
        ReflectionHelper.getCanonicalRecordConstructor(SampleRecord.class);
    assertEquals(SampleRecord.class, ctor.getDeclaringClass());
    Class<?>[] paramTypes = ctor.getParameterTypes();
    assertArrayEquals(new Class<?>[]{String.class, int.class}, paramTypes);
  }
}