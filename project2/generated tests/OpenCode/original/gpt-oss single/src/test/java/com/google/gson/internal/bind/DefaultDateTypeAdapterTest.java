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

    @Test
    @DisplayName("write() should output null for null value")
    void writeNull() throws Exception {
        DefaultDateTypeAdapter<Date> adapter = new DefaultDateTypeAdapter<>(
                DefaultDateTypeAdapter.DateType.DATE, "yyyy-MM-dd");
        StringWriter out = new StringWriter();
        JsonWriter writer = new JsonWriter(out);
        adapter.write(writer, null);
        assertEquals("null", out.toString());
    }

    @Test
    @DisplayName("write() should serialize date using first pattern")
    void writeDate() throws Exception {
        String pattern = "yyyyMMdd";
        DefaultDateTypeAdapter<Date> adapter = new DefaultDateTypeAdapter<>(
                DefaultDateTypeAdapter.DateType.DATE, pattern);
        Date date = new SimpleDateFormat(pattern).parse("20230101");
        StringWriter out = new StringWriter();
        JsonWriter writer = new JsonWriter(out);
        adapter.write(writer, date);
        assertEquals("20230101", out.toString());
    }

    @Test
    @DisplayName("read() should return null for JSON null")
    void readNull() throws Exception {
        DefaultDateTypeAdapter<Date> adapter = new DefaultDateTypeAdapter<>(
                DefaultDateTypeAdapter.DateType.DATE, "yyyy-MM-dd");
        JsonReader reader = new JsonReader(new StringReader("null"));
        assertNull(adapter.read(reader));
    }

    @Test
    @DisplayName("read() should parse date with custom pattern")
    void readWithPattern() throws Exception {
        String pattern = "yyyy-MM-dd";
        DefaultDateTypeAdapter<Date> adapter = new DefaultDateTypeAdapter<>(
                DefaultDateTypeAdapter.DateType.DATE, pattern);
        JsonReader reader = new JsonReader(new StringReader("2023-01-15"));
        Date result = adapter.read(reader);
        Date expected = new SimpleDateFormat(pattern).parse("2023-01-15");
        assertEquals(expected.getTime(), result.getTime());
    }

    @Test
    @DisplayName("read() should fallback to ISO8601 parsing")
    void readIsoFallback() throws Exception {
        // pattern does not match ISO string
        String pattern = "MM/dd/yyyy";
        DefaultDateTypeAdapter<Date> adapter = new DefaultDateTypeAdapter<>(
                DefaultDateTypeAdapter.DateType.DATE, pattern);
        String iso = "2021-12-31T10:15:30Z";
        JsonReader reader = new JsonReader(new StringReader(iso));
        Date result = adapter.read(reader);
        // Verify result by parsing ISO with SimpleDateFormat to time zone UTC
        SimpleDateFormat isoFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        isoFmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date expected = isoFmt.parse(iso);
        assertEquals(expected.getTime(), result.getTime());
    }

    @Test
    @DisplayName("toString() should reflect date pattern of first formatter")
    void toStringPattern() {
        String pattern = "yyyy-MM-dd";
        DefaultDateTypeAdapter<Date> adapter = new DefaultDateTypeAdapter<>(
                DefaultDateTypeAdapter.DateType.DATE, pattern);
        assertTrue(adapter.toString().contains(pattern));
    }
}
