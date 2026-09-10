package com.google.gson.internal;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

class NonNullElementWrapperListTest {

    @Test
    @DisplayName("Basic list operations and null prevention")
    void testListOperations() {
        ArrayList<String> delegate = new ArrayList<>();
        NonNullElementWrapperList<String> list = new NonNullElementWrapperList<>(delegate);

        list.add("one");
        list.add("two");

        assertEquals(2, list.size());
        assertEquals("one", list.get(0));
        assertEquals("two", list.get(1));

        list.set(0, "updated");
        assertEquals("updated", list.get(0));

        list.remove(1);
        assertEquals(1, list.size());
        assertEquals("updated", list.get(0));
    }

    @Test
    @DisplayName("Prevent null on add")
    void testPreventNullOnAdd() {
        ArrayList<String> delegate = new ArrayList<>();
        NonNullElementWrapperList<String> list = new NonNullElementWrapperList<>(delegate);

        assertThrows(NullPointerException.class, () -> list.add(null));
        assertThrows(NullPointerException.class, () -> list.add(0, null));
    }

    @Test
    @DisplayName("Prevent null on set")
    void testPreventNullOnSet() {
        ArrayList<String> delegate = new ArrayList<>();
        delegate.add("one");
        NonNullElementWrapperList<String> list = new NonNullElementWrapperList<>(delegate);

        assertThrows(NullPointerException.class, () -> list.set(0, null));
    }

    @Test
    @DisplayName("Contains and other delegate methods")
    void testDelegateMethods() {
        ArrayList<String> delegate = new ArrayList<>();
        delegate.add("one");
        NonNullElementWrapperList<String> list = new NonNullElementWrapperList<>(delegate);

        assertTrue(list.contains("one"));
        assertFalse(list.contains("two"));
        assertEquals(0, list.indexOf("one"));
        assertEquals(-1, list.indexOf("two"));
    }

    @Test
    @DisplayName("Clear and removeAll")
    void testBulkOperations() {
        ArrayList<String> delegate = new ArrayList<>();
        delegate.add("one");
        delegate.add("two");
        NonNullElementWrapperList<String> list = new NonNullElementWrapperList<>(delegate);

        list.clear();
        assertEquals(0, list.size());

        delegate.add("one");
        delegate.add("two");
        list.removeAll(java.util.List.of("one"));
        assertEquals(1, list.size());
        assertEquals("two", list.get(0));
    }
}
