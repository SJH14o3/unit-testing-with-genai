package com.google.gson.stream;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.google.gson.Strictness;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JsonWriterTest {

    private JsonWriter createWriter() {
        return new JsonWriter(new StringWriter());
    }

    @Test
    @DisplayName("writes a simple array and closes correctly")
    void simpleArray() throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter w = new JsonWriter(writer);
        w.beginArray();
        w.value(1);
        w.value("two");
        w.endArray();
        w.flush();
        w.close();
        assertEquals("[1,\"two\"]", writer.toString());
    }

    @Test
    @DisplayName("throws on nested beginObject after endObject")
    void badObjectSequence() throws IOException {
        JsonWriter w = createWriter();
        w.beginObject();
        w.name("x");
        w.value(1);
        w.endObject();
        assertThrows(IllegalStateException.class, w::endObject);
    }

    @Test
    @DisplayName("serialises nulls when configured")
    void nullValueBehavior() throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter w = new JsonWriter(writer);
        w.setSerializeNulls(false);
        w.beginObject();
        w.name("n");
        w.value((String) null);
        w.endObject();
        assertEquals("{}", writer.toString());

        w.setSerializeNulls(true);
        writer = new StringWriter();
        w = new JsonWriter(writer);
        w.beginObject();
        w.name("n");
        w.value((String) null);
        w.endObject();
        assertEquals("{\"n\":null}", writer.toString());
    }

    @Test
    @DisplayName("honors HTML‑safe escaping")
    void htmlSafeEscaping() throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter w = new JsonWriter(writer);
        w.setHtmlSafe(true);
        w.beginObject();
        w.name("html");
        w.value("<script>");
        w.endObject();
        assertEquals("{\"html\":\"\\u003cscript\\u003e\"}", writer.toString());
    }

    @Test
    @DisplayName("fails on NaN/Infinity when strictness is LEGACY_STRICT")
    void strictnessRejectsNonFinite() {
        JsonWriter w = createWriter();
        w.setStrictness(Strictness.LEGACY_STRICT);
        assertThrows(IllegalArgumentException.class, () -> w.value(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> w.value(Float.POSITIVE_INFINITY));
    }

    @Test
    @DisplayName("supports arbitrary numbers including BigInteger")
    void bigNumberSupport() throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter w = new JsonWriter(writer);
        w.beginArray();
        w.value(new BigInteger("12345678901234567890"));
        w.value(new BigDecimal("12345.6789"));
        w.endArray();
        assertEquals("[12345678901234567890,12345.6789]", writer.toString());
    }
}