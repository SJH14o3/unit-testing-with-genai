package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class DefaultDateTypeAdapterTest {

    @Test
    @DisplayName("Factory returns adapter for Date")
    void factoryReturnsAdapterForDate() {
        Gson gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapterFactory(DefaultDateTypeAdapter.DEFAULT_STYLE_FACTORY)
                .create();

        assertNotNull(gson.getAdapter(Date.class));
        assertNotNull(gson.getAdapter(String.class));
    }

    @Test
    @DisplayName("Serialization and deserialization of Date using default style")
    void serializationDeserializationDefault() throws Exception {
        Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy HH:mm:ss").create();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);
        Date date = sdf.parse("01-01-2025 10:00:00");

        String json = "\"" + sdf.format(date) + "\"";
        Date result = gson.fromJson(json, Date.class);
        assertNotNull(result);
        assertEquals(date.getTime(), result.getTime());

        String writtenJson = gson.toJson(date);
        assertEquals(json, writtenJson);
    }

    @Test
    @DisplayName("Deserialization of null returns null")
    void deserializationNullReturnsNull() throws Exception {
        Gson gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapterFactory(DefaultDateTypeAdapter.DEFAULT_STYLE_FACTORY)
                .create();

        String json = "null";
        Date result = gson.fromJson(json, Date.class);
        assertNull(result);
    }
}
