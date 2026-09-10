import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.FormattingStyle;
import com.google.gson.Strictness;
import org.junit.jupiter.api.*;
import java.io.StringWriter;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class JsonWriterTest {

    @Test
    @DisplayName("write string value with escaping")
    void testStringValueEscaping() throws Exception {
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        writer.value("hello \"world\" \\ \n ");
        writer.flush();
        String expected = "\"hello \"world\" \\\\ \n \u2028\u2029\"";
        assertEquals(expected, sw.toString());
    }

    @Test
    @DisplayName("null value respects serializeNulls flag")
    void testNullSerialization() throws Exception {
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        writer.name("missing");
        writer.nullValue(); // default serializeNulls true
        writer.flush();
        assertEquals("\"missing\":null", sw.toString());

        sw = new StringWriter();
        writer = new JsonWriter(sw);
        writer.setSerializeNulls(false);
        writer.name("missing");
        writer.nullValue(); // should skip
        writer.flush();
        assertEquals("", sw.toString());
    }

    @Test
    @DisplayName("begin/end object writes correct delimiters and throws on mismatched close")
    void testObjectScope() throws Exception {
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        writer.beginObject();
        writer.name("a").value(1);
        writer.endObject();
        writer.flush();
        assertEquals("{\"a\":1}", sw.toString());

        sw = new StringWriter();
        writer = new JsonWriter(sw);
        writer.beginArray();
        Exception e = assertThrows(IllegalStateException.class, writer::endObject);
        assertEquals("Please begin an object before writing a name.", e.getMessage());
    }

    @Test
    @DisplayName("write float NaN throws when not lenient")
    void testFloatNaNStrict() {
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> writer.value(Float.NaN));
        assertEquals("Numeric values must be finite, but was NaN", e.getMessage());
    }

    @Test
    @DisplayName("write float NaN allowed when lenient") throws Exception {
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        writer.setStrictness(Strictness.LENIENT);
        writer.value(Float.NaN);
        writer.flush();
        assertEquals("NaN", sw.toString());
    }

    @Test
    @DisplayName("html safe characters are escaped when htmlSafe true") throws Exception {
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        writer.setHtmlSafe(true);
        writer.value("<>&='");
        writer.flush();
        assertEquals("\"\u003c\u003e\u0026\u003d\u0027\"", sw.toString());
    }

    @Test
    @DisplayName("multiple top-level values allowed only in LENIENT mode") {
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        writer.value(1);
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> writer.value(2));
        assertEquals("JSON must have only one top-level value.", e.getMessage());

        sw = new StringWriter();
        writer = new JsonWriter(sw);
        writer.setStrictness(Strictness.LENIENT);
        writer.value(1);
        assertDoesNotThrow(() -> writer.value(2));
    }
}
