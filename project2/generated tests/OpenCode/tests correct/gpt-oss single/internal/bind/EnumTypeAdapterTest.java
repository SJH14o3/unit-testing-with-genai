package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

class EnumTypeAdapterTest {

    /** a tiny enum used for testing */
    enum TestEnum {
        RED,
        @SerializedName("blue") BLUE,
        GREEN {
            @Override
            public String toString() {
                return "GRN";
            }
        }
    }

    /** helper to create a JsonReader that reads a single JSON value */
    private JsonReader readerOf(String json) {
        return new JsonReader(new StringReader(json));
    }

    @Test
    void read_null_returns_null() throws Exception {
        TypeAdapter<TestEnum> adapter = new Gson().getAdapter(TestEnum.class);
        JsonReader reader = readerOf("null");
        TestEnum result = adapter.read(reader);
        assertNull(result, "Reading JSON null should return null");
    }

    @Test
    void read_using_name_constant_returns_correct_enum() throws Exception {
        TypeAdapter<TestEnum> adapter = new Gson().getAdapter(TestEnum.class);
        JsonReader reader = readerOf("\"RED\"");
        TestEnum result = adapter.read(reader);
        assertEquals(TestEnum.RED, result, "Should map name to enum constant");
    }

    @Test
    void read_using_serialized_name_returns_correct_enum() throws Exception {
        TypeAdapter<TestEnum> adapter = new Gson().getAdapter(TestEnum.class);
        JsonReader reader = readerOf("\"blue\"");
        TestEnum result = adapter.read(reader);
        assertEquals(TestEnum.BLUE, result, "Should map serialized name to enum constant");
    }

    @Test
    void read_using_string_to_constant_when_name_missing_returns_constant() throws Exception {
        TypeAdapter<TestEnum> adapter = new Gson().getAdapter(TestEnum.class);
        // "GRN" is only present in toString() of GREEN
        JsonReader reader = readerOf("\"GRN\"");
        TestEnum result = adapter.read(reader);
        assertEquals(TestEnum.GREEN, result, "Should use stringToConstant when nameToConstant fails");
    }

    @Test
    void write_with_null_writes_null() throws Exception {
        TypeAdapter<TestEnum> adapter = new Gson().getAdapter(TestEnum.class);
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        adapter.write(writer, null);
        assertEquals("null", sw.toString(), "Writing null should produce JSON null");
    }

    @Test
    void write_with_non_null_writes_appropriate_name() throws Exception {
        TypeAdapter<TestEnum> adapter = new Gson().getAdapter(TestEnum.class);
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        adapter.write(writer, TestEnum.BLUE);
        assertEquals("\"blue\"", sw.toString(), "Writing BLUE should output serialized name");
    }
}
