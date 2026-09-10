package com.google.gson.internal.bind;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonTreeWriter Test")
class JsonTreeWriterTest {

    private JsonTreeWriter writer;

    @BeforeEach
    void setUp() {
        writer = new JsonTreeWriter();
    }

    @Nested
    @DisplayName("Basic Writing")
    class BasicWriting {

        @Test
        @DisplayName("Write primitive string")
        void writeString() throws IOException {
            writer.value("test");
            assertEquals(new JsonPrimitive("test"), writer.get());
        }

        @Test
        @DisplayName("Write primitive boolean")
        void writeBoolean() throws IOException {
            writer.value(true);
            assertEquals(new JsonPrimitive(true), writer.get());
        }

        @Test
        @DisplayName("Write primitive long")
        void writeLong() throws IOException {
            writer.value(123L);
            assertEquals(new JsonPrimitive(123L), writer.get());
        }

        @Test
        @DisplayName("Write null value")
        void writeNull() throws IOException {
            writer.nullValue();
            assertEquals(JsonNull.INSTANCE, writer.get());
        }

        @Test
        @DisplayName("Write number (double/float)")
        void writeNumbers() throws IOException {
            writer.value(1.5);
            assertEquals(new JsonPrimitive(1.5), writer.get());

            writer = new JsonTreeWriter();
            writer.value(2.5f);
            assertEquals(new JsonPrimitive(2.5f), writer.get());
        }
    }

    @Nested
    @DisplayName("Object Writing")
    class ObjectWriting {

        @Test
        @DisplayName("Write simple object")
        void writeObject() throws IOException {
            writer.beginObject();
            writer.name("key");
            writer.value("value");
            writer.endObject();

            JsonObject obj = writer.get().getAsJsonObject();
            assertTrue(obj.has("key"));
            assertEquals("value", obj.get("key").getAsString());
        }

        @Test
        @DisplayName("Write object with null value")
        void writeObjectWithNull() throws IOException {
            writer.beginObject();
            writer.name("key");
            writer.nullValue();
            writer.endObject();

            JsonObject obj = writer.get().getAsJsonObject();
            assertTrue(obj.has("key"));
            assertTrue(obj.get("key").isJsonNull());
        }

        @Test
        @DisplayName("Throws exception when name is null")
        void nameNullThrowsException() {
            assertThrows(NullPointerException.class, () -> writer.name(null));
        }

        @Test
        @DisplayName("Throws exception when name used without object")
        void nameWithoutObjectThrowsException() {
            assertThrows(IllegalStateException.class, () -> writer.name("key"));
        }

        @Test
        @DisplayName("Throws exception when endObject called without error")
        void endObjectMismatch() throws IOException {
            writer.beginObject();
            // No endObject
            assertThrows(IOException.class, () -> writer.close());
        }
    }

    @Nested
    @DisplayName("Array Writing")
    class ArrayWriting {

        @Test
        @DisplayName("Write simple array")
        void writeArray() throws IOException {
            writer.beginArray();
            writer.value(1);
            writer.value(2);
            writer.endArray();

            JsonArray array = writer.get().getAsJsonArray();
            assertEquals(2, array.size());
            assertEquals(1, array.get(0).getAsInt());
            assertEquals(2, array.get(1).getAsInt());
        }

        @Test
        @DisplayName("Nested array and object")
        void writeNested() throws IOException {
            writer.beginArray();
            writer.beginObject();
            writer.name("inner");
            writer.beginArray();
            writer.value("item");
            writer.endArray();
            writer.endObject();
            writer.endArray();

            JsonArray array = writer.get().getAsJsonArray();
            JsonObject obj = array.get(0).getAsJsonObject();
            JsonArray innerArray = obj.getAsJsonArray("inner");
            assertEquals("item", innerArray.get(0).getAsString());
        }

        @Test
        @DisplayName("Throws exception when endArray is invalid")
        void endArrayInvalid() throws IOException {
            writer.beginObject();
            assertThrows(IllegalStateException.class, () -> writer.endArray());
        }
    }

    @Nested
    @DisplayName("Edge Cases & Exceptions")
    class EdgeCases {

        @Test
        @DisplayName("Throws exception when get() called with stack not empty")
        void getWithStackNotEmpty() throws IOException {
            writer.beginArray();
            assertThrows(IllegalStateException.class, () -> writer.get());
        }

        @Test
        @DisplayName("Verify NaN and Infinity handling (non-lenient)")
        void handleNaNAndInfinity() {
            // Default is not lenient
            assertThrows(IllegalArgumentException.class, () -> writer.value(Double.NaN));
            assertThrows(IllegalArgumentException.class, () -> writer.value(Double.POSITIVE_INFINITY));
        }

        @Test
        @DisplayName("Throws exception on jsonValue call")
        void jsonValueUnsupported() {
            assertThrows(UnsupportedOperationException.class, () -> writer.jsonValue("test"));
        }

        @Test
        @DisplayName("Verify closing behavior")
        void closeBehavior() throws IOException {
            writer.beginObject();
            assertThrows(IOException.class, () -> writer.close()); // Incomplete document

            writer.endObject();
            assertNotNull(writer.get());
            writer.close();
        }

        @ParameterizedTest
        @MethodSource("provideNumbersForNullCheck")
        @DisplayName("Handle nulls in value overrides")
        void valueNulls(Object val, boolean isNullExpected) throws IOException {
            // Testing various types of null-like inputs if applicable via the overloads
            // Note: The implementation of value(String) returns nullValue() if input is null
            if (val == null) {
                writer.value((String)null);
                assertTrue(writer.get().isJsonNull());
            }
        }

        private static Stream<Arguments> provideNumbersForNullCheck() {
            return Stream.of(Arguments.of(null, true));
        }
    }
}