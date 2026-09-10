package com.google.gson;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

@DisplayName("JsonArray Tests")
class JsonArrayTest {

    private JsonArray jsonArray;

    @BeforeEach
    void setUp() {
        jsonArray = new JsonArray();
    }

    @Test
    @DisplayName("Constructor with default capacity creates empty array")
    void constructor_empty() {
        assertTrue(jsonArray.isEmpty());
        assertEquals(0, jsonArray.size());
    }

    @Test
    @DisplayName("Constructor with capacity creates array with correct capacity")
    void constructor_withCapacity() {
        JsonArray arrayWithCapacity = new JsonArray(10);
        assertTrue(arrayWithCapacity.isEmpty());
        assertEquals(0, arrayWithCapacity.size());
    }

    @Test
    @DisplayName("deepCopy creates a deep copy of the array")
    void deepCopy_correctlyCopies() {
        jsonArray.add("element1");
        JsonObject obj = new JsonObject();
        obj.addProperty("key", "value");
        jsonArray.add(obj);

        JsonArray copy = jsonArray.deepCopy();

        assertEquals(jsonArray, copy);
        assertNotSame(jsonArray, copy);

        // Verify deep copy of elements
        JsonElement copyElement = copy.get(1);
        assertTrue(copyElement.isJsonObject());
        assertEquals("value", copyElement.getAsJsonObject().get("key").getAsString());

        // Modify original, copy should remain unchanged
        jsonArray.add("element2");
        assertEquals(2, copy.size());
    }

    @Test
    @DisplayName("deepCopy of empty array returns empty array")
    void deepCompley_emptyArray() {
        JsonArray copy = jsonArray.deepCopy();
        assertTrue(copy.isEmpty());
        assertEquals(jsonArray, copy);
    }

    @Test
    @DisplayName("add methods work correctly with various types")
    void add_variousTypes() {
        jsonArray.add(true);
        jsonArray.add('a');
        jsonArray.add(123);
        jsonArray.add("string");
        jsonArray.add((JsonElement) JsonNull.INSTANCE);

        assertEquals(5, jsonArray.size());
        assertTrue(jsonArray.get(0).getAsBoolean());
        assertEquals('a', jsonArray.get(1).getAsString().charAt(0));
        assertEquals(123, jsonArray.get(2).getAsInt());
        assertEquals("string", jsonArray.get(3).getAsString());
        assertTrue(jsonArray.get(4).isJsonNull());
    }

    @Test
    @DisplayName("add(JsonElement) handles null by converting to JsonNull")
    void addElement_handlesNull() {
        jsonArray.add((String) null);
        assertTrue(jsonArray.get(0).isJsonNull());
    }

    @Test
    @DisplayName("addAll adds all elements from another JsonArray")
    void addAll_copiesElements() {
        JsonArray other = new JsonArray();
        other.add("a");
        other.add("b");
        jsonArray.addAll(other);
        assertEquals(2, jsonArray.size());
        assertEquals("a", jsonArray.get(0).getAsString());
        assertEquals("b", jsonArray.get(1).getAsString());
    }

    @Test
    @DisplayName("set replaces element and returns previous element")
    void set_replacesElement() {
        jsonArray.add("old");
        JsonElement replaced = jsonArray.set(0, new JsonPrimitive("new"));
        assertEquals("old", replaced.getAsString());
        assertEquals("new", jsonArray.get(0).getAsString());
    }

    @Test
    @DisplayName("remove(JsonElement) removes first occurrence")
    void remove_element() {
        jsonArray.add("a");
        jsonArray.add("b");
        boolean removed = jsonArray.remove(new JsonPrimitive("a"));
        assertTrue(removed);
        assertEquals(1, jsonArray.size());
        assertEquals("b", jsonArray.get(0).getAsString());
    }

    @Test
    @DisplayName("remove(int) removes element at index")
    void remove_index() {
        jsonArray.add("a");
        jsonArray.add("b");
        JsonElement removed = jsonArray.remove(0);
        assertEquals("a", removed.getAsString());
        assertEquals(1, jsonArray.size());
        assertEquals("b", jsonArray.get(0).getAsString());
    }

    @Test
    @DisplayName("contains checks for element existence")
    void contains_checksExistence() {
        jsonArray.add("a");
        assertTrue(jsonArray.contains(new JsonPrimitive("a")));
        assertFalse(jsonArray.contains(new JsonPrimitive("b")));
    }

    @Test
    @DisplayName("iterator traverses elements in order")
    void iterator_traversesInOrder() {
        jsonArray.add("a");
        jsonArray.add("b");
        Iterator<JsonElement> it = jsonArray.iterator();
        assertTrue(it.hasNext());
        assertEquals("a", it.next().getAsString());
        assertTrue(it.hasNext());
        assertEquals("b", it.next().getAsString());
        assertFalse(it.hasNext());
    }

    @Test
    @DisplayName("asList returns a mutable list view")
    void asList_mutableView() {
        jsonArray.add("a");
        List<JsonElement> list = jsonArray.asList();
        list.add(new JsonPrimitive("b"));
        assertEquals(2, jsonArray.size());
        assertEquals("b", jsonArray.get(1).getAsString());
    }

    @Test
    @DisplayName("equals and hashCode work correctly")
    void equalsAndHashCode() {
        JsonArray array1 = new JsonArray();
        array1.add("a");
        JsonArray array2 = new JsonArray();
        array2.add("a");
        JsonArray array3 = new JsonArray();
        array3.add("b");

        assertEquals(array1, array2);
        assertEquals(array1.hashCode(), array2.hashCode());
        assertNotEquals(array1, array3);
    }
}
