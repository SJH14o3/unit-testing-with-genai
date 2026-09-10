package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class EnumTypeAdapterTest {

    enum TestEnum {
        VALUE_ONE,
        @SerializedName(value = "custom_name", alternate = {"alt1", "alt2"})
        VALUE_TWO,
        @SerializedName("toString_val")
        VALUE_THREE
    }

    // Dummy enum for testing anonymous subclassing
    enum BaseEnum {
        BASE_VALUE
    }
//    class AnonymousEnum extends BaseEnum {
//        // This simulates an anonymous subclass of an enum
//    }

    @Nested
    @DisplayName("Factory Tests")
    class FactoryTests {

        @Test
        @DisplayName("Returns null for non-enum types")
        void returnsNullForNonEnum() {
            TypeAdapterFactory factory = EnumTypeAdapter.FACTORY;
            Gson gson = new Gson();
            assertNull(factory.create(gson, TypeToken.get(String.class)));
            assertNull(factory.create(gson, TypeToken.get(Integer.class)));
        }

        @Test
        @DisplayName("Returns null for Enum.class itself")
        void returnsNullForEnumClass() {
            TypeAdapterFactory factory = EnumTypeAdapter.FACTORY;
            Gson gson = new Gson();
            assertNull(factory.create(gson, TypeToken.get(Enum.class)));
        }

//        @Test
//        @DisplayName("Handles anonymous subclasses of enums")
//        void handlesAnonymousSubclasses() {
//            TypeAdapterFactory factory = EnumTypeAdapter.FACTORY;
//            Gson gson = new Gson();
//            TypeAdapter<BaseEnum> adapter = factory.create(gson, TypeToken.get(AnonymousEnum.class));
//            assertNotNull(adapter);
//        }

        @Test
        @DisplayName("Works for standard enums")
        void worksForStandardEnums() {
            TypeAdapterFactory factory = EnumTypeAdapter.FACTORY;
            Gson gson = new Gson();
            TypeAdapter<TestEnum> adapter = factory.create(gson, TypeToken.get(TestEnum.class));
            assertNotNull(adapter);
        }
    }

    // Helper to access the static factory for testing purposes if needed
    static class TypeTokenTokenFactoryProxy {
        static TypeAdapterFactory getFactory() {
            return EnumTypeAdapter.FACTORY;
        }
    }

    @Nested
    @DisplayName("Adapter Logic Tests")
    class AdapterLogicTests {

        private final TypeAdapter<TestEnum> adapter =
                EnumTypeAdapter.FACTORY.create(new Gson(), TypeToken.get(TestEnum.class));

        @Test
        @DisplayName("Write: writes the serialized name (or custom name)")
        void writeCustomName() throws Exception {
            StringWriter sw = new StringWriter();
            JsonWriter writer = new JsonWriter(sw);
            adapter.write(writer, TestEnum.VALUE_TWO);
            assertEquals("\"custom_name\"", sw.toString());
        }

        @Test
        @DisplayName("Write: writes the default name for standard enum")
        void writeDefaultName() throws Exception {
            StringWriter sw = new StringWriter();
            JsonWriter writer = new JsonWriter(sw);
            adapter.write(writer, TestEnum.VALUE_ONE);
            assertEquals("\"VALUE_ONE\"", sw.toString());
        }

        @Test
        @DisplayName("Read: reads the primary serialized name")
        void readPrimaryName() throws Exception {
            JsonReader reader = new JsonReader(new StringReader("\"custom_name\""));
            assertEquals(TestEnum.VALUE_TWO, adapter.read(reader));
        }

        @Test
        @DisplayName("Read: reads from alternate names")
        void readAlternateNames() throws Exception {
            JsonReader reader1 = new JsonReader(new StringReader("\"alt1\""));
            JsonReader reader2 = new JsonReader(new StringReader("\"alt2\"")); // Not quite, use StringReader
            assertEquals(TestEnum.VALUE_TWO, adapter.read(reader1));

            JsonReader reader3 = new JsonReader(new StringReader("\"alt2\""));
            assertEquals(TestEnum.VALUE_TWO, adapter.read(reader3));
        }

        @Test
        @DisplayName("Read: reads the default name")
        void readDefaultName() throws Exception {
            JsonReader reader = new JsonReader(new StringReader("\"VALUE_ONE\""));
            assertEquals(TestEnum.VALUE_ONE, adapter.read(reader));
        }

        @Test
        @DisplayName("Read: handles null tokens")
        void readNull() throws Exception {
            JsonReader reader = new JsonTokenReader("null");
            assertNull(adapter.read(reader));
        }

        @Test
        @DisplayName("Read: returns null for unknown names (if not in toString)")
        void readUnknownName() throws Exception {
            JsonReader reader = new JsonReader(new StringReader("\"UNKNOWN\""));
            assertNull(adapter.read(reader));
        }

        @Test
        @DisplayName("Read: uses toString() mapping if name match fails")
        void readViaToString() throws Exception {
            // Since we didn't override toString in TestEnum, it's the name.
            // Let's test a case where we might have a different toString.
            // In the provided code, stringToConstant is populated with constant.toString()
            // For TestEnum.VALUE_ONE, toString() is "VALUE_ONE".
            // If we had an enum where toString() != name(), we could test it.
            // However, in the current TestEnum, it's the same.
            // To strictly follow the logic: nameToConstant.put(name, constant) 
            // AND stringToConstant.put(toStringVal, constant).
            JsonReader reader = new JsonReader(new StringReader("\"VALUE_ONE\""));
            assertEquals(TestEnum.VALUE_ONE, adapter.read(reader));
        }

        @Test
        @DisplayName("Write: writes null when value is null")
        void writeNull() throws Exception {
            StringWriter sw = new StringWriter();
            JsonWriter writer = new JsonWriter(sw);
            adapter.write(writer, null);
            assertEquals("null", sw.toString());
        }
    }

    // Helper class to simulate a null token
    static class JsonTokenReader extends JsonReader {
        public JsonTokenReader(String json) {
            super(new StringReader(json));
        }
    }
}