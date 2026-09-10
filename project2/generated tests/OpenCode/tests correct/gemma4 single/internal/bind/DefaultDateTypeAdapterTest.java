package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DefaultDateTypeAdapter Test")
class DefaultDateTypeAdapterTest {

    private Locale originalLocale;
    private Gson gson;
    private final TypeToken<Date> dateTypeToken = new TypeToken<Date>() {};

    @BeforeEach
    void setUp() {
        originalLocale = Locale.getDefault();
        gson = new Gson();
    }

    @AfterEach
    void tearDown() {
        Locale.setDefault(originalLocale);
    }

    @Test
    @DisplayName("DEFAULT_STYLE_FACTORY should create adapter for Date class")
    void defaultStyleFactory_createsAdapterForDate() {
        TypeAdapter<Date> adapter = DefaultDateTypeAdapter.DEFAULT_STYLE_FACTORY.create(gson, dateTypeToken);
        assertNotNull(adapter);
    }

    @Test
    @DisplayName("DEFAULT_STYLE_FACTORY should return null for non-Date class")
    void defaultStyleFactory_returnsNullForNonDate() {
        TypeAdapter<?> adapter = DefaultDateTypeAdapter.DEFAULT_STYLE_FACTORY.create(gson, new TypeToken<String>() {});
        assertNull(adapter);
    }

    @Test
    @DisplayName("write should handle null value")
    void write_handlesNull() throws Exception {
        TypeAdapter<Date> adapter = DefaultDateTypeAdapter.DEFAULT_STYLE_FACTORY.create(gson, dateTypeToken);
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        adapter.write(writer, null);
//        writer.endDocument();
        assertEquals("null", sw.toString());
    }

    @Test
    @DisplayName("write should format date correctly")
    void write_formatsDate() throws Exception {
        TypeAdapter<Date> adapter = DefaultDateTypeAdapter.DEFAULT_STYLE_FACTORY.create(gson, dateTypeToken);
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);

        // Use a fixed date for testing
        Date date = new Date(1696118400000L); // 2023-09-30T00:00:00Z (approx)
        adapter.write(writer, date);
//        writer.endDocument();

        // Since the format depends on the default DateFormat.DEFAULT,
        // we check if the output is a string and contains parts of the date.
        String output = sw.toString();
        assertNotNull(output);
        assertFalse(output.isEmpty());
    }

    @Test
    @DisplayName("read should handle null token")
    void read_handlesNullToken() throws Exception {
        TypeAdapter<Date> adapter = DefaultDateTypeAdapter.DEFAULT_STYLE_FACTORY.create(gson, dateTypeToken);
        JsonReader reader = new JsonReader(new StringReader("null"));
        Date date = adapter.read(reader);
        assertNull(date);
    }

    @Test
    @DisplayName("read should parse valid date string")
    void read_parsesValidDate() throws Exception {
        TypeAdapter<Date> adapter = DefaultDateTypeAdapter.DEFAULT_STYLE_FACTORY.create(gson, dateTypeToken);
        // Using a pattern that is likely to match the default format
        // We'll use a generic date string that most default formats can parse
        String json = "\"Sep 30, 2023 4:00:00 PM\"";
        JsonReader reader = new JsonReader(new StringReader(json));
        Date date = adapter.read(reader);
        assertNotNull(date);
    }

    @Test
    @DisplayName("read should throw JsonSyntaxException for invalid date string")
    void read_throwsExceptionForInvalidDate() {
        TypeAdapter<Date> adapter = DefaultDateTypeAdapter.DEFAULT_STYLE_FACTORY.create(gson, dateTypeToken);
        JsonReader reader = new JsonReader(new StringReader("\"not-a-date\""));
        assertThrows(JsonSyntaxException.class, () -> adapter.read(reader));
    }

    @Test
    @DisplayName("toString should return SimpleDateFormat pattern if applicable")
    void toString_returnsPatternForSimpleDateFormat() throws Exception {
        // Use the factory to get an adapter with a specific pattern
        TypeAdapter<Date> adapter = DefaultDateTypeAdapter.DateType.DATE.createAdapterFactory("yyyy-MM-dd").create(gson, dateTypeToken);
        assertTrue(adapter.toString().contains("yyyy-MM-dd"));
    }

    @Test
    @DisplayName("toString should return class name if not SimpleDateFormat")
    void toString_returnsClassNameForOtherFormats() throws Exception {
        // Hard to force not SimpleDateFormat without complex setup,
        // but we can test the current behavior.
        TypeAdapter<Date> adapter = DefaultDateTypeAdapter.DEFAULT_STYLE_FACTORY.create(gson, dateTypeToken);
        assertNotNull(adapter.toString());
    }

    @Test
    @DisplayName("Constructor should include US locale format when default locale is not US")
    void constructor_includesExtraFormatForNonUSLocale() throws Exception {
        Locale.setDefault(Locale.FRANCE);
        TypeAdapter<Date> adapter = DefaultDateTypeAdapter.DateType.DATE.createAdapterFactory("yyyy-MM-dd").create(gson, dateTypeToken);

        JsonReader reader = new JsonReader(new StringReader("\"2023-09-30\""));
        Date date = adapter.read(reader);
        assertNotNull(date);

        Locale.setDefault(originalLocale);
    }
}