package com.google.gson;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class JsonArrayTest {

    private JsonArray jsonArray;

    @BeforeEach
    void setUp() {
        jsonArray = new JsonArray();
    }

    @Test
    @DisplayName("Constructor should create an empty array")
    void testConstructorEmpty() {
        assertTrue(jsonArray.isEmpty());
        assertEquals(0, jsonArray.size());
    }

    @Test
    @DisplayName("Constructor with capacity should create an empty array with specified capacity")
    void testConstructorWithCapacity() {
        JsonArray arrayWithCapacity = new JsonArray(10);
        assertTrue(arrayWithCapacity.isEmpty());
        assertEquals(0, arrayWithCapacity.size());
    }

    @Test
    @DisplayName("deepCopy should return a new array with the same elements")
    void testDeepCopy() {
        jsonArray.add(1);
        jsonArray.add("test");
        JsonArray copy = jsonArray.deepCopy();

        assertEquals(jsonArray, copy);
        assertNotSame(jsonArray, copy);

        // Ensure deep copy of elements
        jsonArray.add(new JsonArray());
        JsonArray deepCopy2 = jsonArray.deepCopy();
        assertEquals(jsonArray, deepCopy2);
        assertNotSame(jsonArray.get(2), deepCopy2.get(2));
    }

    @Test
    @DisplayName("deepCopy should return an empty array if source is empty")
    void testDeepCopyEmpty() {
        JsonArray copy = jsonArray.deepCopy();
        assertTrue(copy.isEmpty());
        assertNotSame(jsonArray, copy);
    }

    @Nested
    @DisplayName("Add methods")
    class AddMethods {
        @Test
        @DisplayName("add(Boolean) should add boolean or JsonNull")
        void testAddBoolean() {
            jsonArray.add(true);
            jsonArray.add(false);
            jsonArray.add((Boolean) null);

            assertEquals(3, jsonArray.size());
            assertEquals(new JsonPrimitive(true), jsonArray.get(0));
            assertEquals(new JsonPrimitive(false), jsonArray.get(1));
            assertEquals(JsonNull.INSTANCE, jsonArray.get(2));
        }

        @Test
        @DisplayName("add(Character) should add character or JsonNull")
        void testAddCharacter() {
            jsonArray.add('a');
            jsonArray.add((Character) null);

            assertEquals(2, jsonArray.size());
            assertEquals(new JsonPrimitive('a'), jsonArray.get(0));
            assertEquals(JsonNull.INSTANCE, jsonArray.get(1));
        }

        @Test
        @DisplayName("add(Number) should add number or JsonNull")
        void testAddNumber() {
            jsonArray.add(123);
            jsonArray.add((Number) null);

            assertEquals(2, jsonArray.size());
            assertEquals(new JsonPrimitive(123), jsonArray.get(0));
            assertEquals(JsonNull.INSTANCE, jsonArray.get(1));
        }

        @Test
        @DisplayName("add(String) should add string or JsonNull")
        void testAddString() {
            jsonArray.add("hello");
            jsonArray.add((String) null);

            assertEquals(2, jsonArray.size());
            assertEquals(new JsonPrimitive("hello"), jsonArray.get(0));
            assertEquals(JsonNull.INSTANCE, jsonArray.get(1));
        }

        @Test
        @DisplayName("add(JsonElement) should add JsonElement or JsonNull")
        void testAddJsonElement() {
            jsonArray.add(new JsonPrimitive(1));
            jsonArray.add((Boolean) null);

            assertEquals(2, jsonArray.size());
            assertEquals(new JsonPrimitive(1), jsonArray.get(0));
            assertEquals(JsonNull.INSTANCE, jsonArray.get(1));
        }

        @Test
        @DisplayName("addAll should add all elements from another JsonArray")
        void testAddAll() {
            JsonArray other = new JsonArray();
            other.add(1);
            other.add(2);

            jsonArray.addAll(other);

            assertEquals(2, jsonArray.size());
            assertEquals(1, jsonArray.get(0).getAsInt());
            assertEquals(2, jsonArray.get(1).getAsInt());
        }
    }

    @Nested
    @DisplayName("Remove and Set methods")
    class RemoveSetMethods {
        @Test
        @DisplayName("set should replace element at index")
        void testSet() {
            jsonArray.add(1);
            jsonArray.add(2);
            JsonElement replaced = jsonArray.set(1, new JsonPrimitive("new"));

            assertEquals(new JsonPrimitive(2), replaced);
            assertEquals("new", jsonArray.get(1).getAsString());
        }

        @Test
        @DisplayName("set should replace with JsonNull if element is null")
        void testSetNull() {
            jsonArray.add(1);
            jsonArray.set(0, null);

            assertEquals(JsonNull.INSTANCE, jsonArray.get(0));
        }

        @Test
        @DisplayName("set should throw IndexOutOfBoundsException if index is invalid")
        void testSetOutOfBounds() {
            assertThrows(IndexOutOfBoundsException.class, () -> jsonArray.set(0, new JsonPrimitive(1)));
        }

        @Test
        @DisplayName("remove(JsonElement) should remove first occurrence")
        void testRemoveElement() {
            jsonArray.add(1);
            jsonArray.add(2);
            jsonArray.add(1);

            assertTrue(jsonArray.remove(new JsonPrimitive(1)));
            assertEquals(2, jsonArray.size());
            assertEquals(2, jsonArray.get(0).getAsInt());
            assertEquals(1, jsonArray.get(1).getAsInt());

            assertFalse(jsonArray.remove(new JsonPrimitive(3)));
            assertEquals(2, jsonArray.size());
        }

        @Test
        @DisplayName("remove(int) should remove element at index")
        void testRemoveIndex() {
            jsonArray.add(1);
            jsonArray.add(2);

            assertEquals(new JsonPrimitive(1), jsonArray.remove(0));
            assertEquals(1, jsonArray.size());
            assertEquals(2, jsonArray.get(0).getAsInt());
        }

        @Test
        @DisplayName("remove(int) should throw IndexOutOfBoundsException if index is invalid")
        void testRemoveIndexOutOfBounds() {
            assertThrows(IndexOutOfBoundsException.class, () -> jsonArray.remove(0));
        }
    }

    @Nested
    @DisplayName("Query methods")
    class QueryMethods {
        @Test
        @DisplayName("contains should return true if element is present")
        void testContains() {
            jsonArray.add(1);
            assertTrue(jsonArray.contains(new JsonPrimitive(1)));
            assertFalse(jsonArray.contains(new JsonPrimitive(2)));
        }

        @Test
        @DisplayName("size should return number of elements")
        void testSize() {
            assertEquals(0, jsonArray.size());
            jsonArray.add(1);
            assertEquals(1, jsonArray.size());
        }

        @Test
        @DisplayName("isEmpty should return true if array is empty")
        void testIsEmpty() {
            assertTrue(jsonArray.isEmpty());
            jsonArray.add(1);
            assertFalse(jsonArray.isEmpty());
        }

        @Test
        @DisplayName("iterator should traverse elements")
        void testIterator() {
            jsonArray.add(1);
            jsonArray.add(2);

            int count = 0;
            for (JsonElement element : jsonArray) {
                assertEquals(count + 1, element.getAsInt());
                count++;
            }
            assertEquals(2, count);
        }

        @Test
        @DisplayName("get should return element at index")
        void testGet() {
            jsonArray.add(1);
            assertEquals(new JsonPrimitive(1), jsonArray.get(0));
        }

        @Test
        @DisplayName("get should throw IndexOutOfBoundsException if index is invalid")
        void testGetOutOfBounds() {
            assertThrows(IndexOutOfBoundsException.class, () -> jsonArray.get(0));
        }
    }

    @Nested
    @DisplayName("getAs... methods")
    class GetAsMethods {
        @ParameterizedTest
        @MethodSource("provideGetAsScenarios")
        @DisplayName("getAs... methods should work for single element arrays")
        void testGetAsMethodsSuccess(Function<JsonArray, Object> getter, Object expected) {
            jsonArray.add(new JsonPrimitive(String.valueOf(expected)));
            assertEquals(expected, getter.apply(jsonArray));
        }

        static Stream<Arguments> provideGetAsScenarios() {
            return Stream.of(
                    Arguments.of((Function<JsonArray, Object>) j -> j.getAsInt(), 1),
                    Arguments.of((Function<JsonArray, Object>) j -> j.getAsLong(), 1L),
                    Arguments.of((Function<JsonArray, Object>) j -> j.getAsDouble(), 1.0),
                    Arguments.of((Function<JsonArray, Object>) j -> j.getAsDouble(), 1.0),
                    Arguments.of((Function<JsonArray, Object>) j -> j.getAsBoolean(), true),
                    Arguments.of((Function<JsonArray, Object>) j -> j.getAsString(), "test"),
                    Arguments.of((Function<JsonArray, Object>) j -> j.getAsByte(), (byte) 1),
                    Arguments.of((Function<JsonArray, Object>) j -> j.getAsShort(), (short) 1),
                    Arguments.of((Function<JsonArray, Object>) j -> j.getAsCharacter(), 'a'),
                    Arguments.of((Function<JsonArray, Object>) j -> j.getAsBigDecimal(), new BigDecimal("1.0")),
                    Arguments.of((Function<JsonArray, Object>) j -> j.getAsBigInteger(), BigInteger.ONE)
            );
        }

        @Test
        @DisplayName("getAs... methods should throw IllegalStateException if array does not have exactly one element")
        void testGetAsMethodsFailure() {
            // Case: empty array
            assertThrows(IllegalStateException.class, () -> jsonArray.getAsInt());
            assertThrows(IllegalStateException.class, () -> jsonArray.getAsString());
            assertThrows(IllegalStateException.class, () -> jsonArray.getAsDouble());
            assertThrows(IllegalStateException.class, () -> jsonArray.getAsBigDecimal());
            assertThrows(IllegalStateException.class, () -> jsonArray.getAsBigInteger());
            assertThrows(IllegalStateException.class, () -> jsonArray.getAsFloat());
            assertThrows(IllegalStateException.class, () -> jsonArray.getAsLong());
            assertThrows(IllegalStateException.class, () -> jsonArray.getAsInt());
            assertThrows(IllegalStateException.class, () -> jsonArray.getAsByte());
            assertThrows(IllegalStateException.class, () -> jsonArray.getAsCharacter());
            assertThrows(IllegalStateException.class, () -> jsonArray.getAsShort());
            assertThrows(IllegalStateException.class, () -> jsonArray.getAsBoolean());

            // Case: more than one element
            jsonArray.add(1);
            jsonArray.add(2);
            assertThrows(IllegalStateException.class, () -> jsonArray.getAsInt());
            assertThrows(IllegalStateException.class, () -> jsonArray.getAsString());
            assertThrows(IllegalStateException.class, () -> jsonArray.getAsDouble());
            assertThrows(IllegalStateException.class, () -> jsonArray.getAsBigDecimal());
            assertThrows(IllegalStateException.class, () -> jsonArray.getAsBigInteger());
            assertThrows(IllegalStateException.class, () -> jsonArray.getAsFloat());
            assertThrows(IllegalStateException.class, () -> jsonArray.getAsLong());
            assertThrows(IllegalStateException.class, () -> jsonArray.getAsInt());
            assertThrows(IllegalStateException.class, () -> jsonArray.getAsByte());
            assertThrows(IllegalStateException.class, () -> jsonArray.getAsCharacter());
            assertThrows(IllegalStateException.class, () -> jsonArray.getAsShort());
            assertThrows(IllegalStateException.class, () -> jsonArray.getAsBoolean());
        }
    }

    @Nested
    @DisplayName("Other methods")
    class OtherMethods {
        @Test
        @DisplayName("asList should return a mutable list view")
        void testAsList() {
            jsonArray.add(1);
            List<JsonElement> list = jsonArray.asList();
            assertEquals(1, list.size());
            assertEquals(new JsonPrimitive(1), list.get(0));

            list.add(new JsonPrimitive(2));
            assertEquals(2, jsonArray.size());
            assertEquals(new JsonPrimitive(2), jsonArray.get(1));

            jsonArray.add(new JsonPrimitive(3));
            assertEquals(3, list.size());
            assertEquals(new JsonPrimitive(3), list.get(2));
        }

        @Test
        @DisplayName("equals should work correctly")
        void testEquals() {
            JsonArray array1 = new JsonArray();
            array1.add(1);
            array1.add("two");

            JsonArray array2 = new JsonArray();
            array2.add(1);
            array2.add("two");

            JsonArray array3 = new JsonArray();
            array3.add(1);
            array3.add("three");

            JsonArray array4 = new JsonArray();
            array4.add(1);

            assertEquals(array1, array1); // same instance
            assertEquals(array1, array2); // same content
            assertNotEquals(array1, array3); // different content
            assertNotEquals(array1, array4); // different size
            assertNotEquals(array1, "not a JsonArray"); // different type
            assertNotEquals(array1, null); // null
        }

        @Test
        @DisplayName("hashCode should be consistent with equals")
        void testHashCode() {
            JsonArray array1 = new JsonArray();
            array1.add(1);
            array1.add("two");

            JsonArray array2 = new JsonArray();
            array2.add(1);
            array2.add("two");

            assertEquals(array1.hashCode(), array2.hashCode());
        }
    }
}