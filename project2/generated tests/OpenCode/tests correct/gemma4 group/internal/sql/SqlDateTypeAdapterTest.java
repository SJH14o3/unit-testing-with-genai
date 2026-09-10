package com.google.gson.internal.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Strictness;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Date;
import java.util.Calendar;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class SqlDateTypeAdapterTest {

    private TypeAdapter<Date> adapter;

    @BeforeEach
    void setUp() {
        adapter = SqlDateTypeAdapter.FACTORY.create(new Gson(), TypeToken.get(Date.class));
    }

    @Test
    @DisplayName("Test writing Date to JSON")
    void testWrite() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.set(2023, Calendar.OCTOBER, 25);
        Date date = new Date(cal.getTimeInMillis());

        StringWriter sw = new StringWriter();
        JsonWriter jw = new JsonWriter(sw);
        adapter.write(jw, date);
        jw.close();

        assertEquals("\"Oct 25, 2023\"", sw.toString());
    }

    @Test
    @DisplayName("Test writing null Date to JSON")
    void testWriteNull() throws Exception {
        StringWriter sw = new StringWriter();
        JsonWriter jw = new JsonWriter(sw);
        adapter.write(jw, null);
        jw.close();

        assertEquals("null", sw.toString());
    }

    @ParameterizedTest
    @MethodSource("provider")
    @DisplayName("Test reading Date from JSON")
    void testRead(String json, int year, int month, int day) throws Exception {
        JsonReader jr = new JsonReader(new StringReader(json));
        jr.setStrictness(Strictness.LENIENT);
        Date result = adapter.read(jr);
        assertNotNull(result);

        Calendar calResult = Calendar.getInstance();
        calResult.setTime(result);

        assertEquals(year, calResult.get(Calendar.YEAR));
        assertEquals(month, calResult.get(Calendar.MONTH) + 1);
        assertEquals(day, calResult.get(Calendar.DAY_OF_MONTH));
    }

    static Stream<Arguments> provider() {
        return Stream.of(
                Arguments.of("'Oct 25, 2023'", 2023, 10, 25),
                Arguments.of("'Jan 01, 2000'", 2000, 1, 1)
        );
    }

    @Test
    @DisplayName("Test reading null from JSON")
    void testReadNull() throws Exception {
        JsonReader jr = new JsonReader(new StringReader("null"));
        Date result = adapter.read(jr);
        assertNull(result);
    }

    @Test
    @DisplayName("Test reading invalid format throws JsonSyntaxException")
    void testReadInvalidFormat() throws Exception {
        JsonReader jr = new JsonReader(new StringReader("\"2023-10-25\""));
        assertThrows(JsonSyntaxException.class, () -> adapter.read(jr));
    }
}
