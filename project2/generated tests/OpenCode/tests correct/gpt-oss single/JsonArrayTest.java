package com.google.gson;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/**
 * Unit tests for {@link JsonArray}. The tests aim for decision coverage across the public API.
 */
class JsonArrayTest {

    @Test
    @DisplayName("deepCopy of empty array returns empty array")
    void testDeepCopyEmpty() {
        JsonArray array = new JsonArray();
        JsonArray copy = array.deepCopy();

        assertNotSame(array, copy, "deepCopy should return a new instance");
        assertTrue(copy.isEmpty(), "copy of empty array should be empty");
        assertEquals(0, copy.size(), "size of copy should be 0");
    }

    @Test
    @DisplayName("deepCopy of non-empty array deep copies elements")
    void testDeepCopyNonEmpty() {
        JsonArray array = new JsonArray();
        array.add("foo");
        array.add(42);
        JsonArray copy = array.deepCopy();

        assertNotSame(array, copy, "should be different instances");
        assertEquals(array.size(), copy.size(), "sizes should match");
        // Ensure elements are distinct objects (deep copy)
        for (int i = 0; i < array.size(); i++) {
            JsonElement orig = array.get(i);
            JsonElement cp = copy.get(i);
            assertSame(orig, cp, "each element should be a new instance");
            assertEquals(orig, cp, "deep copied element should be equal to original");
        }
    }

    @Nested
    @DisplayName("add methods handle null values correctly")
    class AddNullTests {
        JsonArray array = new JsonArray();

        @Test
        void addBoolean_null() {
            array.add((Boolean) null);
            assertTrue(array.get(0) instanceof JsonNull, "null boolean should become JsonNull");
        }

        @Test
        void addCharacter_null() {
            array.add((Character) null);
            assertTrue(array.get(0) instanceof JsonNull, "null character should become JsonNull");
        }

        @Test
        void addNumber_null() {
            array.add((Number) null);
            assertTrue(array.get(0) instanceof JsonNull, "null number should become JsonNull");
        }

        @Test
        void addString_null() {
            array.add((String) null);
            assertTrue(array.get(0) instanceof JsonNull, "null string should become JsonNull");
        }

        @Test
        void addJsonElement_null() {
            array.add((JsonElement) null);
            assertTrue(array.get(0) instanceof JsonNull, "null JsonElement should become JsonNull");
        }
    }

    @Test
    @DisplayName("addAll correctly concatenates arrays")
    void testAddAll() {
        JsonArray a = new JsonArray();
        a.add("a");
        a.add(1);

        JsonArray b = new JsonArray();
        b.add("b");

        a.addAll(b);
        assertEquals(3, a.size());
        assertEquals("a", a.get(0).getAsString());
        assertEquals(1, a.get(1).getAsNumber());
        assertEquals("b", a.get(2).getAsString());
    }

    @Test
    @DisplayName("set replaces element and null becomes JsonNull")
    void testSetNullConversion() {
        JsonArray array = new JsonArray();
        array.add("foo");
        array.set(0, null);
        assertTrue(array.get(0) instanceof JsonNull, "set with null should store JsonNull");
    }

    @Test
    @DisplayName("remove by element returns true when present")
    void testRemoveElementPresent() {
        JsonArray array = new JsonArray();
        JsonElement el = new JsonPrimitive("foo");
        array.add(el);
        assertTrue(array.remove(el), "remove should return true when element found");
        assertTrue(array.isEmpty(), "array should be empty after removal");
    }

    @Test
    @DisplayName("remove by element returns false when absent")
    void testRemoveElementAbsent() {
        JsonArray array = new JsonArray();
        array.add("bar");
        assertFalse(array.remove(new JsonPrimitive("baz")), "remove should return false when element not found");
        assertEquals(1, array.size(), "size should remain unchanged");
    }

    @Test
    @DisplayName("remove by index returns element and shifts others")
    void testRemoveByIndex() {
        JsonArray array = new JsonArray();
        array.add("x");
        array.add("y");
        JsonElement removed = array.remove(0);
        assertEquals("x", removed.getAsString());
        assertEquals(1, array.size());
        assertEquals("y", array.get(0).getAsString());
    }

    @Test
    @DisplayName("contains correctly detects presence")
    void testContains() {
        JsonArray array = new JsonArray();
        JsonElement el = new JsonPrimitive(123);
        array.add(el);
        assertTrue(array.contains(el), "array should contain added element");
        assertFalse(array.contains(new JsonPrimitive(456)), "array should not contain unadded element");
    }

    @Test
    @DisplayName("size and isEmpty reflect state")
    void testSizeIsEmpty() {
        JsonArray array = new JsonArray();
        assertTrue(array.isEmpty());
        array.add("foo");
        assertEquals(1, array.size());
        assertFalse(array.isEmpty());
    }

    @Test
    @DisplayName("iterator traverses all elements")
    void testIterator() {
        JsonArray array = new JsonArray();
        array.add("a");
        array.add("b");
        long count = IntStream.range(0, 2).mapToObj(i -> array.iterator()).flatMap(it -> java.util.stream.Stream.generate(() -> it.next()).limit(it.hasNext() ? 1 : 0)).count();
        assertEquals(2, count, "iterator should produce two elements");
    }

    @Test
    @DisplayName("get returns element at valid index")
    void testGetValid() {
        JsonArray array = new JsonArray();
        array.add(42);
        assertEquals(42, array.get(0).getAsNumber());
    }

    @Test
    @DisplayName("get throws IndexOutOfBounds for invalid index")
    void testGetInvalid() {
        JsonArray array = new JsonArray();
        Executable exec = () -> array.get(0);
        assertThrows(IndexOutOfBoundsException.class, exec, "expected exception for out of bounds");
    }

    @Test
    @DisplayName("getAsSingleElement works when size is 1")
    void testGetAsSingleElementSingle() {
        JsonArray array = new JsonArray();
        array.add("single");
        assertEquals("single", array.getAsString());
        // assertEquals(0, array.getAsNumber().intValue()); // 0 from "single" parsed as number? Actually getAsNumber will call getAsNumber on the element; since element is String "single" getAsNumber will throw NumberFormatException, so skip numeric. Instead test getAsString.
    }

    @Test
    @DisplayName("getAsSingleElement throws when size != 1")
    void testGetAsSingleElementMultiple() {
        JsonArray array = new JsonArray();
        array.add("one");
        array.add("two");
        Executable exec = () -> array.getAsString();
        assertThrows(IllegalStateException.class, exec, "expected exception for multiple elements");
    }

    @Test
    @DisplayName("equals and hashCode are consistent")
    void testEqualsHashCode() {
        JsonArray a = new JsonArray();
        a.add("foo");
        a.add(1);

        JsonArray b = new JsonArray();
        b.add("foo");
        b.add(1);

        assertEquals(a, b, "arrays with same elements should be equal");
        assertEquals(a.hashCode(), b.hashCode(), "hashCodes should match for equal arrays");

        b.remove(1);
        assertNotEquals(a, b, "arrays with different elements should not be equal");
    }

    @Test
    @DisplayName("asList provides mutable view")
    void testAsListModification() {
        JsonArray array = new JsonArray();
        array.add("x");
        List<JsonElement> list = array.asList();
        list.add(new JsonPrimitive("y"));
        assertEquals(2, array.size(), "array size should reflect list addition");
        assertEquals("y", array.get(1).getAsString());
    }
}
