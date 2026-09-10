package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

class EnumTypeAdapterTest {

    enum TestEnum {
        VAL1,
        VAL2,
        @com.google.gson.annotations.SerializedName("custom")
        VAL3
    }

    @Test
    @DisplayName("Factory returns adapter for enum types")
    void factoryReturnsAdapterForEnum() {
        Gson gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapterFactory(EnumTypeAdapter.FACTORY)
                .create();
        
        assertNotNull(gson.getAdapter(TestEnum.class));
        assertNull(gson.getAdapter(String.class));
    }

    @Test
    @DisplayName("Serialization and deserialization of Enum with default names")
    void serializationDeserializationDefaultNames() throws Exception {
        Gson gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapterFactory(EnumTypeAdapter.FACTORY)
                .create();

        String json = "\"VAL1\"";
        TestEnum result = gson.fromJson(json, TestEnum.class);
        assertEquals(TestEnum.VAL1, result);

        String writtenJson = gson.toJson(TestEnum.VAL2);
        assertEquals("\"VAL2\"", writtenJson);
    }

    @Test
    @DisplayName("Serialization and deserialization of Enum with @SerializedName")
    void serializationDeserializationWithSerializedName() throws Exception {
        Gson gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapterFactory(EnumTypeAdapter.FACTORY)
                .create();

        String json = "\"custom\"";
        TestEnum result = gson.fromJson(json, TestEnum.class);
        assertEquals(TestEnum.VAL3, result);

        String writtenJson = gson.toJson(TestEnum.VAL3);
        assertEquals("\"custom\"", writtenJson);
    }

    @Test
    @DisplayName("Deserialization of unknown enum constant returns null")
    void deserializationUnknownReturnsNull() throws Exception {
        Gson gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapterFactory(EnumTypeAdapter.FACTORY)
                .create();

        String json = "\"UNKNOWN\"";
        TestToken<TestEnum> typeToken = new TypeToken<TestEnum>() {};
        TestEnum result = gson.fromJson(json, TestEnum.class);
        assertNull(result);
    }
}
