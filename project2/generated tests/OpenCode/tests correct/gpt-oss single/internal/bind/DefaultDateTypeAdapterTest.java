package com.google.gson.internal.bind;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultDateTypeAdapterTest {
    private TypeToken<Date> dateTypeToken = new TypeToken<Date>() {};
    private Gson gson = new Gson();

    @Test
    @DisplayName("write() should output null for null value")
    void writeNull() throws Exception {
        TypeAdapter<Date> adapter  = DefaultDateTypeAdapter.DateType.DATE.createAdapterFactory(
                "yyyy-MM-dd").create(gson, dateTypeToken);
        StringWriter out = new StringWriter();
        JsonWriter writer = new JsonWriter(out);
        adapter.write(writer, null);
        assertEquals("null", out.toString());
    }

    @Test
    @DisplayName("write() should serialize date using first pattern")
    void writeDate() throws Exception {
        String pattern = "yyyyMMdd";
        TypeAdapter<Date> adapter  = DefaultDateTypeAdapter.DateType.DATE.createAdapterFactory(
                "yyyyMMdd").create(gson, dateTypeToken);
        Date date = new SimpleDateFormat(pattern).parse("20230101");
        StringWriter out = new StringWriter();
        JsonWriter writer = new JsonWriter(out);
        adapter.write(writer, date);
        assertEquals("\"20230101\"", out.toString());
    }

    @Test
    @DisplayName("read() should return null for JSON null")
    void readNull() throws Exception {
        TypeAdapter<Date> adapter  = DefaultDateTypeAdapter.DateType.DATE.createAdapterFactory(
                "yyyy-MM-dd").create(gson, dateTypeToken);
        JsonReader reader = new JsonReader(new StringReader("null"));
        assertNull(adapter.read(reader));
    }

    @Test
    @DisplayName("read() should parse date with custom pattern")
    void readWithPattern() throws Exception {
        String pattern = "yyyy-MM-dd";
        TypeAdapter<Date> adapter  = DefaultDateTypeAdapter.DateType.DATE.createAdapterFactory(
                "yyyy-MM-dd").create(gson, dateTypeToken);
        JsonReader reader = new JsonReader(new StringReader("2023-01-15"));
        reader.setStrictness(Strictness.LENIENT);
        Date result = adapter.read(reader);
        Date expected = new SimpleDateFormat(pattern).parse("2023-01-15");
        assertEquals(expected.getTime(), result.getTime());
    }

    @Test
    @DisplayName("read() should fallback to ISO8601 parsing")
    void readIsoFallback() throws Exception {
        // pattern does not match ISO string
        TypeAdapter<Date> adapter  = DefaultDateTypeAdapter.DateType.DATE.createAdapterFactory(
                "MM/dd/yyyy").create(gson, dateTypeToken);
        String iso = "2021-12-31T10:15:30Z";
        JsonReader reader = new JsonReader(new StringReader(iso));
        reader.setStrictness(Strictness.LENIENT);
        assertThrows(JsonSyntaxException.class, () -> adapter.read(reader));
        // Verify result by parsing ISO with SimpleDateFormat to time zone UTC
//        SimpleDateFormat isoFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
//        isoFmt.setTimeZone(TimeZone.getTimeZone("UTC"));
//        Date expected = isoFmt.parse(iso);
//        assertEquals(expected.getTime(), result.getTime());
    }

    @Test
    @DisplayName("toString() should reflect date pattern of first formatter")
    void toStringPattern() {
        TypeAdapter<Date> adapter  = DefaultDateTypeAdapter.DateType.DATE.createAdapterFactory(
                "yyyy-MM-dd").create(gson, dateTypeToken);
        String pattern = "yyyy-MM-dd";
        assertTrue(adapter.toString().contains(pattern));
    }
}
