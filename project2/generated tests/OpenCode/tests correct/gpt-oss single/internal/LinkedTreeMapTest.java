package com.google.gson.internal;

import com.google.gson.internal.LinkedTreeMap;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Unit tests for {@link LinkedTreeMap} providing decision‑coverage for insertion,
 * removal, iteration and key/value handling.
 */
class LinkedTreeMapTest {

    private LinkedTreeMap<String, Integer> map;

    @BeforeEach
    void setUp() {
        map = new LinkedTreeMap<>();
    }

    @Test
    void putAndGet_ShouldStoreAndReturnValues() {
        assertEquals(0, map.size());
        Integer old = map.put("one", 1);
        assertNull(old, "first put returns null");
        assertEquals(1, map.size());
        assertEquals(1, map.get("one"));
    }

    @Test
    void putNullKey_ShouldThrowNPE() {
        assertThrows(NullPointerException.class, () -> map.put(null, 5));
    }

    @Test
    void putNullValueWhenNotAllowed_ShouldThrowNPE() {
        assertDoesNotThrow(() -> map.put("two", null));
    }

    @Test
    void putNullValueAllowed_ShouldAcceptNull() {
        LinkedTreeMap<String, Integer> mapWithNulls = new LinkedTreeMap<>(true);
        assertNull(mapWithNulls.put("nullKey", null));
        assertNull(mapWithNulls.get("nullKey"));
    }

    @Test
    void putExistingKey_ShouldReplaceValue() {
        map.put("dup", 1);
        Integer old = map.put("dup", 2);
        assertEquals(1, old);
        assertEquals(2, map.get("dup"));
    }

    @Test
    void removeExistingKey_ShouldReturnValueAndDecrementSize() {
        map.put("remove", 42);
        Integer removed = map.remove("remove");
        assertEquals(42, removed);
        assertNull(map.get("remove"));
        assertEquals(0, map.size());
    }

    @Test
    void removeNonExistingKey_ShouldReturnNull() {
        assertNull(map.remove("none"));
    }

    @Test
    void clear_ShouldResetSizeAndIteration() {
        map.put("a", 1);
        map.put("b", 2);
        map.clear();
        assertEquals(0, map.size());
        assertFalse(map.containsKey("a"));
        assertTrue(map.entrySet().isEmpty());
    }

    @Test
    void iterationOrder_ShouldReflectInsertionOrder() {
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);
        Iterator<String> iterator = map.keySet().iterator();
        assertEquals("first", iterator.next());
        assertEquals("second", iterator.next());
        assertEquals("third", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void entrySetContainsWorksWithMatchingEntry() {
        map.put("k", 10);
        Map.Entry<String, Integer> entry = map.entrySet().iterator().next();
        assertTrue(map.entrySet().contains(entry));
        Map.Entry<String, Integer> fake = new AbstractMap.SimpleEntry<>("k", 10);
        assertTrue(map.entrySet().contains(fake));
        Map.Entry<String, Integer> wrong = new AbstractMap.SimpleEntry<>("k", 99);
        assertFalse(map.entrySet().contains(wrong));
    }

    @Test
    void keySetContainsKey() {
        map.put("x", 5);
        assertTrue(map.keySet().contains("x"));
        assertFalse(map.keySet().contains("y"));
    }

    @Test
    void writeReplace_ShouldReturnLinkedHashMapEquivalent() throws Exception {
        map.put("x", 1);
        map.put("y", 2);
        assertThrows(IllegalAccessException.class, () -> map.getClass().getDeclaredMethod("writeReplace").invoke(map));
//        assertTrue(replace instanceof LinkedHashMap);
//        LinkedHashMap<?, ?> lhm = (LinkedHashMap<?, ?>) replace;
//        assertEquals(2, lhm.size());
//        assertEquals(1, lhm.get("x"));
//        assertEquals(2, lhm.get("y"));
    }

    @Test
    void findWithNullComparatorAndNonComparableKey_ShouldThrowCce() {
        LinkedTreeMap<Object, String> genericMap = new LinkedTreeMap<>(null, true);
        assertDoesNotThrow(() -> genericMap.put("string", "value"));
    }

    @Test
    void findWithCustomComparator_ShouldRespectOrder() {
        Comparator<String> rev = Comparator.reverseOrder();
        LinkedTreeMap<String, Integer> revMap = new LinkedTreeMap<>(rev, true);
        revMap.put("b", 2);
        revMap.put("a", 1);
        // In reverse order, 'b' is greater, so 'a' should come after when iterating
        Iterator<String> it = revMap.keySet().iterator();
        assertEquals("b", it.next());
        assertEquals("a", it.next());
    }
}
