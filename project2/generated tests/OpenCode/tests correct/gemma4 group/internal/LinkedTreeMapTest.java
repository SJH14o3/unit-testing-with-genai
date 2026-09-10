package com.google.gson.internal;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

class LinkedTreeMapTest {

    @Test
    @DisplayName("Natural order constructor and basic operations")
    void naturalOrderConstructorAndOperations() {
        LinkedTreeMap<Integer, String> map = new LinkedTreeMap<>();
        map.put(2, "two");
        map.put(1, "one");
        map.put(3, "three");

        assertEquals(3, map.size());
        assertEquals("two", map.get(2));
        assertEquals("one", map.get(1));
        assertEquals("three", map.get(3));
        assertTrue(map.containsKey(1));
        assertFalse(map.containsKey(4));
    }

    @Test
    @DisplayName("Constructor with comparator")
    void constructorWithComparator() {
        Comparator<Integer> comparator = (a, b) -> b.compareTo(a); // Reverse order
        LinkedTreeMap<Integer, String> map = new LinkedTreeMap<>(comparator, true);
        map.put(1, "one");
        map.put(2, "two");

        // In reverse order, 2 comes before 1 in the tree structure,
        // but LinkedTreeMap preserves insertion order for iteration.
        // However, find() uses the comparator.
        Iterator<Map.Entry<Integer, String>> it = map.entrySet().iterator();
        assertEquals(1, it.next().getKey());
        assertEquals(2, it.next().getKey());
    }

    @Test
    @DisplayName("Constructor with allowNullValues = false")
    void constructorDisallowNullValues() {
        LinkedTreeMap<Integer, String> map = new LinkedTreeMap<>(false);
        map.put(1, "one");
        assertThrows(NullPointerException.class, () -> map.put(2, null));
    }

    @Test
    @DisplayName("Put returns previous value")
    void putReturnsPreviousValue() {
        LinkedTreeMap<Integer, String> map = new LinkedTreeMap<>();
        map.put(1, "one");
        String result = map.put(1, "new_one");
        assertEquals("one", result);
        assertEquals("new_one", map.get(1));
    }

    @Test
    @DisplayName("Remove operation")
    void remove() {
        LinkedTreeMap<Integer, String> map = new LinkedTreeMap<>();
        map.put(1, "one");
        map.put(2, "two");
        assertEquals("one", map.remove(1));
        assertEquals(1, map.size());
        assertNull(map.get(1));
    }

    @Test
    @DisplayName("Clear operation")
    void clear() {
        LinkedTreeMap<Integer, String> map = new LinkedTreeMap<>();
        map.put(1, "one");
        map.clear();
        assertEquals(0, map.size());
        assertFalse(map.containsKey(1));
    }

    @Test
    @DisplayName("Null key throws exception")
    void nullKeyThrowsException() {
        LinkedTreeMap<Integer, String> map = new LinkedTreeMap<>();
        assertThrows(NullPointerException.class, () -> map.put(null, "value"));
    }

    @Test
    @DisplayName("Concurrent modification detection")
    void concurrentModification() {
        LinkedTreeMap<Integer, String> map = new LinkedTreeMap<>();
        map.put(1, "one");
        map.put(2, "two");
        Iterator<Integer> it = map.keySet().iterator();
        it.next();
        map.put(3, "three");
        assertThrows(java.util.ConcurrentModificationException.class, it::next);
    }

    @Test
    @DisplayName("Node.setValue and Node.getKey")
    void nodeOperations() {
        LinkedTreeMap<Integer, String> map = new LinkedTreeMap<>();
        map.put(1, "one");
        Map.Entry<Integer, String> entry = map.entrySet().iterator().next();
        assertEquals(1, entry.getKey());
        assertEquals("one", entry.getValue());
        assertEquals("one", entry.setValue("new_one"));
    }
}
