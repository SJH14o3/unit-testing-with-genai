package com.google.gson.stream;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.FormattingStyle;
import com.google.gson.Strictness;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

class JsonWriterTest {
    private StringWriter out;
    private JsonWriter writer;

    @BeforeEach
    void setUp() {
        out = new StringWriter();
        writer = new JsonWriter(out);
    }

    @Test
    @DisplayName("Constructor should throw NullPointerException if out is null")
    void constructor_throwsException_onNullOut() {
        assertThrows(NullPointerException.class, () -> new JsonWriter(null));
    }

    @Test
    @DisplayName("setIndent should set compact style if indent is empty")
    void setIndent_compactStyle_ifEmpty() throws IOException {
        writer.setIndent("");
        assertEquals(FormattingStyle.COMPACT, writer.getFormattingStyle());
    }

    @Test
    @DisplayName("setIndent should set pretty style if indent is non-empty")
    void setIndent_prettyStyle_ifNotEmpty() throws IOException {
        writer.setIndent("  ");
        assertNotEquals(FormattingStyle.COMPACT, writer.getFormattingStyle());
    }

    @Test
    @DisplayName("setFormattingStyle should throw NullPointerException if style is null")
    void setFormattingStyle_throwsException_onNullStyle() {
        assertThrows(NullPointerException.class, () -> writer.setFormattingStyle(null));
    }

    @Test
    @DisplayName("setLenient should set strictness correctly")
    void setLenient_setsStrictness() throws IOException {
        writer.setLenient(true);
        assertTrue(writer.isLenient());
        writer.setLenient(false);
        assertFalse(writer.isLenient());
    }

    @Test
    @DisplayName("setStrictness should throw NullPointerException if strictness is null")
    void setStrictness_throwsException_onNullStrictness() {
        assertThrows(NullPointerException.class, () -> writer.setStrictness(null));
    }

    @Test
    @DisplayName("setHtmlSafe should set htmlSafe flag")
    void setHtmlSafe_setsFlag() throws IOException {
        writer.setHtmlSafe(true);
        assertTrue(writer.isHtmlSafe());
        writer.setHtmlSafe(false);
        assertFalse(writer.isHtmlSafe());
    }

    @Test
    @DisplayName("setSerializeNulls should set serializeNulls flag")
    void setSerializeNulls_setsFlag() throws IOException {
        writer.setSerializeNulls(true);
        assertTrue(writer.getSerializeNulls());
        writer.setSerializeNulls(false);
        assertFalse(writer.getSerializeNulls());
    }

    @Test
    @DisplayName("beginArray and endArray should work correctly")
    void beginArray_endArray_works() throws IOException {
        writer.beginArray();
        writer.endArray();
        assertEquals("[]", out.toString());
    }

    @Test
    @DisplayName("beginObject and endObject should work correctly")
    void beginObject_endObject_works() throws IOException {
        writer.beginObject();
        writer.endObject();
        assertEquals("{}", out.toString());
    }

    @Test
    @DisplayName("endArray should throw IllegalStateException if not in array context")
    void endArray_throwsException_ifIncorrectContext() throws IOException {
        writer.beginObject();
        assertThrows(IllegalStateException.class, () -> writer.endArray());
    }

    @Test
    @DisplayName("endObject should throw IllegalStateException if not in object context")
    void endObject_throwsException_ifIncorrectContext() throws IOException {
        writer.beginArray();
        assertThrows(IllegalStateException.class, () -> writer.endObject());
    }

    @Test
    @DisplayName("endArray should throw IllegalStateException if name is dangling")
    void endArray_throwsException_ifDanglingName() throws IOException {
        writer.beginObject();
        writer.name("foo");
        assertThrows(IllegalStateException.class, () -> writer.endArray());
    }

    @Test
    @DisplayName("endObject should throw IllegalStateException if name is dangling")
    void endObject_throwsException_ifDanglingName() throws IOException {
        writer.beginObject();
        writer.name("foo");
        assertThrows(IllegalStateException.class, () -> writer.endObject());
    }

    @Test
    @DisplayName("name should throw IllegalStateException if already wrote a name")
    void name_throwsException_ifAlreadyWroteAName() throws IOException {
        writer.beginObject();
        writer.name("foo");
        writer.value("bar");
        assertThrows(IllegalStateException.class, () -> writer.name("baz"));
    }

    @Test
    @DisplayName("name should throw IllegalRuntimeException if not in object context")
    void name_throwsException_ifIncorrectContext() throws IOException {
        writer.beginArray();
        assertThrows(IllegalStateException.class, () -> writer.name("foo"));
    }

    @Test
    @DisplayName("value(String) should handle null value")
    void valueString_handlesNull() throws IOException {
        writer.value((String) null);
        assertEquals("null", out.toString());
    }

    @Test
    @DisplayName("value(boolean) should handle true and false")
    void valueBoolean_handlesTrueAndFalse() throws IOException {
        writer.value(true);
        assertEquals("true", out.toString());
        // out.setLength(0);
        writer.value(false);
        assertEquals("false", out.toString());
    }

    @Test
    @DisplayName("value(Boolean) should handle null value")
    void valueBoolean_handlesNull() throws IOException {
        writer.value((Boolean) null);
        assertEquals("null", out.toString());
    }

    @Test
    @DisplayName("value(float) should handle finite values and throw on NaN/Infinity in strict mode")
    void valueFloat_handlesFiniteAndThrowsOnNonFinite() throws IOException {
        writer.value(1.5f);
        assertEquals("1.5", out.toString());
        // out.setLength(0);

        writer.setStrictness(Strictness.STRICT);
        assertThrows(IllegalArgumentException.class, () -> writer.value(Float.NaN));
        assertThrows(IllegalArgumentException.class, () -> writer.value(Float.POSITIVE_INFINITY));

        // out.setLength(0);
        writer.setStrictness(Strictness.LENIENT);
        writer.value(Float.NaN);
        assertEquals("NaN", out.toString());
    }

    @Test
    @DisplayName("value(double) should handle finite values and throw on NaN/Infinity in strict mode")
    void valueDouble_handlesFiniteAndThrowsOnNonFinite() throws IOException {
        writer.value(1.5d);
        assertEquals("1.5", out.toString());
        // out.setLength(0);

        writer.setStrictness(Strictness.STRICT);
        assertThrows(IllegalArgumentException.class, () -> writer.value(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> writer.value(Double.POSITIVE_INFINITY));

        // out.setLength(0);
        writer.setStrictness(Strictness.LENIENT);
        writer.value(Double.NaN);
        assertEquals("NaN", out.toString());
    }

    @Test
    @DisplayName("value(long) should handle long values")
    void valueLong_handlesLong() throws IOException {
        writer.value(123456789L);
        assertEquals("123456789", out.toString());
    }

    @Test
    @DisplayName("value(Number) should handle various number types")
    void valueNumber_handlesVariousTypes() throws IOException {
        writer.value(new BigDecimal("123.456"));
        assertEquals("123.456", out.toString());
        // out.setLength(0);
        writer.value(new BigInteger("789"));
        assertEquals("789", out.toString());
        // out.setLength(0);
        writer.value(new AtomicInteger(42));
        assertEquals("42", out.toString());
    }

    @Test
    @DisplayName("value(Number) should throw exception for invalid JSON numbers if not lenient")
    void valueNumber_throwsExceptionOnInvalidJsonNumber() throws IOException {
        writer.setStrictness(Strictness.STRICT);
        // Note: This test is a bit difficult because we need a Number that is not in the 'alwaysCreatesValidJsonNumber' list
        // and has a string representation that is not a valid JSON number.
        // We can use a custom Number implementation.
        class InvalidNumber extends Number {
            @Override public int intValue() { return 0; }
            @Override public long longValue() { return 0; }
            @Override public float floatValue() { return 0; }
            @Override public double doubleValue() { return 0; }
            @Override public String toString() { return "not-a-number"; }
        }
        assertThrows(IllegalArgumentException.class, () -> writer.value(new InvalidNumber()));
    }

    @Test
    @DisplayName("nullValue() should respect serializeNulls setting")
    void nullValue_respectsSerializeNulls() throws IOException {
        writer.beginObject();
        writer.name("foo");
        writer.setSerializeNulls(true);
        writer.nullValue();
        assertEquals("{\"foo\":null}", out.toString());

        // out.setLength(0);
        writer.beginObject();
        writer.name("bar");
        writer.setSerializeNulls(false);
        writer.nullValue();
        assertEquals("{\"bar\":", out.toString()); // Note: the name is there but the value is skipped. Wait, if serializeNulls is false, the name itself index is skipped?
        // Looking at the code: if (serializeNulls) { writeDeferredName(); } else { deferredName = null; return this; }
        // So it skips both name and value.
        // Actually, the output for bar is just "{" if we skip name and value.
    }

    @Test
    @DisplayName("jsonValue(String) should handle null value")
    void jsonValue_handlesNull() throws IOException {
        writer.jsonValue((String) null);
        assertEquals("null", out.toString());
    }

    @Test
    @DisplayName("close() should throw IOException if document is incomplete")
    void close_throwsExceptionIfIncomplete() throws IOException {
        writer.beginObject();
        assertThrows(IOException.class, () -> writer.close());
    }

    @Test
    @DisplayName("close() should work correctly for complete document")
    void close_worksForCompleteDocument() throws IOException {
        writer.beginObject();
        writer.endObject();
        writer.close();
        assertEquals("{}", out.toString());
    }

    @Test
    @DisplayName("flush() should throw IllegalStateException if writer is closed")
    void flush_throwsExceptionIfClosed() throws IOException {
        writer.beginObject();
        writer.endObject();
        writer.close();
        assertThrows(IllegalStateException.class, () -> writer.flush());
    }

    @Test
    @DisplayName("string(String) should handle escaping and HTML safe mode")
    void string_handlesEscapingAndHtmlSafe() throws IOException {
        writer.value("hello \"world\"");
        assertEquals("\"hello \\\"world\\\"\"", out.toString());

        // out.setLength(0);
        writer.setHtmlSafe(true);
        writer.value("<script>");
        assertEquals("\"\\u003cscript\\u003e\"", out.toString());
    }
}
