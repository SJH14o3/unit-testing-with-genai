package com.google.gson.internal;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class NonNullElementWrapperListTest {

    @Test
    @DisplayName("constructor requires non‑null delegate")
    void constructor_nullDelegate_throws() {
        assertThrows(NullPointerException.class, () -> new NonNullElementWrapperList<>(null));
    }

    @Nested
    @DisplayName("basic read operations")
    class ReadOps {

        private final NonNullElementWrapperList<String> list =
            new NonNullElementWrapperList<>(new ArrayList<>(Arrays.asList("a", "b", "c")));

        @Test
        void get_returnsValue() {
            assertEquals("b", list.get(1));
        }

        @Test
        void size_returnsCount() {
            assertEquals(3, list.size());
        }

        @Test
        void indexOf_and_lastIndexOf_work() {
            // duplicate element
            list.set(2, "a");
            assertEquals(0, list.indexOf("a"));
            assertEquals(2, list.lastIndexOf("a"));
        }

        @Test
        void contains_and_toArray() {
            assertTrue(list.contains("c"));
            assertArrayEquals(new Object[]{"a", "b", "c"}, list.toArray());
        }

        @Test
        void toArrayWithType() {
            //noinspection RedundantArrayCreation
            String[] array = list.toArray(new String[0]);
            assertArrayEquals(new String[]{"a", "b", "c"}, array);
        }
    }

    @Nested
    @DisplayName("write and mutation operations")
    class WriteOps {

        private NonNullElementWrapperList<Integer> list =
            new NonNullElementWrapperList<>(new ArrayList<>(Collections.singletonList(1)));

        @Test
        void set_acceptsNonNull() {
            Integer old = list.set(0, 10);
            assertEquals(1, old);
            assertEquals(10, list.get(0));
        }

        @Test
        void set_throwsOnNull() {
            assertThrows(NullPointerException.class, () -> list.set(0, null));
        }

        @Test
        void add_atIndex_acceptsNonNull() {
            list.add(1, 5);
            assertEquals(2, list.size());
            assertEquals(5, list.get(1));
        }

        @Test
        void add_atIndex_throwsOnNull() {
            assertThrows(NullPointerException.class, () -> list.add(0, null));
        }

        @Test
        void remove_byIndex_returnsElement() {
            assertEquals(1, list.remove(0));
            assertEquals(0, list.size());
        }

        @Test
        void remove_byObject_returnsTrueIfPresent() {
            assertTrue(list.remove((Object) 1));
            assertFalse(list.remove((Object) 1));
        }

        @Test
        void removeAll_and_retainAll_work() {
            list.add(0, 2);
            list.add(0, 3); // list: 3,2,1
            assertTrue(list.removeAll(Arrays.asList(2, 4)));
            assertEquals(2, list.size());
            assertTrue(list.retainAll(Collections.singletonList(3)));
            assertEquals(1, list.size());
            assertEquals(3, list.get(0));
        }

        @Test
        void clear_removesAllElements() {
            list.add(0, 2);
            list.clear();
            assertEquals(0, list.size());
        }
    }

    @Nested
    @DisplayName("utility methods")
    class UtilityOps {

        private final NonNullElementWrapperList<String> list =
            new NonNullElementWrapperList<>(new ArrayList<>(List.of("x", "y")));

        @Test
        void equals_and_hashCode_delegateToInternalList() {
            ArrayList<String> backing = new ArrayList<>(List.of("x", "y"));
            assertEquals(backing, list);
            assertEquals(backing.hashCode(), list.hashCode());
        }

        @Test
        void toString_matchesBackingToString() {
            assertEquals("[" + String.join(", ", "x", "y") + "]", list.toString());
        }
    }
}