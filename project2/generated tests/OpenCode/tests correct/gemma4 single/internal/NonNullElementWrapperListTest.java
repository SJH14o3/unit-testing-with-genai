package com.google.gson.internal;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("NonNullElementWrapperList Test")
class NonNullElementWrapperListTest {

  private ArrayList<String> delegate;
  private NonNullElementWrapperList<String> wrapperList;

  @BeforeEach
  void setUp() {
    delegate = new ArrayList<>();
    wrapperList = new NonNullElementWrapperList<>(delegate);
  }

  @Test
  @DisplayName("Constructor should throw NullPointerException if delegate is null")
  void constructor_throwsException_whenDelegateIsNull() {
    assertThrows(NullPointerException.class, () -> new NonNullElementWrapperList<>(null));
  }

  @Test
  @DisplayName("get should return the element at the specified index")
  void get_returnsElement() {
    delegate.add("test");
    assertEquals("test", wrapperList.get(0));
  }

  @Test
  @DisplayName("size should return the correct size of the delegate")
  void size_returnsCorrectSize() {
    assertEquals(0, wrapperList.size());
    delegate.add("a");
    delegate.add("b");
    assertEquals(2, wrapperList.size());
  }

  @Nested
  @DisplayName("Modification operations (null checks)")
  class ModificationOperations {

    @Test
    @DisplayName("set should update element when not null")
    void set_updatesElement_whenNotNull() {
      delegate.add("old");
      String result = wrapperList.set(0, "new");
      assertEquals("old", result);
      assertEquals("new", wrapperList.get(0));
    }

    @Test
    @DisplayName("set should throw NullPointerException when element is null")
    void set_throwsException_whenNull() {
      delegate.add("old");
      assertThrows(NullPointerException.class, () -> wrapperList.set(0, null));
    }

    @Test
    @DisplayName("add should add element when not null")
    void add_addsElement_whenNotNull() {
      wrapperList.add(0, "new");
      assertEquals(1, wrapperList.size());
      assertEquals("new", wrapperList.get(0));
    }

    @Test
    @DisplayName("add should throw NullPointerException when element is null")
    void add_throwsException_whenNull() {
      assertThrows(NullPointerException.class, () -> wrapperList.add(0, null));
    }
  }

  @Nested
  @DisplayName("Removal operations")
  class RemovalOperations {

    @Test
    @DisplayName("remove(int) should remove element at index")
    void remove_removesElement() {
      delegate.add("a");
      delegate.add("b");
      String removed = wrapperList.remove(0);
      assertEquals("a", removed);
      assertEquals(1, wrapperList.size());
      assertEquals("b", wrapperList.get(0));
    }

    @Test
    @DisplayName("remove(Object) should remove specific object")
    void removeObject_removesObject() {
      delegate.add("a");
      delegate.add("b");
      boolean removed = wrapperList.remove("a");
      assertTrue(removed);
      assertEquals(1, wrapperList.size());
    }

    @Test
    @DisplayName("clear should remove all elements")
    void clear_clearsList() {
      delegate.add("a");
      delegate.add("b");
      wrapperList.clear();
      assertEquals(0, wrapperList.size());
    }

    @Test
    @DisplayName("removeAll should remove all elements in the collection")
    void removeAll_removesAll() {
      delegate.add("a");
      delegate.add("b");
      delegate.add("c");
      wrapperList.removeAll(Arrays.asList("a", "c"));
      assertEquals(1, wrapperList.size());
      assertEquals("b", wrapperList.get(0));
    }

    @Test
    @DisplayName("retainAll should keep only elements in the collection")
    void retainAll_retainsAll() {
      delegate.add("a");
      delegate.add("b");
      delegate.add("c");
      wrapperList.retainAll(Arrays.asList("a", "clear"));
      assertEquals(1, wrapperList.size());
      assertEquals("a", wrapperList.get(0));
    }
  }

  @Nested
  @DisplayName("Search operations")
  class SearchOperations {

    @Test
    @DisplayName("contains should return true if element exists")
    void contains_returnsTrueIfPresent() {
      delegate.add("a");
      assertTrue(wrapperList.contains("a"));
      assertFalse(wrapperList.contains("b"));
    }

    @Test
    @DisplayName("indexOf should return correct index")
    void indexOf_returnsCorrectIndex() {
      delegate.add("a");
      delegate.add("b");
      assertEquals(0, wrapperList.indexOf("a"));
      assertEquals(1, wrapperList.indexOf("b"));
      assertEquals(-1, wrapperList.indexOf("c"));
    }

    @Test
    @DisplayName("lastIndexOf should return correct last index")
    void lastIndexOf_returnsCorrectIndex() {
      delegate.add("a");
      delegate.add("b");
      delegate.add("a");
      assertEquals(2, wrapperList.lastIndexOf("a"));
      assertEquals(1, wrapperList.lastIndexOf("b"));
      assertEquals(-1, wrapperList.lastIndexOf("c"));
    }
  }

  @Nested
  @DisplayName("Array and Object methods")
  class ObjectMethods {

    @Test
    @DisplayName("toArray should return array containing elements")
    void toArray_returnsArray() {
      delegate.add("a");
      delegate.add("b");
      Object[] array = wrapperList.toArray();
      assertArrayEquals(new Object[]{"a", "b"}, array);
    }

    @Test
    @DisplayName("toArray(T[]) should return typed array")
    @SuppressWarnings("unchecked")
    void toArray_typed_returnsTypedArray() {
      delegate.add("a");
      String[] array = wrapperList.toArray(new String[0]);
      assertArrayEquals(new String[]{"a"}, array);
    }

    @Test
    @DisplayName("equals should compare content")
    void equals_comparesContent() {
      ArrayList<String> delegate2 = new ArrayList<>();
      delegate2.add("a");
      NonNullElementWrapperList<String> list2 = new NonNullElementWrapperList<>(delegate2);
      
      delegate.add("a");
      assertEquals(wrapperList, list2);
      
      delegate.add("b");
      assertNotEquals(wrapperList, list2);
    }

    @Test
    @DisplayName("hashCode should be consistent with delegate")
    void hashCode_isConsistent() {
      delegate.add("a");
      assertEquals(delegate.hashCode(), wrapperList.hashCode());
    }
  }
}