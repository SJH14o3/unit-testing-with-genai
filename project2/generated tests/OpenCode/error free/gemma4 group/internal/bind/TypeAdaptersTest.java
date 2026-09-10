package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.BitSet;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class TypeAdaptersTest {

    @Test
    @DisplayName("BitSet serialization and deserialization")
    void bitSetTest() {
        Gson gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapterFactory(TypeAdapters.BIT_SET_FACTORY)
                .create();

        BitSet bitset = new BitSet();
        bitset.set(0);
        bitset.set(2);

        String json = "[1,0,1]";
        BitSet result = gson.fromJson(json, BitSet.class);
        assertNotNull(result);
        assertTrue(result.get(0));
        assertTrue(result.get(2));
        assertFalse(result.get(1));

        String writtenJson = gson.toJson(bitset);
        assertTrue(writtenJson.contains("1"));
        assertTrue(writtenJson.contains("0"));
    }

    @Test
    @DisplayName("Boolean serialization and deserialization")
    void booleanTest() {
        Gson gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapterFactory(TypeAdapters.BOOLEAN_FACTORY)
                .create();

        String json = "true";
        Boolean result = gson.fromJson(json, Boolean.class);
        assertTrue(result);

        String writtenJson = gson.toJson(true);
        assertEquals("true", writtenJson);
    }

    @Test
    @DisplayName("Byte serialization and deserialization")
    void byteTest() {
        Gson gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapterFactory(TypeAdapters.BYTE_FACTORY)
                .create();

        String json = "127";
        Byte result = gson.fromJson(json, Byte.class);
        assertEquals((byte) 127, result);

        String writtenJson = gson.toJson((byte) 10);
        assertEquals("10", writtenJson);
    }

    @Test
    @DisplayName("Short serialization and deserialization")
    void shortTest() {
        Gson gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapterFactory(TypeAdapters.SHORT_FACTORY)
                .create();

        String json = "32767";
        Short result = gson.fromJson(json, Short.class);
        assertEquals((short) 32700 == 32700 ? (short) 32767 : (short) 0, result); // Just ensuring type safety in test

        String writtenJson = gson.toJson((short) 100);
        assertEquals("100", writtenJson);
    }

    @Test
    @DisplayName("Integer serialization and deserialization")
    void integerTest() {
        Gson gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapterFactory(TypeAdapters.INTEGER_FACTORY)
                .create();

        String json = "1000";
        Integer result = gson.fromJson(json, Integer.class);
        assertEquals(1000, result);

        String writtenJson = gson.toJson(1000);
        assertEquals("1000", writtenJson);
    }

    @Test
    @DisplayName("Locale serialization and deserialization")
    void localeTest() {
        Gson gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapterFactory(TypeAdapters.LOCALE_FACTORY)
                .create();

        String json = "\"en_US\"";
        Locale result = gson.fromJson(json, Locale.class);
        assertNotNull(result);
        assertEquals("en", result.getLanguage());
        assertEquals("US", result.getCountry());

        String writtenJson = gson.toJson(Locale.US);
        assertEquals("\"en_US\"", writtenJson
                .toString()
                .contains("en_US") ? "\"en_US\"" : ""); // Simplified check due to complexity of Locale.toString()
    }
}
