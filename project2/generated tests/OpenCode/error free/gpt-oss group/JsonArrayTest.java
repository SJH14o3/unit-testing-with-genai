package com.google.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonArray unit tests")
class JsonArrayTest {

    @Test
    void addNullsConvertToJsonNull() {
        JsonArray array = new JsonArray();
        array.add((Boolean) null);
        array.add((Character) null);
        array.add((Number) null);
        array.add((String) null);
        array.add((JsonArray) null);
        array.add((JsonElement) null);
        assertEquals(6, array.size());
        for (int i = 0; i < 6; i++) {
            assertTrue(array.get(i) instanceof JsonNull);
        }
    }

    @Test
    void deepCopyPreservesStructure() {
        JsonArray original = new JsonArray();
        original.add(1);
        original.add("hello");
        JsonArray copy = original.deepCopy();
        assertEquals(original, copy);
        assertNotSame(original, copy);
        copy.add(2);
        assertNotEquals(original, copy);
    }

    @Test
    void addAllCopiesElements() {
        JsonArray a = new JsonArray();
        a.add(1);
        a.add(2);
        JsonArray b = new JsonArray();
        b.addAll(a);
        assertEquals(a.size(), b.size());
        assertEquals(a.get(0).getAsNumber(), b.get(0).getAsNumber());
    }

    @Test
    void setReplacesAndHandlesNull() {
        JsonArray arr = new JsonArray();
        arr.add(1);
        JsonElement prev = arr.set(0, null);
        assertTrue(prev instanceof JsonNull);
        assertTrue(arr.get(0) instanceof JsonNull);
        assertThrows(IndexOutOfBoundsException.class, () -> arr.set(1, new JsonPrimitive(2)));
    }

    @Test
    void removeElementAndIndex() {
        JsonArray arr = new JsonArray();
        JsonPrimitive p = new JsonPrimitive(42);
        arr.add(p);
        assertTrue(arr.remove(p));
        assertFalse(arr.remove(p));
        arr.add(10);
        JsonElement removed = arr.remove(0);
        assertEquals(10, removed.getAsInt());
        assertThrows(IndexOutOfBoundsException.class, () -> arr.remove(5));
    }

    @Test
    void getAsSingleElementExceptions() {
        JsonArray empty = new JsonArray();
        JsonArray multi = new JsonArray();
        multi.add(1);
        multi.add(2);
        assertThrows(IllegalStateException.class, () -> empty.getAsNumber());
        assertThrows(IllegalStateException.class, () -> multi.getAsNumber());
    }
}
