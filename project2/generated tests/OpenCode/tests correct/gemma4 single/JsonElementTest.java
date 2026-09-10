package com.google.gson;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.math.BigInteger;

@DisplayName("JsonElement Unit Tests")
class JsonElementTest {

    @Nested
    @DisplayName("Type Checks")
    class TypeChecks {
        @Test
        @DisplayName("isJsonArray")
        void testIsJsonArray() {
            assertTrue(new JsonArray().isJsonArray());
            assertFalse(new JsonObject().isJsonArray());
            assertFalse(new JsonPrimitive("test").isJsonArray());
            assertFalse(JsonNull.INSTANCE.isJsonArray());
        }

        @Test
        @DisplayName("isJsonObject")
        void testIsJsonObject() {
            assertTrue(new JsonObject().isJsonObject());
            assertFalse(new JsonArray().isJsonObject());
            assertFalse(new JsonPrimitive("test").isJsonObject());
            assertFalse(JsonNull.INSTANCE.isJsonObject());
        }

        @Test
        @DisplayName("isJsonPrimitive")
        void testIsJsonPrimitive() {
            assertTrue(new JsonPrimitive("test").isJsonPrimitive());
            assertFalse(new JsonArray().isJsonPrimitive());
            assertFalse(new JsonObject().isJsonPrimitive());
            assertFalse(JsonNull.INSTANCE.isJsonPrimitive());
        }

        @Test
        @DisplayName("isJsonNull")
        void testIsJsonNull() {
            assertTrue(JsonNull.INSTANCE.isJsonNull());
            assertFalse(new JsonArray().isJsonNull());
            assertFalse(new JsonObject().isJsonNull());
            assertFalse(new JsonPrimitive("test").isJsonNull());
        }
    }

    @Nested
    @DisplayName("getAs... Methods - Success Cases")
    class GetAsSuccess {
        @Test
        @DisplayName("getAsJsonObject")
        void testGetAsJsonObject() {
            JsonObject obj = new JsonObject();
            assertEquals(obj, obj.getAsJsonObject());
        }

        @Test
        @DisplayName("getAsJsonArray")
        void testGetAsJsonArray() {
            JsonArray array = new JsonArray();
            assertEquals(array, array.getAsJsonArray());
        }

        @Test
        @DisplayName("getAsJsonPrimitive")
        void testGetAsJsonPrimitive() {
            JsonPrimitive primitive = new JsonPrimitive("test");
            assertEquals(primitive, primitive.getAsJsonPrimitive());
        }

        @Test
        @DisplayName("getAsJsonNull")
        void testGetAsJsonNull() {
            assertEquals(JsonNull.INSTANCE, JsonNull.INSTANCE.getAsJsonNull());
        }
    }

    @Nested
    @DisplayName("getAs... Methods - Failure Cases")
    class GetAsFailure {
        @Test
        @DisplayName("getAsJsonObject throws exception")
        void testGetAsJsonObjectThrows() {
            JsonArray array = new JsonArray();
            assertThrows(IllegalStateException.class, array::getAsJsonObject);
        }

        @Test
        @DisplayName("getAsJsonArray throws exception")
        void testGetAsJsonArrayThrows() {
            JsonObject obj = new JsonObject();
            assertThrows(IllegalStateException.class, obj::getAsJsonArray);
        }

        @Test
        @DisplayName("getAsJsonPrimitive throws exception")
        void testGetAsJsonPrimitiveThrows() {
            JsonNull nullElement = JsonNull.INSTANCE;
            assertThrows(IllegalStateException.class, nullElement::getAsJsonPrimitive);
        }

        @Test
        @DisplayName("getAsJsonNull throws exception")
        void testGetAsJsonNullThrows() {
            JsonPrimitive primitive = new JsonPrimitive("test");
            assertThrows(IllegalStateException.class, primitive::getAsJsonNull);
        }
    }

    @Nested
    @DisplayName("Primitive value conversion - Support types")
    class PrimitiveConversions {
        @Test
        @DisplayName("getAsBoolean")
        void testGetAsBoolean() {
            assertTrue(new JsonPrimitive(true).getAsBoolean());
            assertFalse(new JsonPrimitive(false).getAsBoolean());
            assertFalse(new JsonPrimitive("not a boolean").getAsBoolean());
            assertTrue(new JsonPrimitive("true").getAsBoolean());
        }

        @Test
        @DisplayName("getAsNumber")
        void testGetAsNumber() {
            assertEquals(10, new JsonPrimitive(10).getAsNumber().intValue());
            assertEquals(10.5, new JsonPrimitive(10.5).getAsNumber().doubleValue());
        }

        @Test
        @DisplayName("getAsString")
        void testGetAsString() {
            assertEquals("test", new JsonPrimitive("test").getAsString());
            assertEquals("10", new JsonPrimitive(10).getAsString());
            assertEquals("true", new JsonPrimitive(true).getAsString());
        }

        @Test
        @DisplayName("getAsDouble")
        void testGetAsDouble() {
            assertEquals(10.5, new JsonPrimitive(10.5).getAsDouble());
            assertEquals(10.5, new JsonPrimitive("10.5").getAsDouble());
        }

        @Test
        @DisplayName("getAsInt")
        void testGetAsInt() {
            assertEquals(10, new JsonPrimitive(10).getAsInt());
            assertEquals(10, new JsonPrimitive("10").getAsInt());
        }

        @Test
        @DisplayName("getAsLong")
        void testGetAsLong() {
            assertEquals(10L, new JsonPrimitive(10L).getAsLong());
            assertEquals(10L, new JsonPrimitive("10").getAsLong());
        }

        @Test
        @DisplayName("getAsFloat")
        void testGetAsFloat() {
            assertEquals(10.5f, new JsonPrimitive(10.5f).getAsFloat());
        }

        @Test
        @DisplayName("getAsByte")
        void testGetAsByte() {
            assertEquals((byte) 10, new JsonPrimitive(10).getAsByte());
        }

        @Test
        @DisplayName("getAsShort")
        void testGetAsShort() {
            assertEquals((short) 10, new JsonPrimitive(10).getAsShort());
        }
    }
}
