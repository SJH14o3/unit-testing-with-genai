package com.google.gson.internal.bind;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.ConstructorConstructor;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Exhausts MapTypeAdapterFactory paths:
 * - primitive key serialization
 * - complex key serialization
 * - duplicate-key exception
 */
class MapTypeAdapterFactoryTest {

    private final Gson gson = new Gson();

    @BeforeEach


    @Test
    void simpleKeyMapSerializesToObject() {
        Map<Integer, String> map = new LinkedHashMap<>();
        map.put(1, "one");
        map.put(2, "two");
        JsonElement elem = gson.toJsonTree(map);
        assertTrue(elem.isJsonObject());
        assertEquals("one", elem.getAsJsonObject().get("1").getAsString());
    }

    @Test
    void complexKeyMapSerializesToArray() {
        Map<Point, String> map = new LinkedHashMap<>();
        map.put(new Point(5, 6), "a");
        map.put(new Point(8, 8), "b");
        JsonElement elem = gson.toJsonTree(map);
        assertTrue(elem.isJsonArray()); // array of entries
        assertEquals(2, elem.getAsJsonArray().size());
    }

    @Test
    void duplicateKeysThrowException() {
        Map<Object, String> map = new LinkedHashMap<>();
        map.put(new Point(1, 2), "x");
        map.put(new Point(1, 2), "y"); // same key
        assertThrows(JsonSyntaxException.class, () ->
                gson.toJsonTree(map));
    }

    /** Simple point class used for map-key tests. */
    private static class Point {
        final int x, y;
        Point(int x, int y) { this.x = x; this.y = y; }
        @Override public boolean equals(Object o) {
            return o instanceof Point p && p.x == x && p.y == y;
        }
        @Override public int hashCode() {
            return 31 * x + y;
        }
    }
}